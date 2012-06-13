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

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.ISourceReference;


/**
 * @author twilson
 * created	Jul 24, 2003
 */
public abstract class EGLElement extends PlatformObject implements IEGLElement {

	public static final char EGLM_EGLPROJECT= '=';
	public static final char EGLM_PACKAGEFRAGMENTROOT= Path.SEPARATOR;
	public static final char EGLM_PACKAGEFRAGMENT= '<';
	public static final char EGLM_FIELD= '^';
	public static final char EGLM_FUNCTION= '~';
	public static final char EGLM_INITIALIZER= '|';
	public static final char EGLM_EGLFILE= '{';
	public static final char EGLM_CLASSFILE= '(';
	public static final char EGLM_PART= '[';
	public static final char EGLM_PACKAGEDECLARATION= '%';
	public static final char EGLM_IMPORTDECLARATION= '\'';
	public static final char EGLM_PROPERTYBLOCK= '!';
	public static final char EGLM_PROPERTY= '>';
	public static final char EGLM_USE= '*';
	
	protected static final EGLElement[] NO_ELEMENTS = new EGLElement[0];
	/**
	 * A count to uniquely identify this element in the case
	 * that a duplicate named element exists. For example, if
	 * there are two fields in a compilation unit with the
	 * same name, the occurrence count is used to distinguish
	 * them.  The occurrence count starts at 1 (thus the first 
	 * occurrence is occurrence 1, not occurrence 0).
	 */
	protected int fOccurrenceCount = 1;


	/**
	 * This element's type - one of the constants defined
	 * in IEGLLanguageElementTypes.
	 */
	protected int fLEType = 0;

	/**
	 * This element's parent, or <code>null</code> if this
	 * element does not have a parent.
	 */
	protected IEGLElement fParent;

	/**
	 * This element's name, or an empty <code>String</code> if this
	 * element does not have a name.
	 */
	protected String fName;

	protected static final Object NO_INFO = new Object();
	
	/**
	 * Constructs a handle for a java element of the specified type, with
	 * the given parent element and name.
	 *
	 * @param type - one of the constants defined in IEGLLanguageElement
	 *
	 * @exception IllegalArgumentException if the type is not one of the valid
	 *		EGL element type constants
	 *
	 */
	protected EGLElement(int type, IEGLElement parent, String name) throws IllegalArgumentException {
		if (type < EGL_MODEL || type > PROPERTY) {
			throw new IllegalArgumentException(EGLModelResources.elementInvalidType);
		}
		fLEType= type;
		fParent= parent;
		fName= name;
	}
	/**
	 * @see IOpenable
	 */
	public void close() throws EGLModelException {
		Object info = EGLModelManager.getEGLModelManager().peekAtInfo(this);
		if (info != null) {
			boolean wasVerbose = false;
			try {
				if (EGLModelManager.VERBOSE) {
					System.out.println("CLOSING Element ("+ Thread.currentThread()+"): " + this.toStringWithAncestors());  //$NON-NLS-1$//$NON-NLS-2$
					wasVerbose = true;
					EGLModelManager.VERBOSE = false;
				}
				if (this instanceof IParent) {
					IEGLElement[] children = ((EGLElementInfo) info).getChildren();
					for (int i = 0, size = children.length; i < size; ++i) {
						EGLElement child = (EGLElement) children[i];
						child.close();
					}
				}
				closing(info);
				EGLModelManager.getEGLModelManager().removeInfo(this);
				if (wasVerbose) {
					System.out.println("-> Package cache size = " + EGLModelManager.getEGLModelManager().cache.pkgSize()); //$NON-NLS-1$
					System.out.println("-> Openable cache filling ratio = " + EGLModelManager.getEGLModelManager().cache.openableFillingRatio() + "%"); //$NON-NLS-1$//$NON-NLS-2$
				}
			} finally {
				EGLModelManager.VERBOSE = wasVerbose;
			}
		}
	}
	/**
	 * This element is being closed.  Do any necessary cleanup.
	 */
	protected void closing(Object info) throws EGLModelException {
	}	/**
	 * Returns true if this handle represents the same EGL element
	 * as the given handle. By default, two handles represent the same
	 * element if they are identical or if they represent the same type
	 * of element, have equal names, parents, and occurrence counts.
	 *
	 * <p>If a subclass has other requirements for equality, this method
	 * must be overridden.
	 *
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		
		if (this == o) return true;
	
		// EGL model parent is null
		if (fParent == null) return super.equals(o);
	
		if (o instanceof EGLElement) {
			EGLElement other = (EGLElement) o;
			if (fLEType != other.fLEType) return false;
			
			return fOccurrenceCount == other.fOccurrenceCount && 
			       fName.equals(other.fName) &&
				   fParent.equals(other.fParent);
		}
		return false;
	}
	
	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#exists()
	 */
	public boolean exists() {
		
		try {
			getElementInfo();
			return true;
		} catch (EGLModelException e) {
		}
		return false;
	}

	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getAncestor(int)
	 */
	public IEGLElement getAncestor(int ancestorType) {
		IEGLElement element = this;
		while (element != null) {
			if (element.getElementType() == ancestorType)  return element;
			element= element.getParent();
		}
		return null;				
	}

