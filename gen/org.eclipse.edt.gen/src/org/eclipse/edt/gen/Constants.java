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
package org.eclipse.edt.gen;

public class Constants {
	private Constants() {}

	// command parameter internal names
	public static final String parameter_output = "output";
	public static final String parameter_part = "part";
	public static final String parameter_root = "root";
	public static final String parameter_report = "report";

	// Extension to use for generation reports
	public static final String report_fileExtension = ".html";

	// temporary variable prefix
	public static final String temporaryVariablePrefix = "eze$Temp";

	// these are sub key values used as context hashmap keys
	public static final String SubKey_functionArgumentTemporaryVariable = "functionArgumentTemporaryVariable";
	public static final String SubKey_functionHasReturnStatement = "functionHasReturnStatement";
	public static final String SubKey_statementNeedsLabel = "statementNeedsLabel";
	public static final String SubKey_callStatementTempVariables = "callStatementTempVariables";
	public static final String SubKey_FunctionParameterIsConst = "functionParameterIsConst";
}
