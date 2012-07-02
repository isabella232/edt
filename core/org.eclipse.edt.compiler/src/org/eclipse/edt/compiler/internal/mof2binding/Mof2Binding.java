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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.mof.EObject;


public class Mof2Binding extends Mof2BindingMember {
	
	public Mof2Binding(IBindingEnvironment env) {
		super(env);
	}

	public IPartBinding convert(EObject part) {
		part.accept(this);
		IBinding binding = stack.pop();
		if (Binding.isValidBinding(binding) && binding.getActualBindingName() != null) {
			resolvePrimitiveAsExternalTypeBinding = true;
			part.accept(this);
			binding = stack.pop();
			resolvePrimitiveAsExternalTypeBinding = false;
		}
		
		((IPartBinding)binding).setValid(true);
		return (IPartBinding)binding;
	}
	
}
