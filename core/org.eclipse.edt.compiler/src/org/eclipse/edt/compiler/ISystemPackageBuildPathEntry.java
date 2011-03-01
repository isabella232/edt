/*******************************************************************************
 * Copyright � 2010 IBM Corporation and others.
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

import java.util.HashMap;

import org.eclipse.edt.compiler.binding.IPartBinding;

public interface ISystemPackageBuildPathEntry {

	public IPartBinding getPartBinding(String entry);
	public IPartBinding getPartBinding(String[] packageName,String partName);
	public boolean hasPackage(String[] packageName);
	public void clearParts();
	public HashMap getPartBindingsWithoutPackage();
	public HashMap getPartNamesByPackage();
}
