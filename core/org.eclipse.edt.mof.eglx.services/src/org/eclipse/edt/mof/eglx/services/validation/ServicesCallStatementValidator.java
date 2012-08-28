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
package org.eclipse.edt.mof.eglx.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.StatementValidator;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.services.Utils;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;

public class ServicesCallStatementValidator implements StatementValidator{

	private IProblemRequestor problemRequestor;
   	@Override
	public void validate(Node node, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
	}

	@Override
	public void validateStatement(Statement stmt, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(stmt instanceof CallStatement){
			return;
		}
		Member function = ((CallStatement)stmt).getInvocationTarget().resolveMember();
		if (function == null || !(function instanceof Function)) {
			//error...target must be a function if callback or error routine specified
            problemRequestor.acceptProblem(((CallStatement)stmt).getInvocationTarget(), ResourceKeys.FUNCTION_CALL_TARGET_MUST_BE_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		this.problemRequestor = problemRequestor;
		if (((CallStatement)stmt).getUsing() != null) {
			Type usingType = ((CallStatement)stmt).getUsing().resolveType();
			if (usingType != null) {
				if (!Utils.isIHTTP(usingType)) {
					problemRequestor.acceptProblem(((CallStatement)stmt).getUsing(), ResourceKeys.SERVICE_CALL_USING_WRONG_TYPE, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
				}
			}
		}

		//validate the arguments against the parms
		((CallStatement)stmt).accept(new FunctionArgumentValidator((Function)function, problemRequestor, compilerOptions));
		
		//check to make sure a callback is specified
		if (((CallStatement)stmt).getCallSynchronizationValues() == null || ((CallStatement)stmt).getCallSynchronizationValues().getReturnTo() == null) {
			problemRequestor.acceptProblem(((CallStatement)stmt).getInvocationTarget(), ResourceKeys.FUNCTION_CALLBACK_FUNCTION_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
		}

		if (((CallStatement)stmt).getCallSynchronizationValues() != null) {
			//validate callback/error routine
			if (((CallStatement)stmt).getCallSynchronizationValues().getReturnTo() != null) {
				validateCallback(((CallStatement)stmt), ((CallStatement)stmt).getCallSynchronizationValues().getReturnTo().getExpression(), false, ((CallStatement)stmt).getInvocationTarget());
			}
			if (((CallStatement)stmt).getCallSynchronizationValues().getOnException() != null) {
				validateCallback(((CallStatement)stmt), ((CallStatement)stmt).getCallSynchronizationValues().getOnException().getExpression(), true, ((CallStatement)stmt).getInvocationTarget());
			}		
		}
	}
	private void validateCallback(CallStatement stmt, Expression expr, boolean isErrorCallback, Expression invocTarget ) {
		// 1) must be a function or delegeate
		
		Member cbMember = expr.resolveMember();
		if (cbMember == null) {
			return;
		}
		if (!(cbMember instanceof Function)) {
			problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_CALLBACK_MUST_BE_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		//2) if is it is a nested function it must be defined inside this part 
		if (stmt.getParent() instanceof Part && !cbMember.getContainer().equals(((Part)stmt.getParent()).getName().resolveType())) {
			problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_MUST_BE_DEFINED_IN_PART, IMarker.SEVERITY_ERROR, new String[] {cbMember.getId(), ((Part)stmt.getParent()).getName().getCanonicalName()}, ResourceKeys.getResourceBundleForKeys());
		}
		
				
		// 3) must not have a return type
		if (getReturnType(expr) != null) {
			problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_CANNOT_HAVE_RETURN_TYPE, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString()}, ResourceKeys.getResourceBundleForKeys());
		}
		
		List<FunctionParameter> parms = getParameters(expr);
		// 3) all parms must be IN
		for (FunctionParameter parm : parms) {
			if (parm.getParameterKind() != ParameterKind.PARM_IN) {
				problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_MUST_HAVE_ALL_IN_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString()}, ResourceKeys.getResourceBundleForKeys());
				break;
			}
		}
		
		// 4) ErrorRoutine must have 1 parm, with a type of AnyException
		if (isErrorCallback) {
			if( parms.size() == 1 ||
					(parms.size() == 2 && lastParmIsIHttp(parms))) {
				//make sure the parm is AnyException
				if (!isAnyException(parms.get(0).getType())) {
					problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_PARM_MUST_HAVE_TYPE, IMarker.SEVERITY_ERROR, new String[] {"1", expr.getCanonicalString(), getQualAnyExceptionString()}, ResourceKeys.getResourceBundleForKeys());
				}
			}
			else {
				problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_REQUIRES_N_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString(), lastParmIsIHttp(parms)? "2" : "1"}, ResourceKeys.getResourceBundleForKeys());
			}
		}
		else {
			//5) Number of parms in the callback must be correct
			List<Member> args = getArgTypesForCallback(invocTarget);
			
			if(args.size() == parms.size() ||
					(args.size() == parms.size() - 1 && lastParmIsIHttp(parms))) {
				// 6) parms in the callback function should be move compatible with the out/inout args/return type of the service function

				for (int i = 0; i < args.size(); i++) {
					if (!argTypeCompatibleWithParm(args.get(i), parms.get(i))) {
						problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_TYPE_NOT_COMPAT_WITH_PARM, IMarker.SEVERITY_ERROR, new String[] {args.get(i).getType().getTypeSignature(), parms.get(i).getCaseSensitiveName(), expr.getCanonicalString(), parms.get(i).getType().getTypeSignature()}, ResourceKeys.getResourceBundleForKeys());
					}
				}
			
			}
			else{
				problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_REQUIRES_N_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString(),  Integer.toString(lastParmIsIHttp(parms) ? args.size() + 1 :args.size())}, ResourceKeys.getResourceBundleForKeys());
			}
			
		}
		
	}
	
	private boolean lastParmIsIHttp(List<FunctionParameter> parms){
		if(parms.size() > 0){
			//function has 1 extra parameter 
			//see if it's an IHttp 
			Type lastParmType = parms.get(parms.size() - 1).getType();
			return "eglxhttp.IHttp".equals(lastParmType.getTypeSignature());
		}
		return false;
	}
	
	private List<Member> getArgTypesForCallback(Expression invocTarget) {
		List<Member> list = new ArrayList<Member>();
		for (FunctionParameter parameter : getParameters(invocTarget)) {
			if (parameter.getParameterKind() != ParameterKind.PARM_IN) {
				list.add(parameter);
			}
		}
		
		Type retType = invocTarget.resolveType();
		if (retType != null) {
			list.add(invocTarget.resolveMember());
		}
		
		return list;
	}
	private List<FunctionParameter> getParameters(Expression expr) {
		if (expr.resolveType() != null) {
			if (expr.resolveType() instanceof Delegate) {
				return ((Delegate)expr.resolveType()).getParameters();
			}
			if (expr.resolveType() instanceof Function) {
				return ((Function)expr.resolveType()).getParameters();
			}
		}
		return new ArrayList<FunctionParameter>();
	}
	private Type getReturnType(Expression expr) {
		if (expr.resolveMember() != null) {
			if (expr.resolveMember() instanceof Delegate) {
				return ((Delegate)expr.resolveMember()).getReturnType();
			}
			if (expr.resolveMember() instanceof Function) {
				return ((Function)expr.resolveMember()).getReturnType();
			}
		}
		return null;
	}
	
	private FunctionContainerScope getFunctionContainerScope(Scope scope) {
		if (scope == null) {
			return null;
		}
		if (scope instanceof FunctionContainerScope) {
			return (FunctionContainerScope)scope;
		}
		return getFunctionContainerScope(scope.getParentScope());
	}
	
	
	private boolean argTypeCompatibleWithParm(Member argRHS, FunctionParameter parmLHS) {
		if (argRHS == null) {
			return true;
		}
		
    	if (IRUtils.isMoveCompatible(parmLHS.getType(), argRHS.getType(), argRHS)) {
    		return true;
    	}
		
 	   if (argRHS.getType() instanceof NamedElement &&
 			   TypeUtils.areCompatible(parmLHS.getType().getClassifier(), (NamedElement)argRHS.getType())) {
 		   return true;
 	   }
 	   return false;

	}
	
	
	private boolean isAnyException(Type type) {
		return "eglx.lang.AnyException".equals(type.getTypeSignature());
	}
	
	private String getQualAnyExceptionString() {
		return InternUtil.intern("eglx.lang" + "." + IEGLConstants.EGL_ANYEXCEPTION);
	}

}
