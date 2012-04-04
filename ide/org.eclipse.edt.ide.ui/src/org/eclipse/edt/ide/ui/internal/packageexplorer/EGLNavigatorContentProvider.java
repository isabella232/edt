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
package org.eclipse.edt.ide.ui.internal.packageexplorer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

public class EGLNavigatorContentProvider extends EGLElementContentProvider
		implements IPipelinedTreeContentProvider {

	public void getPipelinedChildren(Object parent, Set currentChildren) {
		Object[] children = getChildren(parent);
		for (Iterator iter = currentChildren.iterator(); iter.hasNext();){
			Object obj = iter.next();
			if (obj instanceof IResource){
				if(EGLCore.create((IResource)obj) != null ||
						((IResource)obj).getName().equalsIgnoreCase("EGLGen") || //$NON-NLS-1$
						((IResource)obj).getName().equalsIgnoreCase("EGLbin")) //$NON-NLS-1$
					iter.remove(); 
			}
		}
		currentChildren.addAll(Arrays.asList(children));
	}

	public void getPipelinedElements(Object input, Set currentElements) {
		Object[] children = getElements(input);

		for (Iterator iter = currentElements.iterator(); iter.hasNext();){
			Object obj = iter.next();
			if (obj instanceof IResource){
				if(EGLCore.create((IResource)obj) != null)
					iter.remove();
			}
		}

		currentElements.addAll(Arrays.asList(children));
	}
	
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IWorkspaceRoot){
			IWorkspaceRoot root = (IWorkspaceRoot) inputElement;
			return root.getProjects();			
		}else if(inputElement instanceof IEGLModel){
			return ((IEGLModel)inputElement).getWorkspace().getRoot().getProjects();
		}
		
		if(inputElement instanceof IProject)
			return super.getElements(EGLCore.create((IProject) inputElement));
		
		return super.getElements(inputElement);
	}
	
	public Object getParent(Object element) {
		Object parentObj = super.getParent(element);
		IProject proj = null;
		if(parentObj instanceof IProject){
			proj = (IProject)parentObj;
		}
		else if(parentObj instanceof IEGLProject){
			proj = ((IEGLProject)parentObj).getProject();
		}
		else if(parentObj instanceof IJavaProject){
			IJavaProject javaProj = (IJavaProject)parentObj;
			proj = javaProj.getProject();
		}
		
		if(proj != null)
			return proj;		
		else
			return parentObj;
	}

	public Object getPipelinedParent(Object object, Object aSuggestedParent) {
		return getParent(object);
	}

	public PipelinedShapeModification interceptAdd(
			PipelinedShapeModification anAddModification) {
		

		Object parent= anAddModification.getParent();
		
		if (parent instanceof IEGLProject) {
			anAddModification.setParent(((IEGLProject)parent).getProject());
		} 
		
		if (parent instanceof IWorkspaceRoot) {		
			deconvertEGLProjects(anAddModification);
		}
		
		convertToEGLElements(anAddModification);
		return anAddModification;
	}

	public boolean interceptRefresh(
			PipelinedViewerUpdate refreshSynchronization) {
		return convertToEGLElements(refreshSynchronization.getRefreshTargets());
	}

	public PipelinedShapeModification interceptRemove(
			PipelinedShapeModification aRemoveModification) {
		deconvertEGLProjects(aRemoveModification);
		convertToEGLElements(aRemoveModification.getChildren());
		return aRemoveModification;
	}

	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
		return convertToEGLElements(anUpdateSynchronization.getRefreshTargets());
	}

	public void init(ICommonContentExtensionSite aConfig) {
		// TODO Auto-generated method stub

	}

	public void restoreState(IMemento aMemento) {
		// TODO Auto-generated method stub

	}

	public void saveState(IMemento aMemento) {
		// TODO Auto-generated method stub

	}
	
	private boolean convertToEGLElements(PipelinedShapeModification modification) {
		Object parent = modification.getParent();
		if (parent instanceof IContainer) {
			IEGLElement element = EGLCore.create((IContainer) parent);
			if (element != null && element.exists()) {
				// we don't convert the root
				if( !(element instanceof IEGLModel) && !(element instanceof IEGLProject))
					modification.setParent(element);
				return convertToEGLElements(modification.getChildren());
				
			}
		}
		return false;
	}
	
	private boolean convertToEGLElements(Set currentChildren) {

		Set convertedChildren = new LinkedHashSet();
		IEGLElement newChild;
		for (Iterator childrenItr = currentChildren.iterator(); childrenItr.hasNext();) {
			Object child = childrenItr.next();
			// only convert IFolders and IFiles
			if (child instanceof IFolder || child instanceof IFile) {
				if ((newChild = EGLCore.create((IResource) child)) != null
						&& newChild.exists()) {
					childrenItr.remove();					
					convertedChildren.add(newChild);
					
				}
			}else if(child instanceof IEGLProject) {
				childrenItr.remove();
				convertedChildren.add( ((IEGLProject)child).getProject());
			}
		}
		if (!convertedChildren.isEmpty()) {
			currentChildren.addAll(convertedChildren);
			return true;
		}
		return false;
	}
	
	private void deconvertEGLProjects(PipelinedShapeModification modification) {
		Set convertedChildren = new LinkedHashSet();
		for (Iterator iterator = modification.getChildren().iterator(); iterator.hasNext();) {
			Object added = iterator.next(); 
			if(added instanceof IEGLProject) {
				iterator.remove();
				convertedChildren.add(((IEGLProject)added).getProject());
			}			
		}
		modification.getChildren().addAll(convertedChildren);
	}
	
}
