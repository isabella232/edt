/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core;

import org.eclipse.osgi.util.NLS;




/**
 * @author jshavor
 *
 */

public class CoreIDEPluginStrings extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.core.CoreIDEPluginResources"; //$NON-NLS-1$

	private CoreIDEPluginStrings() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, CoreIDEPluginStrings.class);
	}

	// Generate Parts Operation
	public static String GeneratePartsOperation_TaskName;
	public static String GeneratePartsOperation_SubTaskName;
	public static String GeneratePartsOperation_JobName;

	// Generate Command File Operation
	public static String GenerateCommandFileOperation_TaskName;
	public static String GenerateCommandFileOperation_SubTaskName;
	public static String GenerateCommandFileOperation_ErrorMessage_Title;
	public static String GenerateCommandFileOperation_ErrorMessage_Message;
	public static String GenerateCommandFileOperation_JobName;	
	
	// Working Copy Compiler
	public static String WorkingCopyCompiler_InitializeJobName;
	public static String WorkingCopyCompiler_ResourceChangeJobName;
	
	// System parts
	public static String SystemPartsJob_Starting;
	public static String SystemPartsJob_Initializing;
	
	// generation
	public static String CouldNotGetOutputFolder;
	public static String ProjectNotAccessible;
	public static String analyzingChangedIRs;
	public static String analyzingAllIRs;
	public static String calculatingGeneratorChanges;
	public static String projectHasBuildProblem;
	public static String prereqProjectHasBuildProblem;
	public static String prereqProjectMustBeRebuilt;
}

