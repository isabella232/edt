package org.eclipse.edt.runtime.java.eglx.xml;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.edt.runtime.java.eglx.lang.ETime;

public class TimeAdapter extends DateTimeBase {

	@Override
	public String marshal(Calendar v) throws Exception {
		return stripTimeZone(DatatypeConverter.printTime(convertToGmt(v)));
	}

	@Override
	public Calendar unmarshal(String v) throws Exception {
		return ETime.asTime(DatatypeConverter.parseTime(v));
	}

}
