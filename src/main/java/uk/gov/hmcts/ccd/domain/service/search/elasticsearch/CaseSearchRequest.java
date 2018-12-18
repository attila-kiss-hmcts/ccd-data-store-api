package uk.gov.hmcts.ccd.domain.service.search.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Sample ES json search request.
 * {
 *   "query": {
 *     "bool": {
 *       "filter": {
 *         "match": { "state": "AwaitingPayment"}
 *       }
 *     }
 *   },
 *  "sort": {
 *   "id": { "order":"asc" }
 *   }
 * }
 */
@Slf4j
public class CaseSearchRequest {

    public static final String QUERY_NAME = "query";

    private final String caseTypeId;
    private final JsonNode searchRequestJsonNode;

    public CaseSearchRequest(String caseTypeId, JsonNode searchRequestJsonNode) {
        this.caseTypeId = caseTypeId;
        this.searchRequestJsonNode = searchRequestJsonNode;
    }

    public String getCaseTypeId() {
        return caseTypeId;
    }

    public String getQueryValue() {
        return searchRequestJsonNode.get(QUERY_NAME).toString();
    }

    public String toJsonString() {
        String jsonString = searchRequestJsonNode.toString();
        log.info("json search request: {}", jsonString);
        return jsonString;
    }
}
