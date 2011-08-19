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
package org.eclipse.edt.ide.deployment.internal;

import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.internal.generation.GenerationResultsMessage;

public class EGLMessage {

	public static final String EGL_DEPLOYMENT_DEPLOYED_DYNAMIC_LOADING_FILE = "";
	public static final String EGL_DEPLOYMENT_FAILED = "";
	public static final String EGL_DEPLOYMENT_COMPLETE = "";
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_DYNAMIC_LOADING_FILE = "";
	public static final String EGL_DEPLOYMENT_EXCEPTION = "";
	public static final String EGL_DEPLOYMENT_DEPLOYED_HTML_FILE = "";
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_HTML_FILE = "";
	public static final String EGL_DEPLOYMENT_DEPLOYED_PROPERTY_FILE = "";
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_PROPERTY_FILE = "";
	public static final String EGL_DEPLOYMENT_DEPLOYED_RT_PROPERTY_FILE = "";
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_RT_PROPERTY_FILE = "";
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_PROPERTIES_FOLDER = "";
	public static final String EGL_DEPLOYMENT_DEPLOYED_RT_MSG_BUNDLE = "";
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_RT_MSG_BUNDLE = "";
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_RT_MSG_BUNDLE_FOLDER = "";
	public static final String EGL_DEPLOYMENT_DEPLOYED_BIND_FILE = "";
	public static final String EGL_DEPLOYMENT_FAILED_LOCATE_EGLDD_FILE = "";
	public static final String EGL_DEPLOYMENT_DEPLOYING_RUIHANDLER = "";
	
	public static IGenerationResultsMessage createEGLDeploymentErrorMessage(String messageID, Object messageContributor, String[] inserts) {
		return new GenerationResultsMessage(new org.eclipse.edt.compiler.internal.util.EGLMessage());
	}
	public static IGenerationResultsMessage createEGLDeploymentInformationalMessage(String messageID, Object messageContributor, String[] inserts) {
		return new GenerationResultsMessage(new org.eclipse.edt.compiler.internal.util.EGLMessage());
	}
	
	
}
