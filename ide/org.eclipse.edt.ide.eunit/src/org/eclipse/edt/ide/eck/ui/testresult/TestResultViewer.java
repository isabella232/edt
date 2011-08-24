package org.eclipse.edt.ide.eck.ui.testresult;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TestResultViewer extends FormEditor{

	private Element fResultSummaryRoot;
	
	@Override
	protected void addPages() {
		try{
			TestResultViewerPageOne pageOne = new TestResultViewerPageOne(this, "TestResultPageOne", "ECK Test Results");
			addPage(pageOne);	
			
		}
		catch(PartInitException e){
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}	

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
	}
	
	public Element getResultSummaryRoot(){
		if(fResultSummaryRoot == null){
			//get the factory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
			try {
				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();
	
				IEditorInput editorInput = getEditorInput();
				if(editorInput instanceof IFileEditorInput){
					IFileEditorInput fileInput = (IFileEditorInput)editorInput;
					IFile file = fileInput.getFile();
					file.refreshLocal(IResource.DEPTH_ONE, null);
					//parse using builder to get DOM representation of the XML file
					Document dom = db.parse(file.getContents(true));
					fResultSummaryRoot = dom.getDocumentElement();
				}	
			}catch(ParserConfigurationException pce) {
				pce.printStackTrace();
			}catch(SAXException se) {
				se.printStackTrace();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fResultSummaryRoot;

	}
	
	
}
