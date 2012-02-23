/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.AnnotationTypeBinding.IsStringLiteralChecker;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.ForeignLanguageTypeBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NilBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionParameterSpecialTypeBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.statement.LValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.RValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author Dave Murray
 */
public class FunctionArgumentValidator extends DefaultASTVisitor {
	
	public static interface IInvocationNode{
		Expression getTarget();
		List getArguments();
	}
	
	public static class FunctionInvocationNode implements IInvocationNode{
		FunctionInvocation fInvoc;

		public FunctionInvocationNode(FunctionInvocation invoc) {
			super();
			fInvoc = invoc;
		}
		public Expression getTarget() {
			return fInvoc.getTarget();
		}
		
		public List getArguments() {
			return fInvoc.getArguments();
		}
		
	}
	
	public static class CallInvocationNode implements IInvocationNode {
		CallStatement call;

		public CallInvocationNode(CallStatement call) {
			super();
			this.call = call;
		}
		
		public Expression getTarget() {
			return call.getInvocationTarget();
		}
		
		public List getArguments() {
			return call.getArguments();
		}
		
	}
	
	
    public static class FunctionIdentifier {
    	String[] enclosingPartPackageName;
    	String enclosingPartName;
    	String functionName;
    	
    	FunctionIdentifier(String[] enclosingPartPackageName, String enclosingPartName, String functionName) {
    		if(enclosingPartPackageName != null) {
    			this.enclosingPartPackageName = InternUtil.intern(enclosingPartPackageName);
    		}
    		if(enclosingPartName != null) {
    			this.enclosingPartName = InternUtil.intern(enclosingPartName);
    		}
    		this.functionName = InternUtil.intern(functionName);
    	}
    	
    	public FunctionIdentifier(IFunctionBinding functionBinding) {
    		IPartBinding declarer = functionBinding.getDeclarer();
    		if(declarer != null) {
    			enclosingPartPackageName = declarer.getPackageName();
    			enclosingPartName = declarer.getName();
    		}
    		functionName = functionBinding.getName();
		}

		public boolean equals(Object obj) {
    		if(!(obj instanceof FunctionIdentifier)) {
    			return false;
    		}
    		FunctionIdentifier funcId = (FunctionIdentifier) obj;
    		return enclosingPartPackageName == funcId.enclosingPartPackageName &&
    		       enclosingPartName == funcId.enclosingPartName &&
    		       functionName == funcId.functionName;
    	}
    	
    	public int hashCode() {
			int result = 17;
			if(enclosingPartPackageName != null) {
				result = 37*result + enclosingPartPackageName.hashCode();
			}
			if(enclosingPartName != null) {
				result = 37*result + enclosingPartName.hashCode();
			}
			if(functionName != null) {
				result = 37*result + functionName.hashCode();
			}
			return result;
    	}
    }
	
