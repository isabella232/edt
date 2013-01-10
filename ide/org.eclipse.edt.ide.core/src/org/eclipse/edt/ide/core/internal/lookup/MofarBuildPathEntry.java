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
package org.eclipse.edt.ide.core.internal.lookup;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;

public class MofarBuildPathEntry extends EglarBuildPathEntry {

	public MofarBuildPathEntry(IEnvironment environment, IPath path, String fileExtension) {
		super(environment, path, fileExtension);
	}
	
	@Override
	protected String convertToStoreKey(String entry) {
		//entries are in the form: "pkg1/pkg2/partName.mofxml". Need to convert this to:
		//"pkg1.pkg2.partName"
		
		//strip off the filename extension
		String value = entry.substring(0, entry.indexOf(".")); //$NON-NLS-1$
		return value.replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
