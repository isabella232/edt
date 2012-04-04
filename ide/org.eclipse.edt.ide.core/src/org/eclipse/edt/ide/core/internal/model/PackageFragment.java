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
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.ISourceManipulation;
import org.eclipse.edt.ide.core.model.Signature;

/**
 * @see IPackageFragment
 */
public class PackageFragment extends Openable implements IPackageFragment {
	/**
	 * Constant empty list of compilation units
	 */
	protected static IEGLFile[] fgEmptyEGLFileList= new IEGLFile[] {};
	protected static IPart[] fEmptyPartList = new IPart[] {};
	public String[] names;
/**
 * Constructs a handle for a package fragment
 *
 * @see IPackageFragment
 */
protected PackageFragment(IPackageFragmentRoot root, String name) {
	super(PACKAGE_FRAGMENT, root, name);
	this.names = new String[]{name};
}

protected PackageFragment(PackageFragmentRoot root, String[] names) {
	super(PACKAGE_FRAGMENT, root, null);
	this.names = names;
	this.fName = Util.concatWith(this.names, '.');
}

/**
 * Compute the children of this package fragment.
 *
 * <p>Package fragments which are folders recognize files based on the
 * type of the fragment
 */
protected boolean computeChildren(OpenableElementInfo info, IResource resource) throws EGLModelException {
	ArrayList vChildren = new ArrayList();
	int kind = getKind();
	String extType;
	extType = "egl"; //$NON-NLS-1$
	try {
		char[][] exclusionPatterns = ((PackageFragmentRoot)getPackageFragmentRoot()).fullExclusionPatternChars();
		IResource[] members = ((IContainer) resource).members();
		for (int i = 0, max = members.length; i < max; i++) {
			IResource child = members[i];
			if (child.getType() != IResource.FOLDER
					&& !Util.isExcluded(child, exclusionPatterns)) {
				String extension = child.getProjectRelativePath().getFileExtension();
				if (extension != null) {
					if (extension.equalsIgnoreCase(extType)) {
						IEGLElement childElement;
						if (kind == IPackageFragmentRoot.K_SOURCE && Util.isValidEGLFileName(child.getName())) {
							childElement = getEGLFile(child.getName());
							vChildren.add(childElement);
						} 
						// TODO handle when we can access binary files 
						/*
						else if (Util.isValidClassFileName(child.getName())) {
							childElement = getClassFile(child.getName());
							vChildren.add(childElement);
						}
						*/
					}
				}
			}
		}
	} catch (CoreException e) {
		throw new EGLModelException(e);
	}
	IEGLElement[] children = new IEGLElement[vChildren.size()];
	vChildren.toArray(children);
	info.setChildren(children);
	return true;
}
/**
 * Returns true if this fragment contains at least one java resource.
 * Returns false otherwise.
 */
public boolean containsEGLResources() throws EGLModelException {
	return ((PackageFragmentInfo) getElementInfo()).containsEGLResources();
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
 * @see IPackageFragment
 */
public IEGLFile createEGLFile(String name, String contents, boolean force, IProgressMonitor monitor) throws EGLModelException {
	CreateEGLFileOperation op= new CreateEGLFileOperation(this, name, contents, force);
	op.runOperation(monitor);
	return getEGLFile(name);
}
/**
 * @see EGLElement
 */
protected OpenableElementInfo createElementInfo() {
	return new PackageFragmentInfo();
}
/**
 * @see ISourceManipulation
 */
public void delete(boolean force, IProgressMonitor monitor) throws EGLModelException {
	IEGLElement[] elements = new IEGLElement[] {this};
	getEGLModel().delete(elements, force, monitor);
}
/**
 * @see Openable
 */
protected boolean generateInfos(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws EGLModelException {
	
	return computeChildren(info, underlyingResource);
}
/**
 * @see IPackageFragment#getEGLFile(String)
 */
public IEGLFile getEGLFile(String name) {
	return new EGLFile(this, name);
}
/**
 * @see IPackageFragment#getEGLFiles()
 */
public IEGLFile[] getEGLFiles() throws EGLModelException {
	if (getKind() == IPackageFragmentRoot.K_BINARY) {
		return fgEmptyEGLFileList;
	}
	
	ArrayList list = getChildrenOfType(EGL_FILE);
	IEGLFile[] array= new IEGLFile[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see EGLElement#getHandleMementoDelimiter()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_PACKAGEFRAGMENT;
}
/**
 * @see IPackageFragment#getKind()
 */
public int getKind() throws EGLModelException {
	return ((IPackageFragmentRoot)getParent()).getKind();
}
/**
 * Returns an array of non-java resources contained in the receiver.
 */
public Object[] getNonEGLResources() throws EGLModelException {
	if (this.isDefaultPackage()) {
		// We don't want to show non java resources of the default package (see PR #1G58NB8)
		return EGLElementInfo.NO_NON_EGL_RESOURCES;
	} else {
		return ((PackageFragmentInfo) getElementInfo()).getNonEGLResources(getResource(), (PackageFragmentRoot)getPackageFragmentRoot());
	}
}
/**
 * @see IEGLElement#getPath()
 */
public IPath getPath() {
	PackageFragmentRoot root = this.getPackageFragmentRoot();
	if (root.isArchive()) {
		return root.getPath();
	} else {
		return root.getPath().append(this.getElementName().replace('.', '/'));
	}
}
/**
 * @see IEGLElement#getResource()
 */
public IResource getResource() {
	PackageFragmentRoot root = this.getPackageFragmentRoot();
	if (root.isArchive()) {
		return root.getResource();
	} else {
		String elementName = this.getElementName();
		if (elementName.length() == 0) {
			return root.getResource();
		} else {
			return ((IContainer)root.getResource()).getFolder(new Path(this.getElementName().replace('.', '/')));
		}
	}
}
/**
 * @see IEGLElement#getUnderlyingResource()
 */
public IResource getUnderlyingResource() throws EGLModelException {
	IResource rootResource = fParent.getUnderlyingResource();
	if (rootResource == null) {
		//jar package fragment root that has no associated resource
		return null;
	}
	// the underlying resource may be a folder or a project (in the case that the project folder
	// is atually the package fragment root)
	if (rootResource.getType() == IResource.FOLDER || rootResource.getType() == IResource.PROJECT) {
		IContainer folder = (IContainer) rootResource;
		String[] segs = Signature.getSimpleNames(fName);
		for (int i = 0; i < segs.length; ++i) {
			IResource child = folder.findMember(segs[i]);
			if (child == null || child.getType() != IResource.FOLDER) {
				throw newNotPresentException();
			}
			folder = (IFolder) child;
		}
		return folder;
	} else {
		return rootResource;
	}
}
/**
 * @see IPackageFragment#hasSubpackages()
 */
public boolean hasSubpackages() throws EGLModelException {
	IEGLElement[] packages= ((IPackageFragmentRoot)getParent()).getChildren();
	String name = getElementName();
	int nameLength = name.length();
	String packageName = isDefaultPackage() ? name : name+"."; //$NON-NLS-1$
	for (int i= 0; i < packages.length; i++) {
		String otherName = packages[i].getElementName();
		if (otherName.length() > nameLength && otherName.startsWith(packageName)) {
			return true;
		}
	}
	return false;
}
/**
 * @see IPackageFragment#isDefaultPackage()
 */
public boolean isDefaultPackage() {
	return this.getElementName().length() == 0;
}
/**
 * @see ISourceManipulation#move(IEGLElement, IEGLElement, String, boolean, IProgressMonitor)
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
protected void openWhenClosed(IProgressMonitor pm) throws EGLModelException {
	if (!this.resourceExists()) throw newNotPresentException();
	super.openWhenClosed(pm);
}
/**
 * Recomputes the children of this element, based on the current state
 * of the workbench.
 */
public void refreshChildren() {
	try {
		OpenableElementInfo info= (OpenableElementInfo)getElementInfo();
		computeChildren(info, getResource());
	} catch (EGLModelException e) {
		// do nothing.
	}
}
/**
 * @see ISourceManipulation#rename(String, boolean, IProgressMonitor)
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
	return
		new PackageFragment(
			(IPackageFragmentRoot)((EGLElement)fParent).rootedAt(project), 
			fName);
}
/**
 * Debugging purposes
 */
protected void toStringChildren(int tab, StringBuffer buffer, Object info) {
	if (tab == 0) {
		super.toStringChildren(tab, buffer, info);
	}
}
/**
 * Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	if (getElementName().length() == 0) {
		buffer.append("[default]"); //$NON-NLS-1$
	} else {
		buffer.append(getElementName());
	}
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	} else {
		if (tab > 0) {
			buffer.append(" (...)"); //$NON-NLS-1$
		}
	}
}
	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.core.IPackageFragment#getPart(java.lang.String)
	 */
	public IPart getPart(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.core.IPackageFragment#getParts()
	 */
	public IPart[] getParts() throws EGLModelException {
		return fEmptyPartList;
	}
	public IClassFile getClassFile(String name) {
		if (!IRUtils.isEGLIRFileName(name)) {
			throw new IllegalArgumentException();
		}
		// don't hold on the .ir file extension to save memory
		// also make sure to not use substring as the resulting String may hold on the underlying char[] which might be much bigger than necessary
		int length = name.length() - 3;
		char[] nameWithoutExtension = new char[length];
		name.getChars(0, length, nameWithoutExtension, 0);
		return new ClassFile(this, new String(nameWithoutExtension));
	}
	public IClassFile[] getClassFiles() throws EGLModelException {
		// TODO Auto-generated method stub
		return null;
	}
//	public int hashCode() {
//		int hash = this.parent.hashCode();
//		for (int i = 0, length = this.names.length; i < length; i++)
//			hash = Util.combineHashCodes(this.names[i].hashCode(), hash);
//		return hash;
//	}
}
