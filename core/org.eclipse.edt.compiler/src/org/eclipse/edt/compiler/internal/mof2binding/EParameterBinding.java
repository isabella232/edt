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

import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

@SuppressWarnings("serial")
public class EParameterBinding extends FunctionParameterBinding {

	public EParameterBinding(String caseSensitiveInternedName,
			IPartBinding declarer, ITypeBinding typeBinding,
			IFunctionBinding functionBinding) {
		super(caseSensitiveInternedName, declarer, typeBinding, functionBinding);
		// TODO Auto-generated constructor stub
	}

}
