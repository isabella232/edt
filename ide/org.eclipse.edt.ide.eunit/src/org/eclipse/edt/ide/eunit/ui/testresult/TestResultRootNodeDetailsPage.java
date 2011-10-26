package org.eclipse.edt.ide.eunit.ui.testresult;

import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.TestResultRootNode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class TestResultRootNodeDetailsPage extends TestResultPkgNodeDetailsPage {
	TestResultRootNode fRootNode;
	
	public TestResultRootNodeDetailsPage(TestResultRootNode rootNode) {
		super(rootNode.statisticCnts);
		this.fRootNode = rootNode;		
	}

	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent) {
		createReadOnlyNoBorderText(toolkit, parent, nColumnSpan, "Expected total test variation count: " + fRootNode.expectedTotalTestVariationCnt);
		createReadOnlyNoBorderText(toolkit, parent, nColumnSpan, "Test run Started at:         " + fRootNode.startTS);
		createReadOnlyNoBorderText(toolkit, parent, nColumnSpan, "Test run ended at:           " + fRootNode.endRunTS);
		createReadOnlyNoBorderText(toolkit, parent, nColumnSpan, "Finished writing results at: " + fRootNode.finalTS);
		if(fRootNode.expectedTotalTestVariationCnt != statisticCnt.getExpectedCnt() ||
				statisticCnt.getExpectedCnt() != statisticCnt.getTestCnt()){
			
			String errorMsg = "ERROR: Expected total test variation count [" + fRootNode.expectedTotalTestVariationCnt + "] differs from the calculated expected test count [" + 
					statisticCnt.getExpectedCnt() + "] or the actual test count [" + statisticCnt.getTestCnt() + "]!";
			createErrorReadOnlyNoBoarderText(toolkit, parent, nColumnSpan, errorMsg);		
		}
			
		super.createControlsInTopSection(toolkit, parent);		
	}	
	
	
}