	/**
	 * Subclasses of IArgumentChecker who should be called to validate specific arguments
	 * of system functions should be added to this map. 
	 */
	private static Map specialCaseArgumentCheckers = new HashMap();
	static {
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(null, null, ArrayTypeBinding.APPENDELEMENT.getName()), 1), new ArrayElementArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(null, null, ArrayTypeBinding.APPENDALL.getName()), 1), new AppendAllArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "io", "dli"}, "DLILib", "EGLTDLI"), 2), new EGLTDLIArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(null, null, ArrayTypeBinding.INSERTELEMENT.getName()), 1), new ArrayElementArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(null, null, ArrayTypeBinding.INDEXOFELEMENT1.getName()), 1), new ArrayElementArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "BYTES"), 1), new BytesArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERT"), 1), new ConvertArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTBIDI"), 1), new ConvertBidiArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTBIDI"), 3), new ConvertBidiArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTENCODEDTEXTTOSTRING"), 1), new StringNotAllowedArgumentChecker(true));
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "MAXIMUMSIZE"), 1), new MaximumSizeArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "PURGE"), 1), new PurgeArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "SETERROR"), 1), new SetErrorArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "SIZE"), 1), new SizeArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "vg"}, "VGLib", "STARTTRANSACTION"), 1), new StartTransactionArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "vg"}, "VGLib", "STARTTRANSACTION"), 2), new StringNotAllowedArgumentChecker(true));
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "vg"}, "VGLib", "VGTDLI"), 2), new VGTDLIArgumentChecker());
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTUNICODENUMTONUMBER"), 1), new UnicodeConversionFunctionArgumentChecker(true));
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTUNSIGNEDUNICODENUMTONUMBER"), 1), new UnicodeConversionFunctionArgumentChecker(false));
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTNUMBERTOUNICODENUM"), 2), new UnicodeConversionFunctionArgumentChecker(true));		
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERTNUMBERTOUNSIGNEDUNICODENUM"), 2), new UnicodeConversionFunctionArgumentChecker(false));
		specialCaseArgumentCheckers.put(new ArgInfo(new FunctionIdentifier(new String[] {"egl", "java"}, "PortalLib", "SETPORTLETSESSIONATTR"), 2), new NullNotAllowedArgumentChecker());
	}
	
	/**
	 * Subclasses of IArgumentChecker who should be called to validate all arguments
	 * of system functions should be added to this map. 
	 */
	private static Map specialCaseFunctionCheckers = new HashMap();
	static {
		specialCaseFunctionCheckers.put(new FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "CONVERT"), new ConvertArgumentChecker());
	}
	
	private static IArgumentChecker getSpecialCaseArgChecker(IFunctionBinding functionBinding, int argNum) {
		IArgumentChecker result = (IArgumentChecker) specialCaseArgumentCheckers.get(new ArgInfo(new FunctionIdentifier(functionBinding), argNum));
		if(result == null) {
			result = (IArgumentChecker) specialCaseFunctionCheckers.get(new FunctionIdentifier(functionBinding));
		}
		return result;
	}
	
	private IProblemRequestor problemRequestor;
	private IFunctionBinding functionBinding;
	private IPartBinding functionContainerBinding;
	private IInvocationNode fInvocationNode;
	private String canonicalFunctionName;
	private Iterator parameterIter;
	private int numArgs = 0;

	private ICompilerOptions compilerOptions;
		
	public FunctionArgumentValidator(IFunctionBinding functionBinding, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.functionBinding = functionBinding;
		this.functionContainerBinding = functionContainerBinding;
		parameterIter = functionBinding.getParameters().iterator();
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(FunctionInvocation functionInvocation) {
		fInvocationNode = new FunctionInvocationNode(functionInvocation);
		functionInvocation.getTarget().accept(new DefaultASTVisitor() {
			public boolean visit(SimpleName simpleName) {
				canonicalFunctionName = simpleName.getCanonicalName();
				return false;
			}
			public boolean visit(QualifiedName qualifiedName) {
				String canonicalName = qualifiedName.getCanonicalName();
				canonicalFunctionName = canonicalName.substring(canonicalName.lastIndexOf('.')+1);
				return false;
			}
		});
		if(canonicalFunctionName == null) {
			canonicalFunctionName = functionInvocation.getTarget().getCanonicalString();
		}
		for(Iterator iter = functionInvocation.getArguments().iterator(); iter.hasNext();) {
			checkArg((Expression) iter.next());
		}
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
		fInvocationNode = new CallInvocationNode(callStatement);
		canonicalFunctionName = callStatement.getInvocationTarget().getCanonicalString();

		
		if (!callStatement.hasArguments()) {
			return false;
		}
		
		for(Iterator iter = callStatement.getArguments().iterator(); iter.hasNext();) {
			checkArg((Expression) iter.next());
		}

		return false;
	}
	
	public boolean checkArg(final Expression argExpr) {
		numArgs += 1;
		FunctionParameterBinding parameterBinding;
		if(!parameterIter.hasNext()) {			
			if(functionBinding.isSystemFunction() && functionBinding.getValidNumbersOfArguments()[0] == IFunctionBinding.ARG_COUNT_N_OR_MORE) {
				parameterBinding = (FunctionParameterBinding) functionBinding.getParameters().get(functionBinding.getParameters().size()-1);				
			}
			else {			
				return false;
			}
		}
		else {
			parameterBinding = (FunctionParameterBinding) parameterIter.next();
		}
		ITypeBinding parameterType = parameterBinding.getType();
		
		ITypeBinding argType = argExpr.resolveTypeBinding();
		
		if(argType == null || argType == IBinding.NOT_FOUND_BINDING) {
			return false;
		}
		
		if(!checkArgumentNotSetValuesExpression(argExpr)) {
			return false;
		}
		
		if(!checkNativeLibraryArgumentNotSubstructuredItem(argExpr)) {
			return false;
		}
		
		if(!checkPSBRecordNotUsedAsArgument(argType, argExpr)) {
			return false;
		}

		if(!checkSubstringNotUsedAsArgument(parameterBinding, argExpr)) {
			return false;
		}

		if(!checkArgumentUsedCorrectlyWithInAndOut(argExpr, parameterBinding, parameterType)) {
			return false;
		}
		
		if(!checkArgumentUsedCorrectlyWithNullable(argExpr, parameterBinding)) {
			return false;
		}
		
		if(!checkArgumentUsedCorrectlyWithField(argExpr, parameterBinding)) {
			return false;
		}
				
		IArgumentChecker specialCaseArgChecker = getSpecialCaseArgChecker(functionBinding, numArgs);
		if(specialCaseArgChecker != null) {
			specialCaseArgChecker.checkArg(numArgs, argExpr, argType, fInvocationNode, functionContainerBinding, problemRequestor, compilerOptions, functionBinding);
			if(specialCaseArgChecker.performsCompleteCheck()) {
				return false;
			}
		}
		
		boolean argMatchesParm = true;
				
		if(ITypeBinding.SPECIALSYSTEMFUNCTIONPARAMETER_BINDING == parameterType.getKind()) {
			argMatchesParm = checkArgForSpecialTypeParameter(argExpr, argType, parameterType, numArgs);
		}
		else if(parameterBinding.isInput()) {
			argMatchesParm = checkArgForInParameter(argExpr, argType, parameterBinding, parameterType, numArgs);
		}
		else if(parameterBinding.isOutput()) {
			argMatchesParm = checkArgForOutParameter(argExpr, argType, parameterBinding, parameterType, numArgs);
		}
		else {
			argMatchesParm = checkArgForInOutParameter(argExpr, argType, parameterBinding, parameterType, numArgs);
		}
		
		if(!argMatchesParm) {
			return false;
		}
		
		if(!checkNoDynamicTypesPassedToMathLibFunctions(argExpr, argType, parameterBinding, parameterType)) {
			return false;
		}
		
		checkNullPassedToNonNullable(argExpr, parameterType, parameterBinding);
				
		return false;
	}
	
	private void checkNullPassedToNonNullable(Expression argExpr, ITypeBinding parameterType, FunctionParameterBinding parameterBinding) {
		
		if (argExpr.resolveTypeBinding() == NilBinding.INSTANCE && Binding.isValidBinding(parameterType) && !parameterType.isNullable()) {
			problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.CANNOT_PASS_NULL,
					new String[] {
						parameterBinding.getCaseSensitiveName(),
						functionBinding.getCaseSensitiveName()
					});
		}
	}
	
	public void endVisit(FunctionInvocation functionInvocation) {
//		if(functionBinding.isSystemFunction()) {
			if(!checkCorrectNumberOfArgumentsForSystemFunction()) {
				return;
			}
//		}
//		else {
//			if(!checkCorrectNumberOfArgumentsForUserFunction()) {
//				return;
//			}
//		}
	}

	public void endVisit(CallStatement callStatement) {
			if(!checkCorrectNumberOfArgumentsForSystemFunction()) {
				return;
			}
	}

	private boolean checkArgumentNotSetValuesExpression(final Expression argExpr) {
		final boolean[] result = new boolean[] {true};
		argExpr.accept(new DefaultASTVisitor() {
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			public boolean visit(SetValuesExpression setValuesExpression) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_FUNC_ARG);
				result[0] = false;
				return false;
			}
		});
		return result[0];
	}
	
	private boolean checkNativeLibraryArgumentNotSubstructuredItem(Expression argExpr) {
		if(!functionBinding.isSystemFunction() &&
		   functionBinding.getDeclarer().getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null) {
			IDataBinding argDBinding = argExpr.resolveDataBinding();
			if(argDBinding != null && argDBinding != IBinding.NOT_FOUND_BINDING) {
				if(IDataBinding.STRUCTURE_ITEM_BINDING == argDBinding.getKind() &&
				   !((StructureItemBinding) argDBinding).getChildren().isEmpty()) {
					problemRequestor.acceptProblem(
						argExpr,
						IProblemRequestor.SUBSTRUCTURED_ITEM_CANNOT_BE_ARGUMENT_TO_NATIVE_LIBRARY_FUNCTION,
						new String[] {
							argExpr.getCanonicalString(),
							functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
						});
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkPSBRecordNotUsedAsArgument(ITypeBinding argType, Expression argExpr) {
		if(argType.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null) {
			problemRequestor.acceptProblem(
				argExpr,
				IProblemRequestor.DLI_PSBRECORD_NOT_VALID_AS_ARGUMENT,
				new String[] {argExpr.getCanonicalString()});
			return false;
		}
		return true;
	}

	private boolean checkSubstringNotUsedAsArgument(FunctionParameterBinding parm, Expression argExpr) {
		
		if (Binding.isValidBinding(parm) && !parm.isInput() && argExpr instanceof SubstringAccess) {
			problemRequestor.acceptProblem(argExpr,
					IProblemRequestor.SUBSTRING_IMMUTABLE,
					new String[] {});
			return false;
		}
		
		return true;
	}

    private abstract static class NonLiteralAndNonNameExpressionVisitor extends AbstractASTExpressionVisitor {
		public void endVisit(ParenthesizedExpression parenthesizedExpression) {
			parenthesizedExpression.getExpression().accept(this);
		}

		public void endVisitName(Name name) {}
		public void endVisit(ArrayAccess arrayAccess) {}
		public void endVisit(SubstringAccess substringAccess) {}
		public void endVisit(FieldAccess fieldAccess) {}
		public void endVisitExpression(Expression expression) {
			handleExpressionThatIsNotNameOrLiteral(expression);
		}
		
		abstract void handleExpressionThatIsNotNameOrLiteral(Expression expression);
	} 
        
    private boolean checkArgumentUsedCorrectlyWithInAndOut(final Expression argExpr, final FunctionParameterBinding parmBinding, ITypeBinding parmType) {
    	ITypeBinding argTypeBinding = argExpr.resolveTypeBinding();
		if(!Binding.isValidBinding(argTypeBinding)) {
    		return true;
    	}
    	
    	final boolean[] expressionIsLiteralOrName = new boolean[] {true};
    	final boolean[] foundError = new boolean[] {false};
		argExpr.accept(new NonLiteralAndNonNameExpressionVisitor() {
			void handleExpressionThatIsNotNameOrLiteral(Expression expression) {
				if(!parmBinding.isInput()) {
					if(functionBinding.isSystemFunction()) {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.SYSTEM_FUNCTION_ARG_CANNOT_BE_EXPRESSION,
							new String[] {functionBinding.getCaseSensitiveName()});
					}
					else {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.FUNCTION_ARG_REQUIRES_IN_PARAMETER,
							new String[] {
								expression.getCanonicalString(),
								functionBinding.getCaseSensitiveName()
							});		
					}
					foundError[0] = true;
				}
				expressionIsLiteralOrName[0] = false;
			}
		});
		
		if(foundError[0]) return false;
		
		if(expressionIsLiteralOrName[0] && !parmBinding.isInput()) {
			if(!checkArgNotConstantOrLiteral(argExpr, IProblemRequestor.FUNCTION_ARG_LITERAL_NOT_VALID_WITH_OUT_PARAMETER)) {
				return false;
			}
		}
		
		if(expressionIsLiteralOrName[0] && !parmBinding.isInput() && !parmBinding.isOutput() && !parmBinding.isConst()) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == parmType.getKind() &&
			   Primitive.isDateTimeType(((PrimitiveTypeBinding) parmType).getPrimitive())) {
				if(!checkArgNotConstantOrLiteral(argExpr, IProblemRequestor.FUNCTION_ARG_LITERAL_NOT_VALID_WITH_INOUT_DATETIME_PARAMETER)) {
					return false;
				}
			}
		}
		
		IDataBinding argDBinding = argExpr.resolveDataBinding();
		if(argDBinding != null && argDBinding != IBinding.NOT_FOUND_BINDING) {
			if(IDataBinding.LOCAL_VARIABLE_BINDING == argDBinding.getKind() ||
			   IDataBinding.CLASS_FIELD_BINDING == argDBinding.getKind() ||
			   IDataBinding.SYSTEM_VARIABLE_BINDING == argDBinding.getKind()) {
				VariableBinding variableBinding = (VariableBinding) argDBinding;
				if(variableBinding.isReadOnly()) {
					if(!parmBinding.isInput()) {
						problemRequestor.acceptProblem(
							argExpr,
							IProblemRequestor.READONLY_FIELD_CANNOT_BE_PASSED_TO_OUT_PARM,
							new String[] {getCanonicalStringNoSubscripts(argExpr)});
						return false;
					}
				}
			}
		}
		
    	return true;
    }
    
    private boolean checkArgumentUsedCorrectlyWithNullable(Expression argExpr, FunctionParameterBinding parmBinding) {
    	if(parmBinding.isSQLNullable()) {
    		IDataBinding argDBinding = argExpr.resolveDataBinding();
    		
    		boolean argIsValid = false;
    		if(argDBinding != null) {
    			if(argDBinding == IBinding.NOT_FOUND_BINDING) {
    				argIsValid = true;
    			}
    			else {
    				ITypeBinding declaringPart = argDBinding.getDeclaringPart();
    				IAnnotationBinding isNullableABinding = argDBinding.getAnnotation(new String[] {"egl", "io", "sql"}, "IsSqlNullable"); 
    				if(isNullableABinding != null) {
    					argIsValid = isNullableABinding.getValue() == Boolean.YES;
    				}
    				else if(ITypeBinding.FIXED_RECORD_BINDING == declaringPart.getKind()) {
	    				if(argDBinding.getDeclaringPart().getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") != null) {
	    					argIsValid = true;
	    				}
    				}
    				else if(ITypeBinding.FORM_BINDING == declaringPart.getKind()) {
    					argIsValid = false;
    				}
    				else {
    					argIsValid = true;
    				}
    			}
    		}    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.NULLABLE_ARGUMENT_NOT_SQL_ITEM,
					new String[] {
    					argExpr.getCanonicalString(),
						functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    	}
    	return true;
    }
    
    private boolean checkArgumentUsedCorrectlyWithField(Expression argExpr, FunctionParameterBinding parmBinding) {
    	if(parmBinding.isField() && !functionBinding.isSystemFunction()) {
    		IDataBinding argDBinding = argExpr.resolveDataBinding();
    		
    		boolean argIsValid = false;
    		if(argDBinding != null) {
    			if(argDBinding == IBinding.NOT_FOUND_BINDING) {
    				argIsValid = true;
    			}
    			else if(IDataBinding.FUNCTION_PARAMETER_BINDING == argDBinding.getKind()) {
    				argIsValid = ((FunctionParameterBinding) argDBinding).isField();
    			}
    			else if(IDataBinding.FORM_FIELD == argDBinding.getKind()) {
    				argIsValid = true;
    			}
    		}
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FIELD_ARGUMENT_NOT_FORM_FIELD,
					new String[] {
    					argExpr.getCanonicalString(),
						functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    	}
    	return true;
    }
    
    private boolean checkArgNotConstantOrLiteral(Expression argExpr, final int problemKind) {
    	IDataBinding argDBinding = argExpr.resolveDataBinding();
		if(argDBinding != null && argDBinding != IBinding.NOT_FOUND_BINDING) {
			if(IDataBinding.LOCAL_VARIABLE_BINDING == argDBinding.getKind() ||
			   IDataBinding.CLASS_FIELD_BINDING == argDBinding.getKind()) {
				if(((VariableBinding) argDBinding).isConstant()) {
					problemRequestor.acceptProblem(
						argExpr,
						problemKind,
						new String[] {
							argExpr.getCanonicalString(),
							functionBinding.getName()
						});
					return false;
				}
			}
		}
		final boolean[] foundError = new boolean[] {false};
		argExpr.accept(new AbstractASTExpressionVisitor() {
			public void endVisitName(Name name) {}
			public void endVisit(ArrayAccess arrayAccess) {}
			public void endVisit(FieldAccess fieldAccess) {}
			public void endVisit(SubstringAccess substringAccess) {}
			public void endVisitExpression(Expression expression) {
				problemRequestor.acceptProblem(
					expression,
					problemKind,
					new String[] {
						expression.getCanonicalString(),
						functionBinding.getCaseSensitiveName()
					});
				foundError[0] = true;
			}
		});
		
		return !foundError[0];
    }
    
    private boolean isLooseType(ITypeBinding parmType) {
    	if(ITypeBinding.PRIMITIVE_TYPE_BINDING == parmType.getKind()) {
    		PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) parmType;
    		Primitive primitive = primTypeBinding.getPrimitive();
            
            if (primitive == Primitive.CHAR		|| primitive == Primitive.DBCHAR ||
				primitive == Primitive.MBCHAR	|| primitive == Primitive.STRING ||
				primitive == Primitive.HEX 		|| primitive == Primitive.UNICODE ||
				primitive == Primitive.NUM) {
            	return primTypeBinding.getLength() == 0;
            }
            if (primitive == Primitive.INTERVAL) {
            	return true;
            }
    	}
    	if(SystemFunctionParameterSpecialTypeBinding.VAGTEXT == parmType) {
    		return true;
        }
        return false;
    }
    
    private boolean isTypeCompatibleWithNullLiteral(ITypeBinding parmType) {
    	if(ITypeBinding.PRIMITIVE_TYPE_BINDING == parmType.getKind()) {
    		PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) parmType;
    		Primitive primitive = primTypeBinding.getPrimitive();
            
            if (primitive == Primitive.NUMBER) {
                return false;
            }
            if (primitive == Primitive.CHAR		|| primitive == Primitive.DBCHAR ||
				primitive == Primitive.MBCHAR	||
				primitive == Primitive.HEX 		|| primitive == Primitive.UNICODE ||
				primitive == Primitive.DECIMAL  || primitive == Primitive.NUM) {
            	return primTypeBinding.getLength() != 0;
            }
    	}
        return true;
    }

    
    private static Map specialSystemFunctionParameterCheckers = new HashMap();
    static {
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.ANYEGL, AnyEGLArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.ANYEGLORASJAVA, AnyEGLOrAsJavaArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.ARRAYORTABLE, ArrayOrTableArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.ATTRIBUTE, AttributeArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.CONSOLEFORM, ConsoleFormArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.OBJIDTYPE, ObjIdTypeArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.OBJIDTYPEOPT, ObjIdTypeOptArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.RECORD, RecordArgumentChecker.INSTANCE);    	
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.FLEXIBLERECORD, FlexibleRecordArgumentChecker.INSTANCE);    	
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.RECORDORDICTIONARY, RecordOrDictionaryArgumentChecker.INSTANCE);    	
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.SERVICEORINTERFACE, ServiceOrInterfaceArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.TEXTFIELD, TextFieldArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.VAGTEXT, VAGTextArgumentChecker.INSTANCE);
    	specialSystemFunctionParameterCheckers.put(SystemFunctionParameterSpecialTypeBinding.VAGTEXTORNUMERIC, VAGTextOrNumericChecker.INSTANCE);
    }
    
    private static interface ISpecialSystemFunctionParameterChecker {
    	boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum);    	
    }
    
    private static class AnyEGLArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new AnyEGLArgumentChecker();
    	private AnyEGLArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(argType.isDynamic()) {
    			argIsValid = true;
    		}
    		else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
    			Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
    			argIsValid = prim != Primitive.BLOB && prim != Primitive.CLOB;
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ANYEGL,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		return true;
		}
    }
    
    private static class AnyEGLOrAsJavaArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new AnyEGLOrAsJavaArgumentChecker();
    	private AnyEGLOrAsJavaArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(argType.isDynamic()) {
    			argIsValid = true;
    		}
    		else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
    			Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
    			argIsValid = prim != Primitive.BLOB && prim != Primitive.CLOB;
    		}
    		else if(ITypeBinding.FOREIGNLANGUAGETYPE_BINDING == argType.getKind()) {
    			argIsValid = ForeignLanguageTypeBinding.JAVA_KIND == ((ForeignLanguageTypeBinding) argType).getForeignLanguageKind() ||
    			             ForeignLanguageTypeBinding.NULL == argType ||
    			             ForeignLanguageTypeBinding.OBJIDJAVA == argType;
    		}
    		else {
    			argIsValid = ITypeBinding.FIXED_RECORD_BINDING == argType.getKind();
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ANYEGL,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		return true;
		}
    }
    
    private static class ArrayOrTableArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new ArrayOrTableArgumentChecker();
    	private ArrayOrTableArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
			return true;
		}
    }
    
    private static class AttributeArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new AttributeArgumentChecker();
    	private AttributeArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(ITypeBinding.FIXED_RECORD_BINDING == argType.getKind() ||
    		   ITypeBinding.FLEXIBLE_RECORD_BINDING == argType.getKind() ||
			   ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind() ||
			   ITypeBinding.ARRAY_TYPE_BINDING == argType.getKind() ||
			   argType.isPartBinding() && AbstractBinder.typeIs(((IPartBinding) argType).getSubType(), new String[] {"egl", "idl", "java"}, IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT)) {
    			argIsValid = true;
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ATTRIBUTE,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		return true;
		}
    }
    
    private static class ConsoleFormArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new ConsoleFormArgumentChecker();
    	private ConsoleFormArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
			return true;
		}
    }
    
    private static class ObjIdTypeArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new ObjIdTypeArgumentChecker();
    	private ObjIdTypeArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		if(argType != ForeignLanguageTypeBinding.OBJIDJAVA) {
	    		problemRequestor.acceptProblem(
	   				argExpr,
					IProblemRequestor.JAVA_CAST_OBJID_REQUIRED,
					new String[] { Integer.toString( argNum ), functionBinding.getCaseSensitiveName() });
    		}
    		return true;
		}
    }
    
    private static class ObjIdTypeOptArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new ObjIdTypeOptArgumentChecker();
    	private ObjIdTypeOptArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		if(argType != ForeignLanguageTypeBinding.OBJIDJAVA && ITypeBinding.PRIMITIVE_TYPE_BINDING != argType.getKind()) {
	    		problemRequestor.acceptProblem(
	   				argExpr,
					IProblemRequestor.JAVA_CAST_NO_CAST_OR_OBJID_REQUIRED,
					new String[] { Integer.toString( argNum ), functionBinding.getCaseSensitiveName() });
    		}
    		return true;
		}
    }
    
    private static class RecordArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new RecordArgumentChecker();
    	private RecordArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(isRecord(argType)) {
    			argIsValid = true;
    		}
    		else {
    			if(new AnnotationTypeBinding.IsStringLiteralChecker().isStringLiteral(argExpr)) {
    				argIsValid = true;
    			}
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_RECORD,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		
    		return true;
		}
    }

    private static class FlexibleRecordArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new FlexibleRecordArgumentChecker();
    	private FlexibleRecordArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(isFlexRecord(argType)) {
    			argIsValid = true;
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_RECORD,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		
    		return true;
		}
    }

   private static class RecordOrDictionaryArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new RecordOrDictionaryArgumentChecker();
    	private RecordOrDictionaryArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(isRecord(argType)) {
    			argIsValid = true;
    		}
    		else {
    			if(isDictionary(argType)) {
    				argIsValid = true;
    			}
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_RECORD_OR_DICTIONARY,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		
    		return true;
		}
    }

    private static class ServiceOrInterfaceArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new ServiceOrInterfaceArgumentChecker();
    	private ServiceOrInterfaceArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(ITypeBinding.SERVICE_BINDING == argType.getKind()) {
    			argIsValid = true;
    		}
    		else if(ITypeBinding.INTERFACE_BINDING == argType.getKind()) {
    			argIsValid = SystemPartManager.findType(argType.getName()) != argType;
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_SERVICEORINTERFACE,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		return true;
		}
    }
    
    private static class TextFieldArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new TextFieldArgumentChecker();
    	private TextFieldArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		IDataBinding argDBinding = argExpr.resolveDataBinding();
    		boolean argIsValid = false;
    		if(argDBinding != null) {
    			if(argDBinding == IBinding.NOT_FOUND_BINDING) {
    				argIsValid = true;
    			}
    			else if(IDataBinding.FORM_FIELD == argDBinding.getKind()){
    				argIsValid = ((FormFieldBinding) argDBinding).isTextFormField();
    			}
    			else if(IDataBinding.FUNCTION_PARAMETER_BINDING == argDBinding.getKind()) {
    				argIsValid = ((FunctionParameterBinding) argDBinding).isField();
    			}
    		}
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_TEXTFIELD,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
			return true;
		}
    }
    
    private static class VAGTextArgumentChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new VAGTextArgumentChecker();
    	private VAGTextArgumentChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
    			Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
    			if(prim == Primitive.CHAR ||
    			   prim == Primitive.MBCHAR ||
				   prim == Primitive.DBCHAR ||
				   prim == Primitive.DBCHARLIT ||
				   prim == Primitive.HEX ||
				   prim == Primitive.NUM ||
				   prim == Primitive.UNICODE ||
				   prim == Primitive.STRING && ((PrimitiveTypeBinding) argType).getLength() != 0) {
    				argIsValid = true;
    			}
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_VAGTEXT,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		
    		return true;
		}
    }
    
    private static class VAGTextOrNumericChecker implements ISpecialSystemFunctionParameterChecker {
    	static ISpecialSystemFunctionParameterChecker INSTANCE = new VAGTextOrNumericChecker();
    	private VAGTextOrNumericChecker() {}
    	
    	public boolean checkArgument(Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationAST, IFunctionBinding functionBinding, IProblemRequestor problemRequestor, int argNum) {
    		boolean argIsValid = false;
    		
    		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
    			Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
    			if(prim == Primitive.CHAR ||
    			   prim == Primitive.MBCHAR ||
				   prim == Primitive.DBCHAR ||
				   prim == Primitive.DBCHARLIT ||
				   prim == Primitive.STRING && ((PrimitiveTypeBinding) argType).getLength() != 0 ||
				   prim == Primitive.HEX ||
				   prim == Primitive.NUM ||
				   prim == Primitive.BIN ||
				   prim == Primitive.INT ||
				   prim == Primitive.SMALLINT ||
				   prim == Primitive.BIGINT ||
				   prim == Primitive.PACF ||
				   prim == Primitive.MONEY ||
				   prim == Primitive.DECIMAL) {
    				argIsValid = true;
    			}
    		}
    		
    		if(!argIsValid) {
    			problemRequestor.acceptProblem(
    				argExpr,
					IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_VAGTEXTORNUMERIC,
					new String[] {
    					argExpr.getCanonicalString(),
    					functionBinding.getDeclarer().getCaseSensitiveName() + "." + functionBinding.getCaseSensitiveName()
    				});
    			return false;
    		}
    		return true;
		}
    }
    
    private boolean checkArgForSpecialTypeParameter(Expression argExpr, ITypeBinding argType, ITypeBinding parmType, int argNum) {
    	return ((ISpecialSystemFunctionParameterChecker) specialSystemFunctionParameterCheckers.get(parmType)).checkArgument(argExpr, argType, fInvocationNode, functionBinding, problemRequestor, argNum);
    }
        
    private boolean checkArgForInOrOutParameter(Expression argExpr, ITypeBinding argType, FunctionParameterBinding funcParmBinding, ITypeBinding parmType, int argNum) {
    	if(isIORecord(parmType) && isRecord(argType)) {
    		return checkArgForIORecordParameter(argExpr, argType, funcParmBinding, parmType);
    	}
    	if(qualifiedByActiveForm(argExpr)) {
    		return true;
    	}
    	if(ITypeBinding.ARRAY_TYPE_BINDING == parmType.getKind()) {
    		return checkArgForInOrOutArrayParameter(argExpr, argType, funcParmBinding, parmType);
    	}
    	if(!TypeCompatibilityUtil.isMoveCompatible(parmType, argType, argExpr, compilerOptions) &&
    	   !argType.isDynamic() &&
    	   !TypeCompatibilityUtil.areCompatibleExceptions(argType, parmType, compilerOptions)) {
    		if(argType == SystemFunctionParameterSpecialTypeBinding.ANYEGLORASJAVA) {
    			problemRequestor.acceptProblem(
	    			argExpr,
					IProblemRequestor.JAVA_CAST_NO_CAST_ALLOWED,
					new String[] { Integer.toString( argNum ), functionBinding.getCaseSensitiveName() });    			
    		}
    		else {
	    		problemRequestor.acceptProblem(
	    			argExpr,
	    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
					new String[] {
	    				argExpr.getCanonicalString(),
						funcParmBinding.getCaseSensitiveName(),
						canonicalFunctionName,
						StatementValidator.getShortTypeString(argType),
						StatementValidator.getShortTypeString(parmType)
	    			});
    		}
    		return false;
    	}
    	return true;
    }
    
    private boolean checkArgForInParameter(Expression argExpr, ITypeBinding argType, FunctionParameterBinding funcParmBinding, ITypeBinding parmType, int argNum) {
    	IDataBinding argDBinding = argExpr.resolveDataBinding();
    	if(argDBinding != null) {
    		if(!new RValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr).validate()) {
    			return false;
    		}
    		validateNotPCB(argDBinding, argExpr);
    	}
    	return checkArgForInOrOutParameter(argExpr, argType, funcParmBinding, parmType, argNum);
    }
    
    private void validateNotPCB(IDataBinding argDBinding, Expression argExpr) {
		IAnnotationBinding aBinding = argDBinding.getAnnotation(new String[] { "egl", "io", "dli" }, "PCB");
		if (aBinding != null) {
			problemRequestor.acceptProblem(argExpr, IProblemRequestor.DLI_PCBRECORD_NOT_VALID_AS_IN_ARG, new String[] { argDBinding.getCaseSensitiveName() });
		}
    }
    
    private boolean checkArgForOutParameter(Expression argExpr, ITypeBinding argType, final FunctionParameterBinding funcParmBinding, ITypeBinding parmType, int argNum) {
    	IDataBinding argDBinding = argExpr.resolveDataBinding();
    	if(argDBinding != null) {
    		if(!new LValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr, new LValueValidator.DefaultLValueValidationRules() {
     			public boolean canAssignToFunctionParmConst() {
     				return funcParmBinding.isConst();
     			}
     			public boolean canAssignToConstantVariables() {
     				return funcParmBinding.isConst();
     			}
    		}).validate()) {
    			return false;
    		}
    	}
    	validateNotThis(argExpr);
    	
    	//Cannot pass a value type to an reference type OUT parm
   		if (!isRefCompatForOutParm(argType, parmType)) {
    		problemRequestor.acceptProblem(
	    			argExpr,
	    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
					new String[] {
	    				argExpr.getCanonicalString(),
						funcParmBinding.getCaseSensitiveName(),
						canonicalFunctionName,
						StatementValidator.getShortTypeString(argType, true),
						StatementValidator.getShortTypeString(parmType, true)
	    			});
    		return false;
   		}
    	return checkArgForInOrOutParameter(argExpr, argType, funcParmBinding, parmType, argNum);
    }
    
    private boolean isRefCompatForOutParm(ITypeBinding argType, ITypeBinding parmType) {
    	if (Binding.isValidBinding(argType) && Binding.isValidBinding(parmType)) {
    		return argType.isReference() == parmType.isReference();
    	}
    	return true;
    }
    
    private static boolean isIORecord(ITypeBinding tBinding) {
    	if(ITypeBinding.FIXED_RECORD_BINDING == tBinding.getKind() ||
    	   ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind()) {
    		return tBinding.getAnnotation(new String[] {"egl", "io", "file"}, "SerialRecord") != null ||
			       tBinding.getAnnotation(new String[] {"egl", "io", "file"}, "IndexedRecord") != null ||
				   tBinding.getAnnotation(new String[] {"egl", "io", "file"}, "RelativeRecord") != null ||
				   tBinding.getAnnotation(new String[] {"egl", "io", "mq"}, "MQRecord") != null ||
				   tBinding.getAnnotation(new String[] {"egl", "io", "file"}, "CSVRecord") != null ||
				   tBinding.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") != null;
			
    	}
    	return false;
    }
    
    private static boolean isRecord(ITypeBinding tBinding) {
    	return ITypeBinding.FIXED_RECORD_BINDING == tBinding.getKind() ||
    	       ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind();
    }

    private static boolean isFlexRecord(ITypeBinding tBinding) {
    	return ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind();
    }

    private static boolean isDictionary(ITypeBinding tBinding) {
    	return ITypeBinding.DICTIONARY_BINDING == tBinding.getKind();
    }
    
    private boolean checkArgForIORecordParameter(Expression argExpr, ITypeBinding argType, FunctionParameterBinding funcParmBinding, ITypeBinding parmType) {
    	if(argType != parmType) {
    		problemRequestor.acceptProblem(
    			argExpr,
    			IProblemRequestor.FUNCTION_ARG_NOT_COMPATIBLE_WITH_IO_RECORD_PARM,
				new String[] {
    				argExpr.getCanonicalString(),
					funcParmBinding.getCaseSensitiveName(),
					canonicalFunctionName,					
					StatementValidator.getShortTypeString(parmType)
    			});
    	}
    	return true;
    }
    
    private boolean checkArgForInOrOutArrayParameter(Expression argExpr, ITypeBinding argType, FunctionParameterBinding funcParmBinding, ITypeBinding parmType) {
    	if(argType.isDynamic() ||
    	   (!TypeCompatibilityUtil.isMoveCompatible(parmType, argType, argExpr, compilerOptions) &&
    	    !TypeCompatibilityUtil.areCompatibleExceptions(argType, parmType, compilerOptions))) {
    		problemRequestor.acceptProblem(
    			argExpr,
    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
				new String[] {
    				argExpr.getCanonicalString(),
					funcParmBinding.getCaseSensitiveName(),
					canonicalFunctionName,
					StatementValidator.getShortTypeString(argType),
					StatementValidator.getShortTypeString(parmType)
    			});
    		return false;
    	}
    	return true;
    }
    
    private boolean checkArgForInOutParameter(Expression argExpr, ITypeBinding argType, final FunctionParameterBinding funcParmBinding, ITypeBinding parmType, int argNum) {
    	IDataBinding argDBinding = argExpr.resolveDataBinding();
    	if(argDBinding != null) {
    		if(!new RValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr).validate()) {
    			return false;
    		}    		
    		if(!new LValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr, new LValueValidator.DefaultLValueValidationRules() {
    			public boolean canAssignToFunctionReferences() {
    				return true;
    			}
     			public boolean canAssignToPCB() {
    				return true;
    			}
     			public boolean canAssignToConstantVariables() {
      				return true;
     			}
     			
     			public boolean canAssignToFunctionParmConst() {
     				return funcParmBinding.isConst();
     			}
    		}).validate()) {
    			return false;
    		}
    	}
    	
    	if(isIORecord(parmType) && isRecord(argType)) {
    		return checkArgForIORecordParameter(argExpr, argType, funcParmBinding, parmType);
    	}
    	if(qualifiedByActiveForm(argExpr)) {
    		return true;
    	}
    	
    	boolean argCompatible = TypeCompatibilityUtil.isReferenceCompatible(argType, parmType, compilerOptions) ||
    		                TypeCompatibilityUtil.areCompatibleExceptions(parmType, argType, compilerOptions);
    	
    	if(!argCompatible) {
    		if(argType == SystemFunctionParameterSpecialTypeBinding.ANYEGLORASJAVA) {
    			problemRequestor.acceptProblem(
	    			argExpr,
					IProblemRequestor.JAVA_CAST_NO_CAST_ALLOWED,
					new String[] { Integer.toString( argNum ), functionBinding.getCaseSensitiveName() });    			
    		}
    		else {
	    		problemRequestor.acceptProblem(
	    			argExpr,
	    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
					new String[] {
	    				argExpr.getCanonicalString(),
						funcParmBinding.getCaseSensitiveName(),
						canonicalFunctionName,
						StatementValidator.getShortTypeString(argType, true),
						StatementValidator.getShortTypeString(parmType, true)
	    			});
    		}
    		return false;
   		}
    	
    	validateNotThis(argExpr);
    	
    	return true;
    }
    
    private void validateNotThis(Expression expr) {
    	DefaultASTVisitor visitor = new DefaultASTVisitor() {
    		public boolean visit(org.eclipse.edt.compiler.core.ast.ThisExpression thisExpression) {
		    		problemRequestor.acceptProblem(
			    			thisExpression,
			    			IProblemRequestor.FUNCTION_ARG_CANNOT_BE_THIS,
							new String[] {});
		    		return false;	
	    		}
    	};
    	expr.accept(visitor);
    }
    
    private boolean isLiteral(Expression expr) {
    	final boolean[] result = new boolean[] {false};
    	expr.accept(new DefaultASTVisitor() {
    		public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
    		public boolean visit(IntegerLiteral integerLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(DecimalLiteral decimalLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(FloatLiteral floatLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(BooleanLiteral booleanLiteral) {
				result[0] = true;
				return false;	
			}
			public boolean visit(StringLiteral stringLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(CharLiteral stringLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(DBCharLiteral stringLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(MBCharLiteral stringLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(HexLiteral stringLiteral) {
				result[0] = true;
				return false;
			}
			public boolean visit(BinaryExpression binaryExpression) {
				result[0] = new AnnotationTypeBinding.IsStringLiteralChecker().isStringLiteral(binaryExpression);
				return false;
			}
			public boolean visit(UnaryExpression unaryExpression) {
				return UnaryExpression.Operator.PLUS == unaryExpression.getOperator() ||
				       UnaryExpression.Operator.MINUS == unaryExpression.getOperator();
			}
    	});
    	return result[0];
    }
    
    private boolean qualifiedByActiveForm(Expression expr) {
    	final boolean[] result = new boolean[] {false};
    	expr.accept(new AbstractASTExpressionVisitor() {
    		
    		public boolean visitName(Name name) {    			
				if(AbstractBinder.dataBindingIs(name.resolveDataBinding(), new String[] {"egl", "ui", "console"}, "ConsoleLib", "ACTIVEFORM")) {
					result[0] = true;
				}
				return true;
			}
    		
			public boolean visitExpression(Expression expression) {
				return true;
			}
    	});
    	return result[0];
    }
    
    private boolean checkNoDynamicTypesPassedToMathLibFunctions(Expression argExpr, ITypeBinding argType, FunctionParameterBinding funcParmBinding, ITypeBinding parmType) {
    	if(argType.isDynamic() && AbstractBinder.typeIs(functionBinding.getDeclarer(), new String[] {"egl", "core"}, "MathLib")) {
    		if(!qualifiedByActiveForm(argExpr)) {
	    		if(funcParmBinding.isInput() || funcParmBinding.isOutput()) {
		    		problemRequestor.acceptProblem(
		    			argExpr,
		    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
						new String[] {
		    				argExpr.getCanonicalString(),
							funcParmBinding.getCaseSensitiveName(),
							canonicalFunctionName,
							StatementValidator.getShortTypeString(argType),
							StatementValidator.getShortTypeString(parmType)
		    			});
	    		}
	    		else {
	    			problemRequestor.acceptProblem(
		    			argExpr,
		    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
						new String[] {
		    				argExpr.getCanonicalString(),
							funcParmBinding.getCaseSensitiveName(),
							canonicalFunctionName,
							StatementValidator.getShortTypeString(argType),
							StatementValidator.getShortTypeString(parmType)
		    			});
	    		}
	    		return false;
    		}
    	}
    	return true;
    }
	
	private boolean checkCorrectNumberOfArgumentsForSystemFunction() {
		int[] validNumbersOfArguments = functionBinding.getValidNumbersOfArguments();
		if (validNumbersOfArguments.length == 1) {
            // this will cover anything with an exact number of args, including 0 args
            if (numArgs != validNumbersOfArguments[0]) {
            	problemRequestor.acceptProblem(
            		fInvocationNode.getTarget(),
					IProblemRequestor.ROUTINE_MUST_HAVE_X_ARGS,
					new String[] {
            			canonicalFunctionName,
						Integer.toString(validNumbersOfArguments[0])
					}
            	);
            	return false;
            }
        } else if (validNumbersOfArguments.length == 2 && validNumbersOfArguments[0] < 0) {
            if (validNumbersOfArguments[0] == IFunctionBinding.ARG_COUNT_N_OR_MORE) {
                // a case of needing "at least x arguments"
                int atLeastNumberOfArgs = validNumbersOfArguments[1];
                if (numArgs < atLeastNumberOfArgs) {
                	problemRequestor.acceptProblem(
                		fInvocationNode.getTarget(),
						IProblemRequestor.ROUTINE_MUST_HAVE_ATLEAST_X_ARGS,
						new String[] {
                			canonicalFunctionName,
							Integer.toString(atLeastNumberOfArgs)
                		}
                	);
                	return false;
                }
            }
        } else {
        	//this will cover anything with multiple exact numbers of arguments
            StringBuffer argCountBuffer = new StringBuffer();
            boolean numargsOK = false;
            int loopcntr = 0;
            for (loopcntr = 0; loopcntr < validNumbersOfArguments.length; loopcntr++) {
                if (numArgs == validNumbersOfArguments[loopcntr]) {
                    numargsOK = true;
                    break;
                } else {
                    if (loopcntr != validNumbersOfArguments.length-1) {
                    	// add to first insert list
                        if (argCountBuffer.length() > 0) {
                            argCountBuffer.append(", ");
                        }
                        argCountBuffer.append(Integer.toString(validNumbersOfArguments[loopcntr]));
                    }
                }
            }
            if (!numargsOK) {
                problemRequestor.acceptProblem(
                	fInvocationNode.getTarget(),
					IProblemRequestor.ROUTINE_MUST_HAVE_X_OR_Y_ARGS,
					new String[] {
                		canonicalFunctionName,
						argCountBuffer.toString(),
						Integer.toString(validNumbersOfArguments[loopcntr - 1])
					}
                );
                return false;
            }
        }
		return true;
	}
	
	private String getCanonicalStringNoSubscripts(Expression expr) {
		final String[] result = new String[] {null};
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitExpression(Expression expression) {
				result[0] = expression.getCanonicalString();
				return false;
			}
			
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			
			public boolean visit(ArrayAccess arrayAccess) {
				arrayAccess.getArray().accept(this);
				return false;
			}
			
			public boolean visit(SubstringAccess substringAccess) {
				substringAccess.getPrimary().accept(this);
				return false;
			}
		});
		return result[0];
	}
	
	private static class ArgInfo {
		private int argNum;
		private FunctionIdentifier fBinding;
		
		ArgInfo(FunctionIdentifier fBinding, int argNum) {
			this.fBinding = fBinding;
			this.argNum = argNum;
		}
		
		public boolean equals(Object obj) {
			if(obj instanceof ArgInfo) {
				ArgInfo otherArgInfo = (ArgInfo) obj;
				return fBinding.equals(otherArgInfo.fBinding) &&
				       argNum == otherArgInfo.argNum;
			}
			return false;
		}
		
		public int hashCode() {
			int result = 17;
			result = 37*result + fBinding.hashCode();
			result = 37*result + argNum;
			return result;
		}
	}
	
	private static interface IArgumentChecker {
		boolean performsCompleteCheck();
		void checkArg(int argNum, Expression arg, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding);
	}
	
	private static abstract class CompleteCheckArgChecker implements IArgumentChecker {
		public boolean performsCompleteCheck() {
			return true;
		}
	}
	
	private static abstract class IncompleteCheckArgChecker implements IArgumentChecker {
		public boolean performsCompleteCheck() {
			return false;
		}
	}
	
	private static class ArrayElementArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, final IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, final IProblemRequestor problemRequestor, final ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			ITypeBinding qualifierType = ((ArrayTypeBinding) getQualifier(fInvocationNode.getTarget()).resolveTypeBinding()).getElementType();
			if(!isCompatible(qualifierType, argExpr, argType, compilerOptions)) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.ARRAY_ELEMENT_ARGUMENT_INCORRECT_TYPE,
					new String[] {
						argExpr.getCanonicalString(),
						fInvocationNode.getTarget().getCanonicalString()
					});
			}
		}
		
		private boolean isCompatible(ITypeBinding arrayQualifierType, Expression argExpr, ITypeBinding argumentType, ICompilerOptions compilerOptions) {
			if(ITypeBinding.FIXED_RECORD_BINDING == arrayQualifierType.getKind()) {
				return ITypeBinding.FIXED_RECORD_BINDING == argumentType.getKind();
			}
			return TypeCompatibilityUtil.isMoveCompatible(arrayQualifierType, argumentType, argExpr, compilerOptions);
		}
	}
	
	private static class AppendAllArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, final IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			boolean isValid = false;
			if(ITypeBinding.ARRAY_TYPE_BINDING == argType.getKind()) {
				ITypeBinding anyAry = ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY));
				ITypeBinding qualifierType = getQualifier(fInvocationNode.getTarget()).resolveTypeBinding(); 
				if(qualifierType == anyAry || argType == anyAry) {
					isValid = true;
				}
				else {
					isValid = TypeCompatibilityUtil.isMoveCompatible(					
						qualifierType,
						argType,
						argExpr,
						compilerOptions);
				}
			}
			
			if(!isValid) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.INVALID_APPENDALL_ARG,
					new String[] {
						getQualifier(fInvocationNode.getTarget()).getCanonicalString()
					});
			}
		}
	}
	
	private static class StartTransactionArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			if(ITypeBinding.FIXED_RECORD_BINDING != argType.getKind()) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.INVALID_FIRST_ARG_FOR_STARTTRANS);
			}
		}
	}
	
	private static class StringNotAllowedArgumentChecker extends IncompleteCheckArgChecker {
		private boolean stringLiteralIsAllowed;

		public StringNotAllowedArgumentChecker(boolean stringLiteralIsAllowed) {
			this.stringLiteralIsAllowed = stringLiteralIsAllowed;
		}
		
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind() && Primitive.STRING == ((PrimitiveTypeBinding) argType).getPrimitive()) {
				if(stringLiteralIsAllowed) {
					IsStringLiteralChecker checker = new IsStringLiteralChecker();
					if(checker.isStringLiteral(argExpr)) {
						return;
					}
				}
				
				FunctionParameterBinding funcParmBinding = (FunctionParameterBinding) functionBinding.getParameters().get(argNum-1);
				ITypeBinding parmType = funcParmBinding.getType();
				String canonicalFunctionName = functionBinding.getCaseSensitiveName();
				
				if(funcParmBinding.isInput() || funcParmBinding.isOutput()) {
					problemRequestor.acceptProblem(
		    			argExpr,
		    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
						new String[] {
		    				argExpr.getCanonicalString(),
							funcParmBinding.getCaseSensitiveName(),
							canonicalFunctionName,
							StatementValidator.getShortTypeString(argType),
							StatementValidator.getShortTypeString(parmType)
		    			});
				}
				else if(funcParmBinding.isInputOutput()){
					problemRequestor.acceptProblem(
		    			argExpr,
		    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
						new String[] {
		    				argExpr.getCanonicalString(),
							funcParmBinding.getCaseSensitiveName(),
							canonicalFunctionName,
							StatementValidator.getShortTypeString(argType, true),
							StatementValidator.getShortTypeString(parmType, true)
		    			});
				}
			}
		}
	}

	private static class NullNotAllowedArgumentChecker extends IncompleteCheckArgChecker {

		public NullNotAllowedArgumentChecker() {
			super();
		}
		
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			
			if (argExpr instanceof NullLiteral) {
				problemRequestor.acceptProblem(
		    			argExpr,
		    			IProblemRequestor.FUNCTION_ARG_CANNOT_BE_NULL,
						new String[] {
		    				Integer.toString(argNum),
							functionBinding.getCaseSensitiveName()							
		    			});
			}
		}
	}

	private static class VGTDLIArgumentChecker extends IncompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind() &&
			   ((PrimitiveTypeBinding) argType).getDecimals() != 0) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.ARG_MUST_HAVE_NO_DECIMALS,
					new String[] {
						Integer.toString(argNum),
						"VGTDLI"
					});
			}
		}
	}
	
	private static class EGLTDLIArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			if(functionContainerBinding != null && functionContainerBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLI") != null) {
				IDataBinding argDBinding = argExpr.resolveDataBinding();
				boolean argIsValid = false;
				if(argDBinding != null) {
					if(argDBinding == IBinding.NOT_FOUND_BINDING) {
						argIsValid = true;
					}
					else if(getPCBs(functionContainerBinding).contains(argDBinding)) {
						argIsValid = true;
					}
					else if(getProgramParameterBindings(functionContainerBinding).contains(argDBinding)) {
						argIsValid = true;
					}
				}
				if(!argIsValid) {
					problemRequestor.acceptProblem(
						argExpr,
						IProblemRequestor.DLI_ITEM_MUST_RESOLVE_TO_PCB_IN_PROGRAM_PSB_OR_PARM_LIST,
						new String[] {
							argExpr.getCanonicalString()
						}
					);
				}
			}
		}
		
		private static List getPCBs(ITypeBinding type) {
			List result = new ArrayList();
			IAnnotationBinding dliABinding = type.getAnnotation(new String[] {"egl", "io", "dli"}, "DLI");
			if(dliABinding != null) {
				IAnnotationBinding psbABinding = (IAnnotationBinding) dliABinding.findData(InternUtil.intern(IEGLConstants.PROPERTY_PSB));
				if(psbABinding != IBinding.NOT_FOUND_BINDING) {
					IDataBinding psbRec = (IDataBinding) psbABinding.getValue();
					if(psbRec != IBinding.NOT_FOUND_BINDING) {
						ITypeBinding psbTBinding = psbRec.getType();
						if(ITypeBinding.FLEXIBLE_RECORD_BINDING == psbTBinding.getKind()) {
							result.addAll(((FlexibleRecordBinding) psbTBinding).getDeclaredFields());
						}
					}
				}
			}
			return result;
		}
		
		private static List getProgramParameterBindings(ITypeBinding type) {
			List result = new ArrayList();
			if(ITypeBinding.PROGRAM_BINDING == type.getKind()) {
				result.addAll(((ProgramBinding) type).getParameters());
			}
			return result;
		}
	}
	
	private static class SetErrorArgumentChecker extends CompleteCheckArgChecker {

		public void checkArg(int argNum, Expression arg, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			//Add validation for "itemInError" arg of setError() function here
		}
	}
	
	private static class MaximumSizeArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			if(ITypeBinding.ARRAY_TYPE_BINDING != argType.getKind()) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.MAXIMUMSIZE_ARGUMENT_INCORRECT,
					new String[] {argExpr.getCanonicalString()});
			}
		}
	}
	
	private static boolean isValidForConvertBidiTarget(final Expression argExpr, final ITypeBinding argType) {
		final boolean[] argIsValid = new boolean[] {false};
		
		argExpr.accept(new AbstractASTExpressionVisitor() {
			
			public void endVisitName(Name name) {
				checkArg();
			}
			
			public void endVisit(FieldAccess fieldAccess) {
				checkArg();
			}
			
			public void endVisit(ArrayAccess arrayAccess) {
				checkArg();
			}
						
			private void checkArg() {

				argIsValid[0] = ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()
						&& (((PrimitiveTypeBinding) argType).getPrimitive() == Primitive.CHAR
								|| ((PrimitiveTypeBinding) argType).getPrimitive() == Primitive.UNICODE || ((PrimitiveTypeBinding) argType)
								.getPrimitive() == Primitive.STRING);
			}
		});
		
		return argIsValid[0];
	}

	private static boolean isValidForConvertBidiConvTable(final Expression argExpr) {
		final boolean[] argIsValid = new boolean[] {false};
		
		argExpr.accept(new AbstractASTExpressionVisitor() {
			
			public void endVisit(StringLiteral lit) {
				argIsValid[0] = true;
			}
			
		});
		
		return argIsValid[0];
	}

	
	private static boolean isValidForConvert(final Expression argExpr, final ITypeBinding argType) {
		final boolean[] argIsValid = new boolean[] {false};
		
		argExpr.accept(new AbstractASTExpressionVisitor() {
			
			public void endVisitName(Name name) {
				checkArg();
			}
			
			public void endVisit(FieldAccess fieldAccess) {
				checkArg();
			}
			
			public void endVisit(ArrayAccess arrayAccess) {
				checkArg();
			}
			
			public void endVisit(SubstringAccess substringAccess) {
				checkArg();
			}
			
			private void checkArg() {										
				if(ITypeBinding.ARRAY_TYPE_BINDING == argType.getKind()) {
					argIsValid[0] = isValidForConvert(argExpr, ((ArrayTypeBinding) argType).getElementType());
				}
				else if(argType.isDynamic()) {
					argIsValid[0] = false;
				}
				else if(ITypeBinding.MULTIPLY_OCCURING_ITEM == argType.getKind()) {
					argIsValid[0] = true;
				}
				else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
					Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
					argIsValid[0] = prim != Primitive.BLOB && prim != Primitive.CLOB;
				}
				else if(ITypeBinding.FIXED_RECORD_BINDING == argType.getKind() ||
						ITypeBinding.FLEXIBLE_RECORD_BINDING == argType.getKind()) {
					argIsValid[0] = true;
				}
			}
		});
		
		return argIsValid[0];
	}
	
	private static boolean isValidForSizeInBytes(final Expression argExpr, final ITypeBinding argType) {
		final boolean[] argIsValid = new boolean[] {false};
		
		argExpr.accept(new AbstractASTExpressionVisitor() {
			
			public void endVisitName(Name name) {
				checkArg();
			}
			
			public void endVisit(FieldAccess fieldAccess) {
				checkArg();
			}
			
			public void endVisit(ArrayAccess arrayAccess) {
				checkArg();
			}
			
			public void endVisit(SubstringAccess substringAccess) {
				checkArg();
			}
			
			private void checkArg() {										
				if(ITypeBinding.ARRAY_TYPE_BINDING == argType.getKind()) {
					argIsValid[0] = isValidForSizeInBytes(argExpr, ((ArrayTypeBinding) argType).getElementType());
				}
				else if(argType.isDynamic()) {
					argIsValid[0] = false;
				}
				else if(ITypeBinding.MULTIPLY_OCCURING_ITEM == argType.getKind()) {
					argIsValid[0] = true;
				}
				else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
					Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
					argIsValid[0] = prim != Primitive.STRING &&
					             prim != Primitive.BLOB &&
					             prim != Primitive.CLOB;
				}
				else if(ITypeBinding.FIXED_RECORD_BINDING == argType.getKind() || ITypeBinding.FORM_BINDING == argType.getKind()) {
					argIsValid[0] = true;
				}
			}
		});
		
		return argIsValid[0];
	}
	
	private static class ConvertArgumentChecker extends IncompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			switch(argNum) {			
				case 1:
					if(argType != null) {
						if(!isValidForConvert(argExpr, argType)) {
							problemRequestor.acceptProblem(
								argExpr,
								IProblemRequestor.CONVERT_TARGET_INVALID,
								new String[] {
									argExpr.getCanonicalString()
								});
						}
					}
			}
		}
	}

	private static class ConvertBidiArgumentChecker extends IncompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode,
				IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions,
				IFunctionBinding functionBinding) {
			switch (argNum) {
			case 1:
				if (argType != null) {
					if (!isValidForConvertBidiTarget(argExpr, argType)) {
						problemRequestor.acceptProblem(argExpr, IProblemRequestor.CONVERTBIDI_TARGET_INVALID, new String[] { argExpr
								.getCanonicalString() });
					}
				}
				return;
			case 3:
				if (!isValidForConvertBidiConvTable(argExpr)) {
					problemRequestor.acceptProblem(argExpr, IProblemRequestor.CONVERTBIDI_CONVTABLE_INVALID, new String[] { argExpr
							.getCanonicalString() });
				}
				return;
			}
			
		}
	}
	
	private static class BytesArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			switch(argNum) {			
				case 1:
					if(argType != null) {
						if(!isValidForSizeInBytes(argExpr, argType)) {
							problemRequestor.acceptProblem(
								argExpr,
								IProblemRequestor.SIZEINBYTES_ARGUMENT_INVALID,
								new String[] {
									argExpr.getCanonicalString()
								});
						}
					}
			}
		}
	}
	
	private static class SizeArgumentChecker extends CompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			switch(argNum) {			
				case 1:
					if(argType != null) {
						boolean argIsValid = false;
						
						if(ITypeBinding.ARRAY_TYPE_BINDING == argType.getKind() ||
						   ITypeBinding.DATATABLE_BINDING == argType.getKind()) {
							argIsValid = true;
						}
						else {
							IDataBinding argDBinding = argExpr.resolveDataBinding();
							if(argDBinding != null) {
								switch(argDBinding.getKind()) {
									case IDataBinding.STRUCTURE_ITEM_BINDING:
										argIsValid = ((StructureItemBinding) argDBinding).isMultiplyOccuring();
										break;
									case IDataBinding.FORM_FIELD:
										argIsValid = ((FormFieldBinding) argDBinding).isMultiplyOccuring();
										break;
								}
							}
						}
						
						if(!argIsValid) {
							problemRequestor.acceptProblem(
								argExpr,
								IProblemRequestor.SIZEOF_ARGUMENT_INVALID,
								new String[] {argExpr.getCanonicalString()});
						}
					}
			}
		}
	}
	
	private static class PurgeArgumentChecker extends IncompleteCheckArgChecker {
		public void checkArg(int argNum, Expression argExpr, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			final StringBuffer argStr = new StringBuffer();
								
			argExpr.accept(new AbstractASTExpressionVisitor() {
				public boolean visit(ParenthesizedExpression parenthesizedExpression) {
					return true;
				}
				
				public boolean visit(BinaryExpression binaryExpression) {
					return binaryExpression.getOperator() == BinaryExpression.Operator.PLUS;
				}
				
				public boolean visit(StringLiteral stringLiteral) {
					argStr.append(stringLiteral.getValue());
					return false;
				}
				
				public boolean visit(IntegerLiteral integerLiteral) {
					argStr.append("xxxxxxxxx");
					return false;
				}
			});
			
			if(argStr.length() > 8) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.INVALID_ARG_FOR_PURGE);							
			}
		}
	}
	
	private static class UnicodeConversionFunctionArgumentChecker extends IncompleteCheckArgChecker {
		
		private boolean isSignedUnicode;

		public UnicodeConversionFunctionArgumentChecker(boolean isSignedUnicode) {
			this.isSignedUnicode = isSignedUnicode;
		}
		
		public void checkArg(int argNum, Expression arg, ITypeBinding argType, IInvocationNode fInvocationNode, IPartBinding functionContainerBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IFunctionBinding functionBinding) {
			if(fInvocationNode.getArguments().size() == 2) {
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
					PrimitiveTypeBinding primTBinding = (PrimitiveTypeBinding) argType;
					if(Primitive.UNICODE == primTBinding.getPrimitive()) {
						Expression otherArgExpr = (Expression) fInvocationNode.getArguments().get((argNum==1?1:0));
						ITypeBinding otherArgType = otherArgExpr.resolveTypeBinding();
						if(otherArgType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == otherArgType.getKind()) {
							PrimitiveTypeBinding otherPrimTBinding = (PrimitiveTypeBinding) otherArgType;
							Primitive otherPrim = otherPrimTBinding.getPrimitive();
							int requiredUnicodeLength = -1;							
							int length = otherPrimTBinding.getLength();
							
							switch(otherPrim.getType()) {
							
							case Primitive.SMALLINT_PRIMITIVE:
								requiredUnicodeLength = 6;
								break;
								
							case Primitive.INT_PRIMITIVE:
								requiredUnicodeLength = 11;
								break;
								
							case Primitive.BIGINT_PRIMITIVE:
								requiredUnicodeLength = 20;
								break;
								
							case Primitive.BIN_PRIMITIVE:
								switch(length) {
								case 4:
									requiredUnicodeLength = 6;
									break;
									
								case 9:
									requiredUnicodeLength = 11;
									break;
									
								case 18:
									requiredUnicodeLength = 20;
									break;
								}
								break;
								
							case Primitive.NUM_PRIMITIVE:
							case Primitive.DECIMAL_PRIMITIVE:
								int evenLength = length;
								if(evenLength % 2 == 1) {
									evenLength -= 1;
								}
								requiredUnicodeLength = evenLength + 2;
								
								if(length == 32) {
									requiredUnicodeLength -= 1;
								}
								
								break;
							}
							
							if(requiredUnicodeLength != -1) {
								if(!isSignedUnicode) {
									requiredUnicodeLength -= 1;
								}
								
								if(primTBinding.getLength() != requiredUnicodeLength) {
									problemRequestor.acceptProblem(
										arg,
										IProblemRequestor.INCORRECT_UNICODE_LENGTH_IN_UNICODE_CONVERSION_FUNCTION,
										new String[] {
											otherArgExpr.getCanonicalString(),
											otherPrimTBinding.getName(),
											arg.getCanonicalString(),
											Integer.toString(requiredUnicodeLength),
											Integer.toString(primTBinding.getLength())
										});
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static Expression getQualifier(Expression target) {
		final Expression[] result = new Expression[1];
		target.accept(new DefaultASTVisitor() {
			public boolean visit(QualifiedName qualifiedName) {
				result[0] = qualifiedName.getQualifier();
				return false;				
			}
			
			public boolean visit(FieldAccess fieldAccess) {
				result[0] = fieldAccess.getPrimary();
				return false;
			}
		});
		return result[0];
	}
}
