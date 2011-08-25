package org.eclipse.edt.ide.eunit.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.edt.gen.eck.EGL2JavascriptDriver;
import org.eclipse.edt.gen.eck.IEckGenerationNotifier;
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
		
		String driverProjName = baseProjName + DRIVERPROJSUFFIX_JAVASCRIPT;
		IProject driverProj = wsRoot.getProject(driverProjName);
		IEGLProject driverEGLProject = EGLCore.create(driverProj);

		List<WorkspaceModifyOperation> ops = new ArrayList<WorkspaceModifyOperation>();

		WorkspaceModifyOperation op1 = getCreateEGLProjectOperation(wsRoot, driverProjName, baseProjName);
		ops.add(op1);

		// configure the generator id
		WorkspaceModifyOperation op2 = getSetGeneratorIDOperation(driverProj, GENERATORID_JAVASCRIPT);
		ops.add(op2);

		// set EGL build path
		WorkspaceModifyOperation op3 = getSetEGLBuildPathOperation(driverEGLProject, driverProjName, baseProjName);
		ops.add(op3);

		WorkspaceModifyOperation op5 = getCopyECKRuntimeFilesOperation(driverProj, token_javascript);
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
	protected void invokeDriverGenerator(String[] args, IIDECompiler compiler, IEckGenerationNotifier eckGenerationNotifier) {
		EGL2JavascriptDriver.start(args, compiler, eckGenerationNotifier);		
	}

}
