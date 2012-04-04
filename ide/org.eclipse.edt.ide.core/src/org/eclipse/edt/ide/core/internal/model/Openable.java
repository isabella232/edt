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
package org.eclipse.edt.ide.core.internal.model;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.BufferChangedEvent;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IBufferChangedListener;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IParent;


/**
 * @author twilson
 * created	Jul 24, 2003
 */
public abstract class Openable extends EGLElement implements IOpenable, IBufferChangedListener {

	protected Openable(int type, IEGLElement parent, String name) {
		super(type, parent, name);
	}
	/**
	 * The buffer associated with this element has changed. Registers
	 * this element as being out of synch with its buffer's contents.
	 * If the buffer has been closed, this element is set as NOT out of
	 * synch with the contents.
	 *
	 * @see IBufferChangedListener
	 */
	public void bufferChanged(BufferChangedEvent event) {
		if (event.getBuffer().isClosed()) {
			EGLModelManager.getEGLModelManager().getElementsOutOfSynchWithBuffers().remove(this);
			getBufferManager().removeBuffer(event.getBuffer());
		} else {
			EGLModelManager.getEGLModelManager().getElementsOutOfSynchWithBuffers().put(this, this);
		}
	}	
	/**
	 * Updates the info objects for this element and all of its children by
	 * removing the current infos, generating new infos, and then placing
	 * the new infos into the EGL Model cache tables.
	 */
	protected void buildStructure(OpenableElementInfo info, IProgressMonitor monitor) throws EGLModelException {

		if (monitor != null && monitor.isCanceled()) return;
	
		// remove existing (old) infos
		removeInfo();
		HashMap newElements = new HashMap(11);
		info.setIsStructureKnown(generateInfos(info, monitor, newElements, getResource()));
		EGLModelManager.getEGLModelManager().getElementsOutOfSynchWithBuffers().remove(this);
		for (Iterator iter = newElements.keySet().iterator(); iter.hasNext();) {
			IEGLElement key = (IEGLElement) iter.next();
			Object value = newElements.get(key);
			EGLModelManager.getEGLModelManager().putInfo(key, value);
		}
		
		// add the info for this at the end, to ensure that a getInfo cannot reply null in case the LRU cache needs
		// to be flushed. Might lead to performance issues.
		// see PR 1G2K5S7: ITPJCORE:ALL - NPE when accessing source for a binary type
		EGLModelManager.getEGLModelManager().putInfo(this, info);	
	}
	/**
	 * Close the buffer associated with this element, if any.
	 */
	protected void closeBuffer(OpenableElementInfo info) {
		if (!hasBuffer()) return; // nothing to do
		IBuffer buffer = null;
		buffer = getBufferManager().getBuffer(this);
		if (buffer != null) {
			buffer.close();
			buffer.removeBufferChangedListener(this);
		}
	}
	/**
	 * This element is being closed.  Do any necessary cleanup.
	 */
	protected void closing(Object info) throws EGLModelException {
		OpenableElementInfo openableInfo = (OpenableElementInfo) info;
		closeBuffer(openableInfo);
		super.closing(info);
	}
	/**
	 * Returns a new element info for this element.
	 */
	protected OpenableElementInfo createElementInfo() {
		return new OpenableElementInfo();
	}
	public boolean exists() {
		PackageFragmentRoot root = this.getPackageFragmentRoot();
		if (root == null || root == this ) {
			return parentExists() && resourceExists();
		} else if ( getElementType() == IEGLElement.PACKAGE_FRAGMENT && root.isArchive() ) {
			try {
				EglarPackageFragmentRootInfo info = (EglarPackageFragmentRootInfo)root.getElementInfo();
				return info.rawPackageInfo.containsKey(((PackageFragment)this).names);
			}
			catch (EGLModelException e) {
				return false;
			}
		}
		return super.exists();
	}	
	/**
	 *  Answers true if the parent exists (null parent is answering true)
	 * 
	 */
	protected boolean parentExists(){
	
		IEGLElement parent = this.getParent();
		if (parent == null) return true;
		return parent.exists();
	}

