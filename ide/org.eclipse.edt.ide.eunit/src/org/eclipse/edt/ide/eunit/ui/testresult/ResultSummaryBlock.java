/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.eunit.ui.testresult;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;

public class ResultSummaryBlock extends MasterDetailsBlock {

	private SectionPart spart;
	protected FormPage fPage;
	
	private static final String TREEROOTNODENAME = "Test Result Summary:";
	
	public static class TestResultRootNode{
		String rootname;
		boolean isSuccessful = true;
		int expectedTotalTestVariationCnt = -1;
		String startTS = "";
		String endRunTS = "";
		String finalTS = "";
		Element elemRoot;
		
		ResultStatisticCnts statisticCnts = new ResultStatisticCnts();		
	}
	
	public static class TestResultPkgNode{
		String pkgName;
		boolean isSuccessful = true;		
		ResultStatisticCnts statisticCnts = new ResultStatisticCnts();
		ArrayList<Record_ResultSummary> listRS = new ArrayList<Record_ResultSummary>();
	}

//Record ResultSummary
//	pkgName String {@XMLAttribute{}};	//package name
//	name String{@XMLAttribute{}};		//test library part name
//	resultCode int{@XMLAttribute{}};	//test result code value
//	testCnt int{@XMLAttribute{}};
//	passedCnt int{@XMLAttribute{}};
//	failedCnt int{@XMLAttribute{}};
//	errCnt int{@XMLAttribute{}};
//	badCnt int{@XMLAttribute{}};
//	notRunCnt int{@XMLAttribute{}};	
//end
	
	public static class ResultStatisticCnts{
		private int testCnt;
		private int expCnt;
		private int passedCnt;
		private int failedCnt;
		private int errCnt;
		private int badCnt;
		private int notRunCnt;	
		
		public int getTestCnt() {
			return testCnt;
		}
		
		public int getExpectedCnt(){
			return expCnt;
		}

		public int getPassedCnt() {
			return passedCnt;
		}
		
		public int getFailedCnt() {
			return failedCnt;
		}

		public int getErrCnt() {
			return errCnt;
		}

		public int getBadCnt() {
			return badCnt;
		}

		public int getNotRunCnt() {
			return notRunCnt;
		}

		public ResultStatisticCnts(){
			this(0,0,0,0,0,0,0);
		}
		
		public ResultStatisticCnts(int testCnt, int expCnt, int passedCnt, int failedCnt, int errCnt, int badCnt, int notRunCnt){
			this.testCnt = testCnt;
			this.expCnt = expCnt;
			this.passedCnt = passedCnt;
			this.failedCnt = failedCnt;
			this.errCnt = errCnt;
			this.badCnt = badCnt;
			this.notRunCnt = notRunCnt;
		}
		
		public ResultStatisticCnts clone() {
			return new ResultStatisticCnts(testCnt, expCnt, passedCnt, failedCnt, errCnt, badCnt, notRunCnt);
		}
		
		public ResultStatisticCnts plus(ResultStatisticCnts other){
			return new ResultStatisticCnts(testCnt+other.testCnt, 
											expCnt + other.expCnt,
											passedCnt+other.passedCnt,
											failedCnt+other.failedCnt,
											errCnt+other.errCnt,
											badCnt+other.badCnt,
											notRunCnt+other.notRunCnt);
		}
		
	}
	
	public static class Record_ResultSummary{
		String pkgName;
		String name;
		int resultCode;
		boolean isSuccessful;
		ResultStatisticCnts statisticCnts;
		
		public Record_ResultSummary(String pkgName, String name, int resultCode, ResultStatisticCnts statisticCnts){
			this.pkgName = pkgName;
			this.name = name;
			this.resultCode = resultCode;
			this.isSuccessful = (this.resultCode == ConstantUtil.SPASSED);
			
			this.statisticCnts = statisticCnts.clone(); 
		}
	}
	
	public ResultSummaryBlock(FormPage page){
		fPage = page;
	}
	
	protected Element getResultSummaryRoot(){
		FormEditor formEditor = fPage.getEditor();
		
		if(formEditor instanceof TestResultViewer)		
			return ((TestResultViewer)formEditor).getResultSummaryRoot();
		return null;
	}
	
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {

		FormToolkit toolkit = managedForm.getToolkit();
								
		Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("shows the test result summary");
		section.setDescription("click on the node to view the details");
		section.marginHeight = 5;
		section.marginWidth = 10;
		
		Composite seprator = toolkit.createCompositeSeparator(section);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 3;
		seprator.setLayoutData(gd);
		
		createResultSummarySection(managedForm, section, toolkit);
		

	}
	
	private void createResultSummarySection(final IManagedForm managedForm,
			Section section, FormToolkit toolkit) {
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);

		Tree tr = createResultSummaryTreeControl(client, toolkit);
				
		section.setClient(client);
		spart = new SectionPart(section);
		managedForm.addPart(spart);		
		
		TreeViewer fTreeViewer = new TreeViewer(tr);
		
		fTreeViewer.setContentProvider(new RSTreeContentProvider());
		fTreeViewer.setLabelProvider(new RSTreeLabelProvider());

		TestResultRootNode rootNode = createNewRootNode();
		managedForm.setInput(rootNode);		
		fTreeViewer.setInput(managedForm);
		fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				HandleTreeSelectionChanged(event);
			}			
		});
		fTreeViewer.setSorter( new ViewerSorter() );
			
	}

	private TestResultRootNode createNewRootNode() {
		TestResultRootNode rootNode = new TestResultRootNode();
		rootNode.elemRoot = getResultSummaryRoot();
		IEditorInput editorinput = fPage.getEditor().getEditorInput();
		if(editorinput instanceof IFileEditorInput){
			IFileEditorInput fileinput = (IFileEditorInput)editorinput;
			String parentSegment = fileinput.getFile().getParent().getName();
			rootNode.rootname = TREEROOTNODENAME + " " + parentSegment;
		}
		return rootNode;
	}
		
	protected void HandleTreeSelectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	private Tree createResultSummaryTreeControl(Composite client, FormToolkit toolkit) {
		Tree tr = toolkit.createTree(client, SWT.V_SCROLL);
		tr.setHeaderVisible(true);
		tr.setLinesVisible(true);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		tr.setLayoutData(gd);					
		toolkit.paintBordersFor(client);
		
		return tr;
	}

	@Override
	protected void createToolBarActions(IManagedForm arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerPages(DetailsPart arg0) {
		detailsPart.setPageProvider(new TestResultDetailPageProvider());

	}

}
