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

import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

public class EFieldBinding extends ClassFieldBinding {
	private static final long serialVersionUID = 1L;
	
	boolean isTransient = false;
	boolean containment = false;

	public EFieldBinding(String caseSensitiveInternedName,
			IPartBinding declarer, ITypeBinding typeBinding) {
		super(caseSensitiveInternedName, declarer, typeBinding);
		// TODO Auto-generated constructor stub
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public boolean isContainment() {
		return containment;
	}

	public void setContainment(boolean containment) {
		this.containment = containment;
	}

}
