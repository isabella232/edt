/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.egl2mof.AbstractElementGenerator;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class ServiceElementGenerator extends AbstractElementGenerator {
	
	ServicesFactory factory = ServicesFactory.INSTANCE;
	
	public ServiceElementGenerator() {
		super(null);
	}
	
	public ServiceElementGenerator(IEnvironment env) {
		super(env);
	}

	public boolean visit(CallStatement callStatement) {
		stack.push(factory.createServicesCallStatement());
		return false;
	};
	
	public boolean visit(NestedFunction nestedFunction) {
		stack.push(factory.createServiceFunction());
		return false;
	};
}
