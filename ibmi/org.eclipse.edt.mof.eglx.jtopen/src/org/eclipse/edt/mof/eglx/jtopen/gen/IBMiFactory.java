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
package org.eclipse.edt.mof.eglx.jtopen.gen;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.eglx.jtopen.gen.impl.IBMiFactoryImpl;

public interface IBMiFactory extends EFactory  {
	public static final IBMiFactory INSTANCE = new IBMiFactoryImpl();
	public String packageName = "org.eclipse.edt.mof.eglx.jtopen";
	
	String IBMiFunction = packageName+".IBMiFunction";
	String IBMiCallStatement = packageName+".IBMiCallStatement";
	
	EClass getIBMiFunctionEClass();
	EClass getIBMiCallStatementEClass();
	public IBMiFunction createIBMiFunction();
	public IBMiCallStatement createIBMiCallStatement();
}
