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
package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.util.SimpleLineTracker;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;

public class IDEContext extends Context {

	public IDEContext(IFile file, ICompiler compiler) {
		super(file.getFullPath().toString(), file.getName(), compiler);
		this.file = file;
		this.project = file.getProject();
	}

	private IFile file;
	private IProject project;
	private SimpleLineTracker lineTracker;

	
	private IFile getFile() {
		if (file == null) {
			if (getAbsolutePath() != null) {
				org.eclipse.core.runtime.IPath path = new Path(getAbsolutePath());
				try {
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return file;
	}
	
	private IProject getProject(){
		if(project == null){
			IFile contextFile = getFile();
			if(contextFile != null){
				project = contextFile.getProject();	
			}
		}
		return project;
	}
	

	public int getLineNumber(Node node) {
		//TODO EDT
//		if (sourceResolver != null) {
//			return getLineNumberFromSource(node.getOffset());
//		}
		
		IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(getProject(), getFile().getProjectRelativePath());
		if (fileInfo != null) {
			return fileInfo.getLineNumberForOffset(node.getOffset()) + 1;
		}
		return 0;
	}
	
	protected int getLineNumberFromSource(int offset) {
		
		if (getLineTracker() == null) {
			return 0;
		}
		
		int[] lineOffsets = getLineTracker().getLineOffsets();
		for (int i = 0; i < lineOffsets.length; i++) {
			if (lineOffsets[i] == offset) {
				return i;
			}
			if (lineOffsets[i] > offset) {
				return i - 1;
			}
		}
		return 0;
	}
	
	private SimpleLineTracker getLineTracker() {
		//TODO EDT
//		if (lineTracker == null && sourceResolver != null) {
//			try {
//				lineTracker = new SimpleLineTracker(((IClassFile)sourceResolver).getSource());
//			} catch (EGLModelException e) {
//			}
//		}
		return lineTracker;
	}	
	

	
	public String getAbsolutePath(String fileName) {
		
		IFile file = getFile(fileName);
		if (file == null || !file.exists()) {
			return fileName;
		}
		
		return file.getLocation().toString();
	}

	public IFile getFile(String fileName) {
		if (fileName == null) {
			return null;
		}

		org.eclipse.core.runtime.IPath path = new Path(fileName);
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		} catch (Exception e) {
			return null;
		}
	}
	

}
