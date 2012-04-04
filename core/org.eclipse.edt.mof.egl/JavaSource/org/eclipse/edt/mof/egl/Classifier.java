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

/**
 * Abstract super class for all Classifiers.  Classifiers represent the
 * different kinds of type structures that are represented in the model.
 * 
 * @version 8.0.0
 *
 */
public interface Classifier extends NamedElement, Type {
	/**
	 * List of TypeParameter that can be associated with
	 * given classifier.  Type arguments are then matched
	 * against these parameters in GenericType instances
	 * 
	 * @see TypeParameter
	 * @see GenericType
	 * @return
	 */
	List<TypeParameter> getTypeParameters();
	
	/**
	 * @return File name where the Classifier declaration was created
	 */
	String getFileName();
	
	/**
	 * Set value of file name where given declaration was made
	 * @param value
	 */
	void setFileName(String value);
	
	/**
	 * 
	 * @return whether or not the given declaration had compile errors in it
	 */
	Boolean hasCompileErrors();
	
	/**
	 * Set boolean value for whether or not the given declaration
	 * has compile errors in it
	 * @param value 
	 */
	void setHasCompileErrors(Boolean value);
	
	/**
	 * 
	 * @return declared package name of the receiver
	 */
	String getPackageName();
	
	/**
	 * Set package name of the receiver
	 * @param value
	 */
	void setPackageName(String value);
	
	List<Stereotype> getStereotypes();
	
	/**
	 * Return the first (often only) Stereotype out of the list of stereotypes
	 * @return Stereotype
	 */
	public Stereotype getStereotype();
	
	/**
	 * Return a boolean stating whether or not the given Classifier can be the target of a NEW expression
	 * @return
	 */
	public boolean isInstantiable();
}
