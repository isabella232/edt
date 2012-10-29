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
import org.eclipse.edt.ide.core.internal.lookup.MofarBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.ZipFileOrigin;

public class WorkingCopyMofarBuildPathEntry extends MofarBuildPathEntry implements IWorkingCopyBuildPathEntry {

	public WorkingCopyMofarBuildPathEntry(IEnvironment environment, IPath path, String fileExtension){
		super(environment, path, fileExtension);
	}
	
	@Override
	public IPartOrigin getPartOrigin(String packageName, String partName) {
		return new ZipFileOrigin();
	}
	
	@Override
	protected boolean shouldSetEnvironmentOnIr() {
		return true;
	}
}
