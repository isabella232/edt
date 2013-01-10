/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl;

/**
 * Enumeration that defines the various types of access policies that can be 
 * associated with Classifiers and Members.
 * 
 * @version 8.0.0
 */
public enum AccessKind {
	/**
	 * Element marked as ACC_PUBLIC are accessible to any client
	 */
	ACC_PUBLIC,
	
	/**
	 * Elements marked as ACC_PRIVATE are accessible only to the 
	 * name scope in which it has been defined
	 */
	ACC_PRIVATE,
	
	/**
	 * Element marked as ACC_PROTECTED are accessible only to other
	 * clients that have been defined within the same Package Scope
	 */
	ACC_PROTECTED
}
