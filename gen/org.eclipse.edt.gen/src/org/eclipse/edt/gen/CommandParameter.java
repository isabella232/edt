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
package org.eclipse.edt.gen;

public class CommandParameter {
	private boolean required;
	private Object[] possibleValues;
	private Object value;
	private String promptText;

	public CommandParameter(boolean required, Object[] possibleValues, Object value, String promptText) {
		this.required = required;
		this.possibleValues = possibleValues;
		this.value = value;
		this.promptText = promptText;
	}

	public boolean isRequired() {
		return required;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object[] getPossibleValues() {
		return possibleValues;
	}

	public String getPromptText() {
		return promptText;
	}
}
