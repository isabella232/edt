/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.io.File;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


public interface ISystemPackageBuildPathEntryFactory {
	public static final String EDT_JAR_EXTENSION = ".eglar";
	public static final String EDT_MOF_EXTENSION = ".mofar";
	ISystemPackageBuildPathEntry[] createEntries(IEnvironment env, File[] files,ISystemPartBindingLoadedRequestor req);
}
