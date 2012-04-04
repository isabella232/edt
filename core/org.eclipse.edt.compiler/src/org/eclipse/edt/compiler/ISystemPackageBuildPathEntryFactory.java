/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.edt.mof.serialization.IEnvironment;


public interface ISystemPackageBuildPathEntryFactory {
	public static final String EDT_JAR_EXTENSION = ".eglar";
	public static final String EDT_MOF_EXTENSION = ".mofar";
	List<ISystemPackageBuildPathEntry> createEntries(ISystemEnvironment env, IEnvironment irEnv, File[] files,ISystemPartBindingLoadedRequestor req);
}
