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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.deployment.javascript.DeploymentHTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.rui.internal.deployment.javascript.EGL2HTML4VE;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.edt.ide.rui.utils.EGLResource;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IConstants;
import org.eclipse.edt.ide.rui.utils.IFileLocator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;


public abstract class AbstractContentProvider implements IServerContentProvider {
	
	private IPreferenceStore store = EDTCoreIDEPlugin.getPlugin().getPreferenceStore();

	public byte[] loadContent(String uri) throws IOException, SAXException, CoreException {
		byte[] bytes = null;
		int index = uri.indexOf('/');
		String projectName;
		if (index == -1) {
			projectName = "";
		}
		else {
			try {
				// This uri is only a path.  We never expect a protocol, port, query, or fragment
				uri = new URI(uri).getPath();
			} catch (URISyntaxException e) {
				// do nothing
			}
			projectName = uri.substring(0, uri.indexOf('/')); //$NON-NLS-1$
		}
		
		if(projectName != ""){ //$NON-NLS-1$
			FileLocator locator = getFileLocator(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
			
			EGLResource file = findRequiredFile(uri, projectName, locator);	
			if(file!=null){
				//TODO - Check cache for loaded file
				DataInputStream is = new DataInputStream(new BufferedInputStream(file.getInputStream()));
				bytes = new byte[is.available()];
				is.readFully(bytes);
				is.close();
			}else{
				// If the file is a .js file, try to find the file as a properties file
//TODO EDT NLS				
//				if (uri.endsWith(RuntimePropertiesFileUtil.JS_SUFFIX)) {
//					// It's a runtime properties file.
//					IFile ifile = findRequiredFile(RuntimePropertiesFileUtil.convertToPropertiesFile(uri), projectName, getIFileLocator(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)));
//					if (ifile != null) {
//						bytes = new RuntimePropertiesFileGenerator().generatePropertiesFile(ifile, RuntimePropertiesFileUtil.getBundleName(uri));
//					}
//				}
//TODO EDT deployment		
//				else if (uri.endsWith(DeploymentDescriptorFileUtil.JS_SUFFIX)) {
//					DeploymentDescriptor ir = null;
//					try {
//						ir = DeploymentDescriptorFileUtil.getDeploymentDescriptorIR(DeploymentDescriptorFileUtil.convertToEglddFile(uri).toLowerCase(), ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
//					} catch (Exception e) {}
//					if (ir != null) {
//						bytes = new DeploymentDescGenerator().generateBindFile(ir);
//					}
//				}
//TODO EDT NLS				
//				else if(uri.endsWith(".js")){
//					// Attempt to load this file as a properties file for NLS
//					IFile ifile = findRequiredFile(PropertiesFileUtil.convertToProperitesFile(uri), projectName, getIFileLocator(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)));
//					if(ifile != null && ifile.exists()){	
//						PropertiesFileUtil propFileUtil = new PropertiesFileUtil(uri);
//						NLSPropertiesFileGenerator generator = new NLSPropertiesFileGenerator();
//						bytes = generator.generatePropertiesFile(ifile, propFileUtil.getBundleName());
//					}
//				}
			}
			if(bytes == null && uri.endsWith(".html")){ //$NON-NLS-1$
				// Generate HTML file
				String resourceName = uri.substring(uri.indexOf(projectName) + projectName.length() + 1, uri.length());
				bytes = generateHTMLFile(locator, resourceName.substring(0, resourceName.indexOf(".html")), projectName); //$NON-NLS-1$
			}
		}
		return bytes;
	}
	
	private EGLResource findRequiredFile(String uri, String projectName, FileLocator locator){
		EGLResource resource = null;
		String projectRelativePath = uri.substring(uri.indexOf(projectName) + projectName.length() + 1, uri.length());
		String applicationLocation = ""; //$NON-NLS-1$
		String resourceName  = applicationLocation.length() > 0 ? projectRelativePath.substring(applicationLocation.length() + 1, projectRelativePath.length()) : projectRelativePath;
	
		while(resourceName != ""){ //$NON-NLS-1$
			resource = locator.findResource(resourceName);
			if(resource != null && resource.exists() && resource.isFile()){
				break; // we found a file
			}else if(projectRelativePath.indexOf('/', applicationLocation.length() + 1) == -1){
				resourceName = ""; // we have reached the end of the URI and could not find a file //$NON-NLS-1$
			}else{
				// shift part of the resource name to the application location and try again
				applicationLocation = projectRelativePath.substring(0, projectRelativePath.indexOf("/", applicationLocation.length() + 1)); //$NON-NLS-1$
				resourceName = projectRelativePath.substring(applicationLocation.length() + 1, projectRelativePath.length());
			}
		}
		return resource;
	}
	
//TODO EDT NLS
//	private IFile findRequiredFile(String uri, String projectName, IFileLocator locator){
//		IFile file = null;
//		String projectRelativePath = uri.substring(uri.indexOf(projectName) + projectName.length() + 1, uri.length());
//		String applicationLocation = ""; //$NON-NLS-1$
//		String resourceName  = applicationLocation.length() > 0 ? projectRelativePath.substring(applicationLocation.length() + 1, projectRelativePath.length()) : projectRelativePath;
//	
//		while(resourceName != ""){ //$NON-NLS-1$
//			file = locator.findFile(resourceName);
//			if(file != null && file.exists()){
//				break; // we found a file
//			}else if(projectRelativePath.indexOf('/', applicationLocation.length() + 1) == -1){
//				resourceName = ""; // we have reached the end of the URI and could not find a file //$NON-NLS-1$
//			}else{
//				// shift part of the resource name to the application location and try again
//				applicationLocation = projectRelativePath.substring(0, projectRelativePath.indexOf("/", applicationLocation.length() + 1)); //$NON-NLS-1$
//				resourceName = projectRelativePath.substring(applicationLocation.length() + 1, projectRelativePath.length());
//			}
//		}
//		return file;
//	}
	
	protected byte[] generateHTMLFile( FileLocator locator, String resourceName, String projectName) throws IOException, SAXException{
//		HTMLGenerator generator;
//		// Locate the deploy file for this HTML file
//		EGLResource resource = locator.findResource(resourceName.concat(".deploy")); //$NON-NLS-1$
//		if(resource != null){
//			DotDeployFile deployFile = XmlDeployFileUtil.getDeployFile(resource);
//			HashMap eglProperties = new HashMap();
//			eglProperties.put(IConstants.CONTEXT_ROOT_PARAMETER_NAME, projectName);		
//			eglProperties.put(IConstants.HTML_FILE_LOCALE, getHandlerMessageLocale());
//			eglProperties.put(IConstants.DEFAULT_LOCALE_PARAMETER_NAME, getRuntimeMessageLocale());
//			
//			// If this file is in the preview pane, add information to the returned HTML file as necessary
//			generator = getDevelopmentGenerator(locator, resourceName, eglProperties, 
//					getHandlerMessageLocale(), getRuntimeMessageLocale(), deployFile);
//			return generator.generate();
//		}
//		return null;
		
		
		
		ProjectEnvironment environment = null;
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(projectName);
			environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
			Environment.pushEnv(environment.getIREnvironment());			
			environment.getIREnvironment().initSystemEnvironment(environment.getSystemEnvironment()); 
			
			String[] splits = resourceName.split("/");
			String[] packageName = new String[splits.length-1];
			for(int i=0; i<splits.length-1; i++){
				packageName[i] = splits[i];
			}
			String partName = splits[splits.length-1];
			Part part = environment.findPart(InternUtil.intern(packageName), InternUtil.intern(partName));
			
			if (part != null && !part.hasCompileErrors()) {
				EGL2HTML4VE cmd = new EGL2HTML4VE();
						
				HashMap eglProperties = new HashMap();
				eglProperties.put(IConstants.CONTEXT_ROOT_PARAMETER_NAME, projectName);		
				eglProperties.put(IConstants.HTML_FILE_LOCALE, getHandlerMessageLocale());
				eglProperties.put(IConstants.DEFAULT_LOCALE_PARAMETER_NAME, getRuntimeMessageLocale());
				Generator generator = getDevelopmentGenerator(cmd, null, eglProperties, getHandlerMessageLocale(), getRuntimeMessageLocale());
				String result = cmd.generate(part, generator, environment.getIREnvironment());
				return result.getBytes();
			}
		} catch (PartNotFoundException e) {
			e.printStackTrace();
//			buildPartNotFoundMessage(e, messageRequestor, partName);
		} catch (RuntimeException e) {
			e.printStackTrace();
//			handleRuntimeException(e, messageRequestor, partName, new HashSet());
		} catch (final Exception e) {
			e.printStackTrace();
//			handleUnknownException(e, messageRequestor);
		}
		finally{
			if(environment != null){
				Environment.popEnv();
			}
		}
		
		return null;
	}
	
	protected abstract FileLocator getFileLocator(IProject project)throws CoreException;
	protected abstract IFileLocator getIFileLocator(IProject project)throws CoreException;
	
	protected abstract HTMLGenerator getDevelopmentGenerator(AbstractGeneratorCommand processor, String egldd, HashMap eglProperties, String userMsgLocale, String runtimeMsgLocale);

	
	protected String getRuntimeMessageLocale() {
		String runtimeMsgLocale = this.store.getString(IRUIPreferenceConstants.PREFERENCE_RUNTIME_MESSAGES_LOCALE);
		if (runtimeMsgLocale == null || runtimeMsgLocale.length() == 0) {
			runtimeMsgLocale = LocaleUtility.getDefaultRuntimeLocale().getCode();
		}
		return runtimeMsgLocale;
	}
	
	protected String getHandlerMessageLocale() {
		String runtimeMsgLocale = this.store.getString(IRUIPreferenceConstants.PREFERENCE_HANDLER_LOCALE);
		if (runtimeMsgLocale == null || runtimeMsgLocale.length() == 0) {
			runtimeMsgLocale = LocaleUtility.getDefaultHandlerLocale().getCode();
		}
		return runtimeMsgLocale;
	}
	
}
