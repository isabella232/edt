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
package org.eclipse.edt.compiler.internal.mof2binding;

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;

public class ClassBinding extends ExternalTypeBinding {

	private static final long serialVersionUID = 1L;

	public ClassBinding(String[] packageName, String caseSensitiveInternedName) {
		super(packageName, caseSensitiveInternedName);
	}

}
