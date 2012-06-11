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
package org.eclipse.edt.compiler.internal.egl2mof.eglx.jtopen;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class IBMiProgramCallStatementValidator extends DefaultStatementValidator {

    
    public IBMiProgramCallStatementValidator() {
    }
    
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
		validateIBMiProgramCall(callStatement);
		return false;
	}
	
	
	private void validateIBMiProgramCall(CallStatement callStatement) {
				
		ITypeBinding targType = callStatement.getInvocationTarget().resolveTypeBinding();
		if (!Binding.isValidBinding(targType) || ITypeBinding.FUNCTION_BINDING != targType.getKind()) {
			return;
		}
				
		IFunctionBinding functionBinding = (IFunctionBinding)targType;
		
		//TODO for now, do not allow a local function call to a function that is not an IBMiProgram function.		
		if (functionBinding.getAnnotation(InternUtil.intern(new String[]{"eglx", "jtopen", "annotations"}), InternUtil.intern("IBMiProgram")) == null) {
			problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IProblemRequestor.IBMIPROGRAM_MUST_BE_SPECIFIED, IMarker.SEVERITY_ERROR, new String[] {functionBinding.getCaseSensitiveName()});
			return;
		}

		
		//validate the arguments against the parms
		callStatement.accept(new FunctionArgumentValidator(functionBinding, functionBinding.getDeclarer(), problemRequestor, compilerOptions));
		
		//if the function returns a value, a returns is required
		if (functionBinding.getReturnType() != null && 
				(callStatement.getCallSynchronizationValues() == null || 
						(callStatement.getCallSynchronizationValues().getReturnTo() == null) &&
						(callStatement.getCallSynchronizationValues().getReturns() == null)
			)) {
			problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IProblemRequestor.IBMIPROGRAM_CALLBACK_OR_RETURNS_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {});
		}
		
		if (callStatement.getUsing() != null) {
			ITypeBinding usingType = callStatement.getUsing().resolveTypeBinding();
			if (Binding.isValidBinding(usingType)) {
				if (!isIBMiConnection(usingType)) {
					problemRequestor.acceptProblem(callStatement.getUsing(), IProblemRequestor.IBMIPROGRAM_USING_HAS_WRONG_TYPE, IMarker.SEVERITY_ERROR, new String[] {});
				}
			}
		}
		
		
		if (callStatement.getCallSynchronizationValues() != null) {
			if (callStatement.getCallSynchronizationValues().getReturns() != null) {
				//If a returns is specified, the function must return a value
				if (functionBinding.getReturnType() == null) {
					problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturns(), IProblemRequestor.IBMIPROGRAM_RETURNS_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {functionBinding.getCaseSensitiveName()});
				}
				else {
				//Ensure that the returns type of the call is compatible with the function's return type
					if (Binding.isValidBinding(functionBinding.getReturnType())) {
						Expression callReturnsExpr = callStatement.getCallSynchronizationValues().getReturns().getExpression();
						ITypeBinding callReturnsType = callReturnsExpr.resolveTypeBinding();
						if (!TypeCompatibilityUtil.isMoveCompatible(callReturnsType, functionBinding.getReturnType(), null, compilerOptions)) {
							problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturns(), IProblemRequestor.IBMIPROGRAM_RETURNS_NOT_COMPAT_WITH_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {StatementValidator.getShortTypeString(functionBinding.getReturnType()), functionBinding.getCaseSensitiveName(), StatementValidator.getShortTypeString(callReturnsType), callReturnsExpr.getCanonicalString()});
						}
					}
				}
			}
			//validate callback/error routine 
			//TODO for now, callback/exception functions are not supported
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getReturnTo(), IProblemRequestor.IBMIPROGRAM_CALLBACK_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {});
			}
			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				problemRequestor.acceptProblem(callStatement.getCallSynchronizationValues().getOnException(), IProblemRequestor.IBMIPROGRAM_CALLBACK_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {});
			}		
		}
			
	}
	
	private boolean isIBMiConnection(ITypeBinding type) {
		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			return (type.getName() == InternUtil.intern("IBMiConnection") && type.getPackageName() == InternUtil.intern(new String[] {"eglx", "jtopen"}));
		}
		return false;
	}
	
}
