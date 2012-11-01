package org.eclipse.edt.ide.compiler;

import org.eclipse.edt.ide.core.IDEBaseCompiler;
import org.eclipse.edt.ide.core.IDEBaseCompilerExtension;
import org.eclipse.edt.mof.eglx.services.ext.ServicesExtension;

public class IDEServicesExtension extends IDEBaseCompilerExtension {
	
	public IDEServicesExtension() {
		this.baseExtension = new ServicesExtension();
	}
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{IDEBaseCompiler.getPathToPluginDirectory("org.eclipse.edt.mof.eglx.services", "egllib")};
	}
}
