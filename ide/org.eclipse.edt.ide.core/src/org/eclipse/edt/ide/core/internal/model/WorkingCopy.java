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
package org.eclipse.edt.ide.core.internal.model;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProblemRequestor;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;

/**
 * Implementation of a working copy compilation unit. A working
 * copy maintains the timestamp of the resource it was created
 * from.
 */

public class WorkingCopy extends EGLFile {

	/**
	 * If set, this is the factory that will be used to create the buffer.
	 */
	protected IBufferFactory bufferFactory;

	/**
	 * If set, this is the problem requestor which will be used to notify problems
	 * detected during reconciling.
	 */
	protected IProblemRequestor problemRequestor;
		
	/**
	 * A counter of the number of time clients have asked for this 
	 * working copy. It is set to 1, if the working
	 * copy is not managed. When destroyed, this counter is
	 * set to 0. Once destroyed, this working copy cannot be opened
	 * and non-handle info can not be accessed. This is
	 * never true if this egl file is not a working
	 * copy.
	 */
	protected int useCount = 1;
	
/**
 */
protected WorkingCopy(IPackageFragment parent, String name, IBufferFactory bufferFactory) {
	this(parent, name, bufferFactory, null);
}
/**
 */
protected WorkingCopy(IPackageFragment parent, String name, IBufferFactory bufferFactory, IProblemRequestor problemRequestor) {
	super(parent, name);
	this.bufferFactory = 
		bufferFactory == null ? 
			this.getBufferManager().getDefaultBufferFactory() :
			bufferFactory;
	this.problemRequestor = problemRequestor;
}
/**
 * @see IWorkingCopy
 */
public void commit(boolean force, IProgressMonitor monitor) throws EGLModelException {
	IEGLFile original = (IEGLFile)this.getOriginalElement();
	if (original.exists()) {
		CommitWorkingCopyOperation op= new CommitWorkingCopyOperation(this, force);
		op.runOperation(monitor);
	} else {
		String encoding = this.getEGLProject().getOption(EGLCore.CORE_ENCODING, true);
		String contents = this.getSource();
		if (contents == null) return;
		try {
			byte[] bytes = encoding == null 
				? contents.getBytes() 
				: contents.getBytes(encoding);
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			IFile originalRes = (IFile)original.getResource();
			if (originalRes.exists()) {
				originalRes.setContents(
					stream, 
					force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
					null);
			} else {
				originalRes.create(
					stream,
					force,
					monitor);
			}
		} catch (CoreException e) {
			throw new EGLModelException(e);
		} catch (UnsupportedEncodingException e) {
			throw new EGLModelException(e, IEGLModelStatusConstants.IO_EXCEPTION);
		}
	}
}
/**
 * Returns a new element info for this element.
 */
protected OpenableElementInfo createElementInfo() {
	return new WorkingCopyElementInfo();
}
/**
 * @see IWorkingCopy
 */
public void destroy() {
	if (--this.useCount > 0) {
		if (SHARED_WC_VERBOSE) {
			System.out.println("Decrementing use count of shared working copy " + this.toStringWithAncestors());//$NON-NLS-1$
		}
		return;
	}
	try {
		DestroyWorkingCopyOperation op = new DestroyWorkingCopyOperation(this);
		op.runOperation(null);
	} catch (EGLModelException e) {
		// do nothing
	}
}

public boolean exists() {
	// working copy always exists in the model until it is detroyed
	return this.useCount != 0;
}


/**
 * Answers custom buffer factory
 */
public IBufferFactory getBufferFactory(){

	return this.bufferFactory;
}

/**
 * Working copies must be identical to be equal.
 *
 * @see Object#equals
 */
public boolean equals(Object o) {
	return this == o; 
}

