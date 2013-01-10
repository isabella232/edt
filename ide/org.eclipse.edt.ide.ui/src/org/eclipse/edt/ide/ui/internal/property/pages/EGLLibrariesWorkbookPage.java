/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.property.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.core.model.PPListElementAttribute;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.buildpaths.BuildPathBasePage;
import org.eclipse.edt.ide.ui.internal.wizards.buildpaths.PPListElementSorter;
import org.eclipse.edt.ide.ui.internal.wizards.buildpaths.PPListLabelProvider;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



public class EGLLibrariesWorkbookPage extends BuildPathBasePage {
	
	private final ListDialogField fClassPathList;
	private IEGLProject fCurrProject;
	
	private final TreeListDialogField fLibrariesList;
	
	private Control fSWTControl;

	private final int IDX_ADDJAR= 0;
	private final int IDX_ADDEXT= 1;
//	private final int IDX_ADDVAR= 2;
//	private final int IDX_ADDLIB= 3;
//	private final int IDX_EDIT= 4;
//	private final int IDX_REMOVE= 5;
	private final int IDX_EDIT= 2;
	private final int IDX_REMOVE= 3;
	
	public EGLLibrariesWorkbookPage(CheckedListDialogField classPathList) {
		fClassPathList= classPathList;
		fSWTControl= null;
		
		String[] buttonLabels= new String[] { 
			NewWizardMessages.LibrariesWorkbookPage_libraries_addeglar_button,	
			NewWizardMessages.LibrariesWorkbookPage_libraries_addexteglar_button,
			NewWizardMessages.LibrariesWorkbookPage_libraries_edit_button, 
			NewWizardMessages.LibrariesWorkbookPage_libraries_remove_button
		};		
				
		LibrariesAdapter adapter= new LibrariesAdapter();
				
		fLibrariesList= new TreeListDialogField(adapter, buttonLabels, new PPListLabelProvider());
		fLibrariesList.setDialogFieldListener(adapter);
		fLibrariesList.setLabelText(NewWizardMessages.LibrariesWorkbookPage_libraries_label); 

		fLibrariesList.enableButton(IDX_REMOVE, false);
		fLibrariesList.enableButton(IDX_EDIT, false);

		fLibrariesList.setViewerSorter(new PPListElementSorter());

	}
		
