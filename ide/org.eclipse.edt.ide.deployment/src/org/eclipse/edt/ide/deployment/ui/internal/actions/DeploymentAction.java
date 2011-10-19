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
package org.eclipse.edt.ide.deployment.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.deployment.javascript.DeploymentHTMLGenerator;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.deployment.gen.EclipseEGL2HTML;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class DeploymentAction implements IActionDelegate {

	private IStructuredSelection fSelection;
	@Override
	public void run(IAction action) {
		System.out.println("Run Action " + action.getId() + " , " + action.getText());
		try{
			IResource resource;
			for (Iterator<IResource> iter = fSelection.iterator(); iter.hasNext();) {
				resource = iter.next();
				if (resource instanceof IFile) {	
					final IWorkbench wb = PlatformUI.getWorkbench();
					IProgressService ps = wb.getProgressService();
					final IFile res = (IFile)resource;
				    try{
					    ps.busyCursorWhile(new IRunnableWithProgress() {
					    	public void run(IProgressMonitor monitor) {
					    		deployHandler(res, monitor);
					    	}
					    });
				    }catch( Exception e ) {
						e.printStackTrace();
					}
				}				
			}	
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	private void deployHandler(final IFile resource, final IProgressMonitor monitor){		
		ProjectEnvironment environment = null;
		try {
			Part part = null;
			IProject project = resource.getProject();
			IPath partFile = resource.getFullPath();
			String partName = resource.getName();
			partName = partName.substring(0, partName.length()-4 );
			String[] packageName;
			environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
			Environment.pushEnv(environment.getIREnvironment());
			
			if (EGLCore.create(project).isBinary()) {
				packageName = getPackageName(partFile, environment);
			}
			else {
				packageName = getPackageName(partFile, ProjectInfoManager.getInstance().getProjectInfo(project));
			}

			environment.getIREnvironment().initSystemEnvironment(environment.getSystemEnvironment()); 
			part = environment.findPart(InternUtil.intern(packageName), InternUtil.intern(partName));
			
			if (part != null && !part.hasCompileErrors()) {
				EclipseEGL2HTML cmd = new EclipseEGL2HTML(resource);
				// TODO only for testing, can remove later
				List egldds = new ArrayList();
				egldds.add( "testdd" );
				Generator generator = new DeploymentHTMLGenerator(cmd, egldds, null, null, "en_US", "en_US", environment.getSystemEnvironment());
				cmd.generate(part, generator, environment.getIREnvironment());
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
		//TODO: Judget if this is a RUI Handler
		//TODO: call DeploymentHTMLGenerator
		
		System.out.println(resource.getName());
	}
	
	protected String[] getPackageName(IPath path, ProjectInfo projectInfo) {
		path = path.removeFirstSegments(1); // project name
		path = path.removeLastSegments(1);// filename
		String[] retVal = Util.pathToStringArray(path);
		while (retVal.length > 0) {
			if (projectInfo.hasPackage(InternUtil.intern(retVal))) {
				break;
			}
			path = path.removeFirstSegments(1);// source folder
			retVal = Util.pathToStringArray(path);
		}
		return retVal;
	}
	
	protected String[] getPackageName(IPath partFile, ProjectEnvironment environment) {
		partFile = partFile.removeFirstSegments(1); // project name
		partFile = partFile.removeLastSegments(1);// filename
		String[] retVal = Util.pathToStringArray(partFile);
		while (retVal.length > 0) {
			if (environment.hasPackage(InternUtil.intern(retVal))) {
				break;
			}
			partFile = partFile.removeFirstSegments(1);// source folder
			retVal = Util.pathToStringArray(partFile);
		}
		return retVal;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = (IStructuredSelection)selection;
	}
	
	

}
