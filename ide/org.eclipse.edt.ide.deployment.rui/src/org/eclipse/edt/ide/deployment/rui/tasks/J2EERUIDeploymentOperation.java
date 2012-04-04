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
package org.eclipse.edt.ide.deployment.rui.tasks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.deployment.results.DeploymentResultMessageRequestor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.model.RUIDeploymentModel;
import org.eclipse.edt.ide.deployment.rui.internal.model.RUIDeploymentModel.DeployableFile;
import org.eclipse.edt.ide.deployment.rui.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.rui.internal.util.WebUtilities;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.rui.utils.DeployFileLocator;
import org.eclipse.edt.ide.rui.utils.DeployIFileLocator;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IConstants;
import org.eclipse.edt.ide.rui.utils.IFileLocator;

/**
 * The abstract deployment operation for a J2EE based web project target. <br>
 * Subclasses specialize on the runtime they support.<br>
 * This class is invoked when the user hits the finish button of the
 * deployment wizard and performs the following functions:</br></br>
 * 
 * 1. Copies the __proxy to the web project and configures the servlet in the project's deployment descriptor
 * 2. Generates the HTML file for the handler and then deploys it to the web project
 * 3. Deploys all other required resources to the web project
 * 
 * @version 1.0
 *
 */
public class J2EERUIDeploymentOperation {
	
	protected RUIDeploymentModel model;
	
	protected IPath jsTargetPath = null;

	
	/**
	 * 
	 */
	public J2EERUIDeploymentOperation(RUIDeploymentModel model) {
		this.model = model;
	}
	
	private void setContextRoot( IProject project, RUIDeploymentModel model )
	{
		String contextRoot = model.getContextRoot();
		if( contextRoot == null || contextRoot.length() == 0  )
		{
			contextRoot = WebUtilities.getContextRoot(project);
			contextRoot = cleanupContextRoot(contextRoot);
		}
		model.setContextRoot(contextRoot);
	}

