/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

import java.text.Collator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLPathContainer;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class EGLElementSorter extends ViewerSorter{

	private static final int PROJECTS= 1;
	private static final int PACKAGEFRAGMENTROOTS= 2;
	private static final int PACKAGEFRAGMENT= 3;

	private static final int EGLFILES= 4;
	private static final int CLASSFILES= 5;
	
//	private static final int RESOURCEFOLDERS= 7;
//	private static final int RESOURCES= 8;
//	private static final int STORAGE= 9;	
	
	private static final int PACKAGE_DECL=	10;
	private static final int IMPORT_CONTAINER= 11;
	private static final int IMPORT_DECLARATION= 12;
	
	// Includes all categories ordered using the OutlineSortOrderPage:
	// types, initializers, methods & fields
//	private static final int MEMBERSOFFSET= 15;
	
	private static final int EGLELEMENTS= 50;
	private static final int OTHERS= 51;

	private Collator collator;	
	/*
	 * @see ViewerSorter#category
	 */
	public int category(Object element) {
		if (element instanceof IEGLElement) {
			IEGLElement je= (IEGLElement) element;

			switch (je.getElementType()) {
				case IEGLElement.INITIALIZER :
					{
//							int flags= ((IInitializer) je).getFlags();
//							if (Flags.isStatic(flags))
//								return getMemberCategory(MembersOrderPreferenceCache.STATIC_INIT_INDEX);
//							else
//								return getMemberCategory(MembersOrderPreferenceCache.INIT_INDEX);
					}
//					case IEGLElement.TYPE :
//						return getMemberCategory(MembersOrderPreferenceCache.TYPE_INDEX);
				case IEGLElement.PACKAGE_DECLARATION :
					return PACKAGE_DECL;
				case IEGLElement.IMPORT_CONTAINER :
					return IMPORT_CONTAINER;
				case IEGLElement.IMPORT_DECLARATION :
					return IMPORT_DECLARATION;
				case IEGLElement.PACKAGE_FRAGMENT :
					IPackageFragment pack= (IPackageFragment) je;
					if (pack.getParent().getResource() instanceof IProject) {
						return PACKAGEFRAGMENTROOTS;
					}
					return PACKAGEFRAGMENT;
				case IEGLElement.PACKAGE_FRAGMENT_ROOT :
					return PACKAGEFRAGMENTROOTS;
				case IEGLElement.EGL_PROJECT :
					return PROJECTS;
				case IEGLElement.CLASS_FILE :
					return CLASSFILES;
				case IEGLElement.EGL_FILE :
					return EGLFILES;
			}
			return EGLELEMENTS;
		} 
//		else if (element instanceof IFile) {
//			return RESOURCES;
//		} 
		else if (element instanceof IProject) {
			return PROJECTS;
		} 
//		else if (element instanceof IContainer) {
//			return RESOURCEFOLDERS;
//		} else if (element instanceof IStorage) {
//			return STORAGE;
//		} 
		else if (element instanceof IEGLPathContainer) {
			return PACKAGEFRAGMENTROOTS;
		}
		return OTHERS;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		
		int cat1= category(e1);
		int cat2= category(e2);

		if (cat1 != cat2){
			return cat1 - cat2;
		}
		
		if (cat1 == PROJECTS) {
			IWorkbenchAdapter a1= (IWorkbenchAdapter)((IAdaptable)e1).getAdapter(IWorkbenchAdapter.class);
			IWorkbenchAdapter a2= (IWorkbenchAdapter)((IAdaptable)e2).getAdapter(IWorkbenchAdapter.class);
			if(a1 != null && a2 != null)
				return getCollator().compare(a1.getLabel(e1), a2.getLabel(e2));
		}
			
		if (cat1 == PACKAGEFRAGMENTROOTS) {
			IPackageFragmentRoot root1= getPackageFragmentRoot(e1);
			IPackageFragmentRoot root2= getPackageFragmentRoot(e2);
			if (!root1.getPath().equals(root2.getPath())) {
				int p1= getEGLPathIndex(root1);
				int p2= getEGLPathIndex(root2);
				if (p1 != p2) {
					return p1 - p2;
				}
			}
		}
		
		String name1 = null;
		String name2 = null;
		
		//Special case to handle build files created as Files instead of EGLFiles
		if(cat1 == OTHERS || cat2 == OTHERS){
			if(( (cat1 == OTHERS)&&(e1 instanceof IResource) )){
				name1=((IResource)e1).getName();
			}
			if(( (cat2 == OTHERS)&&(e2 instanceof IResource) )){
				name2=((IResource)e2).getName();
			}
		}
		// non - egl resources are sorted using the label from the viewers label provider
		else if (cat1 == PROJECTS || /*cat1 == RESOURCES || cat1 == RESOURCEFOLDERS || cat1 == STORAGE ||*/ cat1 == OTHERS) {
			return compareWithLabelProvider(viewer, e1, e2);
		}
		
		if(name1==null){		
			name1= ((IEGLElement) e1).getElementName();
		}
		if(name2==null){
			name2= ((IEGLElement) e2).getElementName();
		}
		
		// egl element are sorted by name
		if(name1!=null && name2!=null){
			int cmp= getCollator().compare(name1, name2);
			if (cmp != 0) {
				return cmp;
			}
		}
		else{
			EGLLogger.log(this, "An error occurred sorting navigator EGL elements."); //$NON-NLS-1$
		}
		
		return 0;
	}
	
	public final Collator getCollator() {
		if (collator == null) {
			collator = Collator.getInstance();
		}
		return collator;
	}
	
	private int compareWithLabelProvider(Viewer parent, Object e1, Object e2) {
		if (parent instanceof ContentViewer){
			IBaseLabelProvider prov = ((ContentViewer) parent).getLabelProvider();
			if (prov instanceof ILabelProvider) {
				ILabelProvider lprov= (ILabelProvider) prov;
				String name1 = lprov.getText(e1);
				String name2 = lprov.getText(e2);
				if (name1 != null && name2 != null) {
					return getCollator().compare(name1, name2);
				}
			}
		}
		return 0; // can't compare
	}

	public String getSortName(Object element) {
		//return ((EGLElementNode)element).getResource().getName();
		return ((IEGLElement)element).getResource().getName();
	}
	
	private IPackageFragmentRoot getPackageFragmentRoot(Object element) {
//		if (element instanceof EGLPathContainer) {
//			// return first package fragment root from the container
//			ClassPathContainer cp= (ClassPathContainer)element;
//			Object[] roots= cp.getPackageFragmentRoots();
//			if (roots.length > 0)
//				return (IPackageFragmentRoot)roots[0];
//			// non resolvable - return a dummy package fragment root
//			return cp.getJavaProject().getPackageFragmentRoot("Non-Resolvable");  //$NON-NLS-1$
//		}
		return EGLModelUtil.getPackageFragmentRoot((IEGLElement)element);
	}
	
	private int getEGLPathIndex(IPackageFragmentRoot root) {
		try {
			IPath rootPath= root.getPath();
			IPackageFragmentRoot[] roots= root.getEGLProject().getPackageFragmentRoots();
			for (int i= 0; i < roots.length; i++) {
				if (roots[i].getPath().equals(rootPath)) {
					return i;
				}
			}
		} catch (EGLModelException e) {
		}

		return Integer.MAX_VALUE;
	}

}
