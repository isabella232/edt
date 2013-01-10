/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.impl.InternalEObject;

public class IRPartBinding implements IPartBinding {

	Part irPart;
	IEnvironment environment;
	
	public IRPartBinding(Part irPart) {
		super();
		this.irPart = irPart;
	}

	public Part getIrPart() {
		return irPart;
	}

	public void setIrPart(Part irPart) {
		this.irPart = irPart;
	}

	@Override
	public int getKind() {
		return BindingUtil.getPartTypeConstant(irPart);
	}

	@Override
	public boolean isValid() {
		return BindingUtil.isValid(irPart);
	}

	@Override
	public String getPackageName() {
		return irPart.getPackageName();
	}
	
	@Override
	public String getCaseSenstivePackageName() {
		return irPart.getCaseSensitivePackageName();
	}

	@Override
	public boolean isPartBinding() {
		return true;
	}

	@Override
	public String getPackageQualifiedName() {
		return irPart.getFullyQualifiedName();
	}

	@Override
	public String getName() {
		return irPart.getName();
	}

	@Override
	public String getCaseSensitiveName() {
		return irPart.getCaseSensitiveName();
	}

	@Override
	public boolean isPackageBinding() {
		return false;
	}


	@Override
	public IEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public void setEnvironment(IEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public void clear() {
		
		if (irPart ==  null) {
			return;
		}
		
		EClass eclass = irPart.getEClass();
		String name = irPart.getCaseSensitiveName();
		String pkg = irPart.getCaseSensitivePackageName();
		((InternalEObject)irPart).setSlots(null);
		eclass.initialize(irPart);
		
		irPart.setName(name);
		irPart.setPackageName(pkg);
		BindingUtil.setValid(irPart, false);
	}

	@Override
	public void setValid(boolean isValid) {
		BindingUtil.setValid(irPart, isValid);
	}
	
	@Override
	public boolean isPrivate() {
		return BindingUtil.isPrivate(irPart);
	}

}
