/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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



public class AS400Bin1 extends com.ibm.as400.access.AS400Bin1 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	@Override
	public byte[] toBytes(Object arg0) {
		if(arg0 instanceof Short){
			arg0 = ((Short) arg0).byteValue();
		}
		return super.toBytes(arg0);
	}

	@Override
	public int toBytes(Object arg0, byte[] arg1) {
		if(arg0 instanceof Short){
			arg0 = ((Short) arg0).byteValue();
		}
		return super.toBytes(arg0, arg1);
	}
	
	@Override
	public int toBytes(Object arg0, byte[] arg1, int arg2) {
		if(arg0 instanceof Short){
			arg0 = ((Short) arg0).byteValue();
		}
		return super.toBytes(arg0, arg1, arg2);
	}
	
	@Override
	public Object toObject(byte[] arg0) {
		Object obj = super.toObject(arg0);
		if(obj instanceof Byte){
			return ((Byte) obj).shortValue();
		}
		return obj;
	}
	@Override
	public Object toObject(byte[] arg0, int arg1) {
		Object obj = super.toObject(arg0, arg1);
		if(obj instanceof Byte){
			return ((Byte) obj).shortValue();
		}
		return obj;
	}
}
