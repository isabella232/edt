/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.java.jtopen.access;

import java.math.BigDecimal;
import java.math.BigInteger;


public class AS400UnsignedBin8 extends com.ibm.as400.access.AS400UnsignedBin8 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	
	@Override
	public byte[] toBytes(Object arg0) {
		if(arg0 instanceof BigDecimal){
			return super.toBytes(((BigDecimal)arg0).toBigInteger());
		}
		else{
			return super.toBytes(arg0);
		}
	}
	@Override
	public int toBytes(Object arg0, byte[] arg1) {
		if(arg0 instanceof BigDecimal){
			return super.toBytes(((BigDecimal)arg0).toBigInteger(), arg1);
		}
		else{
			return super.toBytes(arg0, arg1);
		}
	}
	@Override
	public int toBytes(Object arg0, byte[] arg1, int arg2) {
		if(arg0 instanceof BigDecimal){
			return super.toBytes(((BigDecimal)arg0).toBigInteger(), arg1, arg2);
		}
		else{
			return super.toBytes(arg0, arg1, arg2);
		}
	}
	@Override
	public Object toObject(byte[] arg0) {
		Object retVal = super.toObject(arg0);
		if(retVal instanceof BigInteger){
			return new BigDecimal((BigInteger)retVal);
		}
		else{
			return new BigDecimal(retVal.toString());
		}
	}
	@Override
	public Object toObject(byte[] arg0, int arg1) {
		Object retVal = super.toObject(arg0, arg1);
		if(retVal instanceof BigInteger){
			return new BigDecimal((BigInteger)retVal);
		}
		else{
			return new BigDecimal(retVal.toString());
		}
	}
}
