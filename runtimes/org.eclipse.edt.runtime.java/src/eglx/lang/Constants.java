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
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
@javax.xml.bind.annotation.XmlRootElement(name="Constants")
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
		ezeInitialize();
	}
	public void ezeInitialize() {
	}
	@org.eclipse.edt.javart.json.Json(name="isoDateFormat", clazz=EString.class, asOptions={})
	public String getIsoDateFormat() {
		return (isoDateFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="usaDateFormat", clazz=EString.class, asOptions={})
	public String getUsaDateFormat() {
		return (usaDateFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="eurDateFormat", clazz=EString.class, asOptions={})
	public String getEurDateFormat() {
		return (eurDateFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="jisDateFormat", clazz=EString.class, asOptions={})
	public String getJisDateFormat() {
		return (jisDateFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="isoTimeFormat", clazz=EString.class, asOptions={})
	public String getIsoTimeFormat() {
		return (isoTimeFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="usaTimeFormat", clazz=EString.class, asOptions={})
	public String getUsaTimeFormat() {
		return (usaTimeFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="eurTimeFormat", clazz=EString.class, asOptions={})
	public String getEurTimeFormat() {
		return (eurTimeFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="jisTimeFormat", clazz=EString.class, asOptions={})
	public String getJisTimeFormat() {
		return (jisTimeFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="db2TimeStampFormat", clazz=EString.class, asOptions={})
	public String getDb2TimeStampFormat() {
		return (db2TimeStampFormat);
	}
	@org.eclipse.edt.javart.json.Json(name="odbcTimeStampFormat", clazz=EString.class, asOptions={})
	public String getOdbcTimeStampFormat() {
		return (odbcTimeStampFormat);
	}
}
