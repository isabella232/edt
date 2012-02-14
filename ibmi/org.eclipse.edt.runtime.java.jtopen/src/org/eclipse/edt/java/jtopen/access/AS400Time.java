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

import java.util.Calendar;

import com.ibm.as400.access.AS400;


public class AS400Time extends com.ibm.as400.access.AS400Time {
	private static final long serialVersionUID = 1L;

	public AS400Time(int ibmiFormat, String timeZoneID) {
		super(AS400DateTimeUtil.getIBMiTimezoneID(timeZoneID), ibmiFormat);
	}
	public AS400Time(int ibmiFormat, AS400 system) {
		super(AS400DateTimeUtil.getIBMiTimezoneID(system), ibmiFormat);
	}

	public AS400Time(int ibmiFormat, Character seperator, AS400 system ){
		super(AS400DateTimeUtil.getIBMiTimezoneID(system), ibmiFormat, seperator);
	}

	public AS400Time(int ibmiFormat, Character seperator, String timeZoneID ) {
		super(AS400DateTimeUtil.getIBMiTimezoneID(timeZoneID), ibmiFormat, seperator);
	}

	@Override
	public byte[] toBytes(Object object) {
		if(object instanceof Calendar){
			object = new java.sql.Time(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object);
	}
	@Override
	public int toBytes(Object object, byte[] bytes) {
		if(object instanceof Calendar){
			object = new java.sql.Time(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object, bytes);
	}
	@Override
	public int toBytes(Object object, byte[] bytes, int offset) {
		if(object instanceof Calendar){
			object = new java.sql.Time(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object, bytes, offset);
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
//FIXME			return ETime.asDate((Calendar)obj);
		}
		else if(obj instanceof java.sql.Time){
//FIXME				return ETime.asDate(DateTimeUtil.getNewCalendar((java.sql.Time)obj));
		}
		else if(obj instanceof java.util.Date){
//FIXME				return ETime.asDate(DateTimeUtil.getNewCalendar((java.util.Date)obj));
		}
		return obj;
	}
}
