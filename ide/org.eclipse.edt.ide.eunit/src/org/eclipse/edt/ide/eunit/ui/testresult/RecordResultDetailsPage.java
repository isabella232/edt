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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.eunit.Activator;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.Record_ResultSummary;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDeploymentDescriptorEditor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RecordResultDetailsPage extends TestResultPkgNodeDetailsPage {
	

	
	protected Record_ResultSummary fResultSummary;
	
	public RecordResultDetailsPage(Record_ResultSummary resultSummary){
		super(null);
		fResultSummary = resultSummary;
	}
	
	@Override
	public void commit(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setFormInput(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void selectionChanged(IFormPart arg0, ISelection arg1) {
		// TODO Auto-generated method stub

	}

	protected FormPage getContainerFormPage()
	{
		return (FormPage)(mform.getContainer());
	}
	
	@Override
	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent) {
		createSpacer(toolkit, parent, nColumnSpan);
		
		TestResultViewer formeditor = (TestResultViewer)(getContainerFormPage().getEditor());
		IEditorInput editorInput = formeditor.getEditorInput();
		if(editorInput instanceof IFileEditorInput){
			IFileEditorInput fileInput = (IFileEditorInput)editorInput;			
			IFile file = fileInput.getFile();
			IPath parentFolder = file.getParent().getFullPath();	
			String pkgFolder = fResultSummary.pkgName.replace(".", System.getProperty("file.separator"));
			IPath resultFilePath = parentFolder.append(pkgFolder).append(fResultSummary.name);
			resultFilePath = resultFilePath.addFileExtension("etr");
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IFile resultFile = wsRoot.getFile(resultFilePath);
			
			
			getDetailedResult(resultFile, toolkit, parent);
			
		}
	}

	
	protected void getDetailedResult(IFile resultFile, FormToolkit toolkit, Composite parent) {

		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			resultFile.refreshLocal(IResource.DEPTH_ONE, null);
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(resultFile.getContents(true));
			Element testResultRoot = dom.getDocumentElement();
			
			//get TestDescription information
			NodeList elem_tds = testResultRoot.getElementsByTagName(ConstantUtil.ELEM_td);			
			displayNode(toolkit, parent, elem_tds);										
			createSpacer(toolkit, parent, nColumnSpan);
			
			NodeList elem_stats = testResultRoot.getElementsByTagName(ConstantUtil.ELEM_stat);
			displayNode(toolkit, parent, elem_stats);										
			createSpacer(toolkit, parent, nColumnSpan);
			
			NodeList elem_logs = testResultRoot.getElementsByTagName(ConstantUtil.ELEM_log);
			displayNode(toolkit, parent, elem_logs);										
			createSpacer(toolkit, parent, nColumnSpan);
			

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void displayNode(FormToolkit toolkit, Composite parent, NodeList elems) {
		Element elem = elems.getLength()>0 ? (Element)(elems.item(0)) : null;
		NodeList childrenNodes = elem.getChildNodes();
		int childrenCnt = childrenNodes.getLength();
		String pkgName = null;
		String fileName = null;
		for (int i= 0; i<childrenCnt; i++){
			Node childNode = childrenNodes.item(i);
			if(childNode.getNodeType() == Node.ELEMENT_NODE){
				String nodeName = childNode.getNodeName();
				String nodeTextContent = childNode.getTextContent();
				
				//display a meaningful string for the code value
				if(nodeName.equals(ConstantUtil.ELEM_code)){					
					nodeTextContent += " => " + ConstantUtil.STEXTS[Integer.parseInt(nodeTextContent)];
				}
			
				if(nodeName.equals(ConstantUtil.ELEM_pkgName)){
					pkgName = nodeTextContent;
				}				
				if(nodeName.equals(ConstantUtil.ELEM_name)){
					fileName = nodeTextContent;
				}				
				createReadOnlyNoBorderText(toolkit, parent, nColumnSpan, nodeName + ": " + nodeTextContent);
			}
		}
		//create a hyperlink for the file name, so it can be opened
		if(pkgName != null && fileName != null){
			String fqFileName = pkgName.length()>0 ? (pkgName + "." + fileName) : fileName;
			createHyperLink(toolkit, parent, fqFileName);
		}		
	}
	
	private void createHyperLink(FormToolkit toolkit, Composite parent, final String fullyQualifiedFileName){
		ImageHyperlink interfaceLink = toolkit.createImageHyperlink(parent, SWT.NULL);
		interfaceLink.setText(fullyQualifiedFileName);
		interfaceLink.addHyperlinkListener(new HyperlinkAdapter(){
			public void linkActivated(HyperlinkEvent e) {
				TestResultViewer formeditor = (TestResultViewer)(getContainerFormPage().getEditor());
				try2OpenPartInEGLEditor(formeditor, fullyQualifiedFileName, "org.eclipse.edt.ide.ui.EGLEditor");
			}
		});		
	}
	
	public static void try2OpenPartInEGLEditor(TestResultViewer formeditor, String fullyqualifiedPartName, String editorId) {
		IProject proj = formeditor.getProject();
		IEGLProject eglProj = EGLCore.create(proj);				
		IFile file = Util.findPartFile(fullyqualifiedPartName, eglProj);
		if(file != null && file.exists()){
			if(org.eclipse.edt.compiler.Util.isEGLFileName(file.getName())){
				openEGLFile(file, editorId);
			}
		}
		else{
			String errMsg = "Can not find " + fullyqualifiedPartName + ", it does not exist.";
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  -1, errMsg , null);
			ErrorDialog.openError(formeditor.getSite().getShell(), null, null, status);
		}
	}
	
	public static void openEGLFile(final IFile file, final String editorId) {
		final IWorkbenchWindow ww = Activator.getActiveWorkbenchWindow();
	
		Display d = ww.getShell().getDisplay();
		d.asyncExec(new Runnable() {
			public void run() {
				try {
					ww.getActivePage().openEditor(
						new FileEditorInput(file),
						editorId);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}	
}
