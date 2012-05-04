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
package org.eclipse.edt.ide.rui.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.utils.EGLProjectFileUtility;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;

public class JavaScriptPreviewGenerator {

	protected List						_eglProjectPath		= null;
	protected IFile						_file				= null;
	protected IPath 					_buildDescriptorPath = null;
	protected String 					_buildDescriptorName = null;
	protected WorkingCopyGenerationResult	_generationResult	= new WorkingCopyGenerationResult();

	/**
	 * NOTE: If the egl build path of the file being generated is known to have changed, a new 
	 * JavaScriptPreviewGenerator should be created.
	 * NOTE: If the default build descriptor of the file being generated is known to have changed, a new
	 * JavaScriptPreviewGenerator should be created.
	 */
	public JavaScriptPreviewGenerator( IFile file ) {
		_file = file;
		try {
			initializeEGLProjectPath();
			initalizeBuildDescriptor();
		}
		catch( Exception ex ) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error creating JavaScript Preview Generator", ex));
		}
	}

	/**
	 * 
	 */
	protected String createEGLIRPath() {
		StringBuffer eglPath = new StringBuffer();

		for( Iterator iter = _eglProjectPath.iterator(); iter.hasNext(); ) {
			
			//If the path contains an EGLAR, it will come to us as the absolute path string
			Object obj = iter.next();
			if (obj instanceof String) {
				eglPath.append((String) obj);
				eglPath.append( File.pathSeparator );
			}
			else {
				EGLProject eglProject = (EGLProject)obj;
	
				
				boolean isBinaryProject = new EGLProjectFileUtility().isBinaryProject(eglProject.getProject());
				
				//for binary projects, do not include the EGLBin directory, because it wont have one!
				if(!isBinaryProject) {
					String outputLocation = "EGLBin";
					try {
						outputLocation = eglProject.getOutputLocation().lastSegment();
					} catch (EGLModelException e) {
					}
					eglPath.append( eglProject.getResource().getLocation().append( outputLocation ).toOSString() );
					eglPath.append( File.pathSeparator );
				}
				eglPath.append( eglProject.getResource().getLocation().toOSString() );
				eglPath.append( File.pathSeparator );
			}
		}

		return eglPath.toString();
	}

	/**
	 * @param outputDirectory 
	 * 
	 */
	public WorkingCopyGenerationResult generateJavaScript(IPath outputDirectory) {
		_generationResult.clear();

		try {
			//TODO Handle missing default build descriptor
//			if(_buildDescriptorPath != null && _buildDescriptorName != null){
				IEGLFile modelFile = (IEGLFile)EGLCore.create( _file );
				IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy( null, EGLUI.getBufferFactory(), null );
				try {
					sharedWorkingCopy.reconcile( true, null );
	
					WorkingCopyGenerationOperation genOp = new WorkingCopyGenerationOperation( _file, _generationResult, _generationResult, _buildDescriptorPath, _buildDescriptorName, outputDirectory, createEGLIRPath(), new IWorkingCopyGenerationOperationNotifier() {
						public boolean isCancelled() {
							return false;
						}
					} );
	
					genOp.generate();
				}
				catch( Exception e ) {
					_generationResult.setHasError( true );
				}
				finally {
					sharedWorkingCopy.destroy();
				}
//			}
		}
		catch( Exception e ) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error generating JavaScript", e));
		}
		
		return _generationResult;
	}


	private void initalizeBuildDescriptor() {
//TODO EDT build
//		IEGLBuildDescriptorLocator bdLocator = EDTCoreIDEPlugin.getPlugin().getBdLocator();
//		if (bdLocator != null) {
//			IEGLPartWrapper defaultBuildDescriptor = bdLocator.locateDefaultBuildDescriptor(IEGLBuildDescriptorLocator.DEBUG_DEFAULT_JAVASCRIPT_BUILD_DESCRIPTOR_TYPE, _file);
//			if(defaultBuildDescriptor != null){
//				IResource buildDescriptorFile = ResourcesPlugin.getWorkspace().getRoot().findMember(defaultBuildDescriptor.getPartPath());
//				if(buildDescriptorFile != null){
//					_buildDescriptorPath = buildDescriptorFile.getLocation();
//					_buildDescriptorName = defaultBuildDescriptor.getPartName();
//				}
//			}
//		}
	}

	protected void initializeEGLProjectPath() throws EGLModelException {
		IProject project = (IProject)_file.getProject();
		_eglProjectPath = org.eclipse.edt.ide.core.internal.utils.Util.getEGLProjectPath(project, true, true);
	}
}
