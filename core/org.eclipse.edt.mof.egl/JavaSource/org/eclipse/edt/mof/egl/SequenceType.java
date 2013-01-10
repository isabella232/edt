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
 * SequenceTypes are used to model parameterizable types that represent
 * a contiguous sequence of data of a given length where the format of 
 * each element is governed by the base parameterizable type.  A typical example 
 * of this is CHAR(10) which is a sequence of length 10 of a single byte
 * character format. 
 *
 */
public interface SequenceType extends ParameterizedType {
	Integer getLength();
	
	void setLength(Integer value);
	
}
