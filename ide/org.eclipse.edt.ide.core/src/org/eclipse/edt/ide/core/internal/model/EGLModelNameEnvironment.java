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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;


/**
 *	This class provides a resolution mechanism for names in the EGL Model.
 */
public class EGLModelNameEnvironment {
	
	final static IPart[] fEmptyPartsArray = new IPart[] {};
	protected NameLookup nameLookup;
	protected Hashtable eglFileImportsCache;

	protected IEGLProject project;
	private IEGLFile currentEGLFile;

	private void addEGLFile(IEGLFile file) {
		try {
			IImportDeclaration[] imports = file.getImports();
			ArrayList elements = new ArrayList();
			elements.add(file.getParent().getElementName());
			if (imports.length > 0) {
				for (int i = 0; i < imports.length; i++) {
					IImportDeclaration imp = imports[i];
					String impName = imp.getElementName();
					String pkgName = null;
					String partName = null;
					int index= impName.lastIndexOf('.');
					if (index != -1) {
						pkgName= impName.substring(0, index);
					} else { 
						pkgName = impName;
					}
					elements.add(pkgName);
				}
			}
			eglFileImportsCache.put(file, elements);
		}
		catch (EGLModelException e) {}
	}
	/**
	 * Creates a NameEnvironment that sets up the given project as the
	 * definition of the total name space.  This includes all required
	 * projects as well.   
	 */
	public EGLModelNameEnvironment(IEGLProject project) throws EGLModelException {
		this.project = project;
		this.nameLookup = (NameLookup) ((EGLProject) project).getNameLookup();
		this.eglFileImportsCache = new Hashtable();
	}
	/** 
	 * The main entrypoint of the interface.  Return all the parts of
	 * the given name within the scope defined by the give file.  The
	 * given file's scope is defined to be the package the file exists
	 * in (as defined by the package statement) and the set of packages
	 * that have been imported. 
	 */
	public IPart[] findParts(String name, IEGLFile file) {
		// check if it is a compound name
		if (name == null) return fEmptyPartsArray;
		int i = name.lastIndexOf('.');
		if (i != -1) {
			String partName = name.substring(0, i);
			String packageName = name.substring(i + 1);
			return findParts(partName, packageName);
		}
		// Get the import set for the current file
		List imports = (List)eglFileImportsCache.get(file);
		if (imports == null) {
			addEGLFile(file);
			imports = (List)eglFileImportsCache.get(file);
		}
		IPart[] parts = findParts(name, imports);
		return parts;
	}
	
	private IPart[] findParts(String name, String packageName) {
		List names = new ArrayList();
		names.add(packageName);
		return findParts(name, names);
	}
	
	private IPart[] findParts(String name, List packageNames) {
		if (name == null)
			return fEmptyPartsArray;
		EGLElementRequestor requestor = new EGLElementRequestor();
		findParts(name, packageNames, requestor);
		return requestor.getParts();
	}

	private void findParts(
		String name,
		List packageName,
		IEGLElementRequestor requestor) {

		findParts(name, packageName, requestor, NameLookup.ACCEPT_PARTS);
		return;
	}

	private void findParts(
		String partName,
		List packageNames,
		IEGLElementRequestor requestor,
		int type) {
		for (Iterator iter = packageNames.iterator(); iter.hasNext();) {
			String packageName = (String) iter.next();
			this.nameLookup.seekPackageFragments(
				packageName,
				false,
				requestor);
		}
		IPackageFragment[] fragments =
			((EGLElementRequestor)requestor).getPackageFragments();
		if (fragments != null) {
			for (int i = 0, length = fragments.length; i < length; i++)
				if (fragments[i] != null)
					this.nameLookup.seekParts(
						partName,
						fragments[i],
						false,
						type,
						requestor);
		}
	}

	/**
	 * Returns a printable string for the array.
	 */
	protected String toStringArray(String[] names) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < names.length; i++) {
			result.append(names[i].toString());
		}
		return result.toString();
	}

	public void cleanup() {
		eglFileImportsCache = null;
		project = null;
		nameLookup = null;
	}
}
