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


import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.Utils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;

public class IBMiProgramCallStatementValidator extends DefaultStatementValidator {

    
    public IBMiProgramCallStatementValidator() {
    }
    
	public boolean visit(CallStatement callStatement) {
		validateIBMiProgramCall(callStatement);
		return false;
	}
	
	
	private void validateIBMiProgramCall(CallStatement callStatement) {
				
		Member targFunction = callStatement.getInvocationTarget().resolveMember();
		if (!(targFunction instanceof Function)) {
			return;
		}
				
		//TODO for now, do not allow a local function call to a function that is not an IBMiProgram function.		
		if (targFunction.getAnnotation("eglx.jtopen.annotations.IBMiProgram") == null) {
			problemRequestor.acceptProblem(callStatement, IBMiResourceKeys.IBMIPROGRAM_MUST_BE_SPECIFIED, IMarker.SEVERITY_ERROR, new String[] {((Function)targFunction).getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			return;
		}

		
		//validate the arguments against the parms
//FIXME		callStatement.accept(new FunctionArgumentValidator(functionBinding, functionBinding.getDeclarer(), problemRequestor, compilerOptions));
		
		//if the function returns a value, a returns is required
		if (((Function)targFunction).getReturnType() != null &&
						(callStatement.getCallSynchronizationValues().getReturns() == null)) {
			problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IBMiResourceKeys.IBMIPROGRAM_CALLBACK_OR_RETURNS_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
		}
		
		if (callStatement.getUsing() != null) {
			if (!Utils.isIBMiConnection(callStatement.getUsing().resolveType())) {
				problemRequestor.acceptProblem(callStatement.getUsing(), IBMiResourceKeys.IBMIPROGRAM_USING_HAS_WRONG_TYPE, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
			}
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
														new String[] {((Function)targFunction).getReturnType().getTypeSignature(), ((Function)targFunction).getCaseSensitiveName(), callReturnsMember.getType().getTypeSignature(), callReturnsExpr.getCanonicalString()},
														IBMiResourceKeys.getResourceBundleForKeys());
					}
				}
			}
			//validate callback/error routine 
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturns(), IBMiResourceKeys.IBMIPROGRAM_CALLBACK_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
			}
			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getOnException(), IBMiResourceKeys.IBMIPROGRAM_CALLBACK_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {}, IBMiResourceKeys.getResourceBundleForKeys());
			}		
		}			
	}
	
}
