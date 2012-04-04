/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.eunit.internal.actions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.eunit.EGL2JavascriptDriver;
import org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class GenTestDriverActionJavascript extends GenTestDriverAction {

	protected static final String DRIVERPROJSUFFIX_JAVASCRIPT = ".eunit.javascript";	
	protected static final String token_javascript = "javascript";
	
	
	
	public GenTestDriverActionJavascript(){
		super();
		EUNITRUNTIME_FILE_BYLANG = new String[]{"CreateResultFile.egl", "TestResultService.egl"};
	}	
	
	protected List<WorkspaceModifyOperation> getGenTestDriverOperatoins(IWorkspaceRoot wsRoot, 
											String baseProjName, IProject baseProj,IEGLProject baseEGLProj) {
		
		String driverProjName = getDriverProjName(baseProjName);
		IProject driverProj = wsRoot.getProject(driverProjName);
		IEGLProject driverEGLProject = EGLCore.create(driverProj);

		List<WorkspaceModifyOperation> ops = new ArrayList<WorkspaceModifyOperation>();

		WorkspaceModifyOperation op1 = getCreateEGLProjectOperation(wsRoot, driverProjName, baseProjName);
		ops.add(op1);

		// configure the generator id
		WorkspaceModifyOperation op2 = getSetGeneratorIDOperation(driverProj, new String[]{GENERATORID_JAVASCRIPT, GENERATORID_JAVASCRIPT_DEV});
		ops.add(op2);
				
		// set EGL build path
		WorkspaceModifyOperation op3 = getSetEGLBuildPathOperation(driverEGLProject, driverProjName, baseProjName);
		ops.add(op3);

		WorkspaceModifyOperation op5 = getCopyJSECKRuntimeFilesOperation(driverProj);
		ops.add(op5);

		WorkspaceModifyOperation op6 = getCreateRununitPropertyOperation(wsRoot, driverProj);
		ops.add(op6);

		WorkspaceModifyOperation op7 = getGenDriverOperation(wsRoot, baseProj, baseEGLProj, driverProj, driverEGLProject);
		ops.add(op7);

		WorkspaceModifyOperation op8 = getRefreshWSOperation(baseProj, driverProj);
		ops.add(op8);

		return ops;
	}

	protected String getDriverProjName(String baseProjName) {
		String driverProjName = baseProjName + DRIVERPROJSUFFIX_JAVASCRIPT;
		return driverProjName;
	}
	
	protected WorkspaceModifyOperation getCopyJSECKRuntimeFilesOperation(IProject driverProj){
		return getCopyECKRuntimeFilesOperation(driverProj, token_javascript);
	}	

	@Override
	protected void invokeDriverGenerator(String[] args, IIDECompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier) {
		EGL2JavascriptDriver.start(args, compiler, eckGenerationNotifier);		
	}

	//this is a temporary work around to override parent method, 
	//to create rununit.html in WebContent folder, because javascript does not support sysLib.getProperties() 
	protected WorkspaceModifyOperation getCreateRununitPropertyOperation(final IWorkspaceRoot wsRoot, final IProject driverProject){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {			
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				monitor.subTask("Creating rununit property " + RESULTROOT_KEY);
				
				String resultRootFolder = driverProject.getFolder(RESULTROOT_DIR_APPEND).getLocation().toOSString();
				String jsonResultRootFolder = convertJavaToJson(resultRootFolder);
				
				//create rununit.html under WebContent folder, create this folder if not existing.
				IFolder webContentFolder = driverProject.getFolder("WebContent");
				if(!webContentFolder.exists())
					webContentFolder.create(true, true, monitor);
				
				IFile propertyFile = webContentFolder.getFile("rununit.html");
				String propertyOSPath = propertyFile.getLocation().toOSString();
				PrintWriter outWriter;
				try {
					outWriter = new PrintWriter(propertyOSPath);				
					outWriter.println("<script type=\"text/javascript\">");
					outWriter.println("try{egl.eze$$runtimeProperties['rununit'] = ");
					outWriter.print("{\"");
					outWriter.print(RESULTROOT_KEY);
					outWriter.print("\" : \"");
					outWriter.print(jsonResultRootFolder);
					outWriter.println("\"};}catch(e){ }");
					outWriter.println("</script>");
					
					outWriter.flush();
					outWriter.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
								
				monitor.worked(1);	
			}
		};
		return op;
	}
		
	
	private static java.lang.String convertJavaToJson(java.lang.String value) {
		StringBuilder inBuf = new StringBuilder(value);
		StringBuilder outBuf = new StringBuilder();
		char currentChar;
		for( int idx = 0; idx < inBuf.length(); idx++ )
		{
			currentChar = inBuf.charAt( idx );
			switch(currentChar)
			{
				case('\\'):
					outBuf.append( "\\\\" );
					break;
				case('\"'):
					outBuf.append( "\\\"" );
					break;
				case('/'):
					outBuf.append( "\\/" );
					break;
				case('\b'):
					outBuf.append( "\\b" );
					break;
				case('\f'):
					outBuf.append( "\\f" );
					break;
				case('\n'):
					outBuf.append( "\\n" );
					break;
				case('\r'):
					outBuf.append( "\\r" );
					break;
				case('\t'):
					outBuf.append( "\\t" );
					break;
				default:
					if( Character.isISOControl( currentChar ) )
					{
						String controlEsc = "0000";
						controlEsc += Integer.toHexString( currentChar );
						outBuf.append( "\\u" );
						outBuf.append( controlEsc.substring( controlEsc.length() - 4 ) );
					}
					else
					{
						outBuf.append( currentChar );
					}
					break;
			}
		}
		return outBuf.toString();
	}
	
}
