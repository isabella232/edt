package org.eclipse.edt.runtime.java.eglx.xml;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;

public class DateTimeAdapter extends DateTimeBase {

	@Override
	public String marshal(Calendar v) throws Exception {
		return DatatypeConverter.printDateTime(v);
	}

	@Override
	public Calendar unmarshal(String v) throws Exception {
		return ETimestamp.asTimestamp(DatatypeConverter.parseDateTime(v));
	}

}
