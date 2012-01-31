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
package org.eclipse.edt.javart.ide;

import java.lang.reflect.Method;


import eglx.lang.SysLib;

public class MainProgramLauncher {
	
	public static final String MAIN_CLASS_ARG = "egl.main.class.name";
	public static final String IDE_PORT_ARG = "egl.ide.port";
	public static final String DD_FILES_ARG = "egl.dd.list";
	public static final String DEFAULT_DD_ARG = "egl.default.dd";
	
	public static void main(String[] args) throws Exception {
		String className = System.getProperty(MAIN_CLASS_ARG, null);
		if (className == null || (className = className.trim()).length() == 0) {
			System.err.println("ERROR: program class name not specified during launch. Exiting.");
			return;
		}
		
		String idePortStr = System.getProperty(IDE_PORT_ARG, null);
		if (idePortStr == null || (idePortStr = idePortStr.trim()).length() == 0) {
			System.err.println("ERROR: IDE port number not specified during launch. Exiting.");
			return;
		}
		
		int idePort;
		try {
			idePort = Integer.parseInt(idePortStr);
		}
		catch (NumberFormatException e) {
			System.err.println("ERROR: IDE port number value \"" + idePortStr + "\" not a number. Exiting.");
			return;
		}
		
		if (idePort < 0) {
			System.err.println("ERROR: IDE port number \"" + idePortStr + "\" invalid. Exiting.");
			return;
		}
		
		// Used to locate *.egldd files and handle special binding URIs.
		IDEResourceLocator resourceLocator = new IDEResourceLocator();
		IDEBindingResourceProcessor bindingProcessor = new IDEBindingResourceProcessor(idePort, resourceLocator);
		
		// DD files aren't a required setting.
		String ddFiles = System.getProperty(DD_FILES_ARG, null);
		if (ddFiles != null && (ddFiles = ddFiles.trim()).length() != 0) {
			resourceLocator.parseDDArgument(ddFiles, true);
		}
		
		String defaultDD = System.getProperty(DEFAULT_DD_ARG, null);
		bindingProcessor.setDefaultDD(defaultDD);
		
		SysLib.setBindingResourceProcessor( bindingProcessor );
		
		Class clazz = Class.forName(className);
		Method method = clazz.getMethod("main", String[].class);
		method.invoke(null, new Object[] {args});
	}
}
