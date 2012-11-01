package org.eclipse.edt.ide.compiler;

import org.eclipse.edt.ide.core.IDEBaseCompiler;
import org.eclipse.edt.ide.core.IDEBaseCompilerExtension;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.SQLExtension;

public class IDESQLExtension extends IDEBaseCompilerExtension {
	
	public IDESQLExtension() {
		this.baseExtension = new SQLExtension();
	}
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{IDEBaseCompiler.getPathToPluginDirectory("org.eclipse.edt.mof.eglx.persistence.sql", "egllib")};
	}
}
