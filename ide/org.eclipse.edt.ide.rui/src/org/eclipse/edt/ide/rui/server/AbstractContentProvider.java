/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.internal.core.utils.SoftLRUCache;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.deployment.javascript.CompileErrorHTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.DeploymentDescGenerator;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.NLSPropertiesFileGenerator;
import org.eclipse.edt.gen.deployment.util.PropertiesFileUtil;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.utils.DefaultDeploymentDescriptorUtility;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.rui.internal.deployment.javascript.EGL2HTML4VE;
import org.eclipse.edt.ide.rui.internal.lookup.PreviewIREnvironmentManager;
import org.eclipse.edt.ide.rui.internal.nls.EWTPreviewMessages;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.edt.ide.rui.utils.EGLResource;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IConstants;
import org.eclipse.edt.ide.rui.utils.IFileLocator;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;


public abstract class AbstractContentProvider implements IServerContentProvider {
	public static SoftLRUCache RUI_JS_RUNTIME_FILE_CACHE = new SoftLRUCache();
	
	private IPreferenceStore store = EDTCoreIDEPlugin.getPlugin().getPreferenceStore();

	private String getRUIJSRuntimeFilePath(String uri){
		if(uri.contains("/runtime/")){ //$NON-NLS-1$
			String filePath = uri.substring(uri.lastIndexOf("/runtime/") + 9); //$NON-NLS-1$
			if(FileLocator.RUI_JAVASCRIPT_FILES.containsKey(filePath)){
				return filePath;
			}
		}
		return null;
	}
	
