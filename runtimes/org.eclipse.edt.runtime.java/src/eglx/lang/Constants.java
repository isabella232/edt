/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.lang;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
@SuppressWarnings("unused")

public class Constants extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	private static final String ezeConst_isoDateFormat = "yyyy-MM-dd";
	public final String isoDateFormat = ezeConst_isoDateFormat;
	private static final String ezeConst_usaDateFormat = "MM/dd/yyyy";
	public final String usaDateFormat = ezeConst_usaDateFormat;
	private static final String ezeConst_eurDateFormat = "dd.MM.yyyy";
	public final String eurDateFormat = ezeConst_eurDateFormat;
	private static final String ezeConst_jisDateFormat = "yyyy-MM-dd";
	public final String jisDateFormat = ezeConst_jisDateFormat;
	private static final String ezeConst_isoTimeFormat = "HH.mm.ss";
	public final String isoTimeFormat = ezeConst_isoTimeFormat;
	private static final String ezeConst_usaTimeFormat = "hh:mm a";
	public final String usaTimeFormat = ezeConst_usaTimeFormat;
	private static final String ezeConst_eurTimeFormat = "HH.mm.ss";
	public final String eurTimeFormat = ezeConst_eurTimeFormat;
	private static final String ezeConst_jisTimeFormat = "HH:mm:ss";
	public final String jisTimeFormat = ezeConst_jisTimeFormat;
	private static final String ezeConst_db2TimeStampFormat = "yyyy-MM-dd-HH.mm.ss.SSSSSS";
	public final String db2TimeStampFormat = ezeConst_db2TimeStampFormat;
	private static final String ezeConst_odbcTimeStampFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS";
	public final String odbcTimeStampFormat = ezeConst_odbcTimeStampFormat;
	public Constants() {
		super();
	}
	{
		ezeInitialize();
	}
	public void ezeInitialize() {
	}
	public String getIsoDateFormat() {
		return isoDateFormat;
	}
	public String getUsaDateFormat() {
		return usaDateFormat;
	}
	public String getEurDateFormat() {
		return eurDateFormat;
	}
	public String getJisDateFormat() {
		return jisDateFormat;
	}
	public String getIsoTimeFormat() {
		return isoTimeFormat;
	}
	public String getUsaTimeFormat() {
		return usaTimeFormat;
	}
	public String getEurTimeFormat() {
		return eurTimeFormat;
	}
	public String getJisTimeFormat() {
		return jisTimeFormat;
	}
	public String getDb2TimeStampFormat() {
		return db2TimeStampFormat;
	}
	public String getOdbcTimeStampFormat() {
		return odbcTimeStampFormat;
	}
}
