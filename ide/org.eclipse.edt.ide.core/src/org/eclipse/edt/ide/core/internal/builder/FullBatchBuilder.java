/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;

public class FullBatchBuilder extends AbstractBatchBuilder {

	protected FullBatchBuilder(Builder builder, IBuildNotifier notifier) {
		super(builder, notifier);
	}

	@Override
	protected void addEGLFile(IFile file, String packageName) {
		IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(builder.getProject(), file.getProjectRelativePath());
		
		for (Iterator iter = fileInfo.getPartNames().iterator(); iter.hasNext();) {
			String partName = (String) iter.next();
			addPart(new PackageAndPartName(fileInfo.getCaseSensitivePackageName(), fileInfo.getCaseSensitivePartName(partName), packageName));
		}
	}
	
	@Override
	protected void addEGLPackage(String packageName) {
		// noop
	}
}
