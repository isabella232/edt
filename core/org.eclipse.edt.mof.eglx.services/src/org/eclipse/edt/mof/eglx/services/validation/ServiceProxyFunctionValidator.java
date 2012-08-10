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
package org.eclipse.edt.mof.eglx.services.validation;

import org.eclipse.edt.compiler.FunctionValidator;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Statement;


/**
 * @author demurray
 */
public abstract class ServiceProxyFunctionValidator implements FunctionValidator {
	
	@Override
	public void validateFunction(NestedFunction nestedFunction, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(!(nestedFunction.getName().resolveMember() instanceof Function)){
			return;
		}
		
		if (!(((IRPartBinding)declaringPart).getIrPart() instanceof Library || 
				((IRPartBinding)declaringPart).getIrPart() instanceof Handler)) {
			problemRequestor.acceptProblem(nestedFunction, IProblemRequestor.ANNOTATION_NOT_APPLICABLE, IMarker.SEVERITY_ERROR, new String[] {getName()});
		}
		
		validateFunctionBodyIsEmpty((Function)nestedFunction.getName().resolveMember(), nestedFunction, problemRequestor);
		validate(nestedFunction, problemRequestor, compilerOptions);
		
	}

	private void validateFunctionBodyIsEmpty(Function function, Node node, IProblemRequestor problemRequestor) {
		if (function.getStatementBlock() != null && function.getStatementBlock().getStatements() != null && 
				function.getStatementBlock().getStatements().size() > 0) {
			
			for(Statement stmt : function.getStatementBlock().getStatements()) {
				problemRequestor.acceptProblem(stmt, IProblemRequestor.PROXY_FUNCTIONS_CANNOT_HAVE_STMTS, IMarker.SEVERITY_ERROR, new String[] {function.getCaseSensitiveName()});
			}
		}
	}
	
	@Override
	public void validateFunction(Constructor constructor, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		//not used
	}
	protected abstract void validate(NestedFunction nestedFunction, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
	protected abstract String getName();

	@Override
	public void validate(Node node, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
	}
}
