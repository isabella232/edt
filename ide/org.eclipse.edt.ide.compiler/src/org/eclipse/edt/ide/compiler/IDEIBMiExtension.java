package org.eclipse.edt.ide.compiler;

import org.eclipse.edt.ide.core.IDEBaseCompiler;
import org.eclipse.edt.ide.core.IDEBaseCompilerExtension;
import org.eclipse.edt.mof.eglx.jtopen.IBMiExtension;
import org.eclipse.edt.mof.eglx.jtopen.gen.IBMiFactory;

public class IDEIBMiExtension extends IDEBaseCompilerExtension {
	
	public IDEIBMiExtension() {
		this.baseExtension = new IBMiExtension();
	}
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{IDEBaseCompiler.getPathToPluginDirectory(IBMiFactory.packageName, "egllib")};
	}
}
