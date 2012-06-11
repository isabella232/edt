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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.BytesLiteral;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.ContinueStatement;
import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FreeSQLStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.GotoStatement;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullableType;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrintStatement;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.ThrowStatement;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author cduval
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class StatementValidator implements IOStatementValidatorConstants{

	public static interface ITargetsContainerChecker {
		boolean isLibraryDataAllowed(IDataBinding dataBinding);
	}
	
	public static class DefaultTargetsContainerChecker implements ITargetsContainerChecker {
		public boolean isLibraryDataAllowed(IDataBinding dataBinding) {
			return false;
		}
	}

	
	
	public static ITypeBinding getBaseType( ITypeBinding tb ) {
		while( tb.isReference() ) {
			tb = tb.getBaseType();
		}
		return tb;
	}
	
	public static String getTypeString(ITypeBinding binding){
		StringBuffer result = new StringBuffer();
		switch(binding.getKind()) {
			case ITypeBinding.ARRAYDICTIONARY_BINDING:
				result.append(IEGLConstants.MIXED_ARRAYDICTIONARY_STRING);
				break;
			
			case ITypeBinding.DICTIONARY_BINDING:
				result.append(IEGLConstants.MIXED_DICTIONARY_STRING);
				break;
			
			case ITypeBinding.ARRAY_TYPE_BINDING:
				result.append(getShortTypeString(((ArrayTypeBinding) binding).getElementType(), true) + "[]");
				break;
				
			case ITypeBinding.ANNOTATION_BINDING:
				result.append("@");
				result.append(binding.getCaseSensitiveName());
				break;
				
			default:
				result.append(binding.getCaseSensitiveName());			
		}
		
		if(binding.isNullable()) {
			result.append("?");
		}
		
		return result.toString();
	}
	
	public static String getShortTypeString(ITypeBinding binding){
		return getShortTypeString(binding, false);
	}
	
	public static String getShortTypeString(ITypeBinding binding, boolean includeLengthForPrimitives){
		StringBuffer result = new StringBuffer();
		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == binding.getKind()) {
			Primitive prim = ((PrimitiveTypeBinding) binding).getPrimitive();
			if(includeLengthForPrimitives || Primitive.MONTHSPAN_INTERVAL == prim || Primitive.SECONDSPAN_INTERVAL == prim) {
				result = new StringBuffer(getTypeString(binding));
			}
			else {
				result = new StringBuffer(((PrimitiveTypeBinding) binding).getPrimitive().getName());
				if(binding.isNullable()) {
					result.append("?");
				}
			}
		}
		else {
			result = new StringBuffer(getTypeString(binding));
		}
		
		return result.toString();
	}
	
	public static boolean isMultiplyOccuring(IDataBinding dataBinding){
		if (dataBinding == null){
			return false;
		}
		//check for multiple occurs
		if (dataBinding.getKind() == IDataBinding.FORM_FIELD){
			return ((FormFieldBinding)dataBinding).isMultiplyOccuring();
		}
		
		if (dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
			return ((StructureItemBinding)dataBinding).isMultiplyOccuring();
		}
				
		return false;
	}
	
	public static boolean isArray(IDataBinding dataBinding){
		if (dataBinding == null)
			return false;
		
		//check if type binding is array
		if (dataBinding.getType() != null && dataBinding.getType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
			return true;
		}
		
		return false; 
	}

	public static boolean isArrayOrMultiplyOccuring(IDataBinding dataBinding){
		return isArray(dataBinding) || 	isMultiplyOccuring(dataBinding); 
	}
	
	public static String getParmListString( ITypeBinding[] types ) {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < types.length; i++ ) {
			if( types[i] != null ) {
				sb.append( getTypeString( types[i] ) );
				
				if( i < types.length-1 ) {
					sb.append( ", " ); // $NON-NLS-1$
				}
			}
		}
		return sb.toString();
	}

	public static boolean isValidBinding (IBinding binding){
		return binding != null && binding != IBinding.NOT_FOUND_BINDING;
	}
	
	public static boolean isStringCompatible(ITypeBinding aTypeBinding) {
		if (isValidBinding(aTypeBinding) && aTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			  Primitive prim = ((PrimitiveTypeBinding)aTypeBinding).getPrimitive();
			  if (prim == Primitive.CHAR || prim == Primitive.DBCHAR || prim == Primitive.HEX || 
			  		prim == Primitive.MBCHAR || prim == Primitive.UNICODE || prim == Primitive.STRING ||
					prim == Primitive.ANY){
			  		return true;
			  }
		}
		
		return false;

    }
	
	public static boolean isIntegerCompatible(ITypeBinding aTypeBinding) {
        
		if (isValidBinding(aTypeBinding) && aTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
		  Primitive prim = ((PrimitiveTypeBinding)aTypeBinding).getPrimitive();

          if (prim == Primitive.BIGINT || prim == Primitive.INT || prim == Primitive.SMALLINT){
			return true;
          }else if (prim == Primitive.BIN
				|| prim == Primitive.DECIMAL
				|| prim == Primitive.NUM
				|| prim == Primitive.NUMC 
				|| prim == Primitive.PACF ){
          	return (((PrimitiveTypeBinding)aTypeBinding).getDecimals() == 0);
          }else if (prim == Primitive.ANY)
          	return true;
		}
	return false;
	}	
	
	public static boolean isRecordOrRecordArray(ITypeBinding typeBinding) {
		return (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING ) || 
		(typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING && isRecordOrRecordArray(typeBinding.getBaseType()));
	}
	
	protected static boolean isClauseEmpty(String clause){
		for (int i = 0; i < clause.length(); i++){
			if (!Character.isWhitespace(clause.charAt(i))){
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean checkArguments (ITypeBinding[] toArgs,ITypeBinding[] fromArgs, ICompilerOptions compilerOptions){
		boolean ok = true;
		
		if (toArgs.length != fromArgs.length){
			ok = false;
		}else{
			for (int i = 0; i < toArgs.length; i++){
				if(isValidBinding(toArgs[i]) && isValidBinding(fromArgs[i])) {
					boolean compatible = TypeCompatibilityUtil.isMoveCompatible(toArgs[i],fromArgs[i], null, compilerOptions);
					if (!compatible){
						ok = false;
						break;
					}
				}
			}
		}

		return ok;
	}
	
	public static String getQualifiedName (ITypeBinding typeBinding){
		StringBuffer typeName = new StringBuffer();
		String[] packageName = typeBinding.getPackageName();
		for (int i = 0; i < packageName.length;i++){
			typeName.append(packageName[i]);
			typeName.append(".");			
		}
		typeName.append(typeBinding.getName());
		
		return typeName.toString();
	}
	
	public static void validateNodesAsDataItemReferences(List nodes,IProblemRequestor pr){
		Iterator iter = nodes.iterator();
		while (iter.hasNext()){
			validateNodeAsDataItemReferences((Node)iter.next(),pr);
		}
	}
	
	public static boolean validateNodeAsDataItemReferences(Node node,final IProblemRequestor problemRequestor){
		final boolean[] isValid = new boolean[] {true};
		node.accept(new AbstractASTExpressionVisitor(){
			public boolean visitExpression(Expression expression) {
				ITypeBinding typeBinding = expression.resolveTypeBinding();
				IDataBinding dataBinding = expression.resolveDataBinding();
				if (StatementValidator.isValidBinding(typeBinding) && StatementValidator.isValidBinding(dataBinding)){
					if (typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING ||
						(dataBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING && ((ClassFieldBinding)dataBinding).isConstant()) || 
						(dataBinding.getKind() == IDataBinding.LOCAL_VARIABLE_BINDING && ((LocalVariableBinding)dataBinding).isConstant())){
						problemRequestor.acceptProblem(expression,
								IProblemRequestor.VARIABLE_NOT_FOUND_AS_ITEM,
								new String[] {expression.getCanonicalString()});
						isValid[0] = false;
					}
				}
			    return false;
			}
			
		    public boolean visit(SubstringAccess substringAccess) {
		    	problemRequestor.acceptProblem(substringAccess,
		    			IProblemRequestor.SUBSTRING_EXPRESSION_IN_BAD_LOCATION);
		    	isValid[0] = false;
		    	return false;
				
		    }
		    
			
			});
		return isValid[0];
	}
	
	public static void validateNodesInUsingClause(List nodes,IProblemRequestor pr){
		Iterator iter = nodes.iterator();
		while (iter.hasNext()){
			validateNodeInUsingClause((Expression)iter.next(),pr);
		}
	}
	
	protected static void validateNodeInUsingClause(Expression node, final IProblemRequestor problemRequestor) {
		if(validateNodeAsDataItemReferences(node, problemRequestor)) {
			final boolean[] exprValid = new boolean[] {false};
			node.accept(new DefaultASTVisitor() {
				public boolean visit(ParenthesizedExpression parenthesizedExpression) {
					return true;
				}
				
				public boolean visit(SimpleName simpleName) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(QualifiedName qualifiedName) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(ArrayAccess arrayAccess) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(SubstringAccess substringAccess) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(FieldAccess fieldAccess) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(StringLiteral stringLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(CharLiteral charLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(DBCharLiteral dBCharLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(FloatLiteral floatLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(HexLiteral hexLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(MBCharLiteral mBCharLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(IntegerLiteral integerLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(DecimalLiteral decimalLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(BooleanLiteral booleanLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(BytesLiteral bytesLiteral) {
					exprValid[0] = true;
					return false;
				}
				
				public boolean visit(BinaryExpression binaryExpression) {
					exprValid[0] = isStringLiteral(binaryExpression);
					return false;
				}
				
				private boolean isStringLiteral(Expression expr) {
					final boolean[] result = new boolean[] {false};
					expr.accept(new DefaultASTVisitor() {
						public boolean visit(StringLiteral stringLiteral) {
							result[0] = true;
							return false;
						}
						public boolean visit(BinaryExpression binaryExpression) {
							result[0] = BinaryExpression.Operator.PLUS == binaryExpression.getOperator() &&
							            isStringLiteral(binaryExpression.getFirstExpression()) &&
							            isStringLiteral(binaryExpression.getSecondExpression());
							return false;
						}
					});
					return result[0];
				}
			});
			
			if(!exprValid[0]) {
				problemRequestor.acceptProblem(
					node,
		    		IProblemRequestor.INVALID_LITERAL_IN_USING_CLAUSE,
		    		new String[] {node.getCanonicalString()});
			}
		}
	}
	
	private static HashSet CONTAINER_TYPEBINDING_KINDS = new HashSet(Arrays.asList(new Integer[] {
		new Integer(ITypeBinding.FIXED_RECORD_BINDING),
		new Integer(ITypeBinding.FLEXIBLE_RECORD_BINDING),
		new Integer(ITypeBinding.DATATABLE_BINDING),
		new Integer(ITypeBinding.FORM_BINDING),
		new Integer(ITypeBinding.EXTERNALTYPE_BINDING),
		new Integer(ITypeBinding.FIXED_RECORD_BINDING),
	}));
	
	public static void validateItemInIntoClause(Node node,final IProblemRequestor problemRequestor){
		node.accept(new DefaultASTVisitor() {
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			
			public boolean visit(ArrayAccess arrayAccess) {
				for(Iterator iter = arrayAccess.getIndices().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new AbstractASTVisitor() {
						public void endVisit(final FunctionInvocation functionInvocation) {
							problemRequestor.acceptProblem(
								functionInvocation,
								IProblemRequestor.FUNCTION_INVOCOATION_NOT_ALLOWED_IN_ARRAY_SUBSCRIPT_IN_INTO_CLAUSE
							);
						}
					});
				}
				return false;
			}
		});
		
		node.accept(new AbstractASTExpressionVisitor(){
			public boolean visitExpression(Expression expression) {
				ITypeBinding typeBinding = expression.resolveTypeBinding();
				IDataBinding dataBinding = expression.resolveDataBinding();
				if (StatementValidator.isValidBinding(typeBinding)){
					boolean isValid = true;
						
					if(isContainer(typeBinding)) {
						isValid = true;
					}
					else if(dataBinding == null) {
						isValid = false;
					}
					else if(dataBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING) {
						isValid = !((ClassFieldBinding)dataBinding).isConstant(); 
					}
					else if(dataBinding.getKind() == IDataBinding.LOCAL_VARIABLE_BINDING) {
						isValid = !((LocalVariableBinding)dataBinding).isConstant();
					}
					
					if(!isValid) {
						problemRequestor.acceptProblem(expression,
								IProblemRequestor.VARIABLE_NOT_FOUND_AS_ITEM_OR_CONTAINER,
								new String[] {expression.getCanonicalString()});
					}
					else {
						if(typeBinding.isDynamic()) {
							problemRequestor.acceptProblem(
								expression,
								IProblemRequestor.DYNAMIC_ACCESS_NOT_ALLOWED_IN_INTO_CLAUSE
							);
						}
					}
				}
			    return false;
			}
			
		    private boolean isContainer(ITypeBinding typeBinding) {
				return CONTAINER_TYPEBINDING_KINDS.contains(new Integer(typeBinding.getKind()));
			}

			public boolean visit(SubstringAccess substringAccess) {
		    	problemRequestor.acceptProblem(substringAccess,
		    			IProblemRequestor.SUBSTRING_EXPRESSION_IN_BAD_LOCATION);
		    	return false;
				
		    }
		    
			
			});
	}
	
	
	public static void validateIOTargetsContainer(List targets,final IProblemRequestor problemRequestor){
		validateIOTargetsContainer(targets, problemRequestor, new DefaultTargetsContainerChecker());
	}

	public static void validateIOTargetsContainer(List targets,final IProblemRequestor problemRequestor, final ITargetsContainerChecker checker){
		Iterator iter = targets.iterator();
		while (iter.hasNext()){
			Expression expr = (Expression)iter.next();
			expr.accept(new AbstractASTExpressionVisitor(){
			    public boolean visit(SimpleName simpleName) {
			    	IDataBinding dataBinding = (IDataBinding)simpleName.getAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING);
			    	if (StatementValidator.isValidBinding(dataBinding) && dataBinding.getKind() == IDataBinding.LIBRARY_BINDING ){
			    		IDataBinding db = simpleName.resolveDataBinding();
			    		if (!checker.isLibraryDataAllowed(db)) {
			    			addError(simpleName);
			    		}
			    	}
			    	return false;
			    }
			    
			    public boolean visit(QualifiedName qualifiedName) {
			    	Name name = qualifiedName;
			    	IBinding binding = null;
			    	
			    	while (name != null){
			    		binding = name.resolveBinding();
			    		if (!StatementValidator.isValidBinding(binding)){
			    			return false;
			    		}
			    		
			    		if ((binding.isDataBinding() && ((IDataBinding)binding).getKind() == IDataBinding.LIBRARY_BINDING)||
							(binding.isTypeBinding() && ((ITypeBinding)binding).getKind() == ITypeBinding.LIBRARY_BINDING)){
				    		IDataBinding db = qualifiedName.resolveDataBinding();
				    		if (!checker.isLibraryDataAllowed(db)) {
				    			addError(qualifiedName);
				    		}
			    			break;
			    		}
			    		
			    		if (name.isQualifiedName()){
			    			name = ((QualifiedName)name).getQualifier();
			    		}else{
			    			break;
			    		}
			    	}
			    	
			        return false;
			    }
			    
			    public boolean visit(FieldAccess fieldAccess) {
			        return true;
			    }
			    
			    public boolean visit(ArrayAccess arrayAccess) {
//			    	if (arrayAccess.getArray().resolveDataBinding() != dataBinding){
//			    		addError(arrayAccess);
//			    	}
			    		
			        return false;
			    }
			    
			    protected void addError(Expression node){
			    	problemRequestor.acceptProblem(node,
           				IProblemRequestor.USED_LIBRARY_RECORD_USED_FOR_IO);
			    }
			    
				public boolean visitExpression(Expression expression) {
				    return true;
				}
				});
		}
	}

	public static void validateDataDeclarationInitializer(Node dataDeclaration,final IProblemRequestor problemRequestor, final ICompilerOptions compilerOptions){
		dataDeclaration.accept(new AbstractASTVisitor(){
			
			Expression lhs = null;
			Expression rhs = null;
			ITypeBinding lhsBinding = null;
			ITypeBinding rhsBinding = null;
			IDataBinding lhsDataBinding = null;
			IDataBinding rhsDataBinding = null;
			
			public boolean visit (ClassDataDeclaration classDataDeclaration){
				if (classDataDeclaration.getNames().size() > 0){
					SimpleName name  = (SimpleName)classDataDeclaration.getNames().get(0);
					lhs = name;
					IBinding binding = name.resolveBinding();
					if (isValidBinding(binding)){
						if (binding.isTypeBinding()){
							lhsBinding = (ITypeBinding)binding;						
						}else if (binding.isDataBinding()){
							lhsDataBinding = (IDataBinding)binding;
							lhsBinding = lhsDataBinding.getType();
						}
					}
				}
				
				if (classDataDeclaration.hasInitializer()){
					rhs = classDataDeclaration.getInitializer();
					rhsBinding = rhs.resolveTypeBinding();
					rhsDataBinding = rhs.resolveDataBinding();
				}
				return false;
			}
			
			public boolean visit (FunctionDataDeclaration functionDataDeclaration){
				if (functionDataDeclaration.getNames().size() > 0){
					SimpleName name  = (SimpleName)functionDataDeclaration.getNames().get(0);
					lhs = name;
					IBinding binding = name.resolveBinding();
					if (isValidBinding(binding)){
						if (binding.isTypeBinding()){
							lhsBinding = (ITypeBinding)binding;						
						}else if (binding.isDataBinding()){
							lhsDataBinding = (IDataBinding)binding;
							lhsBinding = lhsDataBinding.getType();
						}
					}
				}
				
				if (functionDataDeclaration.hasInitializer()){
					rhs = functionDataDeclaration.getInitializer();
					rhsBinding = rhs.resolveTypeBinding();
					rhsDataBinding = rhs.resolveDataBinding();
				}
							
				return false;
			}
			
			public void endVisit (ClassDataDeclaration classDataDeclaration){
				if (classDataDeclaration.hasInitializer()){
					validate();
				}
			}
			
			public void endVisit (FunctionDataDeclaration functionDataDeclaration){
				validate();
			}
			
			protected void validate(){
				new AssignmentStatementValidator(problemRequestor, compilerOptions, null).validateAssignment(
					Assignment.Operator.ASSIGN,
					lhs,
					rhs,
					lhsBinding,
					rhsBinding,
					lhsDataBinding,
					rhsDataBinding,
					true,
					false,
					new LValueValidator.DefaultLValueValidationRules() {
						public boolean canAssignToConstantVariables() {
							return true;
						}
					});
			}
		});
	

	}

	public static void validatePrimitiveConstant(Type type,final IProblemRequestor problemRequestor){
		if (type.isArrayType()){
			type = ((ArrayType)type).getBaseType();
		}
		
		if (type.isNullableType()) {
			validatePrimitiveConstant(((NullableType)type).getBaseType(), problemRequestor);
		}
		
		if (type.isPrimitiveType()) {
			return;
		}
		
		if (type.isNameType()){
			Name name = ((NameType)type).getName();
			IBinding binding = name.resolveBinding();
			ITypeBinding typeBinding = null;
			if (isValidBinding(binding)){
				if (binding.isDataBinding()){
					typeBinding = ((IDataBinding)binding).getType();
				}else if (binding.isTypeBinding()){
					typeBinding = (ITypeBinding)binding;
					if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING) {
						typeBinding = ((DataItemBinding)typeBinding).getPrimitiveTypeBinding();
					}
				}
			}
			if (StatementValidator.isValidBinding(typeBinding) && typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING){
				problemRequestor.acceptProblem(type,
					IProblemRequestor.CONSTANT_DECL_MUST_BE_PRIMITIVE);
			}
		}
	}
	
	public static void validateDataDeclarationType(Type type, final IProblemRequestor problemRequestor, IPartBinding declaringPart){
		if (type.isArrayType()){
			ITypeBinding typeBinding = ((ArrayType)type).resolveTypeBinding();
			Type thistype = type;
			while (isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
				if (thistype.isNullableType()) {
					NullableType nullType = (NullableType)thistype;
					thistype = nullType.getBaseType();
					typeBinding = thistype.resolveTypeBinding();				
				}
				else {				
					ArrayType atype = (ArrayType)thistype;
					if (atype.hasInitialSize()){
						problemRequestor.acceptProblem(
								type,
								IProblemRequestor.ARRAY_DIMENSION_NOT_ALLOWED);
					}				
					thistype = atype.getElementType();
					typeBinding = ((ArrayTypeBinding)typeBinding).getElementType();
				}
			}
		}	
		
		ITypeBinding tbinding = type.resolveTypeBinding();
		if (Binding.isValidBinding(tbinding) && tbinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
			ITypeBinding typeBinding = tbinding.getBaseType(); 
			if (isValidBinding(typeBinding) ) {
				if(typeBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord") != null){

					problemRequestor.acceptProblem(type,
							IProblemRequestor.ARRAY_OF_UIRECORDS_NOT_ALLOWED);
				}
			
				if(ITypeBinding.SERVICE_BINDING == typeBinding.getKind() ||
				   (ITypeBinding.INTERFACE_BINDING == typeBinding.getKind() &&
				   	SystemPartManager.findType(typeBinding.getName()) != typeBinding)) {
					problemRequestor.acceptProblem(
						type,
						IProblemRequestor.SERVICE_OR_INTERFACE_ARRAYS_NOT_SUPPORTED);
				}
			}						
		}

		type = type.getBaseType();

		if (type.isPrimitiveType())
			return;
		
		if (type.isNameType()){
			Name name = ((NameType)type).getName();
			IBinding binding = name.resolveBinding();
			ITypeBinding typeBinding = null;
			if (isValidBinding(binding)){
				if (binding.isDataBinding()){
					typeBinding = ((IDataBinding)binding).getType();
				}else if (binding.isTypeBinding()){
					typeBinding = (ITypeBinding)binding;
				} else if (binding.isFunctionBinding()) {
					typeBinding = (ITypeBinding)binding;
				}
			}
			if (StatementValidator.isValidBinding(typeBinding) && 
					typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING &&
					typeBinding.getKind() != ITypeBinding.ARRAYDICTIONARY_BINDING &&
					!typeBinding.isReference() &&
					typeBinding.getKind() != ITypeBinding.FIXED_RECORD_BINDING &&
					typeBinding.getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING &&
					typeBinding.getKind() != ITypeBinding.DATAITEM_BINDING &&
					typeBinding.getKind() != ITypeBinding.HANDLER_BINDING &&
					typeBinding.getKind() != ITypeBinding.DICTIONARY_BINDING &&
					typeBinding.getKind() != ITypeBinding.DELEGATE_BINDING &&
					typeBinding.getKind() != ITypeBinding.ENUMERATION_BINDING){
				problemRequestor.acceptProblem(type,
					IProblemRequestor.DATA_DECLARATION_HAS_INCORRECT_TYPE,
					new String[] {typeBinding.getPackageQualifiedName()});
			}
			
			if (isAnnotationRecord(typeBinding) &&
				!isAnnotationRecord(declaringPart)) {
				problemRequestor.acceptProblem(type,
						IProblemRequestor.DATA_DECLARATION_HAS_INCORRECT_TYPE,
						new String[] {typeBinding.getPackageQualifiedName()});
			}
		}
	}
	
	public static boolean isAnnotationRecord(IBinding binding) {
		if (!Binding.isValidBinding(binding)) {
			return false;
		}
		
		if (!binding.isTypeBinding()) {
			return false;
		}
		
		if (((ITypeBinding)binding).getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING) {
			return false;
		}
		
		return ((FlexibleRecordBinding)binding).isAnnotationRecord();
		
	}
	
	public static void validateDeclarationForStereotypeContext(IDataBinding dBinding, IProblemRequestor problemRequestor, Node nodeForErrors) {
		ITypeBinding type = dBinding.getType();
		if(Binding.isValidBinding(type)) {
			type = type.getBaseType();
			if(Binding.isValidBinding(type) && type.isPartBinding()) {
				IPartSubTypeAnnotationTypeBinding subType = ((IPartBinding) type).getSubType();
				if(subType != null && IBinding.NOT_FOUND_BINDING != subType) {
					IAnnotationBinding aBinding = (IAnnotationBinding) dBinding.getAnnotation(subType).getAnnotation(AnnotationAnnotationTypeBinding.getInstance());
					if(aBinding != null) {
						aBinding = aBinding.getAnnotation(StereotypeAnnotationTypeBinding.getInstance());
						if(aBinding != null) {
							aBinding = (IAnnotationBinding) aBinding.findData("stereotypeContexts");
							if(IBinding.NOT_FOUND_BINDING != aBinding) {
								boolean declaringSubtypeIsValid = false;
								IPartBinding declaringType = dBinding.getDeclaringPart();
								if(declaringType != null) {
									ITypeBinding declaringSubtype = declaringType.getSubType();
									if(declaringSubtype instanceof AnnotationTypeBindingImpl) {
										declaringSubtype = ((AnnotationTypeBindingImpl) declaringSubtype).getAnnotationRecord();
									}
									Object[] value = (Object[]) aBinding.getValue();
									for(int i = 0; i < value.length && !declaringSubtypeIsValid; i++) {
										if(value[i] == declaringSubtype) {
											declaringSubtypeIsValid = true;
										}
									}
								}
								if(!declaringSubtypeIsValid) {
									problemRequestor.acceptProblem(
										nodeForErrors,
										IProblemRequestor.TYPE_NOT_VALID_FOR_DECLARATION_IN_STEREOTYPE,
										new String[] {
											getTypeString(type),
											dBinding.getDeclaringPart().getCaseSensitiveName()	
										});
								}
							}
						}
					}
				}
			}
		}
	}
	
	protected static boolean validateRecordParamDimensions(StructureItemBinding sBinding,IProblemRequestor problemRequestor,Node node,String name,String typename,int level){
		if(isValidBinding(sBinding) ){
			if (++level == 7){
				problemRequestor.acceptProblem(node,
						IProblemRequestor.TOO_MANY_DIMENTIONS_FOR_RECORD_ARRAY,
						new String[] {name,typename});
				return false;
			
			}
			
			List children = sBinding.getChildren();
			Iterator iter = children.iterator();
			while(iter.hasNext()){
				StructureItemBinding childbinding = (StructureItemBinding)iter.next();
				if (isValidBinding(childbinding)){
					boolean bContinue = validateRecordParamDimensions(childbinding,problemRequestor,node,name,typename,level);
					if (!bContinue){
						return false;
					}
				}
			}
		
		}
		
		return true;
	}
	
	
	protected static boolean validateRecordParamDimensions(ArrayTypeBinding typeBinding,IProblemRequestor problemRequestor,Node node,String name,String typename,int level){
		if(isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
			if (++level == 7){
				problemRequestor.acceptProblem(node,
						IProblemRequestor.TOO_MANY_DIMENTIONS_FOR_RECORD_ARRAY,
						new String[] {name,typename});
				return false;
			
			}
			ITypeBinding baseBinding = typeBinding.getBaseType();
			if (baseBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
				List list = ((FlexibleRecordBinding) baseBinding).getDeclaredFields();
				Iterator iter = list.iterator();
				while(iter.hasNext()){
					FlexibleRecordFieldBinding fbinding = (FlexibleRecordFieldBinding)iter.next();
					if (isValidBinding(fbinding.getType()) && fbinding.getType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
						boolean bContinue = validateRecordParamDimensions((ArrayTypeBinding)fbinding.getType(),problemRequestor,node,name,typename,level);
						if (!bContinue){
							return false;
						}
					}
				}
			}
				
			if (baseBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING){
				List list = ((FixedRecordBinding) baseBinding).getStructureItems();
				Iterator iter = list.iterator();
				while(iter.hasNext()){
					StructureItemBinding sbinding = (StructureItemBinding)iter.next();
					boolean bContinue = validateRecordParamDimensions(sbinding,problemRequestor,node,name,typename,level);
					if (!bContinue){
						return false;
					}
				}
			}
			
		}
		
		return true;
	}		
	
	
	public static void validateRecordParamDimensions(Type type,IProblemRequestor problemRequestor,Node node,String name){
		if (type.isArrayType()){
			ITypeBinding typeBinding = ((ArrayType)type).resolveTypeBinding();
			if (isValidBinding(typeBinding)){
				String typename = typeBinding.getBaseType().getName();
				validateRecordParamDimensions((ArrayTypeBinding)typeBinding,problemRequestor,node,name,typename,0);
			}
		}
	}
	
	public static List getSQLRecordIOObjects(Statement statement) {
		List sqlRecordObjectDataBindings = new ArrayList();
		List IOObjects = statement.getIOObjects();		
		for(Iterator iter = IOObjects.iterator(); iter.hasNext();) {
			IDataBinding nextDBinding = ((Expression) iter.next()).resolveDataBinding();
			if(nextDBinding != null && IBinding.NOT_FOUND_BINDING != nextDBinding) {
				ITypeBinding type = nextDBinding.getType();
				if(type != null) {
					if(ITypeBinding.ARRAY_TYPE_BINDING == type.getKind() &&
					   ((ArrayTypeBinding) type).getElementType().getAnnotation(EGLIOSQL, "SQLRecord") != null) {
						sqlRecordObjectDataBindings.add(nextDBinding);
					}
				}
			}
		}
		return sqlRecordObjectDataBindings;
	}
	
	public static void validateRequiredFieldsInCUIDeclaration(Type declType, SettingsBlock settingsBlock, IProblemRequestor problemRequestor) {
		validateRequiredFieldsInCUIDeclaration(declType, settingsBlock, false, problemRequestor);		
	}
	
	public static void validateRequiredFieldsInCUIDeclaration(Type declType, SettingsBlock settingsBlock, boolean isConstantConsoleFormField, IProblemRequestor problemRequestor) {
		ITypeBinding declTypeBinding = declType.resolveTypeBinding();
		if(IBinding.NOT_FOUND_BINDING != declTypeBinding && declTypeBinding != null) {
			declTypeBinding = declTypeBinding.getBaseType();
			
			if(settingsBlock != null) {
				if(AbstractBinder.typeIs(declTypeBinding, new String[] {"egl", "ui", "console"}, "Menu")) {
					final boolean[] hasLabelKeyOrLabelText = new boolean[] {false};
				
					for(Iterator iter = settingsBlock.getSettings().iterator(); !hasLabelKeyOrLabelText[0] && iter.hasNext();)
						((Node) iter.next()).accept(new DefaultASTVisitor() {
						public boolean visit(Assignment assignment) {
							assignment.getLeftHandSide().accept(new AbstractASTExpressionVisitor() {
								public boolean visitName(Name name) {
									String identifier = name.getIdentifier();
									if(InternUtil.intern("labelKey") == identifier ||
									   InternUtil.intern("labelText") == identifier) {
										hasLabelKeyOrLabelText[0] = true;
									}								
									return false;
								}
							});
							return false;
						}
					});
					
					if(!hasLabelKeyOrLabelText[0]) {
						problemRequestor.acceptProblem(
							declType,
							IProblemRequestor.MENU_DECLARATION_REQUIRES_LABELKEY_OR_LABELTEXT,
							new String[] {"labelText"});
					}
				}
			}
			
			if(AbstractBinder.typeIs(declTypeBinding, new String[] {"egl", "ui", "console"}, "ConsoleField") && !isConstantConsoleFormField && !declType.isArrayType()) {
				final boolean[] hasFieldLenOrSegements = new boolean[] {false};
				
				if(settingsBlock != null) {			
					for(Iterator iter = settingsBlock.getSettings().iterator(); !hasFieldLenOrSegements[0] && iter.hasNext();)
						((Node) iter.next()).accept(new DefaultASTVisitor() {
						public boolean visit(Assignment assignment) {
							assignment.getLeftHandSide().accept(new AbstractASTExpressionVisitor() {
								public boolean visitName(Name name) {
									String identifier = name.getIdentifier();
									if(InternUtil.intern("fieldLen") == identifier ||
									   InternUtil.intern("segments") == identifier) {
										hasFieldLenOrSegements[0] = true;
									}								
									return false;
								}
							});
							return false;
						}
					});
				}
				
				if(!hasFieldLenOrSegements[0]) {
					problemRequestor.acceptProblem(
						declType,
						IProblemRequestor.CONSOLEFIELD_DECLARATION_REQUIRES_FIELDLEN,
						new String[] {"fieldLen"});
				}
			}
		}
	}

	public static String getName(Statement statement) {
		final String[] result = new String[] {null};
		statement.accept(new DefaultASTVisitor() {
			public void endVisit(AddStatement addStatement) {
				result[0] = IEGLConstants.KEYWORD_ADD;
			}

			public void endVisit(CallStatement callStatement) {
				result[0] = IEGLConstants.KEYWORD_CALL;
			}

			public void endVisit(CaseStatement caseStatement) {
				result[0] = IEGLConstants.KEYWORD_CASE;
			}

			public void endVisit(CloseStatement closeStatement) {
				result[0] = IEGLConstants.KEYWORD_CLOSE;
			}

			public void endVisit(ContinueStatement continueStatement) {
				result[0] = IEGLConstants.KEYWORD_CONTINUE;
			}

			public void endVisit(ConverseStatement converseStatement) {
				result[0] = IEGLConstants.KEYWORD_CONVERSE;
			}
			
			public void endVisit(DeleteStatement deleteStatement) {
				result[0] = IEGLConstants.KEYWORD_DELETE;
			}

			public void endVisit(DisplayStatement displayStatement) {
				result[0] = IEGLConstants.KEYWORD_DISPLAY;
			}

			public void endVisit(ExecuteStatement executeStatement) {
				result[0] = IEGLConstants.KEYWORD_EXECUTE;
			}

			public void endVisit(ExitStatement exitStatement) {
				result[0] = IEGLConstants.KEYWORD_EXIT;
			}

			public void endVisit(ForEachStatement forEachStatement) {
				result[0] = IEGLConstants.KEYWORD_FOREACH;
			}

			public void endVisit(ForStatement forStatement) {
				result[0] = IEGLConstants.KEYWORD_FOR;
			}

			public void endVisit(ForwardStatement forwardStatement) {
				result[0] = IEGLConstants.KEYWORD_FORWARD;
			}

			public void endVisit(FreeSQLStatement freeSQLStatement) {
				result[0] = IEGLConstants.KEYWORD_FREESQL;
			}

			public void endVisit(GetByKeyStatement getByKeyStatement) {
				result[0] = IEGLConstants.KEYWORD_GET;
			}

			public void endVisit(GetByPositionStatement getByPositionStatement) {
				result[0] = IEGLConstants.KEYWORD_GET;
			}

			public void endVisit(GotoStatement gotoStatement) {
				result[0] = IEGLConstants.KEYWORD_GOTO;
			}
			
			public void endVisit(IfStatement ifStatement) {
				result[0] = IEGLConstants.KEYWORD_IF;
			}

			public void endVisit(MoveStatement moveStatement) {
				result[0] = IEGLConstants.KEYWORD_MOVE;
			}

			public void endVisit(OpenStatement openStatement) {
				result[0] = IEGLConstants.KEYWORD_OPEN;
			}

			public void endVisit(OpenUIStatement openUIStatement) {
				result[0] = IEGLConstants.KEYWORD_OPENUI;
			}

			public void endVisit(PrintStatement printStatement) {
				result[0] = IEGLConstants.KEYWORD_PRINT;
			}

			public void endVisit(ReplaceStatement replaceStatement) {
				result[0] = IEGLConstants.KEYWORD_REPLACE;
			}

			public void endVisit(ShowStatement showStatement) {
				result[0] = IEGLConstants.KEYWORD_SHOW;
			}
			
			public void endVisit(ThrowStatement throwStatement) {
				result[0] = IEGLConstants.KEYWORD_THROW;
			}

			public void endVisit(TransferStatement transferStatement) {
				result[0] = IEGLConstants.KEYWORD_TRANSFER;
			}

			public void endVisit(TryStatement tryStatement) {
				result[0] = IEGLConstants.KEYWORD_TRY;
			}

			public void endVisit(WhileStatement whileStatement) {
				result[0] = IEGLConstants.KEYWORD_WHILE;
			}
			
		    public void endVisit(ReturnStatement returnStatement) {
		        result[0] = IEGLConstants.KEYWORD_RETURN;
		    }
		    
		    public void endVisit(SetStatement setStatement) {
		        result[0] = IEGLConstants.KEYWORD_SET;
		    }
		    
		    public void endVisit(PrepareStatement prepareStatement) {
		        result[0] = IEGLConstants.KEYWORD_PREPARE;
		    }
		});
		
		if(result[0] == null) {
			throw new RuntimeException("Must define visit() for class " + statement.getClass() + " in StatementValidator::getName()");
		}
		
		return result[0];
	}
	
	public static boolean isFlexibleBasicOrSQL(ITypeBinding typeBinding) {
		if (Binding.isValidBinding(typeBinding)) {
			return (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING && (typeBinding.getAnnotation(EGLCORE, "BasicRecord") != null || (typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null)));
		}
		return false;
	}
}
