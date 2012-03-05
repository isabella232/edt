/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
package org.eclipse.edt.ide.rui.server;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.gen.deployment.javascript.CompileErrorHTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.GenerationErrorHTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.ide.rui.internal.deployment.javascript.EGL2HTML4VE;
import org.eclipse.edt.ide.rui.internal.lookup.PreviewIREnvironmentManager;
import org.eclipse.edt.ide.rui.internal.nls.EWTPreviewMessages;
import org.eclipse.edt.ide.rui.utils.DebugIFileLocator;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IFileLocator;
import org.eclipse.edt.ide.rui.utils.PreviewFileLocator;
import org.eclipse.edt.ide.rui.utils.WorkingCopyGenerationResult;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.xml.sax.SAXException;


/**
 * Load the file being requested by the browser
 */
public abstract class WorkingCopyContentProvider extends AbstractContentProvider{

	protected EvEditorProvider editorProvider = null;
	
	public WorkingCopyContentProvider( EvEditorProvider editorProvider ){
		this.editorProvider = editorProvider;
	}
	
	protected FileLocator getFileLocator(IProject project) throws CoreException{
		return new PreviewFileLocator(project, editorProvider.getGenerationDirectory());
	}
	
	protected IFileLocator getIFileLocator(IProject project) throws CoreException{
		//TODO - Should this only be looking in the /debug directory, or should it look in the Preview directory?
		return new DebugIFileLocator(project);
	}
	
	protected byte[] generateHTMLFile( FileLocator locator, String resourceName, String projectName) throws SAXException, IOException {
		HTMLGenerator generator = null;
		WorkingCopyGenerationResult result = editorProvider.getLastGenerationResult();
		if(result != null && result.hasError()){
			EGL2HTML4VE cmd = new EGL2HTML4VE();
			IProject project =  ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			IEnvironment environment = getEnvironmentForGeneration(project);
			Environment.pushEnv(environment);
			
			Part part = null;
			EObject eObject = null;
			
			try {
				eObject = environment.find(PreviewIREnvironmentManager.makeEGLKey(resourceName.replace("/", ".")));
			} catch (MofObjectNotFoundException e) {
				String message = MessageFormat.format(EWTPreviewMessages.COMPILEFAILEDPAGE_HEADERMSG, new Object[] {resourceName.replace("/", ".")});
				generator = new CompileErrorHTMLGenerator(cmd, result.getResult(), message);
			} catch (DeserializationException e) {
				String message = MessageFormat.format(EWTPreviewMessages.COMPILEFAILEDPAGE_HEADERMSG, new Object[] {resourceName.replace("/", ".")});
				generator = new CompileErrorHTMLGenerator(cmd, result.getResult(), message);
			}
			
			if(eObject!=null && eObject instanceof Part){
				part = (Part)eObject;
			}			
			
			if(result.hasGenerationError()){
				String message = MessageFormat.format(EWTPreviewMessages.GENFAILEDPAGE_HEADERMSG, new Object[] {
						part.getFullyQualifiedName(),
						Integer.toString(result.getResult().getNumGenErrors()),
						Integer.toString(result.getResult().getNumGenWarnings())
					});
				generator = new GenerationErrorHTMLGenerator(cmd, result.getResult(), message);
			}else{
				String message = MessageFormat.format(EWTPreviewMessages.COMPILEFAILEDPAGE_HEADERMSG, new Object[] {resourceName.replace("/", ".")});
				CompileErrorHTMLGenerator compileErrorHTMLGenerator = new CompileErrorHTMLGenerator(cmd, result.getResult(), message);
				return compileErrorHTMLGenerator.generate().getBytes();
			}
			
			String strResult = cmd.generate(part, generator, environment);
			return strResult.getBytes();	
		}else{
			return super.generateHTMLFile(locator, resourceName, projectName);
		}		
	}

	@Override
	protected  IEnvironment getEnvironmentForGeneration(IProject project) {
		return PreviewIREnvironmentManager.getPreviewIREnvironment(project, editorProvider.getGenerationDirectory().toFile());
	}
		
	
}
