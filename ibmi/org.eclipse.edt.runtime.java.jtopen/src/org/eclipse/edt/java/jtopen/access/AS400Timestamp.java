package org.eclipse.edt.java.jtopen.access;

import java.util.Calendar;

import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;


public class AS400Timestamp extends com.ibm.as400.access.AS400Timestamp {
	private static final long serialVersionUID = 1L;

	private int startCode;
	private int endCode;
	
	public AS400Timestamp(int ibmiFormat, int startCode, int endCode) {
		super();
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
			object = new java.sql.Timestamp(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object);
	}
	@Override
	public int toBytes(Object object, byte[] bytes) {
		if(object instanceof Calendar){
			object = new java.sql.Timestamp(((Calendar)object).getTimeInMillis());
		}
		return super.toBytes(object, bytes);
	}
	@Override
	public int toBytes(Object object, byte[] bytes, int offset) {
		if(object instanceof Calendar){
			object = new java.sql.Timestamp(((Calendar)object).getTimeInMillis());
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
			return ETimestamp.asTimestamp((Calendar)obj, startCode, endCode);
		}
		else if(obj instanceof java.sql.Timestamp){
			return ETimestamp.asTimestamp(DateTimeUtil.getNewCalendar((java.sql.Timestamp)obj), startCode, endCode);
		}
		else if(obj instanceof java.util.Date){
			return ETimestamp.asTimestamp(DateTimeUtil.getNewCalendar((java.util.Date)obj), startCode, endCode);
		}
		return obj;
	}
}
