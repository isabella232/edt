/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.utils;

public interface IConstants {
	
	public static final String EGL_BIN_FOLDER_NAME = "eglbin"; //$NON-NLS-1$
	public static final String JAVASCRIPT_FOLDER_NAME = "javascript"; //$NON-NLS-1$
	public static final String WEB_CONTENT_FOLDER_NAME = "webcontent"; //$NON-NLS-1$
	public static final String DEPLOY_CONFIG_FILE_NAME = ".deployconfig.xml"; //$NON-NLS-1$
	public static final String HTML_FILE_EXTENSION = "html"; //$NON-NLS-1$
	public static final String PROPERTIES_FOLDER_NAME = "properties"; //$NON-NLS-1$
	public static final String RUNTIME_MESSAGES_DEPLOYMENT_FOLDER_NAME = "egl/messages"; //$NON-NLS-1$
	
	public static final String CONTEXT_ROOT_PARAMETER_NAME = "egl__contextRoot"; //$NON-NLS-1$
	public static final String DEFAULT_LOCALE_PARAMETER_NAME = "egl__defaultRuntimeMessagesLocale"; //$NON-NLS-1$
	public static final String DEFAULT_DD_PARAMETER_NAME = "egl__defaultDeploymentDescriptor"; //$NON-NLS-1$
	public static final String HTML_FILE_LOCALE = "egl__htmlLocale"; //$NON-NLS-1$
	public static final String DYNAMIC_LOAD_HANDLERS = "egl__dynamicLoadHandlers"; //$NON-NLS-1$
	
	public static final String JAVASCRIPT_RUNTIME_SYSTEM = "JAVASCRIPT"; //$NON-NLS-1$
	
	/**
	 * The name we register our RUI proxy servlet as in the WAS deployment descriptor
	 */
	public static final String RUI_PROXY_SERVLET_NAME = "EGLRichUIProxy"; //$NON-NLS-1$
	
	/**
	 * The RUI perspective ID.
	 */
//	public static final String RUI_Perspective_id = "com.ibm.etools.egl.rui.EGLRUIPerspective"; //$NON-NLS-1$
	
	/**
	 * EGL RUI Widget Projects
	 */
	public static final String RUI_WIDGETS_PROJECT_1_0_0_NAME = "com.ibm.egl.rui_1.0.0";
	public static final String RUI_WIDGETS_PROJECT_1_0_0_VERSION = "1.0.0";
	public static final String RUI_WIDGETS_PROJECT_1_0_1_NAME = "com.ibm.egl.rui_1.0.1";
	public static final String RUI_WIDGETS_PROJECT_1_0_1_VERSION = "1.0.1";
	public static final String RUI_WIDGETS_PROJECT_1_0_2_NAME = "com.ibm.egl.rui_1.0.2";
	public static final String RUI_WIDGETS_PROJECT_1_0_2_VERSION = "1.0.2";
	public static final String RUI_WIDGETS_PROJECT_1_0_3_NAME = "com.ibm.egl.rui_1.0.3";
	public static final String RUI_WIDGETS_PROJECT_1_0_3_VERSION = "1.0.3";
	public static final String RUI_WIDGETS_PROJECT_1_0_4_NAME = "com.ibm.egl.rui_1.0.4";
	public static final String RUI_WIDGETS_PROJECT_1_0_4_VERSION = "1.0.4";	
		
}
