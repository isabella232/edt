/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.statement;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ThrowStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class ThrowStatementValidator extends DefaultASTVisitor {
	
	public static final String AnyExceptionMofKey = MofConversion.EGL_KeyScheme + "eglx.lang.AnyException";
	
	private IProblemRequestor problemRequestor;
	private IPartBinding enclosingPart;	
	
	public ThrowStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.enclosingPart = enclosingPart;		
	}
	
	public boolean visit(ThrowStatement throwStatement) {
		if (enclosingPart != null) {
			Type type = throwStatement.getExpression().resolveType();
			if (type != null) {
				if (!isAnyException(type)) {
					problemRequestor.acceptProblem(
						throwStatement.getExpression(),
						IProblemRequestor.THROW_TARGET_MUST_BE_EXCEPTION,
						new String[] {BindingUtil.getShortTypeString(type)});
				}
			}
		}
		return false;
	}
	
	public static boolean isAnyException(Type type) {
		return TypeUtils.isTypeOrSubtypeOf(type, AnyExceptionMofKey);
	}
}
