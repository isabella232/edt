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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IPackageFragment;


/**
 * <p>This operation creates a compilation unit (CU).
 * If the CU doesn't exist yet, a new compilation unit will be created with the content provided.
 * Otherwise the operation will override the contents of an existing CU with the new content.
 *
 * <p>Note: It is possible to create a CU automatically when creating a
 * class or interface. Thus, the preferred method of creating a CU is
 * to perform a create type operation rather than
 * first creating a CU and secondly creating a type inside the CU.
 *
 * <p>Required Attributes:<ul>
 *  <li>The package fragment in which to create the compilation unit.
 *  <li>The name of the compilation unit.  
 *      Do not include the <code>".java"</code> suffix (ex. <code>"Object"</code> -
 * 		the <code>".java"</code> will be added for the name of the compilation unit.)
 *  <li>
  * </ul>
 */
public class CreateEGLFileOperation extends EGLModelOperation {

	/**
	 * The name of the compilation unit being created.
	 */
	protected String fName;
	/**
	 * The source code to use when creating the element.
	 */
	protected String fSource= null;
/**
 * When executed, this operation will create a compilation unit with the given name.
 * The name should have the ".java" suffix.
 */
public CreateEGLFileOperation(IPackageFragment parentElement, String name, String source, boolean force) {
	super(null, new IEGLElement[] {parentElement}, force);
	fName = name;
	fSource = source;
}
/**
 * Creates a compilation unit.
 *
 * @exception EGLModelException if unable to create the compilation unit.
 */
protected void executeOperation() throws EGLModelException {
	try {
		beginTask(EGLModelResources.operationCreateEGLFileProgress, 2);
		EGLElementDelta delta = newEGLElementDelta();
		IEGLFile unit = getEGLFile();
		IPackageFragment pkg = (IPackageFragment) getParentElement();
		IContainer folder = (IContainer) pkg.getResource();
		worked(1);
		IFile compilationUnitFile = folder.getFile(new Path(fName));
		if (compilationUnitFile.exists()) {
			// update the contents of the existing unit if fForce is true
			if (fForce) {
				IBuffer buffer = unit.getBuffer();
				if (buffer == null) return;
				buffer.setContents(fSource);
				unit.save(new NullProgressMonitor(), false);
				fResultElements = new IEGLElement[] {unit};
				if (!Util.isExcluded(unit)
						&& unit.getParent().exists()) {
					for (int i = 0; i < fResultElements.length; i++) {
						delta.changed(fResultElements[i], IEGLElementDelta.F_CONTENT);
					}
					addDelta(delta);
				}
			} else {
				throw new EGLModelException(new EGLModelStatus(
					IEGLModelStatusConstants.NAME_COLLISION, 
					EGLModelResources.bind(EGLModelResources.statusNameCollision, compilationUnitFile.getFullPath().toString())));
			}
		} else {
			try {
				String encoding = unit.getEGLProject().getOption(EGLCore.CORE_ENCODING, true);
				InputStream stream = new ByteArrayInputStream(encoding == null ? fSource.getBytes() : fSource.getBytes(encoding));
				createFile(folder, unit.getElementName(), stream, false);
				fResultElements = new IEGLElement[] {unit};
				if (!Util.isExcluded(unit)
						&& unit.getParent().exists()) {
					for (int i = 0; i < fResultElements.length; i++) {
						delta.added(fResultElements[i]);
					}
					addDelta(delta);
				}
			} catch (IOException e) {
				throw new EGLModelException(e, IEGLModelStatusConstants.IO_EXCEPTION);
			}
		} 
		worked(1);
	} finally {
		done();
	}
}
/**
 * @see CreateElementInCUOperation#getEGLFile()
 */
protected IEGLFile getEGLFile() {
	return ((IPackageFragment)getParentElement()).getEGLFile(fName);
}
/**
 * Possible failures: <ul>
 *  <li>NO_ELEMENTS_TO_PROCESS - the package fragment supplied to the operation is
 * 		<code>null</code>.
 *	<li>INVALID_NAME - the compilation unit name provided to the operation 
 * 		is <code>null</code> or has an invalid syntax
 *  <li>INVALID_CONTENTS - the source specified for the compiliation unit is null
 * </ul>
 */
public IEGLModelStatus verify() {
	if (getParentElement() == null) {
		return new EGLModelStatus(IEGLModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
	}
	if (EGLConventions.validateEGLFileName(fName).getSeverity() == IStatus.ERROR) {
		return new EGLModelStatus(IEGLModelStatusConstants.INVALID_NAME, fName);
	}
	if (fSource == null) {
		return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CONTENTS);
	}
	return EGLModelStatus.VERIFIED_OK;
}
protected ISchedulingRule getSchedulingRule() {
	IResource resource  = getEGLFile().getResource();
	IWorkspace workspace = resource.getWorkspace();
	if (resource.exists()) {
		return workspace.getRuleFactory().modifyRule(resource);
	} else {
		return workspace.getRuleFactory().createRule(resource);
	}
}
}
