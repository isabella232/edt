/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.builder;

import org.eclipse.osgi.util.NLS;

/**
 * @author svihovec
 *
 */
public class BuilderResources extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.core.internal.builder.BuilderResources"; //$NON-NLS-1$

	private BuilderResources() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, BuilderResources.class);
	}

	public static String buildUnhandledException;
	public static String buildPrereqProjectHasEGLpathProblems;
	public static String buildPrereqProjectMustBeRebuilt;
	public static String buildAbortDueToEGLpathProblems;
	public static String buildAnalyzingChangedEGLFiles;
	public static String buildSavingDependencyGraph;
	public static String buildSavingIRFiles;
	public static String buildCompiling;
	public static String buildAddingDependentsOf;
	public static String buildProcessingTopLevelFunctions;
	public static String buildCreatingIR;
	public static String buildSavingIR;
	public static String buildProcessingDependentChanges;
	public static String buildAnalyzingAllEGLFiles;
	public static String buildDone;
}
