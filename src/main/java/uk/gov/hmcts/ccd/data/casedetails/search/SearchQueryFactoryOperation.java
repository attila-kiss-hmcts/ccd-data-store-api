package uk.gov.hmcts.ccd.data.casedetails.search;

import org.apache.commons.collections.CollectionUtils;
import uk.gov.hmcts.ccd.ApplicationParams;
import uk.gov.hmcts.ccd.data.casedetails.CaseDetailsEntity;
import uk.gov.hmcts.ccd.domain.model.definition.CaseState;
import uk.gov.hmcts.ccd.domain.service.common.AuthorisedCaseDefinitionDataService;
import uk.gov.hmcts.ccd.infrastructure.user.UserAuthorisation;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static uk.gov.hmcts.ccd.domain.service.common.AccessControlService.CAN_READ;

@Named
@Singleton
public class SearchQueryFactoryOperation {

    private static final String AND = " AND ";
    private static final String OPERATION_EQ = " = ";
    private static final String OPERATION_LIKE = " LIKE ";

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String MAIN_QUERY = "SELECT * FROM case_data WHERE %s ORDER BY created_date %s";
    private static final String MAIN_COUNT_QUERY = "SELECT count(*) FROM case_data WHERE %s";

    private static final String SORT_ASCENDING = "ASC";

    private final CriterionFactory criterionFactory;
    private final ApplicationParams applicationParam;
    private final UserAuthorisation userAuthorisation;
    private final AuthorisedCaseDefinitionDataService authorisedCaseDefinitionDataService;

    public SearchQueryFactoryOperation(CriterionFactory criterionFactory,
                                       EntityManager entityManager,
                                       ApplicationParams applicationParam,
                                       UserAuthorisation userAuthorisation,
                                       AuthorisedCaseDefinitionDataService authorisedCaseDefinitionDataService) {
        this.criterionFactory = criterionFactory;
        this.entityManager = entityManager;
        this.applicationParam = applicationParam;
        this.userAuthorisation = userAuthorisation;
        this.authorisedCaseDefinitionDataService = authorisedCaseDefinitionDataService;
    }

    public Query build(MetaData metadata, Map<String, String> params, boolean isCountQuery) {
        final List<Criterion> criteria = criterionFactory.build(metadata, params);
        String queryString = String.format(isCountQuery ? MAIN_COUNT_QUERY : MAIN_QUERY,
                                           secure(toClauses(criteria), metadata),
                                           metadata.getSortDirection().orElse(SORT_ASCENDING).toUpperCase()
        );
        Query query;
        if (isCountQuery) {
            query = entityManager.createNativeQuery(queryString);
        } else {
            query = entityManager.createNativeQuery(queryString, CaseDetailsEntity.class);
        }
        addParameters(query, criteria);
        return query;
    }

    private String secure(String clauses, MetaData metadata) {
        String newClauses = clauses;
        if (UserAuthorisation.AccessLevel.GRANTED.equals(userAuthorisation.getAccessLevel())) {
            newClauses += String.format(
                " AND id IN (SELECT cu.case_data_id FROM case_users AS cu WHERE user_id = '%s')",
                userAuthorisation.getUserId()
            );
        }

        // restrict cases to the case states the user has access to
        List<CaseState> caseStates = authorisedCaseDefinitionDataService.getUserAuthorisedCaseStates(metadata.getJurisdiction(),
                                                                                                     metadata.getCaseTypeId(),
                                                                                                     CAN_READ);
        if (CollectionUtils.isNotEmpty(caseStates)) {
            newClauses += String.format(" AND state IN ('%s')", String.join("','", caseStates.stream().map(CaseState::getId).collect(Collectors.toList())));
        }

        return newClauses;
    }

    private void addParameters(final Query query, List<Criterion> critereon) {

        IntStream.range(0, critereon.size())
                .forEach(position -> query.setParameter(position, critereon.get(position).getSoughtValue()));
    }

    private String toClauses(final List<Criterion> criterion) {
        return IntStream.range(0, criterion.size())
                .mapToObj(Integer::new)
                .map(position -> criterion.get(position).buildClauseString(position, getOperation()))
                .collect(Collectors.joining(AND));
    }

    private String getOperation() {
        return this.applicationParam.isWildcardSearchAllowed() ? OPERATION_LIKE : OPERATION_EQ;
    }

}
