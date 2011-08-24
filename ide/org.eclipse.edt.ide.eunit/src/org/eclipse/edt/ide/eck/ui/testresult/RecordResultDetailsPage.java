package org.eclipse.edt.ide.eck.ui.testresult;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.eck.Activator;
import org.eclipse.edt.ide.eck.ui.testresult.ResultSummaryBlock.Record_ResultSummary;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
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
		for (int i= 0; i<childrenCnt; i++){
			Node childNode = childrenNodes.item(i);
			if(childNode.getNodeType() == Node.ELEMENT_NODE){
				createOneLabelPerLine(toolkit, parent, nColumnSpan, childNode.getNodeName() + ": " + childNode.getTextContent());
			}
		}
	}
}
