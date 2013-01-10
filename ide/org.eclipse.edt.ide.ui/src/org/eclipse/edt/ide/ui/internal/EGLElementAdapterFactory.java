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
package org.eclipse.edt.ide.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.ui.IContainmentAdapter;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.FilePropertySource;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.ResourcePropertySource;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;

public class EGLElementAdapterFactory implements IAdapterFactory, IContributorResourceAdapter{
	private static Class[] PROPERTIES= new Class[] {
		IPropertySource.class,
		IFile.class,
		IResource.class,
		IWorkbenchAdapter.class,
		IPersistableElement.class,
		IProject.class,
		IContributorResourceAdapter.class,
		ITaskListResourceAdapter.class,
		IContainmentAdapter.class
	};
	
	private static WorkbenchAdapter fgEGLWorkbenchAdapter= new WorkbenchAdapter();
	private static EGLElementContainmentAdapter fgEGLElementContainmentAdapter= new EGLElementContainmentAdapter();
	
	public Class[] getAdapterList() {
		return PROPERTIES;
	}
	
	public Object getAdapter(Object element, Class key) {
		IEGLElement egl= (IEGLElement) element;
		
		if (IPropertySource.class.equals(key)) {
			return getProperties(egl);
		} if (IResource.class.equals(key)) {
			return getResource(egl);
		} 
		
		if (IProject.class.equals(key)) {
			if(element instanceof IEGLProject)			
				return getProject(egl);
			return null;
		} 
		
		if (IFile.class.equals(key)) {
			if(egl instanceof IEGLFile)
				return EGLModelUtil.toOriginal((IEGLFile) egl).getResource();
			return null;
		}
		if (IWorkbenchAdapter.class.equals(key)) {
			return fgEGLWorkbenchAdapter;
		} 
		if (IPersistableElement.class.equals(key)) {
			return new PersistableEGLElementFactory(egl);
		} if (IContributorResourceAdapter.class.equals(key)) {
			return this;
		} 
		if (IContainmentAdapter.class.equals(key)) {
			return fgEGLElementContainmentAdapter;
		}
		return null; 
	}
	
	private IResource getResource(IEGLElement element) {
		switch (element.getElementType()) {
			case IEGLElement.PART:
				// top level types behave like the CU
			IEGLElement parent= element.getParent();
				if (parent instanceof IEGLFile) {
					return EGLModelUtil.toOriginal((IEGLFile) parent).getResource();
				}
				return null;
			case IEGLElement.EGL_FILE:
				return EGLModelUtil.toOriginal((IEGLFile) element).getResource();
			case IEGLElement.CLASS_FILE:
			case IEGLElement.PACKAGE_FRAGMENT:
				// test if in a archive
				IPackageFragmentRoot root= (IPackageFragmentRoot) element.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
				if (!root.isArchive()) {
					return element.getResource();
				}
				return null;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			case IEGLElement.EGL_PROJECT:
			case IEGLElement.EGL_MODEL:
				return element.getResource();
			default:
				return null;
		}		
	}

	/*
	 * @see org.eclipse.ui.IContributorResourceAdapter#getAdaptedResource(org.eclipse.core.runtime.IAdaptable)
	 */
	public IResource getAdaptedResource(IAdaptable adaptable) {
		return getResource((IEGLElement)adaptable);
	}
	
	private IResource getProject(IEGLElement element) {
		IEGLProject eglProject = element.getEGLProject();
		if(eglProject != null)
			return eglProject.getProject();
		return null;
	}

	private IPropertySource getProperties(IEGLElement element) {
		IResource resource= getResource(element);
		if (resource == null)
			return new EGLElementProperties(element);
		if (resource.getType() == IResource.FILE)
			return new FilePropertySource((IFile) resource);
		return new ResourcePropertySource((IResource) resource);
	}

}
