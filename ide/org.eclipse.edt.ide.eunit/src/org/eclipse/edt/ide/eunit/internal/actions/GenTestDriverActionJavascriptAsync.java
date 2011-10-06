package org.eclipse.edt.ide.eunit.internal.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.gen.eunit.EGL2JavascriptAsyncDriver;
import org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class GenTestDriverActionJavascriptAsync extends
		GenTestDriverActionJavascript {
	
	protected static final String DRIVERPROJSUFFIX_JAVASCRIPTASYNC = ".eunit.javascriptasync";	
	protected static final String token_javascriptasync = "javascriptasync";
	
	@Override
	protected void invokeDriverGenerator(String[] args, IIDECompiler compiler,
			IEUnitGenerationNotifier eckGenerationNotifier) {
		EGL2JavascriptAsyncDriver.start(args, compiler, eckGenerationNotifier);
	}
	
	protected WorkspaceModifyOperation getCopyJSECKRuntimeFilesOperation(IProject driverProj){
		return getCopyECKRuntimeFilesOperation(driverProj, token_javascriptasync);
	}		

	protected String getDriverProjName(String baseProjName) {
		String driverProjName = baseProjName + DRIVERPROJSUFFIX_JAVASCRIPTASYNC;
		return driverProjName;
	}	
}
