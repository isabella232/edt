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

import java.io.UnsupportedEncodingException;

import org.eclipse.edt.runtime.java.eglx.lang.EString;

import com.ibm.as400.access.BidiConversionProperties;

import eglx.jtopen.IBMiConnection;


public class AS400Text extends com.ibm.as400.access.AS400Text {
	private static final long serialVersionUID = 1L;

	private boolean retainTrailingSpaces;
	private int characterLength;
	public AS400Text(int length, IBMiConnection conn, boolean retainTrailingSpaces) {
		this(length, getSystemEncoding(conn), retainTrailingSpaces);
	}

	public AS400Text(int length, String encoding, boolean retainTrailingSpaces) {
		super(calculateByteLength(length, encoding), encoding);
		this.retainTrailingSpaces = retainTrailingSpaces;
		characterLength = length;
	}

	private static String getSystemEncoding(IBMiConnection conn){
		String encoding = null;
		if(conn != null && (encoding = conn.getEncoding()) != null && !encoding.isEmpty()){
			return encoding;
		}
		else if(conn != null){
			try {
				if(conn.getAS400() != null){
					return conn.getAS400().getJobCCSIDEncoding();
				}
			} catch (Exception e) {}
		}
		
		return null;
	}

	private static int calculateByteLength(int length, String encoding){
		if(encoding != null && !encoding.isEmpty()){
			try {
				byte[] bytes = new String("A").getBytes(encoding);
				if(bytes != null && bytes.length > 1){
					return length + length;
				}
			} catch (UnsupportedEncodingException e) { }
		}
		return length;
	}
	
	@Override
	public byte[] toBytes(Object arg0) {
		return super.toBytes(pad(arg0));
	}
	@Override
	public int toBytes(Object arg0, byte[] arg1) {
		return super.toBytes(pad(arg0), arg1);
	}
	@Override
	public int toBytes(Object arg0, byte[] arg1, int arg2) {
		return super.toBytes(pad(arg0), arg1, arg2);
	}
	@Override
	public int toBytes(Object arg0, byte[] arg1, int arg2, BidiConversionProperties arg3) {
		return super.toBytes(pad(arg0), arg1, arg2, arg3);
	}
	@Override
	public int toBytes(Object arg0, byte[] arg1, int arg2, int arg3) {
		return super.toBytes(pad(arg0), arg1, arg2, arg3);
	}
	private Object pad(Object str){
		if(str instanceof String &&
				((String)str).length() < characterLength &&
				characterLength != getByteLength()){
			try{
				StringBuilder sb = new StringBuilder((String)str);
				while(sb.toString().getBytes(getEncoding()).length < getByteLength()){
					sb.append(' ');
				}
				return sb.toString();
			}
			catch(Exception e){}
		}
		return str;
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
