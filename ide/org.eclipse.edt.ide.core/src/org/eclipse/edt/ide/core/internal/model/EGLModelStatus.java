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

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.EGLPathContainerInitializer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathContainer;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;


/**
 * @see IEGLModelStatus
 */

public class EGLModelStatus extends Status implements IEGLModelStatus, IEGLModelStatusConstants, IResourceStatus {

	/**
	 * The elements related to the failure, or <code>null</code>
	 * if no elements are involved.
	 */
	protected IEGLElement[] fElements = new IEGLElement[0];
	/**
	 * The path related to the failure, or <code>null</code>
	 * if no path is involved.
	 */
	protected IPath fPath;
	/**
	 * The <code>String</code> related to the failure, or <code>null</code>
	 * if no <code>String</code> is involved.
	 */
	protected String fString;
	/**
	 * Empty children
	 */
	protected final static IStatus[] fgEmptyChildren = new IStatus[] {};
	protected IStatus[] fChildren= fgEmptyChildren;

	/**
	 * Singleton OK object
	 */
	public static final IEGLModelStatus VERIFIED_OK = new EGLModelStatus(OK, OK, EGLModelResources.statusOK);

	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus() {
		// no code for an multi-status
		super(ERROR, EGLCore.PLUGIN_ID, 0, "EGLModelStatus", null); //$NON-NLS-1$
	}
	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus(int code) {
		super(ERROR, EGLCore.PLUGIN_ID, code, "EGLModelStatus", null); //$NON-NLS-1$
		fElements= EGLElementInfo.fgEmptyChildren;
	}
	/**
	 * Constructs an EGL model status with the given corresponding
	 * elements.
	 */
	public EGLModelStatus(int code, IEGLElement[] elements) {
		super(ERROR, EGLCore.PLUGIN_ID, code, "EGLModelStatus", null); //$NON-NLS-1$
		fElements= elements;
		fPath= null;
	}
	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus(int code, String string) {
		this(ERROR, code, string);
	}
	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus(int severity, int code, String string) {
		super(severity, EGLCore.PLUGIN_ID, code, "EGLModelStatus", null); //$NON-NLS-1$
		fElements= EGLElementInfo.fgEmptyChildren;
		fPath= null;
		fString = string;
	}	
	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus(int code, Throwable throwable) {
		super(ERROR, EGLCore.PLUGIN_ID, code, "EGLModelStatus", throwable); //$NON-NLS-1$
		fElements= EGLElementInfo.fgEmptyChildren;
	}
	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus(int code, IPath path) {
		super(ERROR, EGLCore.PLUGIN_ID, code, "EGLModelStatus", null); //$NON-NLS-1$
		fElements= EGLElementInfo.fgEmptyChildren;
		fPath= path;
	}
	/**
	 * Constructs an EGL model status with the given corresponding
	 * element.
	 */
	public EGLModelStatus(int code, IEGLElement element) {
		this(code, new IEGLElement[]{element});
	}
	/**
	 * Constructs an EGL model status with the given corresponding
	 * element and string
	 */
	public EGLModelStatus(int code, IEGLElement element, String string) {
		this(code, new IEGLElement[]{element});
		fString = string;
	}
	
