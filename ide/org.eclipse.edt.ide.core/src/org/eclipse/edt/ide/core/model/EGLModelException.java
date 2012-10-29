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
package org.eclipse.edt.ide.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.edt.ide.core.internal.model.EGLModelStatus;

/**
 * A checked exception representing a failure in the EGL model.
 * EGL model exceptions contain a EGL-specific status object describing the
 * cause of the exception.
 * <p>
 * This class is not intended to be subclassed by clients. Instances of this
 * class are automatically created by the EGL model when problems arise, so
 * there is generally no need for clients to create instances.
 * </p>
 *
 * @see IEGLModelStatus
 * @see IEGLModelStatusConstants
 */
public class EGLModelException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	CoreException nestedCoreException;
/**
 * Creates a EGL model exception that wrappers the given <code>Throwable</code>.
 * The exception contains a EGL-specific status object with severity
 * <code>IStatus.ERROR</code> and the given status code.
 *
 * @param exception the <code>Throwable</code>
 * @param code one of the EGL-specific status codes declared in
 *   <code>IEGLModelStatusConstants</code>
 * @see IEGLModelStatusConstants
 * @see org.eclipse.core.runtime.IStatus#ERROR
 */
public EGLModelException(Throwable e, int code) {
	this(new EGLModelStatus(code, e)); 
}
/**
 * Creates a EGL model exception for the given <code>CoreException</code>.
 * Equivalent to 
 * <code>EGLModelException(exception,IEGLModelStatusConstants.CORE_EXCEPTION</code>.
 *
 * @param exception the <code>CoreException</code>
 */
public EGLModelException(CoreException exception) {
	super(exception.getStatus());
	this.nestedCoreException = exception;
}
/**
 * Creates a EGL model exception for the given EGL-specific status object.
 *
 * @param status the EGL-specific status object
 */
public EGLModelException(IEGLModelStatus status) {
	super(status);
}
/**
 * Returns the underlying <code>Throwable</code> that caused the failure.
 *
 * @return the wrappered <code>Throwable</code>, or <code>null</code> if the
 *   direct case of the failure was at the EGL model layer
 */
public Throwable getException() {
	if (this.nestedCoreException == null) {
		return getStatus().getException();
	} else {
		return this.nestedCoreException;
	}
}
/**
 * Returns the EGL model status object for this exception.
 * Equivalent to <code>(IEGLModelStatus) getStatus()</code>.
 *
 * @return a status object
 */
public IEGLModelStatus getEGLModelStatus() {
	IStatus status = this.getStatus();
	if (status instanceof IEGLModelStatus) {
		return (IEGLModelStatus)status;
	} else {
		// A regular IStatus is created only in the case of a CoreException.
		// See bug 13492 Should handle EGLModelExceptions that contains CoreException more gracefully  
		return new EGLModelStatus(this.nestedCoreException);
	}
}
/**
 * Returns whether this exception indicates that a EGL model element does not
 * exist. Such exceptions have a status with a code of
 * <code>IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST</code>.
 * This is a convenience method.
 *
 * @return <code>true</code> if this exception indicates that a EGL model
 *   element does not exist
 * @see IEGLModelStatus#isDoesNotExist
 * @see IEGLModelStatusConstants#ELEMENT_DOES_NOT_EXIST
 */
public boolean isDoesNotExist() {
	IEGLModelStatus EGLModelStatus = getEGLModelStatus();
	return EGLModelStatus != null && EGLModelStatus.isDoesNotExist();
}
/**
 * Returns a printable representation of this exception suitable for debugging
 * purposes only.
 */
public String toString() {
	StringBuffer buffer= new StringBuffer();
	buffer.append("EGL Model Exception: "); //$NON-NLS-1$
	if (getException() != null) {
		if (getException() instanceof CoreException) {
			CoreException c= (CoreException)getException();
			buffer.append("Core Exception [code "); //$NON-NLS-1$
			buffer.append(c.getStatus().getCode());
			buffer.append("] "); //$NON-NLS-1$
			buffer.append(c.getStatus().getMessage());
		} else {
			buffer.append(getException().toString());
		}
	} else {
		buffer.append(getStatus().toString());
	}
	return buffer.toString();
}
}
