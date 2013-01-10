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
package eglx.jtopen;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;

import com.ibm.as400.access.AS400;

import eglx.lang.AnyException;

public abstract class IBMiConnection {
	
	private static final String SYSTEM = "system";
	private static final String DATE_FORMAT = "dateFormat";
	private static final String DATE_SEPARATOR_CHAR = "dateSeparatorChar";
	private static final String TIME_SEPARATOR_CHAR = "timeSeparatorChar";
	private static final String TIME_FORMAT = "timeFormat";
	private static final String PASSWORD = "password";
	private static final String USER_ID = "userId";
	private static final String ENCODING = "encoding";
	private static final String LIBRARY = "library";
	private static final String TIMEZONE = "timezone";
	private String system;
	private String library;
	private String encoding;
	private String userid;
	private String password;
	private String timezone;
	private String dateSeparatorChar;
	private String timeSeparatorChar;
	private Integer dateFormat;
	private Integer timeFormat;
	protected Binding binding;

	public abstract AS400 getAS400() throws AnyException;

	public String getLibrary() {
		return library != null ? library : (String)getParameterValue(LIBRARY);
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getEncoding() {
		return encoding != null ? encoding : (String)getParameterValue(ENCODING);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getUserid() {
		return userid != null ? userid : (String)getParameterValue(USER_ID);
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password != null ? password : (String)getParameterValue(PASSWORD);
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDateFormat() {
		if(dateFormat != null){
			return dateFormat;
		}
		else{
			Object dt = getParameterValue(DATE_FORMAT);
			return dt == null ? null : Integer.decode(dt.toString());
		}
	}

	public void setDateFormat(int dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getSystem() {
		return system != null ? system : (String)getParameterValue(SYSTEM);
	}

	public void setSystem(String system) {
		this.system = system;
	}
	
	public String getTimezone() {
		return timezone != null ? timezone : (String)getParameterValue(TIMEZONE);
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	
	public String getDateSeparatorChar() {
		return dateSeparatorChar != null ? dateSeparatorChar : (String)getParameterValue(DATE_SEPARATOR_CHAR);
	}

	public void setDateSeparatorChar(String dateSeparatorChar) {
		this.dateSeparatorChar = dateSeparatorChar;
	}

	public String getTimeSeparatorChar() {
		return timeSeparatorChar != null ? timeSeparatorChar : (String)getParameterValue(TIME_SEPARATOR_CHAR);
	}

	public void setTimeSeparatorChar(String timeSeparatorChar) {
		this.timeSeparatorChar = timeSeparatorChar;
	}

	public Integer getTimeFormat() {
		if(timeFormat != null){
			return timeFormat;
		}
		else{
			Object dt = getParameterValue(TIME_FORMAT);
			return dt == null ? null : Integer.decode(dt.toString());
		}
	}

	public void setTimeFormat(Integer timeFormat) {
		this.timeFormat = timeFormat;
	}

	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
	}

	private Object getParameterValue(String key) {
		Parameter parameter = null;
		if(binding != null){
			parameter = binding.getParameter(key);
		}
		return parameter == null ? null : parameter.getValue();
	}

}
