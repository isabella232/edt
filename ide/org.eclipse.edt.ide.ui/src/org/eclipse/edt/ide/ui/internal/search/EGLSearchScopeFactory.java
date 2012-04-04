/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.search.ui.ISearchResultViewEntry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

public class EGLSearchScopeFactory
{	
    private static EGLSearchScopeFactory fgInstance;
    private static IEGLSearchScope EMPTY_SCOPE= SearchEngine.createEGLSearchScope(new IEGLElement[]{});
	private static final Set EMPTY_SET= new HashSet(0);
	
	public EGLSearchScopeFactory() {
	}
	
	public static EGLSearchScopeFactory getInstance() {
		if (fgInstance == null)
			fgInstance = new EGLSearchScopeFactory();
		return fgInstance;
	}
	
	public IWorkingSet[] queryWorkingSets() {
		Shell shell= EDTUIPlugin.getActiveWorkbenchShell();
		if (shell == null)
			return null;
		IWorkingSetSelectionDialog dialog= PlatformUI.getWorkbench().getWorkingSetManager().createWorkingSetSelectionDialog(shell, true);
		if (dialog.open() == Window.OK) {
			IWorkingSet[] workingSets= dialog.getSelection();
			if (workingSets.length > 0)
				return workingSets;
		}
		return null;
	}

	/**
	 * @param eglElements
	 * @return
	 */	
	private IEGLSearchScope createEGLSearchScope(Set eglElements) {
		return SearchEngine.createEGLSearchScope((IEGLElement[])eglElements.toArray(new IEGLElement[eglElements.size()]));
	}
	
	/**
	 * @param sets
	 * @return
	 */
	public IEGLSearchScope createEGLSearchScope(IWorkingSet[] sets) {
		if (sets == null || sets.length < 1)
		return EMPTY_SCOPE;

		Set eglElements= new HashSet(sets.length * 10);
		for (int i= 0; i < sets.length; i++)
			addEGLElements(eglElements, sets[i]);
		return SearchEngine.createEGLSearchScope((IEGLElement[])eglElements.toArray(new IEGLElement[eglElements.size()]),false);
	}
 
	/**
	 * @param eglElements
	 * @param set
	 */
	private void addEGLElements(Set eglElements, IWorkingSet set) {
		if (set == null)
			return;
				
		IAdaptable[] elements= set.getElements();
		for (int i= 0; i < elements.length; i++) {
			if (elements[i] instanceof IEGLElement)
				addEGLElements(eglElements, (IEGLElement)elements[i]);
			else
				addEGLElements(eglElements, elements[i]);
		}
	}
	
	/**
	 * @param eglElements
	 * @param adaptable
	 */
	private void addEGLElements(Set eglElements, IAdaptable resource) {
		IEGLElement eglElement= (IEGLElement)resource.getAdapter(IEGLElement.class);
		if (eglElement == null)
			// not an ICElement resource
			return;
				
		addEGLElements(eglElements, eglElement);
	}
	/**
	 * @param eglElements
	 * @param element
	 */
	private void addEGLElements(Set eglElements, IEGLElement element) {
	    eglElements.add(element);
	}
	
	/**
	 * @param fStructuredSelection
	 * @return
	 */
	public IEGLSearchScope createEGLSearchScope(IStructuredSelection fStructuredSelection) {
		Set eglElements = new HashSet( fStructuredSelection.size() );
		
		Iterator iter = fStructuredSelection.iterator();
		while( iter.hasNext() ){
			addEGLElements( eglElements, (IAdaptable)iter.next() );
		}
		
		return createEGLSearchScope( eglElements );
	}
		
	public IEGLSearchScope createEGLProjectSearchScope(IEGLElement selection) {
		return createEGLProjectSearchScope(new StructuredSelection(selection));
	}
	
	
	public IEGLSearchScope createEGLProjectSearchScope(ISelection selection) {
		IEditorInput input= getActiveEditorInput();
		if (input != null && selection.isEmpty())
			return EGLSearchScopeFactory.getInstance().internalCreateProjectScope(input);
		return internalCreateProjectScope(selection);
		
	}	
	private IEditorInput getActiveEditorInput() {
		IWorkbenchPage page= EDTUIPlugin.getActivePage();
		if (page != null) {
			IEditorPart editor= page.getActiveEditor();
			if (editor != null /*&& editor.equals(page.getActivePart())*/) {
				return editor.getEditorInput();
			}
		}
		return null;
	}	
	
	private IEGLSearchScope internalCreateProjectScope(IEditorInput editorInput) {
		IAdaptable inputElement = getEditorInputElement(editorInput);
		StructuredSelection selection;
		if (inputElement != null) {
			selection= new StructuredSelection(inputElement);
		} else {
			selection= StructuredSelection.EMPTY;
		}
		return internalCreateProjectScope(selection);
	}
	
	private IAdaptable getEditorInputElement(IEditorInput editorInput) {
		IAdaptable inputElement= null;
		if (editorInput instanceof IFileEditorInput) {
			inputElement= ((IFileEditorInput)editorInput).getFile();
		}
		return inputElement;
	}
	
	private IEGLSearchScope internalCreateProjectScope(ISelection selection) {
		Set eglProjects= getEGLProjects(selection);
		return createEGLSearchScope(eglProjects);
	}
	
	private Set getEGLProjects(ISelection selection) {
		Set eglProjects;
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Iterator iter= ((IStructuredSelection) selection).iterator();
			eglProjects= new HashSet(((IStructuredSelection) selection).size());
			while (iter.hasNext()) {
				Object selectedElement= iter.next();

				// Unpack search result view entry
				if (selectedElement instanceof ISearchResultViewEntry)
					selectedElement= ((ISearchResultViewEntry) selectedElement).getGroupByKey();

				if (selectedElement instanceof IAdaptable) {
					IEGLProject eglProject = getEGLProject((IAdaptable) selectedElement);
					if (eglProject != null)
						eglProjects.add(eglProject);
				}
			}
		} else {
			eglProjects= EMPTY_SET;
		}
		return eglProjects;
	}
		
	private IEGLProject getEGLProject(IAdaptable selectedElement) {
		IEGLProject eglProject= (IEGLProject) selectedElement.getAdapter(IEGLProject.class);
		if (eglProject != null)
			return eglProject;
		IEGLElement eglElement= (IEGLElement) selectedElement.getAdapter(IEGLElement.class);
		if (eglElement != null) {
			eglProject= eglElement.getEGLProject();
			if (eglProject != null)
				return eglProject;
		}
		IResource resource= (IResource) selectedElement.getAdapter(IResource.class);
		if (resource != null) {
			IProject project= resource.getProject();
			try {
				if (project != null && project.isAccessible() && project.hasNature(EGLCore.NATURE_ID)) {
					return EGLCore.create(project);
				}
			} catch (CoreException e) {
				// Since the java project is accessible, this should not happen, anyway, don't search this project
			}
		}
		return null;
	}
	
	public IProject[] getProjects(IEGLSearchScope scope) {
		IPath[] paths= scope.enclosingProjects();
		HashSet temp= new HashSet();
		for (int i= 0; i < paths.length; i++) {
			IResource resource= ResourcesPlugin.getWorkspace().getRoot().findMember(paths[i]);
			if (resource != null && resource.getType() == IResource.PROJECT)
				temp.add(resource);
		}
		return (IProject[]) temp.toArray(new IProject[temp.size()]);
	}
	
	
}