	/**
	 * Returns whether the corresponding resource or associated file exists
	 */
	protected boolean resourceExists() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) return false; // workaround for http://bugs.eclipse.org/bugs/show_bug.cgi?id=34069
		return 
			EGLModel.getTarget(
				workspace.getRoot(), 
				this.getPath().makeRelative(), // ensure path is relative (see http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
				true) != null;
	}

	/**
	 * Builds this element's structure and properties in the given
	 * info object, based on this element's current contents (reuse buffer
	 * contents if this element has an open buffer, or resource contents
	 * if this element does not have an open buffer). Children
	 * are placed in the given newElements table (note, this element
	 * has already been placed in the newElements table). Returns true
	 * if successful, or false if an error is encountered while determining
	 * the structure of this element.
	 */
	protected abstract boolean generateInfos(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws EGLModelException;

	/**
	 * Note: a buffer with no unsaved changes can be closed by the EGL Model
	 * since it has a finite number of buffers allowed open at one time. If this
	 * is the first time a request is being made for the buffer, an attempt is
	 * made to create and fill this element's buffer. If the buffer has been
	 * closed since it was first opened, the buffer is re-created.
	 * 
	 * @see IOpenable
	 */
	public IBuffer getBuffer() throws EGLModelException {
		if (hasBuffer()) {
			Object info = null;
			// ensure element is open
//			if (!isOpen()) {
				info = getElementInfo();
//			}
			IBuffer buffer = getBufferManager().getBuffer(this);
			if (buffer == null) {
				// try to (re)open a buffer
				buffer = openBuffer(null, info);
			}
			return buffer;
		} else {
			return null;
		}
	}

	/**
	 * Answers the buffer factory to use for creating new buffers
	 */
	public IBufferFactory getBufferFactory(){
		return getBufferManager().getDefaultBufferFactory();
	}
	/**
	 * Returns the buffer manager for this element.
	 */
	protected BufferManager getBufferManager() {
		return BufferManager.getDefaultBufferManager();
	}
	/**
	 * Return my underlying resource. Elements that may not have a
	 * corresponding resource must override this method.
	 *
	 * @see IEGLElement
	 */
	public IResource getCorrespondingResource() throws EGLModelException {
		return getUnderlyingResource();
	}
	/**
	 * @see IEGLElement
	 */
	public IResource getUnderlyingResource() throws EGLModelException {
		IResource parentResource = fParent.getUnderlyingResource();
		if (parentResource == null) {
			return null;
		}
		int type = parentResource.getType();
		if (type == IResource.FOLDER || type == IResource.PROJECT) {
			IContainer folder = (IContainer) parentResource;
			IResource resource = folder.findMember(fName);
			if (resource == null) {
				throw newNotPresentException();
			} else {
				return resource;
			}
		} else {
			return parentResource;
		}
	}	
	
	/**
	 * @see IParent 
	 */
	public boolean hasChildren() throws EGLModelException {
		return getChildren().length > 0;
	}
	/**
	 * Returns true if this element may have an associated source buffer,
	 * otherwise false. Subclasses must override as required.
	 */
	protected boolean hasBuffer() {
		return false;
	}
	
	/**
	 * @see IOpenable
	 */
	public boolean hasUnsavedChanges() throws EGLModelException{
	
		if (isReadOnly() || !isOpen()) {
			return false;
		}
		IBuffer buf = this.getBuffer();
		if (buf != null && buf.hasUnsavedChanges()) {
			return true;
		}
		// for package fragments, package fragment roots, and projects must check open buffers
		// to see if they have an child with unsaved changes
		if (fLEType == PACKAGE_FRAGMENT ||
			fLEType == PACKAGE_FRAGMENT_ROOT ||
			fLEType == EGL_PROJECT ||
			fLEType == EGL_MODEL) { 
			Enumeration openBuffers= getBufferManager().getOpenBuffers();
			while (openBuffers.hasMoreElements()) {
				IBuffer buffer= (IBuffer)openBuffers.nextElement();
				if (buffer.hasUnsavedChanges()) {
					IEGLElement owner= (IEGLElement)buffer.getOwner();
					if (isAncestorOf(owner)) {
						return true;
					}
				}
			}
		}
	
		return false;
	}

	/**
	 * Subclasses must override as required.
	 *
	 * @see IOpenable
	 */
	public boolean isConsistent() throws EGLModelException {
		return true;
	}

	/**
	 * 
	 * @see IOpenable
	 */
	public boolean isOpen() {
		synchronized(EGLModelManager.getEGLModelManager()){
			return EGLModelManager.getEGLModelManager().getInfo(this) != null;
		}
	}
	/**
	 * Returns true if this represents a source element.
	 * Openable source elements have an associated buffer created
	 * when they are opened.
	 */
	protected boolean isSourceElement() {
		return false;
	}
	/**
	 * @see IOpenable
	 */
	public void makeConsistent(IProgressMonitor pm) throws EGLModelException {
		if (!isConsistent()) {
			buildStructure((OpenableElementInfo)getElementInfo(), pm);
		}
	}
	/**
	 * @see IOpenable
	 */
	public void open(IProgressMonitor pm) throws EGLModelException {
		if (!isOpen()) {
			// TODO: need to synchronize (IOpenable.open(IProgressMonitor) is API
			// TODO: could use getElementInfo instead
			this.openWhenClosed(pm);
		}
	}
	/**
	 * 	Open the parent element if necessary
	 * 
	 */
	protected void openParent(IProgressMonitor pm) throws EGLModelException {

		Openable openableParent = (Openable)getOpenableParent();
		if (openableParent != null) {
			if (!openableParent.isOpen()){
				openableParent.openWhenClosed(pm);
			}
		}
	}
	/**
	 * Open an <code>Openable</code> that is known to be closed (no check for <code>isOpen()</code>).
	 */
	protected void openWhenClosed(IProgressMonitor pm) throws EGLModelException {
		try {
		
			if (EGLModelManager.VERBOSE){
				System.out.println("OPENING Element ("+ Thread.currentThread()+"): " + this.toStringWithAncestors()); //$NON-NLS-1$//$NON-NLS-2$
			}
		
			// 1) Parent must be open - open the parent if necessary
			openParent(pm);

			// 2) create the new element info and open a buffer if needed
			OpenableElementInfo info = createElementInfo();
			if (isSourceElement()) {
				this.openBuffer(pm);
			} 

			// 3) build the structure of the openable
			buildStructure(info, pm);

			// 4) anything special
			opening(info);
		
			if (EGLModelManager.VERBOSE) {
				System.out.println("-> Package cache size = " + EGLModelManager.getEGLModelManager().cache.pkgSize()); //$NON-NLS-1$
				System.out.println("-> Openable cache filling ratio = " + EGLModelManager.getEGLModelManager().cache.openableFillingRatio() + "%"); //$NON-NLS-1$//$NON-NLS-2$
			}

			// if any problems occuring openning the element, ensure that it's info
			// does not remain in the cache	(some elements, pre-cache their info
			// as they are being opened).
		} catch (EGLModelException e) {
			EGLModelManager.getEGLModelManager().removeInfo(this);
			throw e;
		}
	}
	/**
	 * Opens a buffer on the contents of this element, and returns
	 * the buffer, or returns <code>null</code> if opening fails.
	 * By default, do nothing - subclasses that have buffers
	 * must override as required.
	 */
	protected IBuffer openBuffer(IProgressMonitor pm) throws EGLModelException {
		return openBuffer(pm, null);
	}
	
	/**
	 * @see IOpenable
	 */
	public void save(IProgressMonitor pm, boolean force) throws EGLModelException {
		if (isReadOnly() || this.getResource().isReadOnly()) {
			throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
		}
		IBuffer buf = getBuffer();
		if (buf != null) { // some Openables (like a EGLProject) don't have a buffer
			buf.save(pm, force);
			this.makeConsistent(pm); // update the element info of this element
		}
	}
	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getOpenable()
	 */
	public IOpenable getOpenable() {
		return this;
	}
	/**
	 * Find enclosing package fragment root if any
	 */
	public PackageFragmentRoot getPackageFragmentRoot() {
		IEGLElement current = this;
		do {
			if (current instanceof PackageFragmentRoot) return (PackageFragmentRoot)current;
			current = current.getParent();
		} while(current != null);
		return null;
	}
	
	/**
	 * Opens a buffer on the contents of this element, and returns
	 * the buffer, or returns <code>null</code> if opening fails.
	 * By default, do nothing - subclasses that have buffers
	 * must override as required.
	 */
	protected IBuffer openBuffer(IProgressMonitor pm, Object info)  throws EGLModelException{
		return null;
	}

}
