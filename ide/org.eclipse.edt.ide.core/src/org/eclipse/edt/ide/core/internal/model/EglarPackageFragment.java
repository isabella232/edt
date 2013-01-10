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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;

public class EglarPackageFragment extends PackageFragment {
	protected EglarPackageFragment(PackageFragmentRoot root, String name) {
		super(root, name);
	}
	
	protected EglarPackageFragment(PackageFragmentRoot root, String[] names) {
		super(root, names);
	}
	
	@Override
	protected void buildStructure(OpenableElementInfo info, IProgressMonitor monitor) throws EGLModelException {
		EglarPackageFragmentRoot root = (EglarPackageFragmentRoot) getParent();
		EglarPackageFragmentRootInfo parentInfo = (EglarPackageFragmentRootInfo) root.getElementInfo();
		ArrayList[] entries = (ArrayList[]) parentInfo.rawPackageInfo.get(this.names);
		if (entries == null)
			throw newNotPresentException();
		EglarPackageFragmentInfo fragInfo = (EglarPackageFragmentInfo) info;

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

		ArrayList topJarEntries = new ArrayList();

		return topJarEntries.toArray(new Object[topJarEntries.size()]);
	}	
	
	@Override
	protected OpenableElementInfo createElementInfo() {
		return new EglarPackageFragmentInfo();
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
		return ((EglarPackageFragmentInfo) getElementInfo()).getNonJavaResources();
	}	
	@Override
	protected boolean resourceExists() {
		if(fParent instanceof EglarPackageFragmentRoot) {
			return ((EglarPackageFragmentRoot) fParent).resourceExists();
		}
		return super.resourceExists();
	}
	
	public IClassFile getClassFile(String name) {
		if (!IRUtils.isEGLIRFileName(name)) {
			throw new IllegalArgumentException();
		}
		try {
			EGLElementInfo openableInfo;
			openableInfo = (EGLElementInfo) this.getElementInfo();
			IEGLElement[] elements = openableInfo.getChildren();
			for ( int i = 0; i < elements.length; i ++ ) {
				if ( elements[i].getElementName().equals( name ) ) {
					return (IClassFile)elements[i];
				}
			}
		} catch (EGLModelException e) {
		}
		return super.getClassFile(name);
		
	}
	
	@Override
	public IClassFile[] getClassFiles() throws EGLModelException {
		ArrayList list = getChildrenOfType(CLASS_FILE);
		IClassFile[] array= new IClassFile[list.size()];
		list.toArray(array);
		return array;
	}
}
