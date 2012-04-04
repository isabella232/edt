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
import java.util.TimeZone;

import org.eclipse.edt.runtime.java.eglx.lang.EDate;

import eglx.jtopen.IBMiConnection;
import eglx.lang.StringLib;


public class AS400Date extends com.ibm.as400.access.AS400Date {
	private static final long serialVersionUID = 1L;

	private SimpleDateFormat sdf;

	public AS400Date(TimeZone tz) {
		super(tz);
	}

	public AS400Date(Integer ibmiFormat, String seperator, String timeZoneID, IBMiConnection conn) {
		this(AS400DateTimeUtil.getIBMiTimezone(timeZoneID, conn));
		if(ibmiFormat == null){
			ibmiFormat = conn.getDateFormat();
		}
		seperator = AS400DateTimeUtil.getConnSeparator(seperator, conn);
		if(ibmiFormat != null && seperator != null && seperator.isEmpty()){
			setFormat(ibmiFormat.intValue());
		}
		else if(ibmiFormat != null){
			Character seperatorChar = seperator == null ? null : Character.valueOf(seperator.charAt(0));
			setFormat(ibmiFormat.intValue(), seperatorChar);
		}
	}

	@Override
	public byte[] toBytes(Object object) {
		if(object instanceof Calendar){
			object = toSqlDate((Calendar)object);
		}
		return super.toBytes(object);
	}

	@Override
	public int toBytes(Object object, byte[] bytes) {
		if(object instanceof Calendar){
			object = toSqlDate((Calendar)object);
		}
		return super.toBytes(object, bytes);
	}
	@Override
	public int toBytes(Object object, byte[] bytes, int offset) {
		if(object instanceof Calendar){
			object = toSqlDate((Calendar)object);
		}
		return super.toBytes(object, bytes, offset);
	}

	private Object toSqlDate(Calendar cal) {
		Object returnVal = null;
		try {
			returnVal = new java.sql.Date(ibmiSimpleDateFormat().parse(StringLib.format(cal, "MM/dd/yyyy")).getTime());
		} catch (ParseException e) { }
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
			return EDate.asDate(((Calendar)obj));
		}
		else if(obj instanceof java.sql.Date){
			return EDate.asDate(ibmiSimpleDateFormat().format((java.sql.Date)obj));
		}
		else if(obj instanceof java.util.Date){
			return EDate.asDate(ibmiSimpleDateFormat().format((java.util.Date)obj));
		}
		return obj;
	}

	private SimpleDateFormat ibmiSimpleDateFormat(){
		if(sdf == null){
			sdf = new SimpleDateFormat("MM/dd/yyyy");
			sdf.setTimeZone(getTimeZone());
		}
		return sdf;
	}
}