	public void init(IEGLProject project) {
		fCurrProject= project;
		if (Display.getCurrent() != null) {
			updateLibrariesList();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					updateLibrariesList();
				}
			});
		}
		updateEnabledState();
	}
	
	private void updateLibrariesList() {
		List elements= fClassPathList.getElements();
		List libelements= new ArrayList(elements.size());
		
		int nElements= elements.size();
		for (int i= 0; i < nElements; i++) {
			PPListElement ppe= (PPListElement)elements.get(i);
			if (isEntryKind(ppe.getEntryKind())) {
				libelements.add(ppe);
			}
		}
		fLibrariesList.setElements(libelements);
	}		
		
	// -------- UI creation
	
	public Control getControl(Composite parent) {
		PixelConverter converter= new PixelConverter(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
			
		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fLibrariesList }, true, SWT.DEFAULT, SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fLibrariesList.getTreeControl(null));
		
		int buttonBarWidth= converter.convertWidthInCharsToPixels(24);
		fLibrariesList.setButtonsMinWidth(buttonBarWidth);
		
		fLibrariesList.setViewerSorter(new PPListElementSorter());
		
		fSWTControl= composite;
				
		return composite;
	}
		
	
	private class LibrariesAdapter implements IDialogFieldListener, ITreeListAdapter {
		
		private final Object[] EMPTY_ARR= new Object[0];
		
		// -------- IListAdapter --------
		public void customButtonPressed(TreeListDialogField field, int index) {
			libaryPageCustomButtonPressed(field, index);
		}
		
		public void selectionChanged(TreeListDialogField field) {
			libaryPageSelectionChanged(field);
		}
		
		public void doubleClicked(TreeListDialogField field) {
			libaryPageDoubleClicked(field);
		}
		
		public void keyPressed(TreeListDialogField field, KeyEvent event) {
			libaryPageKeyPressed(field, event);
		}

		public Object[] getChildren(TreeListDialogField field, Object element) {
			//RTC76899: disable source attachment
			/*if (element instanceof PPListElement) {
				if(!((PPListElement) element).getEGLPathEntry().isBinaryProject())
					return ((PPListElement) element).getChildren(false);
			}*/
			return EMPTY_ARR;
		}

		public Object getParent(TreeListDialogField field, Object element) {
			if (element instanceof PPListElementAttribute) {
				return ((PPListElementAttribute) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(TreeListDialogField field, Object element) {
			return getChildren(field, element).length > 0;
		}		
			
		// ---------- IDialogFieldListener --------
	
		public void dialogFieldChanged(DialogField field) {
			libaryPageDialogFieldChanged(field);
		}
	}
	
	/**
	 * A button has been pressed.
	 * 
	 * @param field the dialog field containing the button
	 * @param index the index of the button
	 */
	private void libaryPageCustomButtonPressed(DialogField field, int index) {
		PPListElement[] libentries= null;
		switch (index) {
		case IDX_ADDJAR: /* add jar */
			libentries= openJarFileDialog(null);
			break;
		case IDX_ADDEXT: /* add external jar */
			libentries= openExtJarFileDialog(null);
			break;
//		case IDX_ADDVAR: /* add variable */
//			libentries= openVariableSelectionDialog(null);
//			break;
//		case IDX_ADDLIB: /* add library */
//			libentries= openContainerSelectionDialog(null);
//			break;
		case IDX_EDIT: /* edit */
			editEntry();
			return;
		case IDX_REMOVE: /* remove */
			removeEntry();
			return;
		}
		if (libentries != null) {
			int nElementsChosen= libentries.length;					
			// remove duplicates
			List cplist= fLibrariesList.getElements();
			List elementsToAdd= new ArrayList(nElementsChosen);
			
			for (int i= 0; i < nElementsChosen; i++) {
				PPListElement curr= libentries[i];
				if (!cplist.contains(curr) && !elementsToAdd.contains(curr)) {
					elementsToAdd.add(curr);
					curr.setAttribute(PPListElement.SOURCEATTACHMENT, guessSourceAttachment(curr));
//					curr.setAttribute(PPListElement.JAVADOC, BuildPathSupport.guessJavadocLocation(curr));
				}
			}
			
			fLibrariesList.addElements(elementsToAdd);
//			if (index == IDX_ADDLIB || index == IDX_ADDVAR) {
//				fLibrariesList.refresh();
//			}
			fLibrariesList.postSetSelection(new StructuredSelection(libentries));
		}
	}
	
	private static IPath guessSourceAttachment(PPListElement elem) {
		if (elem.getEntryKind() == IEGLPathEntry.CPE_CONTAINER) {
			return null;
		}
		IEGLProject currProject= elem.getEGLProject(); // can be null
		try {
			IEGLModel model= EGLCore.create(ResourcesPlugin.getWorkspace().getRoot());
			IEGLProject[] projects= model.getEGLProjects();
			for (int i= 0; i < projects.length; i++) {
				IEGLProject curr= projects[i];
				if (!curr.equals(currProject)) {
					IEGLPathEntry[] entries= curr.getRawEGLPath();
					for (int k= 0; k < entries.length; k++) {
						IEGLPathEntry entry= entries[k];
						if (entry.getEntryKind() == elem.getEntryKind()
							&& entry.getPath().equals(elem.getPath())) {
							IPath attachPath= entry.getSourceAttachmentPath();
							if (attachPath != null && !attachPath.isEmpty()) {
								return attachPath;
							}
						}
					}
				}
			}
		} catch (EGLModelException e) {
		}
		return null;
	}

	
	public void addElement(PPListElement element) {
		fLibrariesList.addElement(element);
		fLibrariesList.postSetSelection(new StructuredSelection(element));
	}
		
	protected void libaryPageDoubleClicked(TreeListDialogField field) {
		List selection= field.getSelectedElements();
		if (canEdit(selection)) {
			editEntry();
		}
	}

	protected void libaryPageKeyPressed(TreeListDialogField field, KeyEvent event) {
		if (field == fLibrariesList) {
			if (event.character == SWT.DEL && event.stateMask == 0) {
				List selection= field.getSelectedElements();
				if (canRemove(selection)) {
					removeEntry();
				}
			}
		}	
	}	

	private void removeEntry() {
		List selElements= fLibrariesList.getSelectedElements();
		HashMap containerEntriesToUpdate= new HashMap();
		for (int i= selElements.size() - 1; i >= 0 ; i--) {
			Object elem= selElements.get(i);
			if (elem instanceof PPListElementAttribute) {
				PPListElementAttribute attrib= (PPListElementAttribute) elem;
				String key= attrib.getKey();
				PPListElement selElement= attrib.getParent();
				if (PPListElement.SOURCEATTACHMENT.equals(key)) {
					selElement.setAttribute(PPListElement.SOURCEATTACHMENT, "");
					String[] changedAttributes= { PPListElement.SOURCEATTACHMENT };
					attributeUpdated(selElement, changedAttributes);
				}
				attrib.setValue(null);
				selElements.remove(i);
				if (attrib.getParent().getParentContainer() instanceof PPListElement) { // inside a container: apply changes right away
					PPListElement containerEntry= attrib.getParent();
					HashSet changedAttributes= (HashSet) containerEntriesToUpdate.get(containerEntry);
					if (changedAttributes == null) {
						changedAttributes= new HashSet();
						containerEntriesToUpdate.put(containerEntry, changedAttributes);
					}
					changedAttributes.add(key); // collect the changed attributes
				}
			}
		}
		if (selElements.isEmpty()) {
			fLibrariesList.refresh();
			fClassPathList.dialogFieldChanged(); // validate
		} else {
			fLibrariesList.removeElements(selElements);
		}
		for (Iterator iter= containerEntriesToUpdate.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry= (Entry) iter.next();
			PPListElement curr= (PPListElement) entry.getKey();
			HashSet attribs= (HashSet) entry.getValue();
			String[] changedAttributes= (String[]) attribs.toArray(new String[attribs.size()]);
			IEGLPathEntry changedEntry= curr.getEGLPathEntry();
			updateContainerEntry(changedEntry, changedAttributes, fCurrProject, ((PPListElement) curr.getParentContainer()).getPath());
		}
	}
	
	private boolean canRemove(List selElements) {
		if (selElements.size() == 0) {
			return false;
		}
		for (int i= 0; i < selElements.size(); i++) {
			Object elem= selElements.get(i);
			if (elem instanceof PPListElementAttribute) {
				PPListElementAttribute attrib= (PPListElementAttribute) elem;
				if (attrib.getValue() == null) {
					return false;
				}
			} else if (elem instanceof PPListElement) {
				PPListElement curr= (PPListElement) elem;
				if(curr.getEGLPathEntry().isBinaryProject()) {
					return false;
				}
				if (curr.getParentContainer() != null) {
					return false;
				}
			} else { // unknown element
				return false;
			}
		}		
		return true;
	}	

	/**
	 * Method editEntry.
	 */
	private void editEntry() {
		List selElements= fLibrariesList.getSelectedElements();
		if (selElements.size() != 1) {
			return;
		}
		Object elem= selElements.get(0);
		if (fLibrariesList.getIndexOfElement(elem) != -1) {
			editElementEntry((PPListElement) elem);
		} else if (elem instanceof PPListElementAttribute) {
			editAttributeEntry((PPListElementAttribute) elem);
		}
	}
	
	private void editAttributeEntry(PPListElementAttribute elem) {
		String key= elem.getKey();
		PPListElement selElement= elem.getParent();
		
		if (key.equals(PPListElement.SOURCEATTACHMENT)) {
			IEGLPathEntry result= configureSourceAttachment(getShell(), selElement.getEGLPathEntry());
			if (result != null) {
				selElement.setAttribute(PPListElement.SOURCEATTACHMENT, result.getSourceAttachmentPath());
				String[] changedAttributes= { PPListElement.SOURCEATTACHMENT };
				attributeUpdated(selElement, changedAttributes);
				fLibrariesList.refresh(elem);
				fLibrariesList.refresh(selElement); // image
				fClassPathList.refresh(); // images
				updateEnabledState();
			}
		}
	}
	
	private IEGLPathEntry configureSourceAttachment(Shell shell, IEGLPathEntry initialEntry) {
		if (initialEntry == null) {
			throw new IllegalArgumentException();
		}
		int entryKind= initialEntry.getEntryKind();
		if (entryKind != IEGLPathEntry.CPE_LIBRARY && entryKind != IEGLPathEntry.CPE_VARIABLE) {
			throw new IllegalArgumentException();
		}
		
		SourceAttachmentDialog dialog=  new SourceAttachmentDialog(shell, initialEntry);
		if (dialog.open() == Window.OK) {
			return dialog.getResult();
		}
		return null;
	}

	
	private void attributeUpdated(PPListElement selElement, String[] changedAttributes) {
		Object parentContainer= selElement.getParentContainer();
		if (parentContainer instanceof PPListElement) { // inside a container: apply changes right away
			IEGLPathEntry updatedEntry= selElement.getEGLPathEntry();
			updateContainerEntry(updatedEntry, changedAttributes, fCurrProject, ((PPListElement) parentContainer).getPath());
		}
	}

	private void updateContainerEntry(final IEGLPathEntry newEntry, final String[] changedAttributes, final IEGLProject jproject, final IPath containerPath) {
//		try {
//			IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
//				public void run(IProgressMonitor monitor) throws CoreException {				
//					BuildPathSupport.modifyClasspathEntry(null, newEntry, changedAttributes, jproject, containerPath, monitor);
//				}
//			};
//			PlatformUI.getWorkbench().getProgressService().run(true, true, new WorkbenchRunnableAdapter(runnable));
//
//		} catch (InvocationTargetException e) {
//			String title= NewWizardMessages.LibrariesWorkbookPage_configurecontainer_error_title; 
//			String message= NewWizardMessages.LibrariesWorkbookPage_configurecontainer_error_message; 
//			ExceptionHandler.handle(e, getShell(), title, message);
//		} catch (InterruptedException e) {
//			// 
//		}
	}
		
	private void editElementEntry(PPListElement elem) {
		PPListElement[] res= null;
		
		switch (elem.getEntryKind()) {
		case IEGLPathEntry.CPE_CONTAINER:
			res= openContainerSelectionDialog(elem);
			break;
		case IEGLPathEntry.CPE_LIBRARY:
			IResource resource= elem.getResource();
			if (resource == null) {
				File file= elem.getPath().toFile();
				res= openExtJarFileDialog(elem);
			} else if (resource.getType() == IResource.FILE) {
				res= openJarFileDialog(elem);			
			}
			break;
		case IEGLPathEntry.CPE_VARIABLE:
			res= openVariableSelectionDialog(elem);
			break;
		}
		if (res != null && res.length > 0) {
			PPListElement curr= res[0];
			curr.setExported(elem.isExported());
			curr.setAttributesFromExisting(elem);
			fLibrariesList.replaceElement(elem, curr);
			if (elem.getEntryKind() == IEGLPathEntry.CPE_VARIABLE) {
				fLibrariesList.refresh();
			}
		}		
			
	}

	/**
	 * @param field  the dilaog field
	 */
	private void libaryPageSelectionChanged(DialogField field) {
		updateEnabledState();
	}
	
	private void disableAllButtons(){
		fLibrariesList.enableButton(IDX_ADDJAR, false);
		fLibrariesList.enableButton(IDX_ADDEXT, false);
		fLibrariesList.enableButton(IDX_EDIT, false);
		fLibrariesList.enableButton(IDX_REMOVE, false);
	}

	private void updateEnabledState() {
		if(fCurrProject != null && fCurrProject.isBinary()){
			disableAllButtons();
			return;
		}
		List selElements= fLibrariesList.getSelectedElements();
		fLibrariesList.enableButton(IDX_EDIT, canEdit(selElements));
		fLibrariesList.enableButton(IDX_REMOVE, canRemove(selElements));
		
		boolean noAttributes= containsOnlyTopLevelEntries(selElements);
		fLibrariesList.enableButton(IDX_ADDEXT, noAttributes);
		fLibrariesList.enableButton(IDX_ADDJAR, noAttributes);
//		fLibrariesList.enableButton(IDX_ADDLIB, noAttributes);
//		fLibrariesList.enableButton(IDX_ADDVAR, noAttributes);
	}
	
	private boolean containsOnlyTopLevelEntries(List selElements) {
		if (selElements.size() == 0) {
			return true;
		}
		for (int i= 0; i < selElements.size(); i++) {
			Object elem= selElements.get(i);
			if (elem instanceof PPListElement) {
				if (((PPListElement) elem).getParentContainer() != null) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	
	private boolean canEdit(List selElements) {
		if (selElements.size() != 1) {
			return false;
		}
		Object elem= selElements.get(0);
		if (elem instanceof PPListElement) {
			PPListElement curr= (PPListElement) elem;
			if(curr.getEGLPathEntry().isBinaryProject()) {
				return false;
			}
			return !(curr.getResource() instanceof IFolder) && curr.getParentContainer() == null;
		}
		if (elem instanceof PPListElementAttribute) {
			PPListElementAttribute attrib= (PPListElementAttribute) elem;
			return true;
		}
		return false;
	}
	
	/**
	 * @param field the dialog field 
	 */
	private void libaryPageDialogFieldChanged(DialogField field) {
		if (fCurrProject != null) {
			// already initialized
			updateClasspathList();
		}
	}	
		
	private void updateClasspathList() {
		List projelements= fLibrariesList.getElements();
		
		List cpelements= fClassPathList.getElements();
		int nEntries= cpelements.size();
		// backwards, as entries will be deleted
		int lastRemovePos= nEntries;
		for (int i= nEntries - 1; i >= 0; i--) {
			PPListElement cpe= (PPListElement)cpelements.get(i);
			int kind= cpe.getEntryKind();
			if (isEntryKind(kind)) {
				if (!projelements.remove(cpe)) {
					cpelements.remove(i);
					lastRemovePos= i;
				}	
			}
		}
		
		cpelements.addAll(lastRemovePos, projelements);

		if (lastRemovePos != nEntries || !projelements.isEmpty()) {
			fClassPathList.setElements(cpelements);
		}
	}
		
	private PPListElement[] openJarFileDialog(PPListElement existing) {
		IWorkspaceRoot root= fCurrProject.getProject().getWorkspace().getRoot();
		
		if (existing == null) {
			IPath[] selected= BuildPathDialogAccess.chooseJAREntries(getShell(), fCurrProject.getPath(), getUsedEGLARFiles(existing));
			if (selected != null) {
				ArrayList res= new ArrayList();
				
				for (int i= 0; i < selected.length; i++) {
					IPath curr= selected[i];
					IResource resource= root.findMember(curr);
					if (resource instanceof IFile) {
						res.add(newPPLibraryElement(resource));
					}
				}
				return (PPListElement[]) res.toArray(new PPListElement[res.size()]);
			}
		} else {
			IPath configured= BuildPathDialogAccess.configureJAREntry(getShell(), existing.getPath(), getUsedEGLARFiles(existing));
			if (configured != null) {
				IResource resource= root.findMember(configured);
				if (resource instanceof IFile) {
					return new PPListElement[] { newPPLibraryElement(resource) }; 
				}
			}
		}		
		return null;
	}
	
	
	private IPath[] getUsedContainers(PPListElement existing) {
		ArrayList res= new ArrayList();
		if (fCurrProject.exists()) {
				try {
					IPath outputLocation= fCurrProject.getOutputLocation();
					if (outputLocation != null && outputLocation.segmentCount() > 1) { // != Project
						res.add(outputLocation);
					}
				} catch (EGLModelException e) {
				}
		}	
			
		List cplist= fLibrariesList.getElements();
		for (int i= 0; i < cplist.size(); i++) {
			PPListElement elem= (PPListElement)cplist.get(i);
			if (elem.getEntryKind() == IEGLPathEntry.CPE_LIBRARY && (elem != existing)) {
				IResource resource= elem.getResource();
				if (resource instanceof IContainer && !resource.equals(existing)) {
					res.add(resource.getFullPath());
				}
			}
		}
		return (IPath[]) res.toArray(new IPath[res.size()]);
	}
	
	private IPath[] getUsedEGLARFiles(PPListElement existing) {
		List res= new ArrayList();
		List cplist= fLibrariesList.getElements();
		for (int i= 0; i < cplist.size(); i++) {
			PPListElement elem= (PPListElement)cplist.get(i);
			if (elem.getEntryKind() == IEGLPathEntry.CPE_LIBRARY && (elem != existing)) {
				IResource resource= elem.getResource();
				if (resource instanceof IFile) {
					res.add(resource.getFullPath());
				}
			}
		}
		return (IPath[]) res.toArray(new IPath[res.size()]);
	}	
	
	private PPListElement newPPLibraryElement(IResource res) {
		return new PPListElement(fCurrProject, IEGLPathEntry.CPE_LIBRARY, res.getFullPath(), res);
	}

	private PPListElement[] openExtJarFileDialog(PPListElement existing) {
		if (existing == null) {
			IPath[] selected= BuildPathDialogAccess.chooseExternalJAREntries(getShell());
			if (selected != null) {
				ArrayList res= new ArrayList();
				for (int i= 0; i < selected.length; i++) {
					res.add(new PPListElement(fCurrProject, IEGLPathEntry.CPE_LIBRARY, selected[i], null));
				}
				return (PPListElement[]) res.toArray(new PPListElement[res.size()]);
			}
		} else {
			IPath configured= BuildPathDialogAccess.configureExternalJAREntry(getShell(), existing.getPath());
			if (configured != null) {
				return new PPListElement[] { new PPListElement(fCurrProject, IEGLPathEntry.CPE_LIBRARY, configured, null) };
			}
		}		
		return null;
	}
			
	private PPListElement[] openVariableSelectionDialog(PPListElement existing) {
		List existingElements= fLibrariesList.getElements();
		ArrayList existingPaths= new ArrayList(existingElements.size());
		for (int i= 0; i < existingElements.size(); i++) {
			PPListElement elem= (PPListElement) existingElements.get(i);
			if (elem.getEntryKind() == IEGLPathEntry.CPE_VARIABLE) {
				existingPaths.add(elem.getPath());
			}
		}
		IPath[] existingPathsArray= (IPath[]) existingPaths.toArray(new IPath[existingPaths.size()]);
		
		if (existing == null) {
			IPath[] paths= BuildPathDialogAccess.chooseVariableEntries(getShell(), existingPathsArray);
			if (paths != null) {
				ArrayList result= new ArrayList();
				for (int i = 0; i < paths.length; i++) {
					IPath path= paths[i];
					PPListElement elem= createCPVariableElement(path);
					if (!existingElements.contains(elem)) {
						result.add(elem);
					}
				}
				return (PPListElement[]) result.toArray(new PPListElement[result.size()]);
			}
		} else {
			IPath path= BuildPathDialogAccess.configureVariableEntry(getShell(), existing.getPath(), existingPathsArray);
			if (path != null) {
				return new PPListElement[] { createCPVariableElement(path) };
			}
		}
		return null;
	}

	private PPListElement createCPVariableElement(IPath path) {
		PPListElement elem= new PPListElement(fCurrProject, IEGLPathEntry.CPE_VARIABLE, path, null);
		IPath resolvedPath= EGLCore.getResolvedVariablePath(path);
		elem.setIsMissing((resolvedPath == null) || !resolvedPath.toFile().exists());
		return elem;
	}

	private PPListElement[] openContainerSelectionDialog(PPListElement existing) {
		if (existing == null) {
			IEGLPathEntry[] created= BuildPathDialogAccess.chooseContainerEntries(getShell(), fCurrProject, getRawClasspath());
			if (created != null) {
				PPListElement[] res= new PPListElement[created.length];
				for (int i= 0; i < res.length; i++) {
					res[i]= PPListElement.createFromExisting(created[i], fCurrProject);
				}
				return res;
			}
		} else {
			IEGLPathEntry created= BuildPathDialogAccess.configureContainerEntry(getShell(), existing.getEGLPathEntry(), fCurrProject, getRawClasspath());
			if (created != null) {
				PPListElement elem= new PPListElement(fCurrProject, IEGLPathEntry.CPE_CONTAINER, created.getPath(), null);
				return new PPListElement[] { elem };
			}
		}		
		return null;
	}
		
	private IEGLPathEntry[] getRawClasspath() {
		IEGLPathEntry[] currEntries= new IEGLPathEntry[fClassPathList.getSize()];
		for (int i= 0; i < currEntries.length; i++) {
			PPListElement curr= (PPListElement) fClassPathList.getElement(i);
			currEntries[i]= curr.getEGLPathEntry();
		}
		return currEntries;
	}
		
	public boolean isEntryKind(int kind) {
		return kind == IEGLPathEntry.CPE_LIBRARY || kind == IEGLPathEntry.CPE_VARIABLE || kind == IEGLPathEntry.CPE_CONTAINER;
	}
	
	/*
	 * @see BuildPathBasePage#getSelection
	 */
	public List getSelection() {
		return fLibrariesList.getSelectedElements();
	}

	/*
	 * @see BuildPathBasePage#setSelection
	 */	
	public void setSelection(List selElements, boolean expand) {
		fLibrariesList.selectElements(new StructuredSelection(selElements));
		if (expand) {
			for (int i= 0; i < selElements.size(); i++) {
				fLibrariesList.expandElement(selElements.get(i), 1);
			}
		}
	}
	
	public void setSelection(List selElements) {
		setSelection(selElements, false);
	}


	/**
     * {@inheritDoc}
     */
    public void setFocus() {
    	fLibrariesList.setFocus();
    }	

	private Shell getShell() {
		if (fSWTControl != null) {
			return fSWTControl.getShell();
		}
		return EDTUIPlugin.getActiveWorkbenchShell();
	}
   
}
