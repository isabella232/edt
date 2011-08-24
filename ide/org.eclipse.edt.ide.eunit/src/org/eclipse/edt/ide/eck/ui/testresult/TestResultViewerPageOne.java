package org.eclipse.edt.ide.eck.ui.testresult;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class TestResultViewerPageOne extends FormPage {

	private ResultSummaryBlock block;
	
	public TestResultViewerPageOne(FormEditor editor, String id, String title) {
		super(editor, id, title);
		block = new ResultSummaryBlock(this);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		final ScrolledForm form = managedForm.getForm();
		form.setText("Test result summary root");
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);			
		
		block.createContent(managedForm);

	}

}
