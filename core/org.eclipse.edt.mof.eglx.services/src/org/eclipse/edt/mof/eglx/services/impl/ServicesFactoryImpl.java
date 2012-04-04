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
package org.eclipse.edt.mof.eglx.services.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.eglx.services.ServicesCallStatement;
import org.eclipse.edt.mof.eglx.services.ServicesFactory;
import org.eclipse.edt.mof.impl.EFactoryImpl;


public class ServicesFactoryImpl extends EFactoryImpl implements ServicesFactory {
	@Override
	public EClass getServicesCallStatementEClass() {
		return (EClass)getTypeNamed(ServicesCallStatement);
	}
	
	@Override
	public ServicesCallStatement createServicesCallStatement() {
		return (ServicesCallStatement)getServicesCallStatementEClass().newInstance();
	}
	
}