	/**
	 * Returns the info for this handle.  
	 * If this element is not already open, it and all of its parents are opened.
	 * Does not return null.
	 * NOTE: BinaryType infos are NJOT rooted under EGLElementInfo.
	 * @exception EGLModelException if the element is not present or not accessible
	 */
	public Object getElementInfo() throws EGLModelException {

		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		boolean shouldPerformProblemDetection = false;
		synchronized(manager){
			Object info = manager.getInfo(this);
			if (info == null) {
				shouldPerformProblemDetection = true;
			}
		}
		Object info = super.getElementInfo(); // will populate if necessary

		// perform problem detection outside the EGLModelManager lock
		/*
		if (this.problemRequestor != null && shouldPerformProblemDetection && this.problemRequestor.isActive()){
			this.problemRequestor.beginReporting();
			EGLFileProblemFinder.process(this, this.problemRequestor, null); 
			this.problemRequestor.endReporting();
		}
		*/		
		return info;
	}
/**
 * @see IWorkingCopy
 */
public IEGLElement getOriginal(IEGLElement workingCopyElement) {
	//not a element contained in a compilation unit
	int eglElementType = workingCopyElement.getElementType();
	if (eglElementType < EGL_FILE || eglElementType == CLASS_FILE) {
		return null;
	}
	/*
	if (workingCopyElement instanceof BinaryMember) {
		return null;
	}
	*/
	IEGLElement parent = workingCopyElement.getParent();
	ArrayList hierarchy = new ArrayList(4);
	
	while (parent.getElementType() > EGL_FILE) {
		hierarchy.add(parent);
		parent = parent.getParent();
	}
	if (parent.getElementType() == EGL_FILE) {
		hierarchy.add(((IEGLFile)parent).getOriginalElement());
	}
	
	IEGLFile cu = (IEGLFile) getOriginalElement();
	if (eglElementType == EGL_FILE) {
		parent = workingCopyElement;
	}
	if (((IEGLFile) parent).isWorkingCopy() && !((IEGLFile) parent).getOriginalElement().equals(cu)) {
		return null;
	}
	switch (eglElementType) {
		case PACKAGE_DECLARATION :
			return cu.getPackageDeclaration(workingCopyElement.getElementName());
		case IMPORT_CONTAINER :
			return cu.getImportContainer();
		case IMPORT_DECLARATION :
			return cu.getImport(workingCopyElement.getElementName());
		case PART :
			return cu.getPart(workingCopyElement.getElementName());
			/*
			if (hierarchy.size() == 1) {
				return cu.getPart(workingCopyElement.getElementName());
			} else {
				//inner type
				return getOriginalType(hierarchy).getPart(workingCopyElement.getElementName());
			}
			
		case FUNCTION :
			IPart type;
			if (hierarchy.size() == 2) {
				String typeName = ((IEGLElement) hierarchy.get(0)).getElementName();
				type = cu.getFunction(typeName);
			} else {
				//inner type
				type = getOriginalType(hierarchy);
			}
			return type.getMethod(workingCopyElement.getElementName(), ((IFunction) workingCopyElement).getParameterTypes());
		case FIELD :
			if (hierarchy.size() == 2) {
				String typeName = ((IEGLElement) hierarchy.get(0)).getElementName();
				type = cu.getField(typeName);
			} else {
				//inner type
				type = getOriginalType(hierarchy);
			}
			return type.getField(workingCopyElement.getElementName());
			
		/*
		case INITIALIZER :
			if (hierarchy.size() == 2) {
				String typeName = ((IEGLElement) hierarchy.get(0)).getElementName();
				type = cu.getType(typeName);
			} else {
				//inner type
				type = getOriginalType(hierarchy);
			}
			return type.getInitializer(((Initializer) workingCopyElement).getOccurrenceCount());
		*/
		case EGL_FILE :
			return cu;
		default :
			return null;
	}
}
/**
 * @see IWorkingCopy
 */
public IEGLElement getOriginalElement() {
	return new EGLFile((IPackageFragment)getParent(), getElementName());
}
protected IPart getOriginalType(ArrayList hierarchy) {
	int size = hierarchy.size() - 1;
	IEGLFile typeCU = (IEGLFile) hierarchy.get(size);
	String typeName = ((IEGLElement) hierarchy.get(size - 1)).getElementName();
	IPart type = typeCU.getPart(typeName);
	size= size - 2;
	while (size > -1) {
		typeName = ((IEGLElement) hierarchy.get(size)).getElementName();
		type = ((IPart) type).getPart(typeName);
		size--;
	}
	return type;
}

/*
 * @see IEGLElement
 */
public IResource getResource() {
	return null;
}

/**
 * @see IWorkingCopy
 */
public IEGLElement getSharedWorkingCopy(IProgressMonitor monitor, IBufferFactory factory, IProblemRequestor problemRequestor) throws EGLModelException {
	return this;
}
/**
 * Returns <code>null<code> - a working copy does not have an underlying resource.
 *
 * @see IEGLElement
 */
public IResource getUnderlyingResource() throws EGLModelException {
	return null;
}
/**
 * @see IWorkingCopy
 */
public IEGLElement getWorkingCopy() throws EGLModelException {
	return this;
}
/**
 * @see IWorkingCopy
 */
public IEGLElement getWorkingCopy(IProgressMonitor monitor, IBufferFactory factory, IProblemRequestor problemRequestor) throws EGLModelException {
	return this;
}
/**
 * @see IWorkingCopy
 */
public boolean isBasedOn(IResource resource) {
	if (resource.getType() != IResource.FILE) {
		return false;
	}
	if (this.useCount == 0) {
		return false;
	}
	try {
		// if resource got deleted, then #getModificationStamp() will answer IResource.NULL_STAMP, which is always different from the cached
		// timestamp
		return ((EGLFileElementInfo) getElementInfo()).fTimestamp == ((IFile) resource).getModificationStamp();
	} catch (EGLModelException e) {
		return false;
	}
}
/**
 * @see IWorkingCopy
 */
public boolean isWorkingCopy() {
	return true;
}

/**
 * @see IOpenable#makeConsistent(IProgressMonitor)
 */
public void makeConsistent(IProgressMonitor monitor) throws EGLModelException {
	if (!isConsistent()) { // TODO: this code isn't synchronized with regular opening of a working copy (should use getElementInfo)
		super.makeConsistent(monitor);

		if (monitor != null && monitor.isCanceled()) return;
		if (this.problemRequestor != null && this.problemRequestor.isActive()){
			this.problemRequestor.beginReporting();
			EGLFileProblemFinder.process(this, this.problemRequestor, monitor); 
			this.problemRequestor.endReporting();
		}		
	}
}

/**
 * @see IOpenable
 * @see IWorkingCopy
 *
 * @exception EGLModelException attempting to open a read only element for something other than navigation
 * 	or if this is a working copy being opened after it has been destroyed.
 */
public void open(IProgressMonitor monitor) throws EGLModelException {
	if (this.useCount == 0) { // was destroyed
		throw newNotPresentException();
	} else {
		super.open(monitor);
		
		if (monitor != null && monitor.isCanceled()) return;
		if (this.problemRequestor != null && this.problemRequestor.isActive()){
			this.problemRequestor.beginReporting();
			EGLFileProblemFinder.process(this, this.problemRequestor, monitor); 
			this.problemRequestor.endReporting();
		}		
	}
}
/**
 * @see Openable
 */
protected IBuffer openBuffer(IProgressMonitor pm,  Object info) throws EGLModelException {

	if (this.useCount == 0) throw newNotPresentException(); // was destroyed
	
	// create buffer - working copies may use custom buffer factory
	IBuffer buffer = getBufferFactory().createBuffer(this);
	if (buffer == null) return null;

	// set the buffer source if needed
	if (buffer.getCharacters() == null) {
		IEGLFile original = (IEGLFile)this.getOriginalElement();
		if (original.isOpen()) {
			buffer.setContents(original.getSource());
		} else {
			IFile file = (IFile)original.getResource();
			if (file == null || !file.exists()) {
				// initialize buffer with empty contents
				buffer.setContents(CharOperation.NO_CHAR);
			} else {
				buffer.setContents(Util.getResourceContentsAsCharArray(file));
			}
		}
	}

	// add buffer to buffer cache
	this.getBufferManager().addBuffer(buffer);

	// listen to buffer changes
	buffer.addBufferChangedListener(this);

	return buffer;	
}
/*
 * @see Openable#openParent(IProgressMonitor)
 */
/*
protected void openParent(IProgressMonitor pm) throws EGLModelException {
	if (FIX_BUG25184) {
		try {
			super.openParent(pm);
		} catch(EGLModelException e){
			// allow parent to not exist for working copies defined outside classpath
			if (!e.isDoesNotExist()){ 
				throw e;
			}
		}
	} else {
		super.openParent(pm);
	}
}
*/
/**
 * @see IWorkingCopy
 */ 
public IMarker[] reconcile() throws EGLModelException {
	reconcile(false, null);
	return null;
}

/**
 * @see IWorkingCopy
 */ 
public void reconcile(boolean forceProblemDetection, IProgressMonitor monitor) throws EGLModelException {
	ReconcileWorkingCopyOperation op = new ReconcileWorkingCopyOperation(this, forceProblemDetection);
	op.runOperation(monitor);
}

/**
 * @see IWorkingCopy
 */
public void restore() throws EGLModelException {

	if (this.useCount == 0) throw newNotPresentException(); //was destroyed

	EGLFile original = (EGLFile) getOriginalElement();
	IBuffer buffer = this.getBuffer();
	if (buffer == null) return;
	buffer.setContents(original.getContents());
	updateTimeStamp(original);
	makeConsistent(null);
}
/*
 * @see EGLElement#rootedAt(IEGLProject)
 */
public IEGLElement rootedAt(IEGLProject project) {
	return
		new WorkingCopy(
			(IPackageFragment)((EGLElement)fParent).rootedAt(project), 
			fName,
			this.bufferFactory);

}
/**
 * @see IOpenable
 */
public void save(IProgressMonitor pm, boolean force) throws EGLModelException {
	if (isReadOnly()) {
		throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
	}
	// no need to save the buffer for a working copy (this is a noop)
	//IBuffer buf = getBuffer();
	//if (buf != null) { // some Openables (like a EGLProject) don't have a buffer
	//	buf.save(pm, force);
		this.reconcile();   // not simply makeConsistent, also computes fine-grain deltas
							// in case the working copy is being reconciled already (if not it would miss
							// one iteration of deltas).
	//}
}

/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	buffer.append("[Working copy] "); //$NON-NLS-1$
	super.toStringInfo(0, buffer, info);
}
protected void updateTimeStamp(EGLFile original) throws EGLModelException {
	long timeStamp =
		((IFile) original.getResource()).getModificationStamp();
	if (timeStamp == IResource.NULL_STAMP) {
		throw new EGLModelException(
			new EGLModelStatus(IEGLModelStatusConstants.INVALID_RESOURCE));
	}
	((EGLFileElementInfo) getElementInfo()).fTimestamp = timeStamp;
}
}
