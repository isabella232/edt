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
package org.eclipse.edt.compiler;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;


public class SystemPackageMOFPathEntry extends SystemPackageBuildPathEntry implements ISystemPackageBuildPathEntry, IZipFileEntryManager{

	public SystemPackageMOFPathEntry(IEnvironment env, String path, ISystemPartBindingLoadedRequestor req, String fileExtension) {
		super(env, path, req, fileExtension);
	}
	
	protected String convertToStoreKey(String entry) {
		//entries are in the form: "pkg1/pkg2/partName.mofxml". Need to convert this to:
		//"pkg1.pkg2.partName"
		
		//strip off the filename extension
		String value = entry.substring(0, entry.indexOf("."));
		
		return value.replaceAll("/", ".");
		
	}
}