	/**
	 * @see IParent 
	 */
	public IEGLElement[] getChildren() throws EGLModelException {
		return ((EGLElementInfo)getElementInfo()).getChildren();
	}
	/**
	 * Returns a collection of (immediate) children of this node of the
	 * specified type.
	 *
	 * @param type - one of constants defined by IEGLLanguageElementTypes
	 */
	public ArrayList getChildrenOfType(int type) throws EGLModelException {
		IEGLElement[] children = getChildren();
		int size = children.length;
		ArrayList list = new ArrayList(size);
		for (int i = 0; i < size; ++i) {
			EGLElement elt = (EGLElement)children[i];
			if (elt.getElementType() == type) {
				list.add(elt);
			}
		}
		return list;
	}
	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getCorrespondingResource()
	 */
	abstract public IResource getCorrespondingResource() throws EGLModelException; 

	/**
	 * Returns the info for this handle.  
	 * If this element is not already open, it and all of its parents are opened.
	 * Does not return null.
	 * @exception EGLModelException if the element is not present or not accessible
	 */
	public Object getElementInfo() throws EGLModelException {

		// workaround to ensure parent project resolved classpath is available to avoid triggering initializers
		// while the EGLModelManager lock is acquired (can cause deadlocks in clients)
		IEGLProject project = getEGLProject();
		if (project != null && !project.isOpen()) {
			// TODO: need to revisit, since deadlock could still occur if perProjectInfo is removed concurrent before entering the lock
			try {
				project.getResolvedEGLPath(true); // trigger all possible container/variable initialization outside the model lock
			} catch (EGLModelException e) {
				// project is not accessible or is not a java project
			}
		}

		// element info creation is done inside a lock on the EGLModelManager
		EGLModelManager manager;
		synchronized(manager = EGLModelManager.getEGLModelManager()){
			Object info = manager.getInfo(this);
			if (info == null) {
				openHierarchy();
				info= manager.getInfo(this);
				if (info == null) {
					throw newNotPresentException();
				}
			}
			return info;
		}
	}
	/**
	 * @see IAdaptable
	 */
	public String getElementName() {
		return fName;
	}
	/**
	 * @see IEGLElement
	 */
	public int getElementType() {
		return fLEType;
	}
	/**
	 * @see IEGLElement
	 */
	public String getHandleIdentifier() {
		return getHandleMemento();
	}
	/**
	 * @see EGLElement#getHandleMemento()
	 */
	public String getHandleMemento(){
		StringBuffer buff= new StringBuffer(((EGLElement)getParent()).getHandleMemento());
		buff.append(getHandleMementoDelimiter());
		buff.append(getElementName());
		return buff.toString();
	}

	/**
	 * @see IMember
	 */
	public IEGLFile getEGLFile() {
		return null;
	}
	
