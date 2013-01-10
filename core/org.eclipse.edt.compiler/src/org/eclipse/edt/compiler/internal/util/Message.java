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

public class Message {
	
	public static final int ERROR_MESSAGE = 1;
	public static final int WARNING_MESSAGE = 2;
	public static final int INFORMATIONAL_MESSAGE = 3;

	
	String text;
	int severity;	
	
	public Message(String text, int severity) {
		super();
		this.text = text;
		this.severity = severity;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public boolean isError() {
		return severity == ERROR_MESSAGE;
	}
}
