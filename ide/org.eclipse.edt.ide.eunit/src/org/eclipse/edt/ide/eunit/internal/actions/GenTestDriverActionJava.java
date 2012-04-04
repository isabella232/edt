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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.edt.gen.eunit.EGL2JavaDriver;
import org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class GenTestDriverActionJava extends GenTestDriverAction {
	
	//the following generator ids are defined in the plugin.xml of org.eclipse.edt.ide.compiler	
	protected static final String DRIVERPROJSUFFIX_JAVA = ".eunit.java";
	private static final String token_java = "java";	

	public GenTestDriverActionJava(){
		super();
		EUNITRUNTIME_FILE_BYLANG = new String[]{"CreateResultFile.egl"};
	}
	
	protected List<WorkspaceModifyOperation> getGenTestDriverOperatoins(IWorkspaceRoot wsRoot, 
			String baseProjName, IProject baseProj, IEGLProject baseEGLProj) {
		
		String javaDriverProjName = baseProjName + DRIVERPROJSUFFIX_JAVA;
		IProject driverProj = wsRoot.getProject(javaDriverProjName);
		IEGLProject driverEGLProject = EGLCore.create(driverProj);

		List<WorkspaceModifyOperation> ops = new ArrayList<WorkspaceModifyOperation>();

		WorkspaceModifyOperation op1 = getCreateEGLProjectOperation(wsRoot, javaDriverProjName, baseProjName);
		ops.add(op1);

		// configure the generator id
		WorkspaceModifyOperation op2 = getSetGeneratorIDOperation(driverProj, new String[]{GENERATORID_JAVA});
		ops.add(op2);

		// set EGL build path
		WorkspaceModifyOperation op3 = getSetEGLBuildPathOperation(driverEGLProject, javaDriverProjName, baseProjName);
		ops.add(op3);

		// set java build path
		WorkspaceModifyOperation op4 = getSetJavaBuildPathOperation(driverProj, baseProj);
		ops.add(op4);

		WorkspaceModifyOperation op5 = getCopyECKRuntimeFilesOperation(driverProj, token_java);
		ops.add(op5);

		WorkspaceModifyOperation op6 = getCreateRununitPropertyOperation(wsRoot, driverProj);
		ops.add(op6);

		WorkspaceModifyOperation op7 = getGenDriverOperation(wsRoot, baseProj, baseEGLProj, driverProj, driverEGLProject);
		ops.add(op7);

		WorkspaceModifyOperation op8 = getRefreshWSOperation(baseProj, driverProj);
		ops.add(op8);

		return ops;
	}

	@Override
	protected void invokeDriverGenerator(String[] args, IIDECompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier) {
		EGL2JavaDriver.start(args, compiler, eckGenerationNotifier);  			
	}

}