	/**
	 * @see IMember
	 */
	public IClassFile getClassFile() {
		return null;
	}
	
	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	protected abstract char getHandleMementoDelimiter();
	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getEGLModel()
	 */
	public IEGLModel getEGLModel() {
		IEGLElement current = this;
		do {
			if (current instanceof IEGLModel) return (IEGLModel) current;
		} while ((current = current.getParent()) != null);
		return null;
	}

	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getEGLProject()
	 */
	public IEGLProject getEGLProject() {
		IEGLElement current = this;
		do {
			if (current instanceof IEGLProject) return (IEGLProject) current;
		} while ((current = current.getParent()) != null);
		return null;
	}

	/**
	 * Returns the occurrence count of the handle.
	 */
	protected int getOccurrenceCount() {
		return fOccurrenceCount;
	}

	/**
	 * @see IEGLElement
	 */
	public IEGLElement getParent() {
		return fParent;
	}

	/*
	 * @see IEGLElement
	 */
	public IOpenable getOpenable() {
		return this.getOpenableParent();	
	}
	/**
	 * Return the first instance of IOpenable in the parent
	 * hierarchy of this element.
	 *
	 * <p>Subclasses that are not IOpenable's must override this method.
	 */
	public IOpenable getOpenableParent() {
		
		return (IOpenable)fParent;
	}
	/**
	 * Returns the element that is located at the given source position
	 * in this element.  This is a helper method for <code>ICompilationUnit#getElementAt</code>,
	 * and only works on compilation units and types. The position given is
	 * known to be within this element's source range already, and if no finer
	 * grained element is found at the position, this element is returned.
	 */
	protected IEGLElement getSourceElementAt(int position) throws EGLModelException {
		if (this instanceof ISourceReference) {
			IEGLElement[] children = getChildren();
			int i;
			for (i = 0; i < children.length; i++) {
				IEGLElement aChild = children[i];
				if (aChild instanceof SourceRefElement) {
					SourceRefElement child = (SourceRefElement) children[i];
					ISourceRange range = child.getSourceRange();
					if (position < range.getOffset() + range.getLength() && position >= range.getOffset()) {
						if (child instanceof IParent) {
							return child.getSourceElementAt(position);
						} else {
							return child;
						}
					}
				}
			}
		} else {
			// should not happen
			Assert.isTrue(false);
		}
		return this;
	}
	/**
	 * Returns the hash code for this EGL element. By default,
	 * the hash code for an element is a combination of its name
	 * and parent's hash code. Elements with other requirements must
	 * override this method.
	 */
	public int hashCode() {
		if (fParent == null) return super.hashCode();
		return Util.combineHashCodes(fName.hashCode(), fParent.hashCode());
	}
	/**
	 * Returns true if this element is an ancestor of the given element,
	 * otherwise false.
	 */
	protected boolean isAncestorOf(IEGLElement e) {
		IEGLElement parent= e.getParent();
		while (parent != null && !parent.equals(this)) {
			parent= parent.getParent();
		}
		return parent != null;
	}
	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getPath()
	 */
	abstract public IPath getPath(); 

	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getResource()
	 */
	abstract public IResource getResource(); 
	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#getUnderlyingResource()
	 */
	abstract public IResource getUnderlyingResource() throws EGLModelException; 

