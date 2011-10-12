package org.eclipse.edt.ide.eunit.ui.testresult;

import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.ResultStatisticCnts;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class TestResultRootNodeDetailsPage extends TestResultPkgNodeDetailsPage {
	protected int expectedTotalCnt = -1;
	
	public TestResultRootNodeDetailsPage(ResultStatisticCnts statistic, int expectedTotalCnt) {
		super(statistic);
		this.expectedTotalCnt = expectedTotalCnt;		
	}

	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent) {
		createReadOnlyNoBorderText(toolkit, parent, nColumnSpan, "Expected total test variation count: " + expectedTotalCnt);
		if(expectedTotalCnt != statisticCnt.getExpectedCnt() ||
				statisticCnt.getExpectedCnt() != statisticCnt.getTestCnt()){
			
			String errorMsg = "ERROR: Expected total test variation count [" + expectedTotalCnt + "] differs from the calculated expected test count [" + 
					statisticCnt.getExpectedCnt() + "] or the actual test count [" + statisticCnt.getTestCnt() + "]!";
			createErrorReadOnlyNoBoarderText(toolkit, parent, nColumnSpan, errorMsg);		
		}
			
		super.createControlsInTopSection(toolkit, parent);		
	}	
	
	
}
