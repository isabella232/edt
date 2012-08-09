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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ServicesActionStatementValidator extends DefaultStatementValidator {

    
    public ServicesActionStatementValidator() {
    }
    
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
		validateServiceFunctionCall(callStatement);
		return false;
	}
	
	
	private void validateServiceFunctionCall(CallStatement callStatement) {
		
		//if a callback or error callback is specified, the target must point to a function
		if (callStatement.getCallSynchronizationValues() != null && 
				(callStatement.getCallSynchronizationValues().getOnException() != null || 
				 callStatement.getCallSynchronizationValues().getReturnTo() != null)) {
			Member function = callStatement.getInvocationTarget().resolveMember();
			if (function == null || !(function instanceof Function)) {
				//error...target must be a function if callback or error routine specified
	            problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IProblemRequestor.FUNCTION_CALL_TARGET_MUST_BE_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {});
				return;
			}
		}
		

		
		if (callStatement.getUsing() != null) {
			Type usingType = callStatement.getUsing().resolveType();
			if (usingType != null) {
				if (!isIHTTP(usingType, new ArrayList())) {
					problemRequestor.acceptProblem(callStatement.getUsing(), IProblemRequestor.SERVICE_CALL_USING_WRONG_TYPE, IMarker.SEVERITY_ERROR, new String[] {});
				}
			}
		}

		if (dataBinding.getKind() == IDataBinding.NESTED_FUNCTION_BINDING) {
				//validate the arguments against the parms
				callStatement.accept(new FunctionArgumentValidator((IFunctionBinding)dataBinding.getType(), dataBinding.getDeclaringPart(), problemRequestor, compilerOptions));
			}
			
			//check to make sure a callback is specified
			if (callStatement.getCallSynchronizationValues() == null || callStatement.getCallSynchronizationValues().getReturnTo() == null) {
				problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IProblemRequestor.FUNCTION_CALLBACK_FUNCTION_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {});
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
			
		}
	}
	private void validateCallback(CallStatement stmt, Expression expr, boolean isErrorCallback, Expression invocTarget ) {
		// 1) must be a function or delegeate
		
		ITypeBinding cbType = expr.resolveTypeBinding();
		if (!Binding.isValidBinding(cbType)) {
			return;
		}
		if (cbType.getKind() != ITypeBinding.FUNCTION_BINDING) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_CALLBACK_MUST_BE_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {});
			return;
		}
		
		//2) if is it is a nested function it must be defined inside this part 
		if (expr.resolveDataBinding() != null && expr.resolveDataBinding().getKind() == IDataBinding.NESTED_FUNCTION_BINDING) {
//FIXME how do I get the scope			FunctionContainerScope fcScope = getFunctionContainerScope(currentScope);
			IBinding callStmtPart = null;
			Node node = stmt;
			while(!(node.getParent() instanceof File)){
				node = node.getParent();
			}
			if(node instanceof Part){
				callStmtPart = ((Part)node).getName().resolveBinding();
			}
			if (callStmtPart == null || callStmtPart != expr.resolveDataBinding().getDeclaringPart()) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_MUST_BE_DEFINED_IN_PART, IMarker.SEVERITY_ERROR, new String[] {expr.resolveDataBinding().getCaseSensitiveName(), stmt.getStatementBinding().getName()});
			}
		}
		
				
		// 3) must not have a return type
		if (getReturnType(expr) != null) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_CANNOT_HAVE_RETURN_TYPE, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString()});
		}
		
		
		// 3) all parms must be IN
		FunctionParameterBinding[] parms = getParameters(expr);
		for (int i = 0; i < parms.length; i++) {
			if (!parms[i].isInput()) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_MUST_HAVE_ALL_IN_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString()});
				break;
			}
		}
		
		// 4) ErrorRoutine must have 1 parm, with a type of AnyException
		if (isErrorCallback) {
			if( parms.length == 1 ||
					(parms.length == 2 && lastParmIsIHttp(parms))) {
				//make sure the parm is AnyException
				if (Binding.isValidBinding(parms[0]) && Binding.isValidBinding(parms[0].getType()) && !isAnyException(parms[0].getType())) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_PARM_MUST_HAVE_TYPE, IMarker.SEVERITY_ERROR, new String[] {"1", expr.getCanonicalString(), getQualAnyExceptionString()});
				}
			}
			else {
				problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_REQUIRES_N_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString(), lastParmIsIHttp(parms)? "2" : "1"});
			}
		}
		else {
			//5) Number of parms in the callback must be correct
			ITypeBinding[] argTypes = getArgTypesForCallback(invocTarget);
			
			if(argTypes.length == parms.length ||
					(argTypes.length == parms.length - 1 && lastParmIsIHttp(parms))) {
				// 6) parms in the callback function should be move compatible with the out/inout args/return type of the service function

				for (int i = 0; i < argTypes.length; i++) {
					if (!argTypeCompatibleWithParm(argTypes[i], parms[i])) {
						problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_TYPE_NOT_COMPAT_WITH_PARM, IMarker.SEVERITY_ERROR, new String[] {StatementValidator.getShortTypeString(argTypes[i]), parms[i].getCaseSensitiveName(), expr.getCanonicalString(), StatementValidator.getShortTypeString(parms[i].getType())});
					}
				}
			
			}
			else{
				problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_REQUIRES_N_PARMS, IMarker.SEVERITY_ERROR, new String[] {expr.getCanonicalString(),  Integer.toString(lastParmIsIHttp(parms) ? argTypes.length + 1 :argTypes.length)});
			}
			
		}
		
	}
	
	private boolean lastParmIsIHttp(FunctionParameterBinding[] parms){
		if(parms.length > 0){
			//function has 1 extra parameter 
			//see if it's an IHttp 
			ITypeBinding lastParm = parms[parms.length - 1].getType();
			return (InternUtil.intern(new String[]{"eglx", "http"}).equals(lastParm.getPackageName()) && InternUtil.intern("IHttp").equals(lastParm.getName()));
		}
		return false;
	}
	
	private ITypeBinding[] getArgTypesForCallback(Expression invocTarget) {
		FunctionParameterBinding[] parms = getParameters(invocTarget);
		List<ITypeBinding> list = new ArrayList<ITypeBinding>();
		for (int i = 0; i < parms.length; i++) {
			if (parms[i].isOutput() || parms[i].isInputOutput()) {
				list.add(parms[i].getType());
			}
		}
		
		ITypeBinding retType = getReturnType(invocTarget);
		if (retType != null) {
			list.add(retType);
		}
		
		return (ITypeBinding[])list.toArray(new ITypeBinding[list.size()]);
	}
	private FunctionParameterBinding[] getParameters(Expression expr) {
		if (Binding.isValidBinding(expr.resolveTypeBinding())) {
			if (expr.resolveTypeBinding().getKind() == ITypeBinding.DELEGATE_BINDING) {
				List list = ((DelegateBinding)expr.resolveTypeBinding()).getParemeters();
				return (FunctionParameterBinding[])list.toArray(new FunctionParameterBinding[list.size()]);
			}
			if (expr.resolveTypeBinding().getKind() == ITypeBinding.FUNCTION_BINDING) {
				List list = ((IFunctionBinding)expr.resolveTypeBinding()).getParameters();
				return (FunctionParameterBinding[])list.toArray(new FunctionParameterBinding[list.size()]);
			}
		}
		return new FunctionParameterBinding[0];
	}
	private ITypeBinding getReturnType(Expression expr) {
		if (Binding.isValidBinding(expr.resolveTypeBinding())) {
			if (expr.resolveTypeBinding().getKind() == ITypeBinding.DELEGATE_BINDING) {
				return ((DelegateBinding)expr.resolveTypeBinding()).getReturnType();
			}
			if (expr.resolveTypeBinding().getKind() == ITypeBinding.FUNCTION_BINDING) {
				return ((IFunctionBinding)expr.resolveTypeBinding()).getReturnType();
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
	
	
	private boolean argTypeCompatibleWithParm(ITypeBinding argType, FunctionParameterBinding parm) {
		if (!Binding.isValidBinding(argType)) {
			return true;
		}
		
		if (!Binding.isValidBinding(parm) || !Binding.isValidBinding(parm.getType())) {
			return true;
		}

    	if (TypeCompatibilityUtil.isMoveCompatible(parm.getType(), argType, null, compilerOptions)) {
    		return true;
    	}
		
		
 	   if (argType.isDynamic()) {
 		   return true;
 	   }
    	
 	   if (TypeCompatibilityUtil.areCompatibleExceptions(argType, parm.getType(), compilerOptions)) {
 		   return true;
 	   }
 	   return false;

	}
	
	
	private boolean isAnyException(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		IPartBinding part =  (IPartBinding) type;
		if (InternUtil.intern(part.getPackageQualifiedName()) == getQualAnyExceptionString()) {
			return true;
		}
		
		return false;
	}
	
	private String getQualAnyExceptionString() {
		return InternUtil.intern("eglx.lang" + "." + IEGLConstants.EGL_ANYEXCEPTION);
	}
	
	private List getImplementedInterfaces(ITypeBinding type) {
		
		if (Binding.isValidBinding(type) && ITypeBinding.HANDLER_BINDING == type.getKind()) {
			HandlerBinding hand = (HandlerBinding)type;
			return hand.getImplementedInterfaces();
		}

		if (Binding.isValidBinding(type) && ITypeBinding.INTERFACE_BINDING == type.getKind()) {
			InterfaceBinding inter = (InterfaceBinding)type;
			return inter.getExtendedTypes();
		}

		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			ExternalTypeBinding et = (ExternalTypeBinding)type;
			return et.getExtendedTypes();
		}

		return null;
	}
	
	private boolean isIHTTP(Type type) {
		
		return type instanceof ExternalType &&
				
		
		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			if (type.getName() == InternUtil.intern("IHTTP") && type.getPackageName() == InternUtil.intern(new String[] {"eglx", "http"})) {
				return true;
			}
		}

		List interfaces = getImplementedInterfaces(type);
		if (interfaces != null) {
			Iterator i = interfaces.iterator();
			while (i.hasNext()) {
				ITypeBinding imp = (ITypeBinding)i.next();
				if (isIHTTP(imp, seenTypes)) {
					return true;
				}
			}
		}
		

		return false;
	}
	
	
}
