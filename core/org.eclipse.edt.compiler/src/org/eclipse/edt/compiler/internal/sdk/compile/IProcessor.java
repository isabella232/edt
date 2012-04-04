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
package org.eclipse.edt.compiler.internal.sdk.compile;

import org.eclipse.edt.compiler.binding.IPartBinding;

public interface IProcessor {

	void doAddPart(String[] packageName, String caseInsensitiveInternedPartName);
	IPartBinding getPartBindingFromCache(String[] packageName, String partName);
	public boolean hasExceededMaxLoop();
	void addPart(String[] packageName, String caseSensitiveInternedPartName);
	IPartBinding requestCompilationFor(String[] packageName, String caseInsensitiveInternedPartName, boolean force);
	IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName);
	IPartBinding level02Compile(String[] packageName, String caseSensitiveInternedPartName);
    IPartBinding level03Compile(String[] packageName, String caseSensitiveInternedPartName);
}
