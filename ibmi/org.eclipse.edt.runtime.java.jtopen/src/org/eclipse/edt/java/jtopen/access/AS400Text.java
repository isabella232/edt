/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.java.jtopen.access;

import org.eclipse.edt.runtime.java.eglx.lang.EString;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.BidiConversionProperties;


public class AS400Text extends com.ibm.as400.access.AS400Text {
	private static final long serialVersionUID = 1L;

	private boolean retainTrailingSpaces;
	public AS400Text(int length, AS400 system, boolean retainTrailingSpaces) {
		super(length, system);
		this.retainTrailingSpaces = retainTrailingSpaces;
	}

	public AS400Text(int length, String encoding, boolean retainTrailingSpaces) {
		super(length, encoding);
		this.retainTrailingSpaces = retainTrailingSpaces;
	}

	@Override
	public Object toObject(byte[] arg0) {
		Object retVal = super.toObject(arg0);
		if(!retainTrailingSpaces && retVal instanceof String){
			retVal = EString.clip((String)retVal);
		}
		return retVal;
	}
	@Override
	public Object toObject(byte[] arg0, int arg1) {
		Object retVal = super.toObject(arg0, arg1);
		if(!retainTrailingSpaces && retVal instanceof String){
			retVal = EString.clip((String)retVal);
		}
		return retVal;
	}
	@Override
	public Object toObject(byte[] arg0, int arg1, BidiConversionProperties arg2) {
		Object retVal = super.toObject(arg0, arg1, arg2);
		if(!retainTrailingSpaces && retVal instanceof String){
			retVal = EString.clip((String)retVal);
		}
		return retVal;
	}
	@Override
	public Object toObject(byte[] arg0, int arg1, int arg2) {
		Object retVal = super.toObject(arg0, arg1, arg2);
		if(!retainTrailingSpaces && retVal instanceof String){
			retVal = EString.clip((String)retVal);
		}
		return retVal;
	}
}
