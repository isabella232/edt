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
package org.eclipse.edt.ide.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRootContainer;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.core.model.ISourceReference;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
 
/**
 * A base content provider for EGL elements. It provides access to the
 * EGL element hierarchy without listening to changes in the EGL model.
 * If updating the presentation on EGL model change is required than 
 * clients have to subclass, listen to EGL model changes and have to update
 * the UI using corresponding methods provided by the JFace viewers or their 
 * own UI presentation.
 * <p>
 * The following EGL element hierarchy is surfaced by this content provider:
 * <p>
 * <pre>
EGL model (<code>IEGLModel</code>)
   EGL project (<code>IEGLProject</code>)
      package fragment root (<code>IPackageFragmentRoot</code>)
         package fragment (<code>IPackageFragment</code>)
            egl file (<code>IEGLFile</code>)
 * </pre>
 * </p> 			
 * <p>
 * Note that when the entire EGL project is declared to be package fragment root,
 * the corresponding package fragment root element that normally appears between the
 * EGL project and the package fragments is automatically filtered out.
 * </p>
 * This content provider can optionally return working copy elements for members 
 * below compilation units. If enabled, working copy members are returned for those
 * compilation units in the EGL element hierarchy for which a shared working copy exists 
 * in EDT core.
 * 
 * @see org.eclipse.edt.ide.ui.internal.IWorkingCopyProvider
 * @see EGLCore#getSharedWorkingCopies(org.eclipse.edt.ide.core.model.IBufferFactory)
 * 
 * @since 2.0
 */
public class StandardEGLElementContentProvider implements ITreeContentProvider, IWorkingCopyProvider {

	protected static final Object[] NO_CHILDREN= new Object[0];

	protected boolean fProvideMembers= false;
	protected boolean fProvideWorkingCopy= false;
	
	/**
	 * Creates a new content provider. The content provider does not
	 * provide members of compilation units or class files and it does 
	 * not provide working copy elements.
	 */	
	public StandardEGLElementContentProvider() {
	}
	
	/**
	 * Creates a new <code>StandardEGLElementContentProvider</code>.
	 *
	 * @param provideMembers if <code>true</code> members below compilation units 
	 * and class files are provided. 
	 * @param provideWorkingCopy if <code>true</code> the element provider provides
	 * working copies members of compilation units which have an associated working 
	 * copy in EDT core. Otherwise only original elements are provided.
	 */
	public StandardEGLElementContentProvider(boolean provideMembers, boolean provideWorkingCopy) {
		fProvideMembers= provideMembers;
		fProvideWorkingCopy= provideWorkingCopy;
	}
	
	/**
	 * Returns whether members are provided when asking
	 * for a compilation units or class file for its children.
	 * 
	 * @return <code>true</code> if the content provider provides members; 
	 * otherwise <code>false</code> is returned
	 */
	public boolean getProvideMembers() {
		return fProvideMembers;
	}

	/**
	 * Sets whether the content provider is supposed to return members
	 * when asking a compilation unit or class file for its children.
	 * 
	 * @param b if <code>true</code> then members are provided. 
	 * If <code>false</code> compilation units and class files are the
	 * leaves provided by this content provider.
	 */
	public void setProvideMembers(boolean b) {
		fProvideMembers= b;
	}
	
	/**
	 * Returns whether the provided members are from a working
	 * copy or the original compilation unit. 
	 * 
	 * @return <code>true</code> if the content provider provides
	 * working copy members; otherwise <code>false</code> is
	 * returned
	 * 
	 * @see #setProvideWorkingCopy(boolean)
	 */
	public boolean getProvideWorkingCopy() {
		return fProvideWorkingCopy;
	}

	/**
	 * Sets whether the members are provided from a shared working copy 
	 * that exists for a original compilation unit in the EGL element hierarchy.
	 * 
	 * @param b if <code>true</code> members are provided from a 
	 * working copy if one exists in EDT core. If <code>false</code> the 
	 * provider always returns original elements.
	 */
	public void setProvideWorkingCopy(boolean b) {
		fProvideWorkingCopy= b;
	}

