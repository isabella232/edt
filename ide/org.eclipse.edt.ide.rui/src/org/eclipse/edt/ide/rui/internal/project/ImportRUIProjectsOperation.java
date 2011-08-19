/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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
package org.eclipse.edt.ide.rui.internal.project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ImportRUIProjectsOperation extends WorkspaceModifyOperation {
	
	private String resourcePluginName = "org.eclipse.edt.ide.ui.resources";
	private String libraryResourceFolder = "/org.eclipse.edt.ide.ui.rui/";	
	private String widgetsProjectName;	

	public ImportRUIProjectsOperation(ISchedulingRule rule, String resourcePluginName,
			String libraryResourceFolder, String widgetsProjectName) {
		super(rule);
		this.resourcePluginName = resourcePluginName;
		this.libraryResourceFolder = libraryResourceFolder;
		this.widgetsProjectName = widgetsProjectName;
	}
	
	public ImportRUIProjectsOperation(ISchedulingRule rule, String widgetsProjectName) {
		super(rule);
		this.widgetsProjectName = widgetsProjectName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {		
		if( widgetsProjectName != null ) {
			importWidgetsProject( monitor, widgetsProjectName );
		}	
	}
	
	private void importWidgetsProject( IProgressMonitor monitor, String projectName ) {
		final IProject widgetsProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		
		// Only import the widgets project if it doesn't exist
		if(!widgetsProject.exists()){
			try{				
				unzipWidgets( projectName );
				if(!widgetsProject.exists()){
					widgetsProject.create(monitor);
				}
				if(!widgetsProject.isOpen()){
					widgetsProject.open(monitor);
				}
				EGLCore.create(widgetsProject);				
				
				widgetsProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				
				IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
				if(!description.isAutoBuilding()){
					widgetsProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
				}
			} catch (Exception e) {
				EGLLogger.log(this, e);
			}
		}
	}
	
	private void unzipWidgets( String projectName ) throws IOException{
		URL url = CommonUtilities.getWidgetProjectURL(resourcePluginName, libraryResourceFolder, projectName);
			
		if(url != null) {
			IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			
			ZipFile sampleSource = new ZipFile(url.getFile());
			try{
				Enumeration entries = sampleSource.entries();
				while(entries.hasMoreElements()){
					ZipEntry entry = (ZipEntry)entries.nextElement();
					
					if(entry.getName().startsWith(projectName)) {					
						if(entry.isDirectory()){
							workspaceLocation.append(entry.getName()).toFile().mkdir();
						}else{
							File file = workspaceLocation.append(entry.getName()).toFile();
							
							file.getParentFile().mkdirs();
							
							OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
							InputStream inStream = sampleSource.getInputStream(entry);
							try{
								byte[] buffer = new byte[1024];
								int len;
								while((len = inStream.read(buffer)) >= 0){
								      outStream.write(buffer, 0, len);
								}
							}finally{
								outStream.close();
								inStream.close();
							}								
						}
					}
				}
			}finally{
				sampleSource.close();
			}
		}
	}
}
