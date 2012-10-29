/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.mof.EClass;

/**
 * Classifier used to front EClass instances so that EGL can make valid references
 * to EClasses which are not EGL types.  The typical examples of this exist in the
 * <code>egl.lang.reflect</code> package which has instances of this class to represent
 * as EGL types this EGL model itself so that Stereotypes can make references to things
 * like Fields and Functions.
 *
 */
public interface EClassProxy extends EGLClass {
	EClass getProxiedEClass();
	String getProxiedEClassName();
	void setProxiedEClassName(String value);
	
}
