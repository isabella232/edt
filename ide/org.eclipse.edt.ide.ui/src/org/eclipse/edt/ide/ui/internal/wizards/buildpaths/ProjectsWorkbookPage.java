/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.wizards.buildpaths;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;



public class ProjectsWorkbookPage extends BuildPathBasePage {
			
	private ListDialogField fClassPathList;
	private IEGLProject fCurrJProject;
	private List<String> ImportProjectList;
	private List<String> fSelectedImportProjectList;
	private List<PPListElement> projects;
	
	public void setfSelectedImportProjectList(List<String> fSelectedImportProjectList) {
		this.fSelectedImportProjectList = fSelectedImportProjectList;
		if(null == ImportProjectList){
			ImportProjectList = fSelectedImportProjectList;
		}
	}
	
	public void updateSelectedImportProjectList(List<String> newSelectedImportProjectList){
		final List<PPListElement> newCheckedList = new ArrayList<PPListElement>();
		final List<PPListElement> currentCheckedList = fProjectsList.getCheckedElements();
		if(null != fProjectsList && null !=projects){
			
			for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
				PPListElement prjElement = (PPListElement) iterator.next();
				String prjName = prjElement.getPath().lastSegment();
				if(newSelectedImportProjectList.contains(prjName) && !fSelectedImportProjectList.contains(prjName)){
					newCheckedList.add(prjElement);
				}
			}
			
			for (Iterator iterator = currentCheckedList.iterator(); iterator.hasNext();) {
				PPListElement prjElement = (PPListElement) iterator.next();
				String prjName = prjElement.getPath().lastSegment();
				if(!(!newSelectedImportProjectList.contains(prjName) && fSelectedImportProjectList.contains(prjName))){
					newCheckedList.add(prjElement);
				}
			}
			
			fProjectsList.setCheckedElements(newCheckedList);
		}
		
	}

	private CheckedListDialogField fProjectsList;
	
	public ProjectsWorkbookPage(ListDialogField classPathList) {
		fClassPathList= classPathList;
				
		ProjectsListListener listener= new ProjectsListListener();
		
		String[] buttonLabels= new String[] {
			/* 0 */ NewWizardMessages.ProjectsWorkbookPageProjectsCheckallButton,
			/* 1 */ NewWizardMessages.ProjectsWorkbookPageProjectsUncheckallButton
		};
		
		fProjectsList= new CheckedListDialogField(null, buttonLabels, new PPListLabelProvider());
		fProjectsList.setDialogFieldListener(listener);
		fProjectsList.setLabelText(NewWizardMessages.ProjectsWorkbookPageProjectsLabel);
		fProjectsList.setCheckAllButtonIndex(0);
		fProjectsList.setUncheckAllButtonIndex(1);
		
		fProjectsList.setViewerSorter(new PPListElementSorter());
	}
	
	public void init(IEGLProject jproject) {
		updateProjectsList(jproject);
	}
		
	private void updateProjectsList(IEGLProject currJProject) {
		try {
			IEGLModel jmodel= currJProject.getEGLModel();
			IEGLProject[] jprojects= jmodel.getEGLProjects();
			
			projects = new ArrayList<PPListElement>(jprojects.length);
			
			final List<PPListElement> checkedProjects= new ArrayList<PPListElement>(jprojects.length);
			// add the projects-cpentries that are already on the class path
			List cpelements= fClassPathList.getElements();
			for (int i= cpelements.size() - 1 ; i >= 0; i--) {
				PPListElement cpelem= (PPListElement)cpelements.get(i);
				if (isEntryKind(cpelem.getEntryKind())) {
					checkedProjects.add(cpelem);
					projects.add(cpelem);
				} 
			}
			
			for (int i= 0; i < jprojects.length; i++) {
				IProject proj= jprojects[i].getProject();
				if(currJProject.getProject().getName().equals(proj.getProject().getName())) {
					continue;
				}
				boolean found = false;
				for(PPListElement cpelem : projects) {
					if(proj.getName().equals(cpelem.getPath().lastSegment())) {
						found = true;
						break;
					}
				}
				if(!found) {
					projects.add(new PPListElement(fCurrJProject, IEGLPathEntry.CPE_PROJECT, proj.getFullPath(), proj));
				}
			}	
			
			for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
				PPListElement prjElement = (PPListElement) iterator.next();
				if(fSelectedImportProjectList.contains(prjElement.getPath().lastSegment())){
					checkedProjects.add(prjElement);
				}
			}
			
			fProjectsList.setElements(projects);
			fProjectsList.setCheckedElements(checkedProjects);
				
		} catch (EGLModelException e) {
			// no solution exists or other problems: create an empty list
			fProjectsList.setElements(new ArrayList(5));
		}
		fCurrJProject= currJProject;
	}		
		
	private boolean checkIfBinaryProjExisting(List<PPListElement> existingProjects, PPListElement toCompare) {
		Iterator<PPListElement> it = existingProjects.iterator();
		while(it.hasNext()) {
			PPListElement current = it.next();
			if(current.getPath().lastSegment().equals(toCompare.getPath().lastSegment())) {
				return true;
			}
		}
		return false;
	}
	
	// -------- UI creation ---------
		
	public Control getControl(Composite parent) {
		PixelConverter converter= new PixelConverter(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
			
		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fProjectsList }, true, 0, 0, 5, 5);
		LayoutUtil.setHorizontalGrabbing(fProjectsList.getListControl(null));
		
		int buttonBarWidth= converter.convertWidthInCharsToPixels(24);
		fProjectsList.setButtonsMinWidth(buttonBarWidth);
				
		return composite;
	}
	
	private class ProjectsListListener implements IDialogFieldListener {
			
		// ---------- IDialogFieldListener --------
	
		public void dialogFieldChanged(DialogField field) {
			if (fCurrJProject != null) {
				// already initialized
				updateEGLPathList();
			}
		}
	}
	
	private void updateEGLPathList() {
		List projelements= fProjectsList.getCheckedElements();
		
		boolean remove= false;
		List cpelements= fClassPathList.getElements();
		// backwards, as entries will be deleted
		for (int i= cpelements.size() -1; i >= 0 ; i--) {
			PPListElement cpe= (PPListElement)cpelements.get(i);
			if (isEntryKind(cpe.getEntryKind())) {
				if (!projelements.remove(cpe)) {
					cpelements.remove(i);
					remove= true;
				}	
			}
		}
		for (int i= 0; i < projelements.size(); i++) {
			cpelements.add(projelements.get(i));
		}
		if (remove || (projelements.size() > 0)) {
			fClassPathList.setElements(cpelements);
		}
	}
	
	/*
	 * @see BuildPathBasePage#getSelection
	 */
	public List getSelection() {
		return fProjectsList.getSelectedElements();
	}

	/*
	 * @see BuildPathBasePage#setSelection
	 */	
	public void setSelection(List selElements) {
		fProjectsList.selectElements(new StructuredSelection(selElements));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage#isEntryKind(int)
	 */
	public boolean isEntryKind(int kind) {
		return kind == IEGLPathEntry.CPE_PROJECT;
	}


}
