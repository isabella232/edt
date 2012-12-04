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
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IASTFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.ResourceFileInfoCreator;

public class CleanBatchBuilder extends AbstractBatchBuilder {

	protected CleanBatchBuilder(Builder builder, IBuildNotifier notifier) {
		super(builder, notifier);
	}
	
	@Override
	protected void addEGLFile(IFile file, String packageName) {
		try {
			String fileContents = org.eclipse.edt.ide.core.internal.utils.Util.getFileContents(file);
			
		    File fileAST = ASTManager.getInstance().getFileAST(file, fileContents);
		    ResourceFileInfoCreator fileInfoCreator = new ResourceFileInfoCreator(projectInfo, packageName, file, fileAST, fileContents, new DuplicatePartRequestor(builder.getProject(), packageName, file));
		    IASTFileInfo info = fileInfoCreator.getASTInfo();
			FileInfoManager.getInstance().saveFileInfo(builder.getProject(), file.getProjectRelativePath(), info);
	
			// report fileInfo errors
			info.accept(new FileMarkerProblemRequestor(file, info));
		   
	        // Perform Syntax Checking
	        fileAST.accept(new MarkerSyntaxErrorRequestor(new SyntaxMarkerProblemRequestor(file, info), fileContents));
			
			Set partNames = info.getPartNames();
			for (Iterator iter = partNames.iterator(); iter.hasNext();) {
				String partName = (String) iter.next();
				PackageAndPartName ppName = new PackageAndPartName(info.getCaseSensitivePackageName(), info.getCaseSensitivePartName(partName));
				projectInfo.partAdded(packageName, partName, info.getPartType(partName), file, ppName);
				addPart(ppName);
			}
		}catch(Exception e){
			throw new BuildException("Error adding EGL File: " + file.getProjectRelativePath(), e);
		}
	}

	@Override
	protected void addEGLPackage(String packageName) {
		projectInfo.packageAdded(packageName);		
	}

}
