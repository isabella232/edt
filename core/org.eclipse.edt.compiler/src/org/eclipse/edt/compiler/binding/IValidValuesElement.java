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
package org.eclipse.edt.compiler.binding;

import java.io.Serializable;


/**
 * @author Dave Murray
 */
public interface IValidValuesElement extends Serializable{
	boolean isString();
	boolean isInt(); 
	boolean isFloat();
	boolean isRange();

	String getStringValue();
	long getLongValue();
	double getFloatValue();
	/**
	 * An array with 2 elements.
	 */
	IValidValuesElement[] getRangeElements();
}