	/* (non-EGLdoc)
	 * @see com.ibm.etools.egl.internal.model.core.IEGLElement#isReadOnly()
	 */
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * @see IEGLElement
	 */
	public boolean isStructureKnown() throws EGLModelException {
		return ((EGLElementInfo)getElementInfo()).isStructureKnown();
	}
	/**
	 * Creates and returns and not present exception for this element.
	 */
	protected EGLModelException newNotPresentException() {
		return new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, this));
	}
	/**
	 * Opens this element and all parents that are not already open.
	 *
	 * @exception EGLModelException this element is not present or accessible
	 */
	protected void openHierarchy() throws EGLModelException {
		if (this instanceof IOpenable) {
			((Openable) this).openWhenClosed(null);
		} else {
			Openable openableParent = (Openable)getOpenableParent();
			if (openableParent != null) {
				EGLElementInfo openableParentInfo = (EGLElementInfo) EGLModelManager.getEGLModelManager().getInfo((IEGLElement) openableParent);
				if (openableParentInfo == null) {
					openableParent.openWhenClosed(null);
				} else {
					throw newNotPresentException();
				}
			}
		}
	}
	/**
	 * This element has just been opened.  Do any necessary setup.
	 */
	protected void opening(Object info) {
	}
	/**
	 */
	public String readableName() {
		return this.getElementName();
	}
	/**
	 * Removes all cached info from the EGL Model, including all children,
	 * but does not close this element.
	 */
	protected void removeInfo() {
		Object info = EGLModelManager.getEGLModelManager().peekAtInfo(this);
		if (info != null) {
			if (this instanceof IParent) {
				IEGLElement[] children = ((EGLElementInfo)info).getChildren();
				for (int i = 0, size = children.length; i < size; ++i) {
					EGLElement child = (EGLElement) children[i];
					child.removeInfo();
				}
			}
			EGLModelManager.getEGLModelManager().removeInfo(this);
		}
	}
	/**
	 * Sets the occurrence count of the handle.
	 */
	protected void setOccurrenceCount(int count) {
		fOccurrenceCount = count;
	}
	protected String tabString(int tab) {
		StringBuffer buffer = new StringBuffer();
		for (int i = tab; i > 0; i--)
			buffer.append("  "); //$NON-NLS-1$
		return buffer.toString();
	}
	/**
	 * Debugging purposes
	 */
	public String toDebugString() {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO);
		return buffer.toString();
	}
	/**
	 *  Debugging purposes
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(0, buffer);
		return buffer.toString();
	}
	/**
	 *  Debugging purposes
	 */
	protected void toString(int tab, StringBuffer buffer) {
		Object info = this.toStringInfo(tab, buffer);
		if (tab == 0) {
			this.toStringAncestors(buffer);
		}
		this.toStringChildren(tab, buffer, info);
	}
	/**
	 *  Debugging purposes
	 */
	public String toStringWithAncestors() {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO);
		this.toStringAncestors(buffer);
		return buffer.toString();
	}
	/**
	 *  Debugging purposes
	 */
	protected void toStringAncestors(StringBuffer buffer) {
		EGLElement parent = (EGLElement)this.getParent();
		if (parent != null && parent.getParent() != null) {
			buffer.append(" [in "); //$NON-NLS-1$
			parent.toStringInfo(0, buffer, NO_INFO);
			parent.toStringAncestors(buffer);
			buffer.append("]"); //$NON-NLS-1$
		}
	}
	/**
	 *  Debugging purposes
	 */
	protected void toStringChildren(int tab, StringBuffer buffer, Object info) {
		if (info == null || !(info instanceof EGLElementInfo)) return;
		IEGLElement[] children = ((EGLElementInfo)info).getChildren();
		for (int i = 0; i < children.length; i++) {
			buffer.append("\n"); //$NON-NLS-1$
			((EGLElement)children[i]).toString(tab + 1, buffer);
		}
	}
	/**
	 *  Debugging purposes
	 */
	public Object toStringInfo(int tab, StringBuffer buffer) {
		Object info = EGLModelManager.getEGLModelManager().peekAtInfo(this);
		this.toStringInfo(tab, buffer, info);
		return info;
	}
	/**
	 *  Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
		buffer.append(this.tabString(tab));
		buffer.append(getElementName());
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}
	/**
	 * Returns a copy of this element rooted at the given project.
	 */
	public abstract IEGLElement rootedAt(IEGLProject project);
	
	/**
	 * Returns the SourceMapper facility for this element, or
	 * <code>null</code> if this element does not have a
	 * SourceMapper.
	 */
	public SourceMapper getSourceMapper() {
		return ((EGLElement)getParent()).getSourceMapper();
	}

}