	/**
	 * Constructs an EGL model status with the given corresponding
	 * element and path
	 */
	public EGLModelStatus(int code, IEGLElement element, IPath path) {
		this(code, new IEGLElement[]{element});
		fPath = path;
	}	
	/**
	 * Constructs an EGL model status with no corresponding elements.
	 */
	public EGLModelStatus(CoreException coreException) {
		super(ERROR, EGLCore.PLUGIN_ID, CORE_EXCEPTION, "EGLModelStatus", coreException); //$NON-NLS-1$
		fElements= EGLElementInfo.fgEmptyChildren;
	}
	protected int getBits() {
		int severity = 1 << (getCode() % 100 / 33);
		int category = 1 << ((getCode() / 100) + 3);
		return severity | category;
	}
	/**
	 * @see IStatus
	 */
	public IStatus[] getChildren() {
		return fChildren;
	}
	/**
	 * @see IEGLModelStatus
	 */
	public IEGLElement[] getElements() {
		return fElements;
	}
	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	public String getMessage() {
		Throwable exception = getException();
		if (exception == null) {
			switch (getCode()) {
				case CORE_EXCEPTION :
					return EGLModelResources.statusCoreException;

				case BUILDER_INITIALIZATION_ERROR:
					return EGLModelResources.buildInitializationError;

				case BUILDER_SERIALIZATION_ERROR:
					return EGLModelResources.buildSerializationError;

				case DEVICE_PATH:
					return EGLModelResources.bind(EGLModelResources.statusCannotUseDeviceOnPath, getPath().toString());

// EGLTODO: Should not happen
//				case DOM_EXCEPTION:
//					return EGLModelResources.bind(EGLModelResources.statusJDOMError);

				case ELEMENT_DOES_NOT_EXIST:
					return EGLModelResources.bind(EGLModelResources.elementDoesNotExist,((EGLElement)fElements[0]).toStringWithAncestors());

				case EVALUATION_ERROR:
					return EGLModelResources.bind(EGLModelResources.statusEvaluationError, fString);

				case INDEX_OUT_OF_BOUNDS:
					return EGLModelResources.statusIndexOutOfBounds;

				case INVALID_CONTENTS:
					return EGLModelResources.statusInvalidContents;

				case INVALID_DESTINATION:
					return EGLModelResources.bind(EGLModelResources.statusInvalidDestination, ((EGLElement)fElements[0]).toStringWithAncestors());

				case INVALID_ELEMENT_TYPES:
					StringBuffer buff= new StringBuffer(EGLModelResources.operationNotSupported);
					for (int i= 0; i < fElements.length; i++) {
						if (i > 0) {
							buff.append(", "); //$NON-NLS-1$
						}
						buff.append(((EGLElement)fElements[i]).toStringWithAncestors());
					}
					return buff.toString();

				case INVALID_NAME:
					return EGLModelResources.bind(EGLModelResources.statusInvalidName, fString);

				case INVALID_PACKAGE:
					return EGLModelResources.bind(EGLModelResources.statusInvalidPackage, fString);

				case INVALID_PATH:
					if (fString != null) {
						return fString;
					} else {
						return EGLModelResources.bind(EGLModelResources.statusInvalidPath, getPath() == null ? "null" : getPath().toString()); //$NON-NLS-1$
					}

				case INVALID_PROJECT:
					return EGLModelResources.bind(EGLModelResources.statusInvalidProject, fString);

				case INVALID_RESOURCE:
					return EGLModelResources.bind(EGLModelResources.statusInvalidResource, fString);

				case INVALID_RESOURCE_TYPE:
					return EGLModelResources.bind(EGLModelResources.statusInvalidResourceType, fString);

				case INVALID_SIBLING:
					if (fString != null) {
						return EGLModelResources.bind(EGLModelResources.statusInvalidSibling, fString);
					} else {
						return EGLModelResources.bind(EGLModelResources.statusInvalidSibling, ((EGLElement)fElements[0]).toStringWithAncestors());
					}

				case IO_EXCEPTION:
					return EGLModelResources.statusIOException;

				case NAME_COLLISION:
					if (fElements != null && fElements.length > 0) {
						IEGLElement element = fElements[0];
						String name = element.getElementName();
						if (element instanceof IPackageFragment && name.equals(IPackageFragment.DEFAULT_PACKAGE_NAME)) {
							return EGLModelResources.operationCannotRenameDefaultPackage;
						}
					}
					if (fString != null) {
						return fString;
					} else {
						return EGLModelResources.bind(EGLModelResources.statusNameCollision, ""); //$NON-NLS-1$
					}
				case NO_ELEMENTS_TO_PROCESS:
					return EGLModelResources.operationNeedElements;

				case NULL_NAME:
					return EGLModelResources.operationNeedName;

				case NULL_PATH:
					return EGLModelResources.operationNeedPath;

				case NULL_STRING:
					return EGLModelResources.operationNeedString;

				case PATH_OUTSIDE_PROJECT:
					return EGLModelResources.bind(EGLModelResources.operationPathOutsideProject, fString, ((EGLElement)fElements[0]).toStringWithAncestors());

				case READ_ONLY:
					IEGLElement element = fElements[0];
					String name = element.getElementName();
					if (element instanceof IPackageFragment && name.equals(IPackageFragment.DEFAULT_PACKAGE_NAME)) {
						return EGLModelResources.statusDefaultPackeReadOnly;
					}
					return  EGLModelResources.bind(EGLModelResources.statusReadOnly, name);

				case RELATIVE_PATH:
					return EGLModelResources.bind(EGLModelResources.operationNeedAbsolutePath, getPath().toString());

				case TARGET_EXCEPTION:
					return EGLModelResources.statusTargetException;

				case UPDATE_CONFLICT:
					return EGLModelResources.statusUpdateConflict;

				case NO_LOCAL_CONTENTS :
					return EGLModelResources.bind(EGLModelResources.statusNoLocalContents, getPath().toString());

				case CP_CONTAINER_PATH_UNBOUND:
					IPath path = this.fPath;
					IEGLProject EGLProject = (IEGLProject)fElements[0];
					EGLPathContainerInitializer initializer = EGLCore.getEGLPathContainerInitializer(path.segment(0));
					String description = null;
					if (initializer != null) description = initializer.getDescription(path, EGLProject);
					if (description == null) description = path.makeRelative().toString();
					return EGLModelResources.bind(EGLModelResources.eglpathUnboundContainerPath, description);

				case INVALID_CP_CONTAINER_ENTRY:
					path = this.fPath;
					EGLProject = (IEGLProject)fElements[0];
					IEGLPathContainer container = null;
					description = null;
					try {
						container = EGLCore.getEGLPathContainer(path, EGLProject);
					} catch(EGLModelException e){
					}
					if (container == null) {
						 initializer = EGLCore.getEGLPathContainerInitializer(path.segment(0));
						if (initializer != null) description = initializer.getDescription(path, EGLProject);
					} else {
						description = container.getDescription();
					}
					if (description == null) description = path.makeRelative().toString();
					return EGLModelResources.bind(EGLModelResources.eglpathInvalidContainer, description);

			case CP_VARIABLE_PATH_UNBOUND:
					path = this.fPath;
					return EGLModelResources.bind(EGLModelResources.eglpathUnboundVariablePath, path.makeRelative().toString());
					
			case EGLPATH_CYCLE: 
					EGLProject = (IEGLProject)fElements[0];
					return EGLModelResources.bind(EGLModelResources.eglpathCycle, EGLProject.getElementName());
												 
			case DISABLED_CP_EXCLUSION_PATTERNS:
					path = this.fPath;
					return EGLModelResources.bind(EGLModelResources.eglpathDisabledExclusionPatterns, path.makeRelative().toString());

			case DISABLED_CP_MULTIPLE_OUTPUT_LOCATIONS:
					path = this.fPath;
					return EGLModelResources.bind(EGLModelResources.eglpathDisabledMultipleOutputLocations, path.makeRelative().toString());
			}
			if (fString != null) {
				return fString;
			} else {
				return ""; // //$NON-NLS-1$
			}
		} else {
			String message = exception.getMessage();
			if (message != null) {
				return message;
			} else {
				return exception.toString();
			}
		}
	}
	/**
	 * @see IEGLModelStatus#getPath()
	 */
	public IPath getPath() {
		return fPath;
	}
	/**
	 * @see IStatus#getSeverity()
	 */
	public int getSeverity() {
		if (fChildren == fgEmptyChildren) return super.getSeverity();
		int severity = -1;
		for (int i = 0, max = fChildren.length; i < max; i++) {
			int childrenSeverity = fChildren[i].getSeverity();
			if (childrenSeverity > severity) {
				severity = childrenSeverity;
			}
		}
		return severity;
	}
	/**
	 * @see IEGLModelStatus#getString()
	 * @deprecated
	 */
	public String getString() {
		return fString;
	}
	/**
	 * @see IEGLModelStatus#isDoesNotExist()
	 */
	public boolean isDoesNotExist() {
		return getCode() == ELEMENT_DOES_NOT_EXIST;
	}
	/**
	 * @see IStatus#isMultiStatus()
	 */
	public boolean isMultiStatus() {
		return fChildren != fgEmptyChildren;
	}
	/**
	 * @see IStatus#isOK()
	 */
	public boolean isOK() {
		return getCode() == OK;
	}
	/**
	 * @see IStatus#matches(int)
	 */
	public boolean matches(int mask) {
		if (! isMultiStatus()) {
			return matches(this, mask);
		} else {
			for (int i = 0, max = fChildren.length; i < max; i++) {
				if (matches((EGLModelStatus) fChildren[i], mask))
					return true;
			}
			return false;
		}
	}
	/**
	 * Helper for matches(int).
	 */
	protected boolean matches(EGLModelStatus status, int mask) {
		int severityMask = mask & 0x7;
		int categoryMask = mask & ~0x7;
		int bits = status.getBits();
		return ((severityMask == 0) || (bits & severityMask) != 0) && ((categoryMask == 0) || (bits & categoryMask) != 0);
	}
	/**
	 * Creates and returns a new <code>IEGLModelStatus</code> that is a
	 * a multi-status status.
	 *
	 * @see IStatus#isMultiStatus()
	 */
	public static IEGLModelStatus newMultiStatus(IEGLModelStatus[] children) {
		EGLModelStatus jms = new EGLModelStatus();
		jms.fChildren = children;
		return jms;
	}
	/**
	 * Returns a printable representation of this exception for debugging
	 * purposes.
	 */
	public String toString() {
		if (this == VERIFIED_OK){
			return "EGLModelStatus[OK]"; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("EGL Model Status ["); //$NON-NLS-1$
		buffer.append(getMessage());
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}
}
