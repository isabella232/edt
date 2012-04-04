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

import java.util.List;

import org.eclipse.edt.mof.EClass;


/**
 * All Annotation subclasses are instances of this class.
 * 
 * @version 8.0.0
 *
 */
public interface AnnotationType extends EClass, Part {
	
	/**
	 * This list specifies the set of elements that are valid targets
	 * to associate values of the given AnnotationType to.
	 * 
	 * @see ElementKind
	 * @return
	 */
	List<ElementKind> getTargets();
	
	String getValidationProxy();
	void setValidationProxy(String className);

	
}
