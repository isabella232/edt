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
package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="ConstantsLib")
public class ConstantsLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	private static final String ezeConst_NEWLINE = "\r\n";
	public final String NEWLINE = ezeConst_NEWLINE;
	private static final String ezeConst_EXIT_PREFIX = "STATUS: ";
	public final String EXIT_PREFIX = ezeConst_EXIT_PREFIX;
	private static final int ezeConst_SPASSED = 0;
	public final int SPASSED = ezeConst_SPASSED;
	private static final int ezeConst_SFAILED = 1;
	public final int SFAILED = ezeConst_SFAILED;
	private static final int ezeConst_SERROR = 2;
	public final int SERROR = ezeConst_SERROR;
	private static final int ezeConst_SNOT_RUN = 3;
	public final int SNOT_RUN = ezeConst_SNOT_RUN;
	private static final int ezeConst_SBAD = 4;
	public final int SBAD = ezeConst_SBAD;
	public ConstantsLib() {
		super();
	}
	{
		ezeInitialize();
	}
	public void ezeInitialize() {
	}
	public String getNEWLINE() {
		return NEWLINE;
	}
	public String getEXIT_PREFIX() {
		return EXIT_PREFIX;
	}
	public int getSPASSED() {
		return SPASSED;
	}
	public int getSFAILED() {
		return SFAILED;
	}
	public int getSERROR() {
		return SERROR;
	}
	public int getSNOT_RUN() {
		return SNOT_RUN;
	}
	public int getSBAD() {
		return SBAD;
	}
}
