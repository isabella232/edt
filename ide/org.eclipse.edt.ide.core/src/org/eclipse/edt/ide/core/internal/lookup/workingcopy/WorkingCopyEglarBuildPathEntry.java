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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.mof2binding.Mof2Binding;
import org.eclipse.edt.ide.core.internal.lookup.EglarBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.ZipFileOrigin;

public class WorkingCopyEglarBuildPathEntry extends EglarBuildPathEntry implements IWorkingCopyBuildPathEntry {

	public WorkingCopyEglarBuildPathEntry(IEnvironment environment, IPath path, String fileExtension, Mof2Binding converter){
		super(environment, path, fileExtension, converter);
	}
	
	public IPartOrigin getPartOrigin(String[] packageName, String partName) {
		return new ZipFileOrigin();
	}
}
