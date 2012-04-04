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


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.ElementChangedEvent;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IElementChangedListener;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.internal.EGLElementAdapterFactory;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.StandardEGLElementContentProvider;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;


public class EGLElementContentProvider extends StandardEGLElementContentProvider 
implements IElementChangedListener
{
	protected static final Object[] NO_CHILDREN= new Object[0];
	protected TreeViewer fViewer;
	protected Object fInput;
	protected boolean fProvideMembers= false;
    
	private EGLElementAdapterFactory fEGLElementAdapterFactory;

	
	public EGLElementContentProvider(){
		registerAdapters();		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		fViewer= (TreeViewer)viewer;
		if (oldInput == null && newInput != null) {
			EGLCore.addElementChangedListener(this); 
		} else if (oldInput != null && newInput == null) {
			EGLCore.removeElementChangedListener(this); 
		}
		fInput= newInput;
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

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.model.IElementChangedListener#elementChanged(org.eclipse.edt.ide.core.model.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		try {
			processDelta(event.getDelta());
		} catch(EGLModelException e) {
			//EGLCorePlugin.getDefault().logErrorStatus(ResourceHandler.getString("EGLElementContentProvider.errorMessage"), e.getStatus()); //$NON-NLS-1$
		}
		
	}
	
	/**
	 * Processes a delta recursively. When more than two children are affected the
	 * tree is fully refreshed starting at this node. The delta is processed in the
	 * current thread but the viewer updates are posted to the UI thread.
	 */
	protected void processDelta(IEGLElementDelta delta) throws EGLModelException {
		int kind= delta.getKind();
		int flags= delta.getFlags();
		IEGLElement element= delta.getElement();

//		if (!getProvideWorkingCopy() && isWorkingCopy(element))
//			return;

		if (element != null && element.getElementType() == IEGLElement.EGL_FILE && !element.getEGLProject().isOnEGLPath(element))
			return;
			 
		// handle open and closing of a solution or project
		if (((flags & IEGLElementDelta.F_CLOSED) != 0) || ((flags & IEGLElementDelta.F_OPENED) != 0)) {			
			postRefresh(element);
			return;
		}

		if (kind == IEGLElementDelta.REMOVED) {
			// when a working copy is removed all we have to do
			// is to refresh the compilation unit
			if (isWorkingCopy(element)) {
				refreshWorkingCopy((IWorkingCopy)element);
				return;
			}
			Object parent= internalGetParent(element);			
			postRemove(element);
			if (element instanceof IEGLProject)                 //@A
				postRemove(((IEGLProject)element).getProject());   //@A

			if (parent instanceof IPackageFragment) 
				updatePackageIcon((IPackageFragment)parent);
			// we are filtering out empty subpackages, so we
			// a package becomes empty we remove it from the viewer. 
			if (isPackageFragmentEmpty(element.getParent())) {
				if (fViewer.testFindItem(parent) != null)
					postRefresh(internalGetParent(parent));
			}  
			return;
		}

		if (kind == IEGLElementDelta.ADDED) { 
			// when a working copy is added all we have to do
			// is to refresh the compilation unit
			if (isWorkingCopy(element)) {
				refreshWorkingCopy((IWorkingCopy)element);
				return;
			}
			Object parent= internalGetParent(element);
			// we are filtering out empty subpackages, so we
			// have to handle additions to them specially. 
			if (parent instanceof IPackageFragment) {
				Object grandparent= internalGetParent(parent);
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an unvisible parent
				if (parent.equals(fInput)) {
					postRefresh(parent);
				} else {
					// refresh from grandparent if parent isn't visible yet
					if (fViewer.testFindItem(parent) == null)
						postRefresh(grandparent);
					else {
						postRefresh(parent);
					}	
				}
			} else {  
				postAdd(parent, element);
			}
		}

		if (element instanceof IEGLFile) {
//			if (getProvideWorkingCopy()) {
//				IEGLElement original= ((IWorkingCopy)element).getOriginalElement();
//				if (original != null)
//					element= original;
//			}
			if (kind == IEGLElementDelta.CHANGED) {
				postRefresh(element);
				return;
			}
		}
		// we don't show the contents of a compilation or IClassFile, so don't go any deeper
		if ((element instanceof IEGLFile) || (element instanceof IClassFile))
			return;
		
		if (isClassPathChange(delta)) {
			 // throw the towel and do a full refresh of the affected java project. 
			postRefresh(element.getEGLProject());
		}
		
		if (delta.getResourceDeltas() != null) {
			IResourceDelta[] rd= delta.getResourceDeltas();
			for (int i= 0; i < rd.length; i++) {
				processResourceDelta(rd[i], element);
			}
		}
		
		IEGLElementDelta[] affectedChildren= delta.getAffectedChildren();
		if (affectedChildren.length > 1) {
			// a package fragment might become non empty refresh from the parent
			if (element instanceof IPackageFragment) {
				IEGLElement parent= (IEGLElement)internalGetParent(element);
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an unvisible parent
				if (element.equals(fInput)) {
					postRefresh(element);
				} else {
					postRefresh(parent);
				}
				return;
			}
			// more than one child changed, refresh from here downwards
			if (element instanceof IPackageFragmentRoot)
				postRefresh(skipProjectPackageFragmentRoot((IPackageFragmentRoot)element));
			else
				postRefresh(element);
			return;
		}
		for (int i= 0; i < affectedChildren.length; i++) {
			processDelta(affectedChildren[i]);
		}
	}
	
	private void postRefresh(final Object root) {
		postRunnable(new Runnable() {
			public void run() {
				// 1GF87WR: ITPUI:ALL - SWTEx + NPE closing a workbench window.
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					if(root instanceof IEGLElement){
						IEGLElement parentElement = ((IEGLElement)root).getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
						if(parentElement!=null){
								fViewer.refresh(parentElement);
						}
						else{
							if(root instanceof EGLProject) {
								IProject project = ((EGLProject)root).getProject();
								fViewer.refresh(project);
							} else {
								fViewer.refresh(root);
							}
						}
					}
				}
			}
		});
	}
	
	private void postAdd(final Object parent, final Object element) {
		postRunnable(new Runnable() {
			public void run() {
				// 1GF87WR: ITPUI:ALL - SWTEx + NPE closing a workbench window.
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) 
					fViewer.add(parent, element);
			}
		});
	}

	private void postRemove(final Object element) {
		postRunnable(new Runnable() {
			public void run() {
				// 1GF87WR: ITPUI:ALL - SWTEx + NPE closing a workbench window.
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					// Set selection to nothing to work around an Eclipse bug in the
					// Properties view.  Bugzilla 22966.
					fViewer.setSelection(null);
					fViewer.remove(element);
				}
			}
		});
	}

	private void postRunnable(final Runnable r) {
		Control ctrl= fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {
			ctrl.getDisplay().asyncExec(r); 
		}
	}
	
	/**
	 * Refreshes the Compilation unit corresponding to the workging copy
	 * @param iWorkingCopy
	 */
	private void refreshWorkingCopy(IWorkingCopy workingCopy) {
		IEGLElement original= workingCopy.getOriginalElement();
		if (original != null)
			postRefresh(original);
	}

	private boolean isWorkingCopy(IEGLElement element) {
		return (element instanceof IWorkingCopy) && ((IWorkingCopy)element).isWorkingCopy();
	}
	
	/**
	 * Updates the package icon
	 */
	 private void updatePackageIcon(final IEGLElement element) {
		postRunnable(new Runnable() {
			public void run() {
				// 1GF87WR: ITPUI:ALL - SWTEx + NPE closing a workbench window.
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) 
					fViewer.update(element, new String[]{IBasicPropertyConstants.P_IMAGE});
			}
		});
	 }
	 
	/**
	 * Process resource deltas
	 */
	private void processResourceDelta(IResourceDelta delta, Object parent) {
		int status= delta.getKind();
		IResource resource= delta.getResource();
		// filter out changes affecting the output folder
		if (resource == null) 
			return;
		
		// this could be optimized by handling all the added children in the parent
		if ((status & IResourceDelta.REMOVED) != 0) {
			if (parent instanceof IPackageFragment) 
				// refresh one level above to deal with empty package filtering properly
				postRefresh(internalGetParent(parent));
			else 
				postRemove(resource);
		}
		if ((status & IResourceDelta.ADDED) != 0) {
			if (parent instanceof IPackageFragment) 
				// refresh one level above to deal with empty package filtering properly
				postRefresh(internalGetParent(parent));
			else
				postAdd(parent, resource);
		}
		IResourceDelta[] affectedChildren= delta.getAffectedChildren();
	
		if (affectedChildren.length > 1) {
			// more than one child changed, refresh from here downwards
			postRefresh(resource);
			return;
		}

		for (int i= 0; i < affectedChildren.length; i++)
			processResourceDelta(affectedChildren[i], resource);
	}

	private void registerAdapters() {
		fEGLElementAdapterFactory= new EGLElementAdapterFactory();
//		fMarkerAdapterFactory= new MarkerAdapterFactory();
//		fEditorInputAdapterFactory= new EditorInputAdapterFactory();
//		fResourceAdapterFactory= new ResourceAdapterFactory();
//		fLogicalPackageAdapterFactory= new LogicalPackageAdapterFactory();

		IAdapterManager manager= Platform.getAdapterManager();		
		manager.registerAdapters(fEGLElementAdapterFactory, IEGLElement.class);
//		manager.registerAdapters(fMarkerAdapterFactory, IMarker.class);
//		manager.registerAdapters(fEditorInputAdapterFactory, IEditorInput.class);
//		manager.registerAdapters(fResourceAdapterFactory, IResource.class);
//		manager.registerAdapters(fLogicalPackageAdapterFactory, LogicalPackage.class);
	}
	
	/*
	 * Convert elements from the EGL model.
	 * EGLProject -> Project (for Web projects only)
	 * EGLModel -> WorkspaceRoot
	 */	
	private Object convertElement(Object element) {
		if (element instanceof IEGLProject) {
			IProject project= ((IEGLProject)element).getProject();
			try{
				if (project.hasNature(EGLCore.NATURE_ID))
					return project;
				else
					return element;
			}
			catch(CoreException e){
				EGLLogger.log(this, e);
			}
		}
//		if (element instanceof IEGLModel)
//			return WebViewPlugin.getWorkspace().getRoot();
		return element;
	}
	
	protected Object internalGetParent(Object element){
		return convertElement(super.internalGetParent(element));
	}

	
	//override the parent class, since the resource content provider is already contributing to provide the resource
	//we need to filter it out, only provide egl content
	protected Object[] getResources(IFolder folder) {
		return NO_CHILDREN;		
	}
}
