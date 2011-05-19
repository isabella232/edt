/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;

import java.util.Locale;

import org.eclipse.edt.javart.resources.JavartProperties;
import org.eclipse.edt.javart.resources.LocalizedText;
import org.eclipse.edt.javart.resources.RecoverableResource;
import org.eclipse.edt.javart.resources.ResourceManager;
import org.eclipse.edt.javart.resources.StartupInfo;
import org.eclipse.edt.javart.resources.Trace;

public interface RunUnit {
	
	ResourceManager getResourceManager();
	JavartProperties getProperties();
	void commit() throws JavartException;
	void rollback() throws JavartException;
	void registerResource(RecoverableResource rs);
	void unregisterResource(RecoverableResource rs);
	void start(Program exec, String...args) throws Exception;
	void debugStart(Program exec, String...args) throws Exception;
	void exit();
	void transferCleanup(boolean isTransaction);
	StartupInfo getStartupInfo();
	void endRunUnit(Executable exec) throws Exception;
	void endRunUnit(Executable exec, Exception ex) throws Exception;
	int getReturnCode();

	Executable getExecutable(String name) throws JavartException;
	void switchLocale(Locale loc);
	LocalizedText getLocalizedText();
	String getDefaultDateFormat();
	String getDefaultTimestampFormat();
	String getDefaultTimeFormat();
	String getDefaultNumericFormat();
	Trace getTrace();
}
