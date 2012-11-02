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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.validation.AbstractStatementValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.services.ext.Utils;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;

public class ServicesCallStatementValidator extends AbstractStatementValidator {
	@Override
	public boolean visit(CallStatement callStatement){
		Member function = callStatement.getInvocationTarget().resolveMember();
		if (function == null || !(function instanceof Function)) {
			//error...target must be a function if callback or error routine specified
            problemRequestor.acceptProblem(callStatement.getInvocationTarget(), ResourceKeys.FUNCTION_CALL_TARGET_MUST_BE_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
			return false;
		}
		
		//validate the arguments against the parms
		callStatement.accept(new FunctionArgumentValidator((Function)function, problemRequestor, compilerOptions));
		
		callStatement.getInvocationTarget().accept( new AbstractASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName qualifiedName){
				if(qualifiedName.getQualifier() instanceof SimpleName &&
						qualifiedName.getQualifier().resolveElement() instanceof Part &&
						!(qualifiedName.getQualifier().resolveElement() instanceof Library ||
								qualifiedName.getQualifier().resolveElement() instanceof Service)){
					problemRequestor.acceptProblem(qualifiedName.getQualifier(), ResourceKeys.TARGET_QUALIFIER_ERROR, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
					return false;
				}
				return false;
			}
		});

		//@Resource or a using clause are not required if the qualifier is a service
		if (callStatement.getUsing() == null && function.getAnnotation("eglx.lang.Resource") == null) {
			final boolean[] isService = new boolean[1];
			callStatement.getInvocationTarget().accept( new AbstractASTVisitor() {
				public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName qualifiedName){
					if(qualifiedName.getQualifier() instanceof SimpleName &&
							qualifiedName.getQualifier().resolveElement() instanceof Part &&
							!(qualifiedName.getQualifier().resolveElement() instanceof Library)){
						isService[0] = qualifiedName.getQualifier().resolveElement() instanceof Service;
						return false;
					}
					return false;
				}
			});
			if(!isService[0]){
				problemRequestor.acceptProblem(callStatement, ResourceKeys.USING_HAS_NO_CONNECTION, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
			}
		}
	
		if (callStatement.getUsing() != null && !Utils.isIHTTP(callStatement.getUsing().resolveType())) {
			problemRequestor.acceptProblem(callStatement, ResourceKeys.WRONG_USING_CLAUSE_TYPE, IMarker.SEVERITY_ERROR, new String[] {BindingUtil.getShortTypeString(callStatement.getUsing().resolveType(), true)}, ResourceKeys.getResourceBundleForKeys());
		}
	
		//the target function has a return but there is no returning to or returns expression
		if(((Function)function).getReturnType() != null
				&& (callStatement.getCallSynchronizationValues() == null ||
						(callStatement.getCallSynchronizationValues().getReturns() == null &&
								callStatement.getCallSynchronizationValues().getReturnTo() == null))){
			
			problemRequestor.acceptProblem(callStatement.getInvocationTarget(), ResourceKeys.FUNCTION_CALLBACK_FUNCTION_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
		}
		if (callStatement.getCallSynchronizationValues() != null) {
			//validate callback/error routine
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				validateCallback(callStatement, callStatement.getCallSynchronizationValues().getReturnTo().getExpression(), false, callStatement.getInvocationTarget());
			}
			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				validateCallback(callStatement, callStatement.getCallSynchronizationValues().getOnException().getExpression(), true, callStatement.getInvocationTarget());
			}		
		}
		return false;
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
		Node container = getContainer(stmt.getParent());
		if (container instanceof org.eclipse.edt.compiler.core.ast.Part && !cbMember.getContainer().equals(((org.eclipse.edt.compiler.core.ast.Part)container).getName().resolveType())) {
			problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_MUST_BE_DEFINED_IN_PART, IMarker.SEVERITY_ERROR, new String[] {cbMember.getCaseSensitiveName(), ((org.eclipse.edt.compiler.core.ast.Part)container).getName().getCanonicalName()}, ResourceKeys.getResourceBundleForKeys());
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
						problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_TYPE_NOT_COMPAT_WITH_PARM, IMarker.SEVERITY_ERROR, new String[] {BindingUtil.getTypeName(args.get(i)), parms.get(i).getCaseSensitiveName(), expr.getCanonicalString(), BindingUtil.getTypeName(parms.get(i))}, ResourceKeys.getResourceBundleForKeys());
					}
				}
			
			}
			else{
				problemRequestor.acceptProblem(expr, ResourceKeys.FUNCTION_REQUIRES_N_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString(),  Integer.toString(lastParmIsIHttp(parms) ? args.size() + 1 :args.size())}, ResourceKeys.getResourceBundleForKeys());
			}
			
		}
		
	}
	
	private Node getContainer(Node node){
		while(node.getParent() != null && !(node instanceof org.eclipse.edt.compiler.core.ast.Part)){
			node = node.getParent();
		}
		return node;
	}
	private boolean lastParmIsIHttp(List<FunctionParameter> parms){
		if(parms.size() > 0){
			//function has 1 extra parameter 
			//see if it's an IHttp 
			Type lastParmType = parms.get(parms.size() - 1).getType();
			return "eglx.http.IHttp".equals(lastParmType.getTypeSignature());
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
		
		Member function = invocTarget.resolveMember();
		if (function != null && function.getType() != null) {
			list.add(function);
		}
		
		return list;
	}
	private List<FunctionParameter> getParameters(Expression expr) {
		if (expr.resolveMember() instanceof Delegate) {
			return ((Delegate)expr.resolveMember()).getParameters();
		}
		if (expr.resolveMember() instanceof Function) {
			return ((Function)expr.resolveMember()).getParameters();
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
	
	private boolean argTypeCompatibleWithParm(Member argRHS, FunctionParameter parmLHS) {
		if (argRHS == null) {
			return true;
		}
		
    	if (IRUtils.isMoveCompatible(parmLHS.getType(), parmLHS, argRHS.getType(), argRHS)) {
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
		return NameUtile.getAsName("eglx.lang" + "." + IEGLConstants.EGL_ANYEXCEPTION);
	}

}
