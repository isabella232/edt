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
package org.eclipse.edt.mof.eglx.jtopen.validation;


import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.validation.AbstractStatementValidator;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.Utils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;

public class IBMiProgramCallStatementValidator extends AbstractStatementValidator {

	public boolean visit(CallStatement callStatement){
		Member targFunction = callStatement.getInvocationTarget().resolveMember();
		if (!(targFunction instanceof Function)) {
			//error...target must be a function if callback or error routine specified
            problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IBMiResourceKeys.FUNCTION_CALL_TARGET_MUST_BE_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
			return false;
		}
		
		if (targFunction.getAnnotation("eglx.jtopen.annotations.IBMiProgram") == null) {
			problemRequestor.acceptProblem(callStatement, IBMiResourceKeys.IBMIPROGRAM_MUST_BE_SPECIFIED, IMarker.SEVERITY_ERROR, new String[] {((Function)targFunction).getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			return false;
		}

		
		//validate the arguments against the parms
		callStatement.accept(new FunctionArgumentValidator((Function)targFunction, problemRequestor, compilerOptions));
		
		callStatement.getInvocationTarget().accept( new AbstractASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName qualifiedName){
				if(qualifiedName.getQualifier() instanceof SimpleName
						&& !(qualifiedName.getQualifier().resolveType() instanceof Library)){
					problemRequestor.acceptProblem(qualifiedName.getQualifier(), IBMiResourceKeys.IBMIPROGRAM_TARGET_IS_SERVICE_QUALIFIED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
					return false;
				}
				return true;
			}
		});
		
		
		//if the function returns a value, a returns is required
		if (((Function)targFunction).getReturnType() != null &&
				(callStatement.getCallSynchronizationValues() == null || callStatement.getCallSynchronizationValues().getReturns() == null)) {
			problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IBMiResourceKeys.IBMIPROGRAM_CALLBACK_OR_RETURNS_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
		}
		
		if (callStatement.getUsing() == null && targFunction.getAnnotation("eglx.lang.Resource") == null) {
			problemRequestor.acceptProblem(callStatement, IBMiResourceKeys.IBMIPROGRAM_USING_HAS_NO_CONNECTION, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	
		
		if (callStatement.getCallSynchronizationValues() != null) {
			if (callStatement.getCallSynchronizationValues().getReturns() != null) {
				//If a returns is specified, the function must return a value
				if (((Function)targFunction).getReturnType() == null) {
					problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturns(), IBMiResourceKeys.IBMIPROGRAM_RETURNS_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {targFunction.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
				}
				else {
				//Ensure that the returns type of the call is compatible with the function's return type
					Expression callReturnsExpr = callStatement.getCallSynchronizationValues().getReturns().getExpression();
					Member callReturnsMember = callReturnsExpr.resolveMember();
					if (!TypeUtils.areCompatible(((Function)targFunction).getReturnType().getClassifier(), callReturnsMember)){
						problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturns(), 
														IBMiResourceKeys.IBMIPROGRAM_RETURNS_NOT_COMPAT_WITH_FUNCTION, 
														IMarker.SEVERITY_ERROR, 
														new String[] {Utils.getTypeName((Function)targFunction), ((Function)targFunction).getCaseSensitiveName(), Utils.getTypeName(callReturnsMember), callReturnsExpr.getCanonicalString()},
														IBMiResourceKeys.getResourceBundleForKeys());
					}
				}
			}
			//validate callback/error routine 
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturnTo(), IBMiResourceKeys.IBMIPROGRAM_CALLBACK_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
			}
			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getOnException(), IBMiResourceKeys.IBMIPROGRAM_CALLBACK_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
			}		
		}			
		return false;
	}
}
