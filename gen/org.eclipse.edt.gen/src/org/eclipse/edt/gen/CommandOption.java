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
package org.eclipse.edt.gen;

public class CommandOption {
	private String internalName;
	private String[] aliases;
	private CommandParameter parameter;

	public CommandOption(String internalName, String[] aliases, CommandParameter parameter) {
		this.internalName = internalName;
		this.aliases = aliases;
		this.parameter = parameter;
	}

	public String getInternalName() {
		return internalName;
	}

	public String[] getAliases() {
		return aliases;
	}

	public CommandParameter getParameter() {
		return parameter;
	}
}
