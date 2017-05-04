package com.thomsonreuters.oa.filing.invoker;

import java.io.IOException;
import java.util.List;

import com.thomsonreuters.oa.filing.contentMatch.EdgarFileGenerator;
import com.thomsonreuters.oa.filing.resource.FilingInput;

public class TestScenarious {
	static EdgarFileGenerator edFileGenerator = new EdgarFileGenerator();
	
	public static void TestCases(FilingInput FilingData,
			final List<FilingInput> DataList) throws IOException {
		//DataList.clear();
		if (DataList != null) {
			if (FilingData.getTestCaseId().equals("OAUI_TC001")) {
				FilingData = edFileGenerator
						.CreateEntityWithInformaticaVerifiedAddress(DataList,
								FilingData);

			} else if (FilingData.getTestCaseId().equals("OAUI_TC002")) {
				FilingData = edFileGenerator
						.CreateEntityWithInformaticaUnVerifiedAddress(DataList,
								FilingData);

			} else if (FilingData.getTestCaseId().equals("OAUI_TC003")) {
				FilingData = edFileGenerator
						.UpdateVerifiedAddressToUnverifiedAddressForVerifiedEntity(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC004")) {
				FilingData = edFileGenerator
						.UpdateUnverifiedAddressToVerifiedAddressForVerifiedEntity(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC005")) {
				FilingData = edFileGenerator
						.EntityNotToBeCreatedAndRequestIDNeedToBeGererateWhenSICandFilingDateIsLessThan1Jan1987(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC006")) {
				FilingData = edFileGenerator
						.EntityNeedToCreateWithoutRequestIDWhenSICandFilingDateIsGreaterThan1Jan1987(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC007")) {
				FilingData = edFileGenerator
						.AnalystRequestToBeGeneratedWhenPhoneNumberIsLessThan10digits(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC008")) {
				FilingData = edFileGenerator
						.AnalystRequestToBeGeneratedWhenPhoneNumberIsGreaterThan10digits(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC009")) {
				FilingData = edFileGenerator
						.AnalystRequestNotToBeGeneratedWhenPhoneNumbersIsEqaulTo10digitsAndNotStartsWith1(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC010")) {
				FilingData = edFileGenerator
						.AnalystRequestToBeGeneratedWhenPhoneNumbersIsEqaulTo10digitsAndStartsWith1(
								DataList, FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC011")) {
				FilingData = edFileGenerator
						.VerifyEntityToBeCreatedWithNewIRSnumber(DataList,
								FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC012")) {
				FilingData = edFileGenerator
						.VerifyEntiytIsNotCreatedForStateAsNull(DataList,
								FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC013")) {
				FilingData = edFileGenerator
						.VerifyAddressIsVerifiedAddressWhenEdgarCityIsNullforVerifiedEntity(DataList,
								FilingData);
			} else if (FilingData.getTestCaseId().equals("OAUI_TC014")) {
				FilingData = edFileGenerator
						.VerifyOldSICinHistoryTableWhenNewSICadded(DataList,
								FilingData);
			}else if (FilingData.getTestCaseId().equals("OAUI_TC015")) {
				FilingData = edFileGenerator
						.VerifyEntityNotToBeUpdatedWithExistingIRSnumberWhichIsMappedToAnotherEntity(DataList,
								FilingData);
			}else if (FilingData.getTestCaseId().equals("OAUI_TC016")) {
				FilingData = edFileGenerator
						.VerifyEntityShouldNotCreateIfCIKgreaterThan10Digits(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC017")) {
				FilingData = edFileGenerator
						.VerifyEntityShouldNotCreateAndRequestShouldGeneratedForHeaderHavingConfirmedNameWhichIsAlreadyExistingWithDifferentCIK(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC018")) {
				FilingData = edFileGenerator
						.VerifyDuplicateDataShouldMoveToINGOREtable(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC019")) {
				FilingData = edFileGenerator
						.VerifyHeaderDataShouldMoveToIGNOREtableForAssigned_SIC8880(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC020")) {
				FilingData = edFileGenerator
						.VerifyTAXIDaddedForNewEntityUsingIRSnumber(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC021")) {
				FilingData = edFileGenerator
						.VerifyTAXIDAddedForExistingEntityWhichIsNotHaivngTAXIDusingIRSNumber(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC022")) {
				FilingData = edFileGenerator
						.VerifyTAXIDAddedForExistingEntityWhichIsHaivngTAXIDusingIRSNumber(DataList,
								FilingData);
			}
			else if (FilingData.getTestCaseId().equals("OAUI_TC023")) {
				FilingData = edFileGenerator
						.VerifyTAXIDnotUpdatedWithNullForExistingEntityWhichIsHavingTAXIDusingIRSnumberAsNull(DataList,
								FilingData);
			}
			
			

		}

	}

}
