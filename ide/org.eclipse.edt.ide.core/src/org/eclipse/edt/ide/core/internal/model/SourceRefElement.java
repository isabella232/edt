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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.ISourceManipulation;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.ISourceReference;


/**
 * Abstract class for EGL elements which implement ISourceReference.
 */
/* package */ abstract class SourceRefElement extends EGLElement implements ISourceReference {
protected SourceRefElement(int type, IEGLElement parent, String name) {
	super(type, parent, name);
}
/**
 * @see ISourceManipulation
 */
public void copy(IEGLElement container, IEGLElement sibling, String rename, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (container == null) {
		throw new IllegalArgumentException(EGLModelResources.operationNullContainer);
	}
	IEGLElement[] elements= new IEGLElement[] {this};
	IEGLElement[] containers= new IEGLElement[] {container};
	IEGLElement[] siblings= null;
	if (sibling != null) {
		siblings= new IEGLElement[] {sibling};
	}
	String[] renamings= null;
	if (rename != null) {
		renamings= new String[] {rename};
	}
	getEGLModel().copy(elements, containers, siblings, renamings, force, monitor);
}
/**
 * @see ISourceManipulation
 */
public void delete(boolean force, IProgressMonitor monitor) throws EGLModelException {
	IEGLElement[] elements = new IEGLElement[] {this};
	getEGLModel().delete(elements, force, monitor);
}
/**
 * @see IMember
 */
public IEGLFile getEGLFile() {
	return ((EGLElement)getParent()).getEGLFile();
}
/**
 * Elements within compilation units and class files have no
 * corresponding resource.
 *
 * @see IEGLElement
 */
public IResource getCorrespondingResource() throws EGLModelException {
	if (!exists()) throw newNotPresentException();
	return null;
}
/**
 * Return the first instance of IOpenable in the hierarchy of this
 * type (going up the hierarchy from this type);
 */
public IOpenable getOpenableParent() {
	IEGLElement current = getParent();
	while (current != null){
		if (current instanceof IOpenable){
			return (IOpenable) current;
		}
		current = current.getParent();
	}
	return null;
}
/*
 * @see IEGLElement
 */
public IPath getPath() {
	return this.getParent().getPath();
}
/*
 * @see IEGLElement
 */
public IResource getResource() {
	return this.getParent().getResource();
}
/**
 * @see ISourceReference
 */
public String getSource() throws EGLModelException {
	IOpenable openable = getOpenableParent();
	IBuffer buffer = openable.getBuffer();
	if (buffer == null) {
		return null;
	}
	ISourceRange range = getSourceRange();
	int offset = range.getOffset();
	int length = range.getLength();
	if (offset == -1 || length == 0 ) {
		return null;
	}
	return buffer.getText(offset, length);
}
/**
 * @see ISourceReference
 */
public ISourceRange getSourceRange() throws EGLModelException {
	SourceRefElementInfo info = (SourceRefElementInfo) getElementInfo();
	return info.getSourceRange();
}
/**
 * @see IEGLElement
 */
public IResource getUnderlyingResource() throws EGLModelException {
	if (!exists()) throw newNotPresentException();
	return getParent().getUnderlyingResource();
}
/**
 * @see ISourceManipulation
 */
public void move(IEGLElement container, IEGLElement sibling, String rename, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (container == null) {
		throw new IllegalArgumentException(EGLModelResources.operationNullContainer);
	}
	IEGLElement[] elements= new IEGLElement[] {this};
	IEGLElement[] containers= new IEGLElement[] {container};
	IEGLElement[] siblings= null;
	if (sibling != null) {
		siblings= new IEGLElement[] {sibling};
	}
	String[] renamings= null;
	if (rename != null) {
		renamings= new String[] {rename};
	}
	getEGLModel().move(elements, containers, siblings, renamings, force, monitor);
}
/**
 * @see ISourceManipulation
 */
public void rename(String name, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (name == null) {
		throw new IllegalArgumentException(EGLModelResources.elementNullName);
	}
	IEGLElement[] elements= new IEGLElement[] {this};
	IEGLElement[] dests= new IEGLElement[] {this.getParent()};
	String[] renamings= new String[] {name};
	getEGLModel().rename(elements, dests, renamings, force, monitor);
}
/*
 * @see EGLElement#rootedAt(IEGLProject)
 */
public IEGLElement rootedAt(IEGLProject project) {
	// not needed
	return null;
}
}
