package uk.gov.hmcts.ccd.v2.internal.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseEventTrigger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.CaseEventTriggerBuilder.anEventTrigger;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.CaseViewFieldBuilder.aViewField;
import static uk.gov.hmcts.ccd.domain.service.common.TestBuildersUtil.WizardPageBuilder.newWizardPage;

@DisplayName("CaseResource")
class UIStartTriggerResourceTest {
    private static final String NAME = "eventName";
    private static final String DESCRIPTION = "eventDescription";
    private static final String TOKEN = "Token";
    private static final String END_BUTTON_LABEL = "Submit";
    private static final boolean IS_SHOW_SUMMARY = true;
    private static final boolean IS_SHOW_EVENT_NOTES = false;
    private static final boolean IS_SAVE_DRAFT = true;
    private static final String CASE_ID = "1111222233334444";
    private static final String FIELD_ID = "PersonFirstName";
    private static final String CASE_TYPE_ID = "TestAddressBookCase";
    private static final String ID = "createCase";

    private CaseEventTrigger caseEventTrigger;
    private boolean ignoreWarning;

    @BeforeEach
    void setUp() {
        caseEventTrigger = newCaseEventTrigger();
        ignoreWarning = true;
    }


    private CaseEventTrigger newCaseEventTrigger() {
        return anEventTrigger()
            .withId(ID)
            .withName(NAME)
            .withDescription(DESCRIPTION)
            .withCaseId(CASE_ID)
            .withField(aViewField()
                           .withId(FIELD_ID).build())
            .withEventToken(TOKEN)
            .withWizardPage(newWizardPage()
                                .withField(aViewField()
                                               .withId(FIELD_ID)
                                               .build())
                                .build())
            .withShowSummary(IS_SHOW_SUMMARY)
            .withShowEventNotes(IS_SHOW_EVENT_NOTES)
            .withEndButtonLabel(END_BUTTON_LABEL)
            .withCanSaveDraft(IS_SAVE_DRAFT)
            .build();
    }

    @Nested
    @DisplayName("Start case trigger")
    class StartTriggerForCase {
        private final String LINK_SELF_FOR_CASE = String.format("/internal/case-types/%s/event-triggers/%s?ignore-warning=true", CASE_TYPE_ID, ID);

        @Test
        @DisplayName("should copy case event trigger")
        void shouldCopyCaseDetails() {
            final UIStartTriggerResource uiStartTriggerResource = new UIStartTriggerResource(caseEventTrigger, CASE_TYPE_ID, ignoreWarning, false);

            assertAll(
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getId(), equalTo(ID)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getName(), equalTo(NAME)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getDescription(), equalTo(DESCRIPTION)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getCaseId(), equalTo(CASE_ID)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getCaseFields(), hasItems(hasProperty("id", is(FIELD_ID)))),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getEventToken(), equalTo(TOKEN)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getWizardPages().get(0).getWizardPageFields().get(0),
                                 hasProperty("caseFieldId", is(FIELD_ID))),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getShowSummary(), equalTo(IS_SHOW_SUMMARY)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getShowEventNotes(), equalTo(IS_SHOW_EVENT_NOTES)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getEndButtonLabel(), equalTo(END_BUTTON_LABEL)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getCanSaveDraft(), equalTo(IS_SAVE_DRAFT))
            );
        }

        @Test
        @DisplayName("should link to itself")
        void shouldLinkToSelf() {
            final UIStartTriggerResource uiStartTriggerResource = new UIStartTriggerResource(caseEventTrigger, CASE_TYPE_ID, ignoreWarning, false);

            assertThat(uiStartTriggerResource.getLink("self").getHref(), equalTo(LINK_SELF_FOR_CASE));
        }

    }

    @Nested
    @DisplayName("Start event trigger")
    class StartTriggerForEvent {
        private final Long CASE_REFERENCE = 1111222233334444L;
        private final String LINK_SELF_FOR_EVENT = String.format("/internal/cases/%s/event-triggers/%s?ignore-warning=true", CASE_REFERENCE, ID);

        @BeforeEach
        void setUp() {
            caseEventTrigger.setCaseId(CASE_REFERENCE.toString());
        }

        @Test
        @DisplayName("should copy case event trigger")
        void shouldCopyCaseDetails() {
            final UIStartTriggerResource uiStartTriggerResource = new UIStartTriggerResource(caseEventTrigger, CASE_TYPE_ID, ignoreWarning, true);

            assertAll(
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getId(), equalTo(ID)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getName(), equalTo(NAME)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getDescription(), equalTo(DESCRIPTION)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getCaseId(), equalTo(CASE_ID)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getCaseFields(), hasItems(hasProperty("id", is(FIELD_ID)))),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getEventToken(), equalTo(TOKEN)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getWizardPages().get(0).getWizardPageFields().get(0),
                                 hasProperty("caseFieldId", is(FIELD_ID))),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getShowSummary(), equalTo(IS_SHOW_SUMMARY)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getShowEventNotes(), equalTo(IS_SHOW_EVENT_NOTES)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getEndButtonLabel(), equalTo(END_BUTTON_LABEL)),
                () -> assertThat(uiStartTriggerResource.getCaseEventTrigger().getCanSaveDraft(), equalTo(IS_SAVE_DRAFT))
            );
        }

        @Test
        @DisplayName("should link to itself")
        void shouldLinkToSelf() {
            final UIStartTriggerResource uiStartTriggerResource = new UIStartTriggerResource(caseEventTrigger, CASE_TYPE_ID, ignoreWarning, true);

            assertThat(uiStartTriggerResource.getLink("self").getHref(), equalTo(LINK_SELF_FOR_EVENT));
        }

    }
}