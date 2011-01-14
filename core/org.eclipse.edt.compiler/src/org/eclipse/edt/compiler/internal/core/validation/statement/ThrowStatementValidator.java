/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ThrowStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;


/**
 * @author Dave Murray
 */
public class ThrowStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private IPartBinding enclosingPart;	
		
	public ThrowStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.enclosingPart = enclosingPart;		
	}
	
	public boolean visit(ThrowStatement throwStatement) {
		if(enclosingPart != null) {
			IAnnotationBinding aBinding = enclosingPart.getAnnotation(new String[] {"egl", "core"}, "V60ExceptionCompatibility");
			boolean isV60ExceptionCompatibility = aBinding != null && Boolean.YES == aBinding.getValue();
			
			if(isV60ExceptionCompatibility) {
				problemRequestor.acceptProblem(
					throwStatement,
					IProblemRequestor.THROW_NOT_VALID_WITH_V60EXCEPTIONCOMPATIBILITY);
			}
			else {
				ITypeBinding tBinding = throwStatement.getExpression().resolveTypeBinding();
				if(tBinding != null && IBinding.NOT_FOUND_BINDING != tBinding) {
					if(SystemPartManager.ANYEXCEPTION_BINDING != tBinding &&
					   tBinding.getAnnotation(new String[] {"egl", "core"}, "Exception") == null) {
						problemRequestor.acceptProblem(
							throwStatement.getExpression(),
							IProblemRequestor.THROW_TARGET_MUST_BE_EXCEPTION,
							new String[] {tBinding.getCaseSensitiveName()});
					}
				}
			}			
		}
		return false;
	}
}
