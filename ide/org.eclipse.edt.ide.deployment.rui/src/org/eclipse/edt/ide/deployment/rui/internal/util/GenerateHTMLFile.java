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
package org.eclipse.edt.ide.deployment.rui.internal.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.deployment.javascript.DeploymentHTMLGenerator;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.rui.internal.deployment.javascript.EGL2HTML4VE;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.utils.NameUtile;
import org.xml.sax.SAXException;

/**
 * Create a .html file given a .egl file containing an RUIHandler
 * 		Deployment is the only customer of this operation and this is embedded into a WorkspaceModifyOperation at
 * 		the wizard level. So I am removing it from this operation. 
 *
 */
public class GenerateHTMLFile{

	private IFile eglFile;
	private HashMap eglParameters;
	private String userMsgLocale;
	private final String runtimeMsgLocale;
	private List egldds;
	private FileLocator fileLocator;

	public GenerateHTMLFile(IFile eglFile, HashMap eglParameters, 
			String userMsgLocale, String runtimeMsgLocale, List egldds, FileLocator fileLocator){
		this.eglFile = eglFile;
		this.eglParameters = eglParameters;
		this.runtimeMsgLocale = runtimeMsgLocale;
		this.userMsgLocale = userMsgLocale;
		this.egldds = egldds;
		this.fileLocator = fileLocator;
	}

	public byte[] execute(final IGenerationMessageRequestor messageRequestor) throws Exception {
		IEGLElement element = EGLCore.create(eglFile);
		if(element instanceof EGLFile ){
			String[] packageName = ((EGLFile)element).getPackageName();
			IPath resourceName = new Path(""); //$NON-NLS-1$
			for (int i = 0; i < packageName.length; i++) {
				resourceName = resourceName.append(packageName[i]);				
			}
			resourceName = resourceName.append(((EGLFile)element).getElementName()).removeFileExtension();
			byte[] bytes = null;
			bytes = generateHTMLFileContents( resourceName.toString(), eglFile.getProject() );
//			DeploymentHTMLGenerator generator = new DeploymentHTMLGenerator(new DeployFileLocator(eglFile.getProject()), 
//					resourceName.toOSString(), eglParameters, userMsgLocale, runtimeMsgLocale, deployFile); // Always deploy as single file
//			bytes = generator.generate();
//			if( messageRequestor!= null && generator.getMissingImportErrors().size() > 0)
//			{
//				for( Iterator<String> itr = generator.getMissingImportErrors().iterator(); itr.hasNext();){
//					messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//							EGLMessage.EGL_DEPLOYMENT_MISSING_IMPORT_EXCEPTION, 
//							null,
//							new String[]{itr.next(), eglFile.getFullPath().toPortableString()}));
//				}
//			}
			
			return bytes;
		} else if ( element instanceof ClassFile ) {
//			String[] packageName = ((ClassFile)element).getPackageName();
//			IPath resourceName = new Path(""); //$NON-NLS-1$
//			for (int i = 0; i < packageName.length; i++) {
//				resourceName = resourceName.append(packageName[i]);				
//			}
//			resourceName = resourceName.append(((ClassFile)element).getElementName()).removeFileExtension();
//			byte[] bytes = null;
//			DeploymentHTMLGenerator generator = new DeploymentHTMLGenerator(new DeployFileLocator(eglFile.getProject()), 
//					resourceName.toOSString(), eglParameters, userMsgLocale, runtimeMsgLocale, deployFile); // Always deploy as single file
//			bytes = generator.generate();
//			if( messageRequestor!= null && generator.getMissingImportErrors().size() > 0)
//			{
//				for( Iterator<String> itr = generator.getMissingImportErrors().iterator(); itr.hasNext();){
//					messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//							EGLMessage.EGL_DEPLOYMENT_MISSING_IMPORT_EXCEPTION, 
//							null,
//							new String[]{itr.next(), eglFile.getFullPath().toPortableString()}));
//				}
//			}
//			
//			return bytes;
		}
		return null;
	}
	
	protected byte[] generateHTMLFileContents(String resourceName, IProject project) throws IOException, SAXException{
		ProjectEnvironment environment = null;
		try {
			environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
			Environment.pushEnv(environment.getIREnvironment());			
			environment.initIREnvironments();
			
			String[] splits = resourceName.split("/");
			String[] packageName = new String[splits.length-1];
			for(int i=0; i<splits.length-1; i++){
				packageName[i] = splits[i];
			}
			String partName = splits[splits.length-1];
			Part part = environment.findPart(NameUtile.getAsName(Util.stringArrayToQualifiedName(packageName)), NameUtile.getAsName(partName));
			
			if (part != null && !part.hasCompileErrors()) {
				EGL2HTML4VE cmd = new EGL2HTML4VE();
				Generator generator = new DeploymentHTMLGenerator(cmd, egldds,
						eglParameters, userMsgLocale, runtimeMsgLocale);
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
}