	/* (non-EGLdoc)
	 * @see IWorkingCopyProvider#providesWorkingCopies()
	 */
	public boolean providesWorkingCopies() {
		return fProvideWorkingCopy;
	}

	/* (non-EGLdoc)
	 * Method declared on IStructuredContentProvider.
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
	
	/* (non-EGLdoc)
	 * Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/* (non-EGLdoc)
	 * Method declared on IContentProvider.
	 */
	public void dispose() {
	}

	/* (non-EGLdoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;
			
		try {
			if (element instanceof IEGLModel)
				return getEGLProjects((IEGLModel)element);
			
			// The first two cases are used by the project navigator.  The third is used for dialogs.
			if ((element instanceof IProject)&&(((IProject)element).hasNature(EGLCore.NATURE_ID))){
				 Object[] rawChildren = getPackageFragmentRoots(EGLCore.create((IProject)element));
				 return removeNonEGLElements(rawChildren);					
			}
			else if(element instanceof IJavaProject){
				Object[] rawChildren = getPackageFragmentRoots(EGLCore.create(((IJavaProject)element).getProject()));
				return removeNonEGLElements(rawChildren);
			}
			else if(element instanceof IEGLProject){
				return getPackageFragmentRoots((IEGLProject)element); 
			}
			if(element instanceof EglarPackageFragmentRootContainer) {
				return ((EglarPackageFragmentRootContainer)element).getAllJarPackageFragmentRoot();
			}
			if (element instanceof IPackageFragmentRoot) 
				return getPackageFragments((IPackageFragmentRoot)element);
			
			if (element instanceof IPackageFragment) 
				return getPackageContents((IPackageFragment)element);
				
			if (element instanceof IFolder)
			{
				return getResources((IFolder)element);
			}
			
//			if(element instanceof IClassFile) {
//				return getClassFileMethods((IClassFile) element);
//			}
			
			if (fProvideMembers && element instanceof ISourceReference && element instanceof IParent) {
				if (fProvideWorkingCopy && element instanceof IEGLFile) {
					element= EGLModelUtil.toWorkingCopy((IEGLFile) element);
				}
				return ((IParent)element).getChildren();
			}
		} catch (EGLModelException e) {
			return NO_CHILDREN;
		} catch (CoreException e) {
			EGLLogger.log(this, e.toString());		
		}
		return NO_CHILDREN;	
	}

	/* (non-EGLdoc)
	 * @see ITreeContentProvider
	 */
	public boolean hasChildren(Object element) {
		if (fProvideMembers) {
			// assume CUs and class files are never empty
			if (element instanceof IEGLFile) {
				return true;
			}
		} else {
			// don't allow to drill down into a compilation unit or class file
			if (element instanceof IEGLFile ||
				element instanceof IFile || element instanceof IClassFile)
			return false;
		}
			
		if (element instanceof IEGLProject) {
			IEGLProject jp= (IEGLProject)element;
			if (!jp.getProject().isOpen()) {
				return false;
			}	
		}
		
		if(element instanceof EglarPackageFragmentRootContainer) {
			EglarPackageFragmentRootContainer container = (EglarPackageFragmentRootContainer)element;
			return container.hasJarPackageFragmentRoot();
		}
		
		if (element instanceof IParent) {
			try {
				// when we have EGL children return true, else we fetch all the children
				if (((IParent)element).hasChildren())
					return true;
			} catch(EGLModelException e) {
				return true;
			}
		}
		Object[] children= getChildren(element);
		return (children != null) && children.length > 0;
	}
	 
