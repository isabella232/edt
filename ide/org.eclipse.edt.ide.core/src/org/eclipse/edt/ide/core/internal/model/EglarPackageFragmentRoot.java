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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jdt.core.IJavaElement;

public class EglarPackageFragmentRoot extends PackageFragmentRoot {
	protected final IPath jarPath;
	private boolean isBinaryProject = false;

	/**
	 * Constant for an empty String array.
	 * @since 3.1
	 */
	public static final String[] NO_STRINGS = new String[0];
	private final static ArrayList EMPTY_LIST = new ArrayList();
	/**
	 * Constructs a package fragment root which is the root of the Java package directory hierarchy
	 * based on a JAR file that is not contained in a <code>IJavaProject</code> and
	 * does not have an associated <code>IResource</code>.
	 */
	protected EglarPackageFragmentRoot(IPath externalJarPath, IEGLProject project) {
		super(null, project, null);
		this.jarPath = externalJarPath;
	}
	/**
	 * 
	 * @param resource
	 * @param project
	 * @param name
	 */
	protected EglarPackageFragmentRoot(IResource resource, IEGLProject project, String name) {
		super(resource, project, name);
		this.jarPath = resource.getFullPath();
	}
	/**
	 * Compute the package fragment children of this package fragment root.
	 * These are all of the directory zip entries, and any directories implied
	 * by the path of class files contained in the jar of this package fragment root.
	 */
	protected boolean computeChildren(OpenableElementInfo info) throws EGLModelException {
		HashtableOfArrayToObject rawPackageInfo = new HashtableOfArrayToObject();
		IEGLElement[] children;
		ZipFile jar = null;
		try {
			jar = getJar();

			// always create the default package
			rawPackageInfo.put(NO_STRINGS, new ArrayList[] { EMPTY_LIST, EMPTY_LIST });

			for (Enumeration e= jar.entries(); e.hasMoreElements();) {
				ZipEntry member= (ZipEntry) e.nextElement();
				initRawPackageInfo(rawPackageInfo, member.getName(), member.isDirectory());
			}

			// loop through all of referenced packages, creating package fragments if necessary
			// and cache the entry names in the rawPackageInfo table
			children = new IEGLElement[rawPackageInfo.size()];
			int index = 0;
			for (int i = 0, length = rawPackageInfo.keyTable.length; i < length; i++) {
				String[] pkgName = (String[]) rawPackageInfo.keyTable[i];
				if (pkgName == null) continue;
				children[index++] = getPackageFragment(pkgName);
			}
		} catch (CoreException e) {
			if (e.getCause() instanceof ZipException) {
				// not a ZIP archive, leave the children empty
				Util.log(e, "Invalid ZIP archive: " + toStringWithAncestors()); //$NON-NLS-1$
				children = NO_ELEMENTS;
			} else if (e instanceof EGLModelException) {
				throw (EGLModelException)e;
			} else {
				throw new EGLModelException(e);
			}
		} finally {
			EGLModelManager.getEGLModelManager().closeZipFile(jar);
		}

		info.setChildren(children);
		((EglarPackageFragmentRootInfo) info).rawPackageInfo = rawPackageInfo;
		return true;
	}
	@Override
	protected OpenableElementInfo createElementInfo() {
		return new EglarPackageFragmentRootInfo();
	}
	/**
	 * A Jar is always K_BINARY.
	 */
	protected int determineKind(IResource underlyingResource) {
		return IPackageFragmentRoot.K_BINARY;
	}
	/**
	 * Returns true if this handle represents the same jar
	 * as the given handle. Two jars are equal if they share
	 * the same zip file.
	 *
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof EglarPackageFragmentRoot) {
			EglarPackageFragmentRoot other= (EglarPackageFragmentRoot) o;
			return (this.jarPath.equals(other.jarPath) && this.getParent().equals(other.getParent()));
		}
		return false;
	}
	public String getElementName() {
		return this.jarPath.lastSegment();
	}
	/**
	 * Returns the underlying ZipFile for this Jar package fragment root.
	 *
	 * @exception CoreException if an error occurs accessing the jar
	 */
	public ZipFile getJar() throws CoreException {
		try {
			return EGLModelManager.getEGLModelManager().getZipFile(getPath());
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * @see IPackageFragmentRoot
	 */
	public int getKind() {
		return IPackageFragmentRoot.K_BINARY;
	}
	int internalKind() throws EGLModelException {
		return IPackageFragmentRoot.K_BINARY;
	}
	/**
	 * Returns an array of non-java resources contained in the receiver.
	 */
	public Object[] getNonJavaResources() throws EGLModelException {
		return null;
	}

	public IResource resource(PackageFragmentRoot root) {
		return null;
	}
	
	@Override
	public PackageFragment getPackageFragment(String[] pkgName) {
		return new EglarPackageFragment(this, pkgName);
	}
	/**
	 * @see IJavaElement
	 */
	public IResource getUnderlyingResource() throws EGLModelException {
		if (isExternal()) {
			if (!exists()) throw newNotPresentException();
			return null;
		} else {
			return super.getUnderlyingResource();
		}
	}
	public int hashCode() {
		return this.jarPath.hashCode();
	}
	private void initRawPackageInfo(HashtableOfArrayToObject rawPackageInfo, String entryName, boolean isDirectory) {
		int lastSeparator = isDirectory ? entryName.length()-1 : entryName.lastIndexOf('/');
		String[] pkgName = Util.splitOn('/', entryName, 0, lastSeparator);
		String[] existing = null;
		int length = pkgName.length;
		int existingLength = length;
		while (existingLength >= 0) {
			existing = (String[]) rawPackageInfo.getKey(pkgName, existingLength);
			if (existing != null) break;
			existingLength--;
		}
//		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		for (int i = existingLength; i < length; i++) {
			if (Util.isValidFolderNameForPackage(pkgName[i])) {
				System.arraycopy(existing, 0, existing = new String[i+1], 0, i);
				//NameUtile.getAsName(String) returns the matching string ignore cases,
				//it is possible that 'existing' and pkgName in different case. However, later
				//rawPackageInfo.get(pkgName) retrieves exactly the same case string, which may
				//cause the result null if the string put into rawPackageInfo is in different case
				//with the pkgName. Therefore, need to put the same case string into rawPackageInfo,
				//using NameUtile.getAsCaseSensitiveName instead.
				existing[i] = NameUtile.getAsCaseSensitiveName(pkgName[i]);

				rawPackageInfo.put(existing, new ArrayList[] { EMPTY_LIST, EMPTY_LIST });
			} else {
				// non-Java resource folder
				if (!isDirectory) {
					ArrayList[] children = (ArrayList[]) rawPackageInfo.get(existing);
					if (children[1/*NON_JAVA*/] == EMPTY_LIST) children[1/*NON_JAVA*/] = new ArrayList();
					children[1/*NON_JAVA*/].add(entryName);
				}
				return;
			}
		}
		if (isDirectory)
			return;

		// add classfile info amongst children
		ArrayList[] children = (ArrayList[]) rawPackageInfo.get(pkgName);
		if (IRUtils.isEGLIRFileName(entryName)) {
			if (children[0/*JAVA*/] == EMPTY_LIST) children[0/*JAVA*/] = new ArrayList();
			String nameWithoutExtension = entryName.substring(lastSeparator + 1, entryName.length() - IRUtils.SUFFIX_eglxml.length);
			children[0/*JAVA*/].add(nameWithoutExtension);
		} else {
			if (children[1/*NON_JAVA*/] == EMPTY_LIST) children[1/*NON_JAVA*/] = new ArrayList();
			children[1/*NON_JAVA*/].add(entryName);
		}

	}
	/**
	 * @see IPackageFragmentRoot
	 */
	public boolean isArchive() {
		return true;
	}
	/**
	 * @see IPackageFragmentRoot
	 */
	public boolean isExternal() {
		return resource == null;
	}
	/**
	 * Jars and jar entries are all read only
	 */
	public boolean isReadOnly() {
		return true;
	}
	@Override
	public IPath getPath() {
		if (isExternal()) {
			return this.jarPath;
		} else {
			return super.getPath();
		}
	}
	/**
	 * Returns whether the corresponding resource or associated file exists
	 */
	protected boolean resourceExists() {
		if (this.resource == null) {
			return
				EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), getPath(), true) != null;
		} else {
			return super.resourceExists();
		}
	}

	protected void toStringAncestors(StringBuffer buffer) {
		if (isExternal())
			// don't show project as it is irrelevant for external jar files.
			// also see https://bugs.eclipse.org/bugs/show_bug.cgi?id=146615
			return;
		super.toStringAncestors(buffer);
	}
	
	public boolean isBinaryProject() {
		return isBinaryProject;
	}
	public void setBinaryProject(boolean isBinaryProject) {
		this.isBinaryProject = isBinaryProject;
	}
}
