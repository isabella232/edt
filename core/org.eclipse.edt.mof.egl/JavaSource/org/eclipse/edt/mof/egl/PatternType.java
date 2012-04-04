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


/**
 * FixedPrecisionTypes are used to model parameterizable types that can be
 * parameterized with a string that represents the internal format of the
 * data type.  Typical examples of the are the TIMESTAMP and INTERVAL types.
 *
 */
public interface PatternType extends ParameterizedType {
	String getPattern();
	
	void setPattern(String value);
	
}
