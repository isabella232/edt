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
package org.eclipse.edt.mof.eglx.jtopen;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class Utils {
	
	private static final String IBMiConnectionMofKey = MofConversion.EGL_KeyScheme + "eglx.jtopen.IBMiConnection";
	
	public static boolean isIBMiConnection(Type type) {
		return TypeUtils.isTypeOrSubtypeOf(type, IBMiConnectionMofKey);
	}
	
	public static boolean isFunctionServiceQualified(Expression exp, Function function) {
		return !(exp instanceof FieldAccess && ((FieldAccess)exp).getPrimary() instanceof ThisExpression) &&
				function.getContainer() instanceof Service;
	}
}
