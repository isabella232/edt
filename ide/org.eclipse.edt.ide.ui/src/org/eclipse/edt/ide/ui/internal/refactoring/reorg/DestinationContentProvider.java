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
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.StandardEGLElementContentProvider;

public final class DestinationContentProvider extends StandardEGLElementContentProvider {
	
	private IReorgDestinationValidator fValidator;
	
	public DestinationContentProvider(IReorgDestinationValidator validator) {
		super(true, true);
		fValidator= validator;
	}
	
	public boolean hasChildren(Object element) {
		if (element instanceof IEGLElement){
			IEGLElement eglElement= (IEGLElement) element;
			if (! fValidator.canChildrenBeDestinations(eglElement))
				return false;
			if (eglElement.getElementType() == IEGLElement.PACKAGE_FRAGMENT_ROOT){
				if (((IPackageFragmentRoot)eglElement).isArchive())
					return false;
			}
		} else if (element instanceof IResource) {
			IResource resource= (IResource) element;
			if (! fValidator.canChildrenBeDestinations(resource))
				return false;
		}
		return super.hasChildren(element);
	}
	
	public Object[] getChildren(Object parentElement) {
		try {
			if (parentElement instanceof IEGLModel) {
				return concatenate(getEGLProjects((IEGLModel)parentElement), getOpenNonEGLProjects((IEGLModel)parentElement));
			} else {
				Object[] children= super.getChildren(parentElement);
				ArrayList result= new ArrayList(children.length);
				for (int i= 0; i < children.length; i++) {
					if (children[i] instanceof IEGLElement) {
						IEGLElement eglElement= (IEGLElement) children[i];
						if (fValidator.canElementBeDestination(eglElement) || fValidator.canChildrenBeDestinations(eglElement))
							result.add(eglElement);
					} else if (children[i] instanceof IResource) {
						IResource resource= (IResource) children[i];
						if (fValidator.canElementBeDestination(resource) || fValidator.canChildrenBeDestinations(resource))
							result.add(resource);
					}
				}
				return result.toArray();
			}
		} catch (EGLModelException e) {
			EDTUIPlugin.log(e);
			return new Object[0];
		}
	}

	private static Object[] getOpenNonEGLProjects(IEGLModel model) throws EGLModelException {
		Object[] nonEGLProjects= model.getNonEGLResources();
		ArrayList result= new ArrayList(nonEGLProjects.length);
		for (int i= 0; i < nonEGLProjects.length; i++) {
			IProject project = (IProject) nonEGLProjects[i];
			if (project.isOpen())
				result.add(project);
		}
		return result.toArray();
	}

}
