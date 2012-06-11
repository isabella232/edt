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

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;

@SuppressWarnings("serial")
public class ProxyTypeBinding extends PartBinding {
	ITypeBinding realBinding;
	
	public ProxyTypeBinding(String[] packageName, String caseSensitiveInternedName) {
		super(packageName, caseSensitiveInternedName);
	}

	public ITypeBinding getRealBinding() {
		return realBinding;
	}

	public void setRealBinding(ITypeBinding realBinding) {
		this.realBinding = realBinding;
	}

	@Override
	public boolean isDeclarablePart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getKind() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}

}
