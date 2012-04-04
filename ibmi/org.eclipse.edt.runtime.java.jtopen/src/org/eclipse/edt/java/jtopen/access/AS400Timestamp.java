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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;

import eglx.jtopen.IBMiConnection;
import eglx.lang.StringLib;


public class AS400Timestamp extends com.ibm.as400.access.AS400Timestamp {
	private static final long serialVersionUID = 1L;

	private int startCode;
	private int endCode;
	private SimpleDateFormat sdf;
	
	public AS400Timestamp(Integer ibmiFormat, int startCode, int endCode, String timeZoneID, IBMiConnection conn) {
		super(AS400DateTimeUtil.getIBMiTimezone(timeZoneID, conn));
		this.startCode = startCode;
		this.endCode = endCode;
	}

	public int getStartCode() {
		return startCode;
	}
	public int getEndCode() {
		return endCode;
	}
	@Override
	public byte[] toBytes(Object object) {
		if(object instanceof Calendar){
			object = toSqlTimestamp((Calendar)object);
		}
		return super.toBytes(object);
	}
	@Override
	public int toBytes(Object object, byte[] bytes) {
		if(object instanceof Calendar){
			object = toSqlTimestamp((Calendar)object);
		}
		return super.toBytes(object, bytes);
	}
	@Override
	public int toBytes(Object object, byte[] bytes, int offset) {
		if(object instanceof Calendar){
			object = toSqlTimestamp((Calendar)object);
		}
		return super.toBytes(object, bytes, offset);
	}
	private Object toSqlTimestamp(Calendar cal) {
		Object returnVal = null;
		try {
			returnVal = new java.sql.Timestamp(ibmiSimpleDateFormat().parse(StringLib.format(cal, ETimestamp.DefaultFormatPattern)).getTime());
		} catch (ParseException e) { 
			e.printStackTrace();
		}
		return returnVal;
	}
	
	@Override
	public Object toObject(byte[] arg0) {
		Object obj = super.toObject(arg0);
		return convert(obj);
	}

	@Override
	public Object toObject(byte[] arg0, int arg1) {
		Object obj = super.toObject(arg0, arg1);
		return convert(obj);
	}
	private Object convert(Object obj) {
		if(obj instanceof Calendar){
			return ETimestamp.asTimestamp((Calendar)obj, startCode, endCode);
		}
		else if(obj instanceof java.sql.Timestamp){
			return ETimestamp.asTimestamp(ibmiSimpleDateFormat().format((java.sql.Timestamp)obj), startCode, endCode);
		}
		else if(obj instanceof java.util.Date){
			return ETimestamp.asTimestamp(ibmiSimpleDateFormat().format((java.util.Date)obj), startCode, endCode);
		}
		return obj;
	}
	private SimpleDateFormat ibmiSimpleDateFormat(){
		if(sdf == null){
			sdf = new SimpleDateFormat(ETimestamp.DefaultFormatPattern);
			sdf.setTimeZone(getTimeZone());
		}
		return sdf;
	}

}
