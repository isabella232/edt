package org.eclipse.edt.compiler.internal.egl2mof.eglx.jtopen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
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
			problemRequestor.acceptProblem(callStatement.getInvocationTarget(), IProblemRequestor.IBMIPROGRAM_MUST_BE_SPECIFIED, IMarker.SEVERITY_ERROR, new String[] {});
			return;
		}

		
		//validate the arguments against the parms
		callStatement.accept(new FunctionArgumentValidator(functionBinding, functionBinding.getDeclarer(), problemRequestor, compilerOptions));
		
		//if the function returns a value, either a callback or a returns is required
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
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				validateCallback(callStatement, callStatement.getCallSynchronizationValues().getReturnTo().getExpression(), false, callStatement.getInvocationTarget());
			}
			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				validateCallback(callStatement, callStatement.getCallSynchronizationValues().getOnException().getExpression(), true, callStatement.getInvocationTarget());
			}		
		}
			
	}
	private void validateCallback(CallStatement stmt, Expression expr, boolean isErrorCallback, Expression invocTarget ) {
		// 1) must be a function or delegeate
		
		ITypeBinding cbType = expr.resolveTypeBinding();
		if (!Binding.isValidBinding(cbType)) {
			return;
		}
		if (cbType.getKind() != ITypeBinding.FUNCTION_BINDING && cbType.getKind() != ITypeBinding.DELEGATE_BINDING) {
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
		
		if (type.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
			IPartBinding part =  (IPartBinding) type;
			if (InternUtil.intern(part.getPackageQualifiedName()) == getQualAnyExceptionString()) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getQualAnyExceptionString() {
		return InternUtil.intern("eglx.lang" + "." + IEGLConstants.EGL_ANYEXCEPTION);
	}
	
	private boolean isIBMiConnection(ITypeBinding type) {
		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			return (type.getName() == InternUtil.intern("IBMiConnection") && type.getPackageName() == InternUtil.intern(new String[] {"eglx", "jtopen"}));
		}
		return false;
	}
	
}