	/* (non-EGLdoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object getParent(Object element) {
		if (!exists(element))
			return null;
		return internalGetParent(element);			
	}
	
	protected Object[] getPackageFragments(IPackageFragmentRoot root) throws EGLModelException {
		IEGLElement[] fragments= root.getChildren();
		Object[] nonEGLResources= root.getNonEGLResources();
		if (nonEGLResources == null)
			return fragments;
		return concatenate(fragments, nonEGLResources);
	}
	
	protected Object[] getEGLPackageFragmentRoots(IEGLProject project) throws EGLModelException {
		if (!project.getProject().isOpen())
			return NO_CHILDREN;
			
		IPackageFragmentRoot[] roots= project.getPackageFragmentRoots();
		List list= new ArrayList();
		EglarPackageFragmentRootContainer container = null;
		// filter out package fragments that correspond to projects and
		// replace them with the package fragments directly
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= (IPackageFragmentRoot)roots[i];
			if(root.getRawEGLPathEntry().getEntryKind() == IEGLPathEntry.CPE_CONTAINER){
				continue;
			}else if (isProjectPackageFragmentRoot(root)) {
				Object[] children= root.getChildren();
				for (int k= 0; k < children.length; k++) 
					list.add(children[k]);
			} 
		}
		

		IEGLPathEntry[] eglpaths = project.getRawEGLPath();
		for (int i = 0; i < eglpaths.length; i++) {
			if(eglpaths[i].getEntryKind() == IEGLPathEntry.CPE_CONTAINER){
				boolean foundJarPkgRoot = false;
				IPackageFragmentRoot[] cRoots= project.findPackageFragmentRoots(eglpaths[i]);
				container = new EglarPackageFragmentRootContainer(project);
				for (int j= 0; j < cRoots.length; j++) {
					IPackageFragmentRoot root= (IPackageFragmentRoot)cRoots[j];
					if (hasChildren(root)) {
						if(root instanceof EglarPackageFragmentRoot && ((EglarPackageFragmentRoot)root).isArchive()) {
							foundJarPkgRoot = true;
							container.addJarPackageFragmentRoot((EglarPackageFragmentRoot) root);
						}
					} 
				}
				if(foundJarPkgRoot) {
					list.add(container);
				}
			}
		}
		return list.toArray();
	}

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected Object[] getPackageFragmentRoots(IEGLProject project) throws EGLModelException {
		return concatenate(getEGLPackageFragmentRoots(project), project.getNonEGLResources());
	}

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected Object[] getEGLProjects(IEGLModel jm) throws EGLModelException {
		return jm.getEGLProjects();
	}

	protected Object[] getIProjects(IEGLModel jm) throws EGLModelException {
	    Object[] eglprojs = getEGLProjects(jm); 
	    int ilen = eglprojs.length;
		IProject[] projs = new IProject[ilen];
		for(int i=0; i<ilen; i++)
		{
		    projs[i] = ((IEGLProject)eglprojs[i]).getProject();
		}
		return projs;
	}

	protected Object[] getPackageContents(IPackageFragment fragment) throws EGLModelException {
		if (fragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
			return concatenate(fragment.getEGLFiles(), fragment.getNonEGLResources());
		}
		return concatenate(fragment.getClassFiles(), fragment.getNonEGLResources());
	}
	
	protected Object[] getClassFileMethods(IClassFile classFile) {
		try {
			return concatenate(classFile.getPart().getFields(), classFile.getPart().getFunctions());
		} catch (EGLModelException e) {
			return concatenate(new Object[0], new Object[0]);
		}
	}
		
	protected Object[] getResources(IFolder folder) {
		try {
			// filter out folders that are package fragment roots
			Object[] members= folder.members();
			List nonEGLResources= new ArrayList();
			for (int i= 0; i < members.length; i++) {
				Object o= members[i];
				// A folder can also be a package fragement root in the following case
				// Project
				//  + src <- source folder
				//    + excluded <- excluded from class path
				//      + included  <- a new source folder.
				// Included is a member of excluded, but since it is rendered as a source
				// folder we have to exclude it as a normal child.
				if (o instanceof IFolder) {
					IEGLElement element= EGLCore.create((IFolder)o);
					if (element instanceof IPackageFragmentRoot && element.exists()) {
						continue;
					}
				}
				nonEGLResources.add(o);
			}
			return nonEGLResources.toArray();
		} catch(CoreException e) {
			return NO_CHILDREN;
		}
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected boolean isClassPathChange(IEGLElementDelta delta) {
		
		// need to test the flags only for package fragment roots
		if (delta.getElement().getElementType() != IEGLElement.PACKAGE_FRAGMENT_ROOT)
			return false;
		
		int flags= delta.getFlags();
		return (delta.getKind() == IEGLElementDelta.CHANGED && 
			((flags & IEGLElementDelta.F_ADDED_TO_EGLPATH) != 0) ||
			 ((flags & IEGLElementDelta.F_REMOVED_FROM_EGLPATH) != 0) ||
			 ((flags & IEGLElementDelta.F_REORDER) != 0));
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected Object skipProjectPackageFragmentRoot(IPackageFragmentRoot root) {
		try {
			if (isProjectPackageFragmentRoot(root))
				return root.getParent(); 
			return root;
		} catch(EGLModelException e) {
			return root;
		}
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected boolean isPackageFragmentEmpty(IEGLElement element) throws EGLModelException {
		if (element instanceof IPackageFragment) {
			IPackageFragment fragment= (IPackageFragment)element;
			if (!(fragment.hasChildren() || fragment.getNonEGLResources().length > 0) && fragment.hasSubpackages()) 
				return true;
		}
		return false;
	}

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected boolean isProjectPackageFragmentRoot(IPackageFragmentRoot root) throws EGLModelException {
		IResource resource= root.getResource();
		return (resource instanceof IProject);
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected boolean exists(Object element) {
		if (element == null) {
			return false;
		}
		if (element instanceof IResource) {
			return ((IResource)element).exists();
		}
		if (element instanceof IEGLElement) {
			return ((IEGLElement)element).exists();
		}
		return true;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected Object internalGetParent(Object element) {
		if (element instanceof IEGLProject) {
			return ((IEGLProject)element).getEGLModel();
		}
		// try to map resources to the containing package fragment
		if (element instanceof IResource) {
			IResource parent= ((IResource)element).getParent();
			IEGLElement jParent= EGLCore.create(parent);
			// http://bugs.eclipse.org/bugs/show_bug.cgi?id=31374
			if (jParent != null && jParent.exists()) 
				return jParent;
			return parent;
		}

		// for package fragments that are contained in a project package fragment
		// we have to skip the package fragment root as the parent.
		if (element instanceof IPackageFragment) {
			IPackageFragmentRoot parent= (IPackageFragmentRoot)((IPackageFragment)element).getParent();
			return skipProjectPackageFragmentRoot(parent);
		}
		if (element instanceof IEGLElement) {
			IEGLElement candidate= ((IEGLElement)element).getParent();
			// If the parent is a CU we might have shown working copy elements below CU level. If so
			// return the original element instead of the working copy.
			if (candidate != null && candidate.getElementType() == IEGLElement.EGL_FILE) {
				candidate= EGLModelUtil.toOriginal((IEGLFile) candidate);
			}
			return candidate;
		}
		return null;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected static Object[] concatenate(Object[] a1, Object[] a2) {
		int a1Len= a1.length;
		int a2Len= a2.length;
		Object[] res= new Object[a1Len + a2Len];
		System.arraycopy(a1, 0, res, 0, a1Len);
		System.arraycopy(a2, 0, res, a1Len, a2Len); 
		return res;
	}
	
	private Object[] removeNonEGLElements(Object[] sourceElements){
		ArrayList resultElements = new ArrayList();
		for(int j=0; j<sourceElements.length; j++){
			if(sourceElements[j] instanceof IEGLElement){
				resultElements.add(sourceElements[j]);
			}
		}
		if(resultElements!=null)
			return resultElements.toArray();
		else
			return null;
	}


}
