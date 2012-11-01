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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * @author Dave Murray
 */
public class TryStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private IPartBinding enclosingPart;
	private Set caughtExceptionTypes = new HashSet();
		
	public TryStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.enclosingPart = enclosingPart;		
	}
	
	public boolean visit(TryStatement tryStatement) {
		for(Iterator iter = tryStatement.getOnExceptionBlocks().iterator(); iter.hasNext();) {
			((OnExceptionBlock) iter.next()).accept(this);
		}
		return false;
	}
	
	public boolean visit(OnExceptionBlock onExceptionBlock) {
		if (enclosingPart != null) {
			Type exceptionType = onExceptionBlock.getExceptionType();
			org.eclipse.edt.mof.egl.Type exceptionTypeBinding = exceptionType.resolveType();
			
			if (exceptionTypeBinding != null) {
				if (caughtExceptionTypes.contains(exceptionTypeBinding)) {
					problemRequestor.acceptProblem(
						exceptionType,
						IProblemRequestor.DUPLICATE_ONEXCEPTION_EXCEPTION,
						new String[] {exceptionType.getCanonicalName()});
				}
				else {
					if (!ThrowStatementValidator.isAnyException(exceptionTypeBinding)) {
						problemRequestor.acceptProblem(
							exceptionType,
							IProblemRequestor.TYPE_IN_CATCH_BLOCK_NOT_EXCEPTION,
							new String[] {exceptionType.getCanonicalName()});
					}
					else {
						caughtExceptionTypes.add(exceptionTypeBinding);
					}
				}
			}
		}
		return false;
	}
}
