/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class RBDCompiler extends BaseCompiler {
	
	public RBDCompiler() {}
	
	@Override
	public String getSystemEnvironmentPath() {
		
		if (systemEnvironmentRootPath == null) {

			String path = SystemEnvironmentUtil.getSystemLibraryPath(BindingCreator.class, "lib");
			
			systemEnvironmentRootPath = path + File.pathSeparator + super.getSystemEnvironmentPath();
		}
		return systemEnvironmentRootPath;
	}

	@Override
	public List<String> getImplicitlyUsedEnumerations() {
		
		List<String> implicitlyUsedEnumerations = new ArrayList<String>();
        implicitlyUsedEnumerations.add(InternUtil.intern("AlignKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("CaseFormatKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ColorKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ConvertDirection"));
        implicitlyUsedEnumerations.add(InternUtil.intern("HighlightKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("IntensityKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("LineWrapKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("EventKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("WindowAttributeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("DataSource"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ExportFormat"));
        implicitlyUsedEnumerations.add(InternUtil.intern("CommitScopeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("DisconnectKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("IsolationLevelKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("CommitControlKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ConsoleEventKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("PortletModeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("SessionScopeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("WindowStateKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("SecretKind"));
		return implicitlyUsedEnumerations;
	}
	
	@Override
	public List<String> getAllImplicitlyUsedEnumerations() {
		List<String> all = new ArrayList<String>();
		all.addAll(super.getAllImplicitlyUsedEnumerations());
		all.addAll(getImplicitlyUsedEnumerations());
		return all;
	}
}
