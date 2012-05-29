package org.eclipse.edt.ide.core.internal.lookup;

import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class IDESystemEnvironment extends SystemEnvironment {

	public IDESystemEnvironment(IEnvironment irEnv,
			ISystemEnvironment parentEnv,
			List<String> implicitlyUsedEnumerations, ICompiler compiler) {
		super(irEnv, parentEnv, implicitlyUsedEnumerations, compiler);
	}
	
	@Override
	protected void addSystemPackages(List<ISystemPackageBuildPathEntry> entries) {
		// TODO Auto-generated method stub
		super.addSystemPackages(entries);
		for (ISystemPackageBuildPathEntry entry : entries) {
			ZipFileBuildPathEntryManager.addSystemPathEntry(getCompiler(), entry);
		}
	}

}