	public byte[] loadContent(String uri) throws IOException, SAXException, CoreException {
		byte[] bytes = null;
		int index = uri.indexOf('/'); //$NON-NLS-1$
		String projectName;
		if (index == -1) {
			projectName = ""; //$NON-NLS-1$
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
			//first get RUI JS Runtime file from cache
			String filePath = getRUIJSRuntimeFilePath(uri);
			if(filePath != null){
				//first find in cache
				bytes = (byte[])RUI_JS_RUNTIME_FILE_CACHE.get(filePath);
				if(bytes != null){
					return bytes;
				}
			}
			
			//if can not find in cache, go on to find in file system
			FileLocator locator = getFileLocator(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
			
			EGLResource file = findRequiredFile(uri, projectName, locator);	
			if(file!=null){
				DataInputStream is = new DataInputStream(new BufferedInputStream(file.getInputStream()));
				bytes = new byte[is.available()];
				is.readFully(bytes);
				is.close();
				
				//save RUI JS Runtime file in cache
				String filePath2 = getRUIJSRuntimeFilePath(uri);
				if(filePath2 != null && RUI_JS_RUNTIME_FILE_CACHE.get(filePath2) == null){
					RUI_JS_RUNTIME_FILE_CACHE.put(filePath2, bytes);
				}
			}else{
				// If the file is a .js file, try to find the file as a properties file
//TODO EDT runtime properties			
//				if (uri.endsWith(RuntimePropertiesFileUtil.JS_SUFFIX)) {
//					// It's a runtime properties file.
//					IFile ifile = findRequiredFile(RuntimePropertiesFileUtil.convertToPropertiesFile(uri), projectName, getIFileLocator(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)));
//					if (ifile != null) {
//						bytes = new RuntimePropertiesFileGenerator().generatePropertiesFile(ifile, RuntimePropertiesFileUtil.getBundleName(uri));
//					}
//				}
//				else
				if (uri.endsWith("-bnd.js")) {
					DeploymentDesc model = null;
					try {
						uri = uri.substring( projectName.length() + 1 );
						uri = uri.substring( 0, uri.length() - 7 );
						IFile eglddResource = (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(uri);

						String ddName = eglddResource.getName();
			    		ddName = ddName.substring(0, ddName.indexOf( eglddResource.getFileExtension() ) - 1);
		    			model = DeploymentDesc.createDeploymentDescriptor(ddName, eglddResource.getContents());
					} catch (Exception e) {}
					if (model != null) {
						bytes = new DeploymentDescGenerator().generateBindFile(model);
					}
				}
				else if(uri.endsWith(".js")){
					// Attempt to load this file as a properties file for NLS
					String[] propNames = PropertiesFileUtil.convertToProperitesFiles(uri);
					for(String propName : propNames){
						IFile ifile = findRequiredFile(propName, projectName, getIFileLocator(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)));
						if(ifile != null && ifile.exists()){	
							PropertiesFileUtil propFileUtil = new PropertiesFileUtil(uri);
							NLSPropertiesFileGenerator generator = new NLSPropertiesFileGenerator();
							bytes = generator.generatePropertiesFile(ifile.getContents(), propFileUtil.getBundleName());
							break;
						}
					}					
				}
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
	
	private IFile findRequiredFile(String uri, String projectName, IFileLocator locator){
		IFile file = null;
		String projectRelativePath = uri.substring(uri.indexOf(projectName) + projectName.length() + 1, uri.length());
		String applicationLocation = ""; //$NON-NLS-1$
		String resourceName  = applicationLocation.length() > 0 ? projectRelativePath.substring(applicationLocation.length() + 1, projectRelativePath.length()) : projectRelativePath;
	
		while(resourceName != ""){ //$NON-NLS-1$
			file = locator.findFile(resourceName);
			if(file != null && file.exists()){
				break; // we found a file
			}else if(projectRelativePath.indexOf('/', applicationLocation.length() + 1) == -1){
				resourceName = ""; // we have reached the end of the URI and could not find a file //$NON-NLS-1$
			}else{
				// shift part of the resource name to the application location and try again
				applicationLocation = projectRelativePath.substring(0, projectRelativePath.indexOf("/", applicationLocation.length() + 1)); //$NON-NLS-1$
				resourceName = projectRelativePath.substring(applicationLocation.length() + 1, projectRelativePath.length());
			}
		}
		return file;
	}
	
	protected byte[] generateHTMLFile( FileLocator locator, String resourceName, String projectName) throws IOException, SAXException{
		IEnvironment environment = null;
		try {
			IProject project =  ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			environment = getEnvironmentForGeneration(project);
			Environment.pushEnv(environment);			
			
			Part part=null;
			EObject eObject = environment.find(PreviewIREnvironmentManager.makeEGLKey(resourceName.replace("/", ".")));
			if(eObject!=null && eObject instanceof Part){
				part = (Part)eObject;
			}
			
			if (part != null && !part.hasCompileErrors()) {
				EGL2HTML4VE cmd = new EGL2HTML4VE();
						
				HashMap<String, String> eglProperties = new HashMap<String, String>();
				eglProperties.put(IConstants.CONTEXT_ROOT_PARAMETER_NAME, projectName);		
				eglProperties.put(IConstants.HTML_FILE_LOCALE, getHandlerMessageLocale());
				eglProperties.put(IConstants.DEFAULT_LOCALE_PARAMETER_NAME, getRuntimeMessageLocale());
				
				String egldd = DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(project).getPartName();
				List<String> egldds = EclipseUtilities.getDependentDescriptors(project);
				eglProperties.put(IConstants.DEFAULT_DD_PARAMETER_NAME, egldd);
							
				Generator generator = getDevelopmentGenerator(cmd, egldds, eglProperties, getHandlerMessageLocale(), getRuntimeMessageLocale());
				String result = cmd.generate(part, generator, environment);
				return result.getBytes();
			}
		} catch (Exception e) {
			EGL2HTML4VE cmd = new EGL2HTML4VE();
			String message = MessageFormat.format(EWTPreviewMessages.COMPILEFAILEDPAGE_HEADERMSG, new Object[] {resourceName.replace("/", ".")});
			CompileErrorHTMLGenerator generator = new CompileErrorHTMLGenerator(cmd, null, message);
			return generator.generate().getBytes();
		}
		finally{
			if(environment != null){
				Environment.popEnv();
			}
		}
		
		return null;
	}
	
	protected abstract IEnvironment getEnvironmentForGeneration(IProject project);
	protected abstract FileLocator getFileLocator(IProject project)throws CoreException;
	protected abstract IFileLocator getIFileLocator(IProject project)throws CoreException;
	
	protected abstract HTMLGenerator getDevelopmentGenerator(AbstractGeneratorCommand processor, List egldds, HashMap eglProperties, String userMsgLocale, String runtimeMsgLocale);

	
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
