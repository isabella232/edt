/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;

public class JarPackageFragment extends PackageFragment {

	protected JarPackageFragment(PackageFragmentRoot root, String name) {
		super(root, name);
	}
	
	protected JarPackageFragment(PackageFragmentRoot root, String[] names) {
		super(root, names);
	}
	
	@Override
	protected void buildStructure(OpenableElementInfo info, IProgressMonitor monitor) throws EGLModelException {
		JarPackageFragmentRoot root = (JarPackageFragmentRoot) getParent();
		JarPackageFragmentRootInfo parentInfo = (JarPackageFragmentRootInfo) root.getElementInfo();
		ArrayList[] entries = (ArrayList[]) parentInfo.rawPackageInfo.get(this.names);
		if (entries == null)
			throw newNotPresentException();
		JarPackageFragmentInfo fragInfo = (JarPackageFragmentInfo) info;

		// compute children
		fragInfo.setChildren(computeChildren(entries[0/*class files*/]));

		// compute non-Java resources
		fragInfo.setNonEGLResources(computeNonJavaResources(entries[1/*non Java resources*/]));

		// add the info for this at the end, to ensure that a getInfo cannot reply null in case the LRU cache needs
		// to be flushed. Might lead to performance issues.
		// see PR 1G2K5S7: ITPJCORE:ALL - NPE when accessing source for a binary type
		EGLModelManager.getEGLModelManager().putInfo(this, info);
	}
	/**
	 * Compute the children of this package fragment. Children of jar package fragments
	 * can only be IClassFile (representing .class files).
	 */
	private IEGLElement[] computeChildren(ArrayList namesWithoutExtension) {
		int size = namesWithoutExtension.size();
		if (size == 0)
			return NO_ELEMENTS;
		IEGLElement[] children = new IEGLElement[size];
		for (int i = 0; i < size; i++) {
			String nameWithoutExtension = (String) namesWithoutExtension.get(i);
			children[i] = new ClassFile(this, nameWithoutExtension);
		}
		return children;
	}
	/**
	 * Compute all the non-java resources according to the given entry names.
	 */
	private Object[] computeNonJavaResources(ArrayList entryNames) {
		int length = entryNames.size();
		if (length == 0)
			return EGLElementInfo.NO_NON_EGL_RESOURCES;
//		HashMap jarEntries = new HashMap(); // map from IPath to IJarEntryResource
//		HashMap childrenMap = new HashMap(); // map from IPath to ArrayList<IJarEntryResource>
		ArrayList topJarEntries = new ArrayList();
//		for (int i = 0; i < length; i++) {
//			String resName = (String) entryNames.get(i);
//			// consider that a .java file is not a non-java resource (see bug 12246 Packages view shows .class and .java files when JAR has source)
//			if (!Util.isEGLFileName(resName)) {
//				IPath filePath = new Path(resName);
//				IPath childPath = filePath.removeFirstSegments(this.names.length);
//				if (jarEntries.containsKey(childPath)) {
//					// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=222665
//					continue;
//				}
//				JarEntryFile file = new JarEntryFile(filePath.lastSegment());
//				jarEntries.put(childPath, file);
//				if (childPath.segmentCount() == 1) {
//					file.setParent(this);
//					topJarEntries.add(file);
//				} else {
//					IPath parentPath = childPath.removeLastSegments(1);
//					while (parentPath.segmentCount() > 0) {
//						ArrayList parentChildren = (ArrayList) childrenMap.get(parentPath);
//						if (parentChildren == null) {
//							Object dir = new JarEntryDirectory(parentPath.lastSegment());
//							jarEntries.put(parentPath, dir);
//							childrenMap.put(parentPath, parentChildren = new ArrayList());
//							parentChildren.add(childPath);
//							if (parentPath.segmentCount() == 1) {
//								topJarEntries.add(dir);
//								break;
//							}
//							childPath = parentPath;
//							parentPath = childPath.removeLastSegments(1);
//						} else {
//							parentChildren.add(childPath);
//							break; // all parents are already registered
//						}
//					}
//				}
//			}
//		}
//		Iterator entries = childrenMap.entrySet().iterator();
//		while (entries.hasNext()) {
//			Map.Entry entry = (Map.Entry) entries.next();
//			IPath entryPath = (IPath) entry.getKey();
//			ArrayList entryValue =  (ArrayList) entry.getValue();
//			JarEntryDirectory jarEntryDirectory = (JarEntryDirectory) jarEntries.get(entryPath);
//			int size = entryValue.size();
//			IJarEntryResource[] children = new IJarEntryResource[size];
//			for (int i = 0; i < size; i++) {
//				JarEntryResource child = (JarEntryResource) jarEntries.get(entryValue.get(i));
//				child.setParent(jarEntryDirectory);
//				children[i] = child;
//			}
//			jarEntryDirectory.setChildren(children);
//			if (entryPath.segmentCount() == 1) {
//				jarEntryDirectory.setParent(this);
//			}
//		}
		return topJarEntries.toArray(new Object[topJarEntries.size()]);
	}	
	
	@Override
	protected OpenableElementInfo createElementInfo() {
		return new JarPackageFragmentInfo();
	}
	/**
	 * Returns an array of non-java resources contained in the receiver.
	 */
	public Object[] getNonJavaResources() throws EGLModelException {
		if (isDefaultPackage()) {
			// We don't want to show non java resources of the default package (see PR #1G58NB8)
			return EGLElementInfo.NO_NON_EGL_RESOURCES;
		} else {
			return storedNonJavaResources();
		}
	}
	/**
	 * Jars and jar entries are all read only
	 */
	public boolean isReadOnly() {
		return true;
	}
	protected Object[] storedNonJavaResources() throws EGLModelException {
		return ((JarPackageFragmentInfo) getElementInfo()).getNonJavaResources();
	}	
	@Override
	protected boolean resourceExists() {
		if(fParent instanceof JarPackageFragmentRoot) {
			return ((JarPackageFragmentRoot) fParent).resourceExists();
		}
		return super.resourceExists();
	}
	
	@Override
	public IClassFile[] getClassFiles() throws EGLModelException {
		ArrayList list = getChildrenOfType(CLASS_FILE);
		IClassFile[] array= new IClassFile[list.size()];
		list.toArray(array);
		return array;
	}
}
