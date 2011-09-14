/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.services.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.eglx.services.ServicesAddStatement;
import org.eclipse.edt.mof.eglx.services.ServicesDeleteStatement;
import org.eclipse.edt.mof.eglx.services.ServicesReplaceStatement;
import org.eclipse.edt.mof.eglx.services.ServicesUpdateStatement;
import org.eclipse.edt.mof.eglx.services.ServicesCallStatement;
import org.eclipse.edt.mof.eglx.services.ServicesFactory;
import org.eclipse.edt.mof.impl.EFactoryImpl;


public class ServicesFactoryImpl extends EFactoryImpl implements ServicesFactory {
	@Override
	public EClass getServicesCallStatementEClass() {
		return (EClass)getTypeNamed(ServicesCallStatement);
	}
	@Override
	public EClass getServicesAddStatementEClass() {
		return (EClass)getTypeNamed(ServicesAddStatement);
	}
	@Override
	public EClass getServicesDeleteStatementEClass() {
		return (EClass)getTypeNamed(ServicesDeleteStatement);
	}
	@Override
	public EClass getServicesReplaceStatementEClass() {
		return (EClass)getTypeNamed(ServicesReplaceStatement);
	}
	@Override
	public EClass getServicesUpdateStatementEClass() {
		return (EClass)getTypeNamed(ServicesUpdateStatement);
	}
	
	@Override
	public ServicesCallStatement createServicesCallStatement() {
		return (ServicesCallStatement)getServicesCallStatementEClass().newInstance();
	}
	@Override
	public ServicesAddStatement createServicesAddStatement() {
		return (ServicesAddStatement)getServicesAddStatementEClass().newInstance();
	}
	@Override
	public ServicesDeleteStatement createServicesDeleteStatement() {
		return (ServicesDeleteStatement)getServicesDeleteStatementEClass().newInstance();
	}
	@Override
	public ServicesReplaceStatement createServicesReplaceStatement() {
		return (ServicesReplaceStatement)getServicesReplaceStatementEClass().newInstance();
	}
	@Override
	public ServicesUpdateStatement createServicesUpdateStatement() {
		return (ServicesUpdateStatement)getServicesUpdateStatementEClass().newInstance();
	}
	
}
