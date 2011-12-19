package org.eclipse.edt.java.jtopen.access;

import java.util.Calendar;

import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;


public class AS400Date extends com.ibm.as400.access.AS400Date {
	private static final long serialVersionUID = 1L;

	public AS400Date(int ibmiFormat) {
		super(ibmiFormat);
	}

	public AS400Date(int ibmiFormat, Character seperator) {
		super(ibmiFormat, seperator);
	}

	@Override
	public byte[] toBytes(Object object) {
		if(object instanceof Calendar){
			object = new java.sql.Date(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object);
	}
	@Override
	public int toBytes(Object object, byte[] bytes) {
		if(object instanceof Calendar){
			object = new java.sql.Date(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object, bytes);
	}
	@Override
	public int toBytes(Object object, byte[] bytes, int offset) {
		if(object instanceof Calendar){
			object = new java.sql.Date(((Calendar)object).getTimeInMillis());
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
			return EDate.asDate((Calendar)obj);
		}
		else if(obj instanceof java.sql.Date){
			return EDate.asDate(DateTimeUtil.getNewCalendar((java.sql.Date)obj));
		}
		else if(obj instanceof java.util.Date){
			return EDate.asDate(DateTimeUtil.getNewCalendar((java.util.Date)obj));
		}
		return obj;
	}
}
