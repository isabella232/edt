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
package org.eclipse.edt.mof.eglx.services.gen;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.eglx.services.impl.ServicesFactoryImpl;

public interface ServicesFactory extends EFactory  {
	public static final ServicesFactory INSTANCE = new ServicesFactoryImpl();
	public String packageName = "org.eclipse.edt.mof.eglx.services";
	
	String ServicesCallStatement = packageName+".ServicesCallStatement";
	
	String ServiceFunction = packageName+".ServiceFunction";
	
	EClass getServiceFunctionEClass();
	public org.eclipse.edt.mof.egl.Function createServiceFunction();
	EClass getServicesCallStatementEClass();
	public CallStatement createServicesCallStatement();
}
