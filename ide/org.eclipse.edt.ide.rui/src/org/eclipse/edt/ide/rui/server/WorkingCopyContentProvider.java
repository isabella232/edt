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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.rui.utils.DebugIFileLocator;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IFileLocator;
import org.eclipse.edt.ide.rui.utils.PreviewFileLocator;
import org.eclipse.edt.ide.rui.utils.WorkingCopyGenerationResult;
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
//TODO EDT generator
//			if(result.hasGenerationError()){
//				generator = new GenerationErrorHTMLGenerator(resourceName, result);
//			}else{
//				generator = new CompileErrorHTMLGenerator(resourceName);
//			}
//			return generator.generate();
			return null;
		}else{
			return super.generateHTMLFile(locator, resourceName, projectName);
		}		
	}

	@Override
	protected ProjectEnvironment getProjectEnvironment(IProject project) {
		IPath generationDirectory = editorProvider.getGenerationDirectory();
		ProjectEnvironment environment = ProjectEnvironmentManager.getInstance().getProjectEnvironmentForPreview(project, generationDirectory);
		return environment;
	}

	

		
	
}
