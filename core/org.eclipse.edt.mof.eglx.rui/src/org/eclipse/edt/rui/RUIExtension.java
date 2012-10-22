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
package org.eclipse.edt.rui;

import org.eclipse.edt.compiler.BaseCompilerExtension;
import org.eclipse.edt.compiler.SystemLibraryUtil;

public class RUIExtension extends BaseCompilerExtension {
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{SystemLibraryUtil.getSystemLibraryPath(RUIExtension.class, "egllib")};
	}
}
