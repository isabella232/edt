/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.util;


public interface IGenerationResultsMessage {
	public final int EGL_ERROR_MESSAGE = EGLMessage.EGL_ERROR_MESSAGE;
	public final int EGL_WARNING_MESSAGE = EGLMessage.EGL_WARNING_MESSAGE;
	public final int EGL_INFORMATIONAL_MESSAGE = EGLMessage.EGL_INFORMATIONAL_MESSAGE;
	
	boolean isError();
	boolean isWarning();
	boolean isInformational();
	String getResourceName();
	int getSeverity();
	String getBuiltMessage();
	String getBuiltMessageWithLineAndColumn();
	String getId();
	int getStartOffset();
	int getEndOffset();
	int getStartLine();
        
}
