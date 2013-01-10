/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.utils.Util;

public class HandlerFieldsResolver {
	
	private class WorkingCopyCompileRequestor implements IWorkingCopyCompileRequestor {
		Handler boundPageHandler = null;
		public void acceptResult(WorkingCopyCompilationResult result) {
			boundPageHandler = (Handler)result.getBoundPart();
		}
		public Handler getBoundPart() {
			return boundPageHandler;
		}
	};
	
	private IFile currentFile;
	private Handler ruiHandler;
	
	public HandlerFieldsResolver(IFile currentFile){
		this.currentFile = currentFile;
	}
	
	public void resolve() {
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
			
			// Create a temporary working copy that is not tied to the editor
			IEGLFile tempWorkingCopy = (IEGLFile)modelFile.getWorkingCopy();
			
			// Set the contents of the temporary working copy to match the current (possibly unsaved) contents of the editor
			tempWorkingCopy.getBuffer().setContents(sharedWorkingCopy.getBuffer().getContents());
			WorkingCopyCompileRequestor requestor = new WorkingCopyCompileRequestor();
			
			try{
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				WorkingCopyCompiler.getInstance().compilePart(currentFile.getProject(), Util.stringArrayToQualifiedName(((EGLFile)modelFile).getPackageName()), currentFile, new IWorkingCopy[] {tempWorkingCopy}, partName, requestor);
				ruiHandler = requestor.getBoundPart();
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type Name Resolver: Error resolving type name", e));
			}finally{
				tempWorkingCopy.destroy();
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type Name Resolver: Error resolving type name", e));
		}
	}
	
	public Handler getRUIHandler() {
		return ruiHandler;
	}
}
