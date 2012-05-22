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

import org.eclipse.edt.mof.EMetadataObject;

/**
 * This class is the superclass for all instances of <code>AnnotationType</code>.
 * Annotation values are meta-data that are associated to other model
 * elements to provide extra information beyond the information provided within
 * the given element itself. 
 * 
 * For instance the following field declaration adds an instance of the
 * Annotation <code>egl.core.ui.DisplayName</code> to the field declaration
 * @example f1 char(20) { @DisplayName{"Form Field 1" }};
 * 
 * @version 8.0.0
 *
 */
public interface Annotation extends Element, EMetadataObject {
	
	/**
	 * Dynamic interface method returns value of field where
	 * that field is the first or only field of the AnnotationType
	 * of the receiver.
	 * 
	 * @return java.lang.Object
	 */
	public Object getValue();
	
	/**
	 * Dynamic interface method that sets <code>value</code> into
	 * first or only field of the AnnotationType of the receiver
	 * 
	 * @param value
	 */
	public void setValue(Object value);
	
	/**
	 * Dynamic interface method to retrieve a value from the field
	 * with the same name as the given <code>key</code>
	 * @param key
	 * @return
	 */
	public Object getValue(String key);
	
	/**
	 * Dynamic interface method to set the <code>value</code> into the field
	 * with the same name as the given <code>key</code>
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key,Object value);
}
