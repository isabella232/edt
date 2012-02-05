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
	public static final String parameter_contribution = "contribution";
	public static final String parameter_report = "report";
	public static final String parameter_report_dir = "reportDir";
	public static final String parameter_report_dir_default = "genReports/";

	// Extension to use for generation reports
	public static final String report_fileExtension = ".html";

	// temporary variable prefix
	public static final String temporaryVariablePrefix = "eze$Temp";

	// these are sub key values used as context hashmap keys
	public static final String SubKey_functionArgumentTemporaryVariable = "functionArgumentTemporaryVariable";
	public static final String SubKey_functionHasReturnStatement = "functionHasReturnStatement";
	public static final String SubKey_functionArgumentNeedsWrapping = "functionArgumentNeedsWrapping";
	public static final String SubKey_statementNeedsLabel = "statementNeedsLabel";
	public static final String SubKey_statementHasBeenReorganized = "statementHasBeenReorganized";
	public static final String SubKey_fieldsProcessed4Resource = "fieldsProcessed4Resource";

	// these are sub key values used on annotations 
	public static final String SubKey_uri = "uri";

	// invoke names
	public static final String isAssignmentBreakupWanted = "isAssignmentBreakupWanted";
	public static final String isAssignmentArrayMatchingWanted = "isAssignmentArrayMatchingWanted";
	public static final String isListReorganizationWanted = "isListReorganizationWanted";
	public static final String isMathLibDecimalBoxingWanted = "isMathLibDecimalBoxingWanted";
	public static final String isStringLibFormatBoxingWanted = "isStringLibFormatBoxingWanted";
	public static final String isStatementRequiringWrappedParameters = "isStatementRequiringWrappedParameters";

	// part names
	public static final String AnnotationXmlAttribute = "eglx.xml.binding.annotation.xmlAttribute";
	public static final String AnnotationXMLRootElement = "eglx.xml.binding.annotation.XMLRootElement";
	public static final String AnnotationXmlElement = "eglx.xml.binding.annotation.xmlElement";
	public static final String AnnotationJsonName = "eglx.json.JsonName";
	public static final String AnnotationDedicatedService = "eglx.services.DedicatedService";
	public static final String AnnotationResource = "eglx.lang.Resource";
	public static final String PartHttpRest = "eglx.http.HttpRest";
	public static final String LibrarySys = "eglx.lang.SysLib";
	public static final String AnnotationXMLStructureKind = "eglx.xml.binding.annotation.XMLStructureKind";
	public static final String AnnotationXMLValue = "eglx.xml.binding.annotation.XMLValue";
}