	private String cleanupContextRoot(String contextRoot) {
		if ( contextRoot == null )
		{
			contextRoot = "";
		}
		else if( contextRoot.length() > 0 && contextRoot.charAt(0) == '/' )
		{
			if( contextRoot.length() > 1 )
			{
				contextRoot = contextRoot.substring(1);
			}
			else
			{
				contextRoot = "";
			}
		}
		if( contextRoot.length() > 0 && contextRoot.charAt(contextRoot.length() - 1 ) == '/' )
		{
			contextRoot = contextRoot.substring(0,(contextRoot.length() - 1));
		}
		return contextRoot;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProgressMonitor monitor, IDeploymentResultsCollector resultsCollector) throws CoreException {
		IProject deploymentProject = null;
		deploymentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(model.getTarget());
		setContextRoot(deploymentProject, model);
	
		if (deploymentProject != null && deploymentProject.exists() && deploymentProject.isOpen()) {
			/**
			 * figure out the directory within the web project to copy our stuff into
			 */
			IFolder projectRootFolder = Utils.getContextDirectory(deploymentProject);
			

			/**
			 * copy the appropriate runtime message bundle TODO - EDT
			 */
//			DeploymentResultMessageRequestor messageRequestorRuntimeMessageBundles = new DeploymentResultMessageRequestor(resultsCollector);
//			monitor.subTask(Messages.J2EEDeploymentOperation_8);
//			deployRuntimeMessageBundles(deploymentProject, projectRootFolder, messageRequestorRuntimeMessageBundles, monitor);
//			monitor.worked(1);
			
			IFileLocator iFileLocator = new DeployIFileLocator(model.getSourceProject());
			FileLocator fileLocator = new DeployFileLocator(model.getSourceProject());
			model.startAllHandlerGeneration();
			
			for( Iterator<IFile> itr = model.getSourceRUIHandlers().iterator(); itr.hasNext() && !monitor.isCanceled(); )
			{
				IFile ruiHandler = itr.next();
				String fullName = "";
				
/* TODO - EDT				
				if ( ruiHandler instanceof EglarFileResource ) {
					fullName = ruiHandler.getName();
				} else {
*/
					IEGLFile eglFile = (IEGLFile)EGLCore.create(ruiHandler);
					IPart ruiHandlerPart = eglFile.findPrimaryPart();

					if(ruiHandlerPart != null){
						fullName = ruiHandlerPart.getFullyQualifiedName();
					}
//				}
				DeploymentResultMessageRequestor messageRequestor = new DeploymentResultMessageRequestor(resultsCollector);
				messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
								EGLMessage.EGL_DEPLOYMENT_DEPLOYING_RUIHANDLER, 
								null,
								new String[] { fullName, deploymentProject.getName() }));
				/**
				 * generate the HTML file for the handler
				 */
				HashMap<String, String> eglProperties = new HashMap<String, String>();
				eglProperties.put(IConstants.CONTEXT_ROOT_PARAMETER_NAME, model.getContextRoot());
				eglProperties.put(IConstants.DEFAULT_DD_PARAMETER_NAME, ((String)model.getEgldds().get(0)).toLowerCase());
				if( !monitor.isCanceled() ){
					monitor.subTask(Messages.J2EEDeploymentOperation_2);
					model.generateHandler(ruiHandler, 
													iFileLocator, fileLocator, eglProperties, messageRequestor, monitor);
					
					monitor.worked(1);
				}
				/**
				 * deploy the properties .js files
				 */
				if( !monitor.isCanceled() ){
					monitor.subTask(Messages.J2EEDeploymentOperation_7);
					deployPropertiesFiles(projectRootFolder, messageRequestor, monitor);
					monitor.worked(1);
				}
				/**
				 * copy the bind files (from the .deploy file) 
				 */
				if( !monitor.isCanceled() ){
					monitor.subTask(Messages.J2EEDeploymentOperation_15);
					deployBindFiles(deploymentProject, projectRootFolder, messageRequestor, monitor);
					monitor.worked(1);
				}
				/**
				 * copy the HTML file over to the web project
				 */
				if( !monitor.isCanceled() ){
					monitor.subTask(Messages.J2EEDeploymentOperation_3);
					deployHandlers(ruiHandler, projectRootFolder, messageRequestor, monitor, resultsCollector);
					monitor.worked(1);
				}
				
				if( !monitor.isCanceled() ){
					if (messageRequestor.isError()) {
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
								EGLMessage.EGL_DEPLOYMENT_FAILED, 
								null,
								new String[] { fullName }));
					} 
					else {
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
										EGLMessage.EGL_DEPLOYMENT_COMPLETE, 
										null,
										new String[] { fullName }));
					}
				}
				resultsCollector.addMessage(Utils.createEmptyStatus());
			}
			
		} else {
			IStatus status = DeploymentUtilities.createErrorStatus(Messages.J2EEDeploymentSolution_22);
			throw new CoreException(status);
		}
	
	}
	
	private void deployHandlers(IFile ruiHandler, IFolder folder, DeploymentResultMessageRequestor messageRequestor, IProgressMonitor monitor, IDeploymentResultsCollector resultsCollector) {
		monitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
		for (Iterator<Entry<String, DeployableFile>> htmlFilesByLocaleIterator = model.getHtmlFileContents().entrySet().iterator(); htmlFilesByLocaleIterator.hasNext() && !monitor.isCanceled();) {
			Map.Entry<String, DeployableFile> entry = htmlFilesByLocaleIterator.next();
			if( !entry.getValue().isDeployed() ){
				String localeCode = (String)entry.getKey();
				String htmlFileName = (String)model.getHTMLFileNames().get(ruiHandler);
				
				try {
					String fullTargetPath = DeploymentUtilities.deriveHTMLFilePath(folder.getFullPath().toOSString(), htmlFileName, localeCode, entry.getValue().isFilenameWithLocal(), IConstants.HTML_FILE_EXTENSION);
					
					IFile outputLocation = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fullTargetPath));
					InputStream inputStream = new ByteArrayInputStream(entry.getValue().getFile());
					
					try{
						if(outputLocation.exists()){
							outputLocation.setContents(inputStream, true, false, monitor);
						}else{
							outputLocation.create(inputStream, IResource.FORCE, monitor);
						}
					}finally{
						inputStream.close();
					}
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
								EGLMessage.EGL_DEPLOYMENT_DEPLOYED_HTML_FILE,
								null,
								new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
				}catch(Exception e){
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
									EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_HTML_FILE, 
									null,
									new String[] { htmlFileName, localeCode }));
					resultsCollector.addMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_compile_error, ruiHandler.getFullPath().toOSString())));
				}
				entry.getValue().setDeployed(true);
			}
		}
		monitor.done();
	}
	
	private void deployPropertiesFiles(IFolder projectRootFolder, DeploymentResultMessageRequestor messageRequestor, IProgressMonitor monitor) {
		monitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
		/**
		 * The model has generated these files and has them cached as byte arrays. We now need to write the byte arrays
		 * out to to correct target file
		 */

		String targetDirectory = projectRootFolder.getFullPath().toString() + File.separator + IConstants.PROPERTIES_FOLDER_NAME; //$NON-NLS-1$
		try{
			IFolder targetFolder = Utils.createDirectory(targetDirectory);
	
			for (Iterator<Entry<String, DeployableFile>> iterator = model.getPropertiesFileByteArrays().entrySet().iterator(); iterator.hasNext() && !monitor.isCanceled();) {
				Map.Entry<String, DeployableFile> entry = iterator.next();
				if( !entry.getValue().isDeployed() ){
					String fileName = new Path((String)entry.getKey()).removeFileExtension().addFileExtension("js").toOSString(); //$NON-NLS-1$
					IFile outputLocation = targetFolder.getFile(fileName);
					
					try {
						InputStream inputStream = new ByteArrayInputStream(entry.getValue().getFile());
						
						try{
							if(outputLocation.exists()){
								outputLocation.setContents(inputStream, true, false, monitor);
							}else{
								outputLocation.create(inputStream, true, monitor);
							}
						}finally{
							inputStream.close();
						}
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
										EGLMessage.EGL_DEPLOYMENT_DEPLOYED_PROPERTY_FILE,
										null,
										new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
					}catch(Exception e){
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
										EGLMessage.EGL_DEPLOYMENT_FAILED_DEPLOY_PROPERTY_FILE,
										null,
										new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
										EGLMessage.EGL_DEPLOYMENT_EXCEPTION,
										null,
										new String[] { DeploymentUtilities.createExceptionMessage(e) }));
					}
					entry.getValue().setDeployed(true);
				}
			}
			
			/**
			 * Deploy the runtime properties files. This can be created in two ways depending on the genProperties
			 * attribute in the build descriptor. If genProperties = "GLOBAL" then a <code><pkg>/<part>-rt.js</code>
			 * is created. If genProperties = "PROGRAM" then a properties file per handler is created and placed into
			 * the JavaScript folder in the same directory structure as the handler is in.
			 */
			for (Iterator<Entry<String, DeployableFile>> it = model.getRuntimePropertiesFileByteArrays().entrySet().iterator(); it.hasNext();) {
				// Target directory is the same as for the root handler.
				Map.Entry<String, DeployableFile> entry = it.next();
				
				if( !entry.getValue().isDeployed() ){
					String fileName = entry.getKey() + ".properties"; //RuntimePropertiesFileUtil.getJavascriptFileName(entry.getKey()); TODO - EDT
					if (File.separatorChar != '/') {
						fileName = fileName.replace('/', File.separatorChar);
					}
					
					String fileDir = ""; //$NON-NLS-1$
					int lastSlash = fileName.lastIndexOf(File.separatorChar);
					if (lastSlash != -1) {
						fileDir = fileName.substring(0, lastSlash);
					}		
					/**
					 * now strip the directory off the file name
					 */
					String temp = fileDir;
					if (temp != "") {
						temp = temp + File.separatorChar;
					}
					fileName = fileName.substring(temp.length());

					IFile outputLocation = projectRootFolder.getFile(fileName);
				
					try {
						InputStream inputStream = new ByteArrayInputStream(entry.getValue().getFile());
						
						try{
							if(outputLocation.exists()){
								outputLocation.setContents(inputStream, true, false, monitor);
							}else{
								outputLocation.create(inputStream, IResource.FORCE, monitor);
							}
						}finally{
							inputStream.close();
						}
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
										EGLMessage.EGL_DEPLOYMENT_DEPLOYED_RT_PROPERTY_FILE,
										null, 
										new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
					}catch(Exception e){
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
										EGLMessage.EGL_DEPLOYMENT_FAILED_DEPLOY_RT_PROPERTY_FILE,
										null, 
										new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
						messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
										EGLMessage.EGL_DEPLOYMENT_EXCEPTION, 
										null,
										new String[] { DeploymentUtilities.createExceptionMessage(e) }));
					}
					entry.getValue().setDeployed(true);
				}
			}
		}catch(Exception e){
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_PROPERTIES_FOLDER,
							null, 
							new String[] { targetDirectory }));
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_EXCEPTION, 
							null,
							new String[] { DeploymentUtilities.createExceptionMessage(e) }));
		}
		monitor.done();	
	}
	
	/* TODO - EDT
	private void deployRuntimeMessageBundles(IProject project, IFolder folder, DeploymentResultMessageRequestor messageRequestor, IProgressMonitor monitor) {
		monitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
		String targetDirectory = folder.getFullPath().toString() + File.separator + IConstants.RUNTIME_MESSAGES_DEPLOYMENT_FOLDER_NAME;
		try {
			IPath targetPath = Utils.createDirectory(targetDirectory).getProjectRelativePath();
			
			Bundle sourcePlugin = Platform.getBundle("com.ibm.etools.egl.rui"); //$NON-NLS-1$
			IPath sourceRelative2PluginPath = new Path("/runtime/egl/messages"); //$NON-NLS-1$
			for (Iterator<DeployLocale> iterator = this.model.getHandlerLocales().iterator(); iterator.hasNext() && !monitor.isCanceled();) {
				DeployLocale locale = iterator.next();
				String localeCode = "en_US"; //locale.getRuntimeLocaleCode(); TODO - EDT
				String fileName = "RuiMessages-" + localeCode + ".js";  //$NON-NLS-1$//$NON-NLS-2$
			    IPath filepathRelative2Proj = targetPath.append(fileName);

				IFile outputLocation = project.getFile(filepathRelative2Proj);
				IPath sourceFile = sourceRelative2PluginPath.append(fileName);
				try{
					InputStream sourceStream = org.eclipse.core.runtime.FileLocator.openStream(sourcePlugin, sourceFile, false);
					try{
						if (outputLocation.exists()) {
							outputLocation.setContents(sourceStream, true, false, monitor);
						}
						else {
							outputLocation.create(sourceStream, true, monitor);
						}
					}
					finally{
							if( sourceStream != null ){
								sourceStream.close();
							}
					}
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
									EGLMessage.EGL_DEPLOYMENT_DEPLOYED_RT_MSG_BUNDLE,
									null, 
									new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
				}catch(Exception e){
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
									EGLMessage.EGL_DEPLOYMENT_FAILED_DEPLOY_RT_MSG_BUNDLE, 
									null,
									new String[] { outputLocation.getProjectRelativePath().toPortableString() }));
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
									EGLMessage.EGL_DEPLOYMENT_EXCEPTION,
									null, 
									new String[] { DeploymentUtilities.createExceptionMessage(e) }));
				}
			}
		}catch(Exception e){
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_RT_MSG_BUNDLE_FOLDER,
							null, 
							new String[] { targetDirectory }));
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_EXCEPTION,
							null, 
							new String[] { DeploymentUtilities.createExceptionMessage(e) }));
		}
		monitor.done();
	}
	*/
	
	private void deployBindFiles (IProject project, IFolder targetRootFolder, DeploymentResultMessageRequestor messageRequestor, IProgressMonitor monitor) {
		monitor = SubMonitor.convert(monitor, IProgressMonitor.UNKNOWN);
		for( Iterator<Entry<String, DeployableFile>> itr = model.getBindFileByteArrays().entrySet().iterator(); itr.hasNext() && !monitor.isCanceled(); ) {
			Entry<String, DeployableFile> entry  = itr.next();
			if( !entry.getValue().isDeployed() ){
				String file = targetRootFolder.getProjectRelativePath().toOSString() + File.separator + entry.getKey() + ".js"; //DeploymentDescriptorFileUtil.JS_SUFFIX; TODO - EDT
				IFile f = project.getFile(new Path(file));
				try {
					if (f.exists()) {
						f.setContents(new ByteArrayInputStream(entry.getValue().getFile()), true, false, monitor);
					}
					else{
						f.create(new ByteArrayInputStream(entry.getValue().getFile()), true, monitor);
					}
					entry.getValue().setDeployed(true);
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
									EGLMessage.EGL_DEPLOYMENT_DEPLOYED_BIND_FILE,
									null, 
									new String[] { f.getProjectRelativePath().toPortableString() }));
				} catch (Exception e) {
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
									EGLMessage.EGL_DEPLOYMENT_FAILED_LOCATE_EGLDD_FILE,
									null, 
									new String[] { f.getProjectRelativePath().toPortableString() }));
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
									EGLMessage.EGL_DEPLOYMENT_EXCEPTION, 
									null,
									new String[] { DeploymentUtilities.createExceptionMessage(e) }));
				}
				entry.getValue().setDeployed(true);
			}
		}
		monitor.done();
	}
}
