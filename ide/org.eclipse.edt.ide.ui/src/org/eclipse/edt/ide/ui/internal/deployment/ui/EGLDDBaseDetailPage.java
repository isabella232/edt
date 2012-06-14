/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUIStatus;
import org.eclipse.edt.ide.ui.internal.editor.BinaryEditorInput;
import org.eclipse.edt.ide.ui.internal.editor.BinaryFileEditor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;

public abstract class EGLDDBaseDetailPage implements IDetailsPage {

	public static Color READONLY_BACKGROUNDCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	public static void try2OpenPartInEGLEditor(EGLDeploymentDescriptorEditor serviceBindingEditor, String fullyqualifiedPartName) {
		try2OpenPartInEGLEditor( serviceBindingEditor, fullyqualifiedPartName, EDTUIPlugin.EGL_EDITOR_ID );
	}
	public static void try2OpenPartInEGLEditor(EGLDeploymentDescriptorEditor serviceBindingEditor, String fullyqualifiedPartName, String editorId) {
		IProject proj = serviceBindingEditor.getProject();
		IEGLProject eglProj = EGLCore.create(proj);				
		IFile file = Util.findPartFile(fullyqualifiedPartName, eglProj);
		if(file != null && file.exists()){
			if(org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(file.getName())){
				openEGLFile(file, editorId);
			}
			else if(org.eclipse.edt.ide.core.internal.model.Util.isEGLARFileName(file.getName())){
				openClassFile(proj, file.getFullPath().toString(), fullyqualifiedPartName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
			}
		}
		else if(file == null){	//if the part is in external eglar, file is null
			//TODO verify
			String eglarFilePath = Util.findPartFilePath(fullyqualifiedPartName, eglProj);
			String fileName = eglarFilePath;
			if(fileName.endsWith(File.separator) || fileName.endsWith("/"))
				fileName = fileName.substring(0, fileName.length() - 1);
			if(org.eclipse.edt.ide.core.internal.model.Util.isEGLARFileName(fileName)){
				openClassFile(proj, eglarFilePath, fullyqualifiedPartName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
			}
		}
		else{
			IStatus status = EGLUIStatus.createError(-1, SOAMessages.bind(SOAMessages.ModuleBaseDetailPageFileNotExist, fullyqualifiedPartName), null);
			ErrorDialog.openError(serviceBindingEditor.getSite().getShell(), null, null, status);
		}
	}
	
	public static class StringStorage implements IStorage {
		  private String pathString;
		 
		  public StringStorage(String input) {
		    this.pathString = input;
		  }
		 
		  public InputStream getContents() throws CoreException {
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(pathString);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		    return fs;
		  }
		 
		  public IPath getFullPath() {
		    return new Path(pathString);
		  }
		 
		  public Object getAdapter(Class adapter) {
		    return null;
		  }
		 
		  public String getName() {
		    return getFullPath().lastSegment();
		  }
		 
		  public boolean isReadOnly() {
		    return true;
		  }
		}
	
	public static class StringInput implements IStorageEditorInput {
	    private IStorage storage;
	    public StringInput(IStorage storage) {this.storage = storage;}
	    public boolean exists() {return true;}
	    public ImageDescriptor getImageDescriptor() {return null;}
	    public String getName() {
	       return storage.getName();
	    }
	    public IPersistableElement getPersistable() {return null;}
	    public IStorage getStorage() {
	       return storage;
	    }
	    public String getToolTipText() {
	       return "file: " + storage.getName();
	    }
	    public Object getAdapter(Class adapter) {
	      return null;
	    }
	 }
		
	/**
	 * @param proj: the project which uses the eglar library (not necessarily the one includes eglar definition)
	 * @param eglarFilePath: path for eglar definition location
	 * @param irFullQualifiedFile: name of the ir file to be opened from eglar, without extension .ir
	 * @param editorId: the ID of class file read-only editor
	 */
	public static void openClassFile(final IProject proj, final String eglarFilePath, final String irFullQualifiedFile, final String editorId){
		final IWorkbenchWindow ww = EDTUIPlugin.getActiveWorkbenchWindow();
		
		Display d = ww.getShell().getDisplay();
		d.asyncExec(new Runnable() {
			public void run() {
				try {
//					ww.getActivePage().openEditor(
//						new FileEditorInput(file),
//						editorId);
					BinaryReadOnlyFile storage = null;
					//TODO this isn't right
					if(proj == null){
						storage = new BinaryReadOnlyFile(eglarFilePath, irFullQualifiedFile, "");
					} else{
						storage = new BinaryReadOnlyFile(eglarFilePath, irFullQualifiedFile, "", proj, true);
					}
					BinaryEditorInput input = new BinaryEditorInput(storage);
					ww.getActivePage().openEditor(
					input, editorId);
				} catch (PartInitException e) {
					EDTUIPlugin.log( e );
				}
			}
		});
	}

	public static void openEGLFile(final IFile file, final String editorId) {
		final IWorkbenchWindow ww = EDTUIPlugin.getActiveWorkbenchWindow();
	
		Display d = ww.getShell().getDisplay();
		d.asyncExec(new Runnable() {
			public void run() {
				try {
					ww.getActivePage().openEditor(
						new FileEditorInput(file),
						editorId);
				} catch (PartInitException e) {
					EDTUIPlugin.log( e );
				}
			}
		});
	}

	protected IManagedForm mform;
	protected int nColumnSpan = 3;

	public void createContents(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 5;
		layout.bottomMargin = 2;
		parent.setLayout(layout);
		FormToolkit toolkit = mform.getToolkit();
		createTopNonExpandableSection(parent, toolkit);						
	}
	
	protected Composite createDetailSection(Composite parent, FormToolkit toolkit, int sectionStyle, int columnSpan){
		return createSection(parent, toolkit, "", "", sectionStyle, columnSpan); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void createTopNonExpandableSection(Composite parent, FormToolkit toolkit) {
		Composite client = createDetailSection(parent, toolkit, Section.DESCRIPTION|Section.SHORT_TITLE_BAR, nColumnSpan);		
		createControlsInTopSection(toolkit, client);
	}
	
	protected abstract void createControlsInTopSection(FormToolkit toolkit, Composite client);	

	protected Composite createSection(Composite parent, FormToolkit toolkit, String title,
			String desc, int SectionStyle, int numColumns) 
	{		
		Section section = toolkit.createSection(parent, SectionStyle);
		section.marginWidth = 10;
		section.setText(title);
		section.setDescription(desc);
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);		
		//toolkit.createCompositeSeparator(section);
		
		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 2;
		layout.numColumns = numColumns;
		client.setLayout(layout);
		//toolkit.paintBordersFor(client);
		section.setClient(client);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				mform.getForm().reflow(false);
			}
		});
//		SectionPart spart = new SectionPart(section);
//		mform.addPart(spart);		
		return client;
	}
	
	protected void createSpacer(FormToolkit toolkit, Composite parent, int span) {
		Label spacer = toolkit.createLabel(parent, ""); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}
	
	protected FormPage getContainerFormPage()
	{
		return (FormPage)(mform.getContainer());
	}
	
	protected EGLDeploymentDescriptorEditor getEGLServiceBindingEditor(){
		return (EGLDeploymentDescriptorEditor)(getContainerFormPage().getEditor());
	}
	
	protected IProject getEditorProject() {
		return getEGLServiceBindingEditor().getProject();
	}

	public void initialize(IManagedForm form) {
		this.mform = form;
	}	
}
