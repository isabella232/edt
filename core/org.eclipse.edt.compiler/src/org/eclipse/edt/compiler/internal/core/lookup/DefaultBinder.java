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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.lang.model.type.PrimitiveType;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Assignment.Operator;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.InExpression;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.IsNotExpression;
import org.eclipse.edt.compiler.core.ast.LikeMatchesExpression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.MoveModifier;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.ObjectExpression;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturningToNameClause;
import org.eclipse.edt.compiler.core.ast.ReturnsDeclaration;
import org.eclipse.edt.compiler.core.ast.SQLLiteral;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.TypeLiteralExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.statement.LValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.RValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public abstract class DefaultBinder extends AbstractBinder {
    
    protected IProblemRequestor problemRequestor;
       
	private boolean bindingFunctionInvocationTarget;
    
    public DefaultBinder(Scope currentScope, Part currentBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, currentBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.compilerOptions = compilerOptions;
    }

    /**
     * This method binds names that are expression names
     * Clients must be careful to prevent this method from being called on names that are not expression names
     * e.g. package names, types names
     * Usually the owner of these names have their own visitor and that visit method should return false 
     * to prevent this method from being called
     */
    public boolean visit(QualifiedName qualifiedName) {
    	if(!qualifiedName.isBindAttempted()) {
	        try {        	
	            bindExpressionName(qualifiedName, bindingFunctionInvocationTarget);
	        } catch (ResolutionException e) {
	        	qualifiedName.setBindAttempted(true);
	        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
	        }
    	}
        return false;
    }
    
    protected void handleNameResolutionException(ResolutionException e) {
    	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    }

    /**
     * This methods binds names that are expression names
     * See method above for more info
     */
    public boolean visit(SimpleName simpleName) {    	
        // In some cases (particular for names of part declarations), the simple name has already been bound
        if(!simpleName.isBindAttempted()) {
            try {
            	bindExpressionName(simpleName, bindingFunctionInvocationTarget);	            	
            } catch (ResolutionException e) {
                handleNameResolutionException(e);
            }
            return false;
        }
        return false;
    }
           
	public boolean visit(FunctionInvocation functionInvocation) {
		bindingFunctionInvocationTarget = true;
		functionInvocation.getTarget().accept(this);
		bindingFunctionInvocationTarget = false;
		
		org.eclipse.edt.mof.egl.Type type = functionInvocation.getTarget().resolveType();
		if (type != null) {
			if (functionInvocation.getTarget() instanceof ThisExpression) {
				List<Constructor> constructors = getConstructors(type);
				if (constructors == null || constructors.isEmpty()) {
					problemRequestor.acceptProblem(
							functionInvocation.getTarget(),
							IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
							new String[]{BindingUtil.getName(type)});
				}
				else {
					functionInvocation.getTarget().setElement(constructors);
				}
			}
		}
		
        for(Iterator iter = functionInvocation.getArguments().iterator(); iter.hasNext();) {
        	((Node) iter.next()).accept(this);
        }
		
        return false;
	}
	
	public boolean visit(ThisExpression thisExpression) {
	    if (thisExpression.isBindAttempted()) {
	        return false;
	    }
		Scope scopeForThis = currentScope.getScopeForKeywordThis();
		if(scopeForThis instanceof FunctionContainerScope) {
			thisExpression.setType(((FunctionContainerScope) scopeForThis).getPart());
		}
		else {
			thisExpression.setBindAttempted(true);
			problemRequestor.acceptProblem(thisExpression, IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {thisExpression.getCanonicalString()});
		}
		return false;
	}
	
	public boolean visit(TypeLiteralExpression typeLiteralExpression) {
		try {
			bindType(typeLiteralExpression.getType());
		}
		catch(ResolutionException e) {
			handleNameResolutionException(e);
		}
		return false;
	}

	public boolean visit(ArrayAccess arrayAccess) {
				
		return true;
	}
	
    public void endVisit(ArrayAccess arrayAccess) {
        if (arrayAccess.isBindAttempted()) {
            return;
        }
    	org.eclipse.edt.mof.egl.Type type = arrayAccess.getArray().resolveType();
    	if (type == null) {
    		arrayAccess.setBindAttempted(true);
    	}
    	else {
    		if (BindingUtil.isDynamicallyAccessible(type)) {
    			arrayAccess.setType(BindingUtil.getEAny());
    		}
    		else {
    			if (type instanceof org.eclipse.edt.mof.egl.ArrayType) {
    				arrayAccess.setType(((org.eclipse.edt.mof.egl.ArrayType)type).getElementType());
    			}
    			else {
    	    		arrayAccess.setBindAttempted(true); 
    			}
    		}
    	}
    }
        
        
	public void endVisit(SubstringAccess substringAccess) {
		org.eclipse.edt.mof.egl.Type type = substringAccess.getPrimary().resolveType();
		if (type == null) {
			substringAccess.setBindAttempted(true);
		}
		else {
    		Operation op = IRUtils.getOperation(type.getClassifier(), "[:");
    		if (op == null) {
    			substringAccess.setBindAttempted(true);
    		}
    		else {
    			substringAccess.setType(op.getType());
    		}
		}
	}
		
     public void endVisit(final FieldAccess fieldAccess) {
        if (fieldAccess.isBindAttempted()) {
            return;
        }
        
        org.eclipse.edt.mof.egl.Type type = fieldAccess.getPrimary().resolveType();
        if (type == null) {
        	fieldAccess.setBindAttempted(true);
        	return;
        }
        Member member = BindingUtil.createDynamicAccessMember(type, fieldAccess.getCaseSensitiveID());
        if (member != null) {
        	fieldAccess.setMember(member);
        	fieldAccess.setType(member.getType());
        	return;
        }
        
        List<Member> mbrs = findData(type, fieldAccess.getID());
        if (mbrs == null) {
        	int endOffset = fieldAccess.getOffset() + fieldAccess.getLength();
        	problemRequestor.acceptProblem(
        		endOffset - fieldAccess.getID().length(),
        		endOffset,
				IProblemRequestor.VARIABLE_NOT_FOUND,
				new String[] {fieldAccess.getCanonicalString()});
        	fieldAccess.setBindAttempted(true);
        	return;
        }
        if (mbrs.size() > 1) {
        	if (!isFunctionSet(mbrs) || !isFunctionInvocationTarget(fieldAccess)) {
        		problemRequestor.acceptProblem(
	        			fieldAccess,
						IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS,
						new String[] {fieldAccess.getCanonicalString()});
        		fieldAccess.setBindAttempted(true);
        		return;
        	}
        	fieldAccess.setElement(mbrs);
        	return;
        }
        
        member = mbrs.get(0);
        fieldAccess.setMember(member);
        org.eclipse.edt.mof.egl.Type mbrType = BindingUtil.getType(mbrs.get(0));
        fieldAccess.setType(mbrType);
        dependencyRequestor.recordType(mbrType);
    }
     
    private boolean isFunctionInvocationTarget(Expression expr) {
    	if (expr.getParent() instanceof FunctionInvocation) {
    		return ( ((FunctionInvocation)expr.getParent()).getTarget() == expr );
    	}
    	if (expr.getParent() instanceof ParenthesizedExpression) {
    		return isFunctionInvocationTarget((ParenthesizedExpression)expr.getParent());
    	}
    	return false;
    }
    
    
    public void endVisit(SimpleName simpleName) {
	}
    
    public void endVisit(QualifiedName qualifiedName) {
	}
        	
	public boolean visit(OpenStatement openStatement) {
		return true;
	}
	
	public void endVisit(OpenStatement openStatement) {
	}
	
	public boolean visit(GetByKeyStatement openStatement) {
		return true;
	}
	
	public void endVisit(final GetByKeyStatement getByKeyStatement) {
	}
	
	public void endVisit(final DeleteStatement getByKeyStatement) {
	}
	
	public boolean visit(GetByPositionStatement openStatement) {
		return true;
	}
	
	public void endVisit(final GetByPositionStatement openStatement) {
	}
	
	public void endVisit(final AddStatement openStatement) {
	}
	
	public void endVisit(final ReplaceStatement openStatement) {
	}
	
	public boolean visit(ExecuteStatement openStatement) {
		return true;
	}
	
	public void endVisit(ExecuteStatement openStatement) {
	}
	
	public boolean visit(IntoClause intoClause) {
		return true;		
	}
	
	public void endVisit(IntoClause intoClause) {
	}
	
	public boolean visit(IsAExpression isAExpression) {
		isAExpression.getExpression().accept(this);
		try {
			Type typeAST = isAExpression.getType();
			bindType(typeAST);
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		return false;
	}
	
	public boolean visit(AsExpression asExpression) {
		asExpression.getExpression().accept(this);
		try {
			if(asExpression.hasType()) {
				Type typeAST = asExpression.getType();
				bindType(typeAST);
			}
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		return false;
	}
		
	public boolean visit(InExpression inExpression) {
		inExpression.getFirstExpression().accept(this);		
		inExpression.getSecondExpression().accept(this);		
		if(inExpression.hasFromExpression()) {
			inExpression.getFromExpression().accept(this);
		}
		return false;
	}
	
	protected NamedElement getOperandType(Expression expr) {
		Object element = expr.resolveElement();
		if (element instanceof Function) {
			return (Function)element;
		}
		else {	
			if (expr.resolveType() != null) {
				return (Classifier)expr.resolveType().getClassifier();
			}
		}	
		return null;
	}

	public void endVisit(LikeMatchesExpression likeMatchesExpression) {
		
		Operation op = IRUtils.getBinaryOperation(
									getOperandType(likeMatchesExpression.getFirstExpression()), 
									getOperandType(likeMatchesExpression.getSecondExpression()), 
									likeMatchesExpression.getOperator().toString());
		if (op != null) {
			likeMatchesExpression.setType(op.getType());		
		}
	}
		
	public boolean visit(NewExpression newExpression) {
		try {
		    if (!newExpression.isBindAttempted()) {
		        newExpression.setType(bindType(newExpression.getType()));	
		    }
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
			newExpression.setBindAttempted(true);
		}
				
		if(newExpression.hasSettingsBlock()) {			
			org.eclipse.edt.mof.egl.Type type = newExpression.resolveType();
		    if (type != null) {		    	
		          TypeScope newScope = new TypeScope(NullScope.INSTANCE, type);
		            newExpression.getSettingsBlock().accept(
		                    new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
		    }
		}
		
		if(newExpression.hasArgumentList()) {
			for(Iterator iter = newExpression.getArguments().iterator(); iter.hasNext();) {
				((Node) iter.next()).accept(this);
			}
		}		
		return false;
	}
		
	public void endVisit(NewExpression newExpression) {
		org.eclipse.edt.mof.egl.Type targetType = newExpression.getType().resolveType();
		if(targetType != null) {
			
			if(newExpression.hasArgumentList()) {
				
				Constructor cons = null;
				if (targetType.getClassifier() instanceof EGLClass) {
					cons = IRUtils.resolveConstructorReferenceFromArgTypes((EGLClass)targetType.getClassifier(), getArgumentTypes(newExpression.getArguments()), false);
				}
				if (cons == null) {
					if (newExpression.getArguments().size() > 0) {
						problemRequestor.acceptProblem(
							newExpression.getType(),
							IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
							new String[] {
								newExpression.getType().getCanonicalName()
							});
					}
					else {
						newExpression.setConstructor(cons);
						if (BindingUtil.isPrivate(cons)) {
							if (!targetType.equals(currentScope.getPart())) {
								problemRequestor.acceptProblem(newExpression.getType(),
										IProblemRequestor.PRIVATE_CONSTRUCTOR,
									new String[] {newExpression.getType().getCanonicalName()});
							}
						}							
					}
				}					
			}
		}
	}
	
	private List<NamedElement> getArgumentTypes(List<Expression> functionArguments) {
		List<NamedElement> list = new ArrayList<NamedElement>();
		for (Expression expr : functionArguments) {
			list.add(getOperandType(expr));
		}
		return list;
	}
	
	private List<Constructor> getConstructors(org.eclipse.edt.mof.egl.Type type) {
		if (type instanceof StructPart) {
			return ((StructPart)type).getConstructors();
		}
		return null;
	}

	public boolean visit(CloseStatement closeStatement) {
		//The target might be a resultSetID, so don't issue error
		IProblemRequestor pRequestor = problemRequestor;
		problemRequestor = NullProblemRequestor.getInstance();
		closeStatement.getExpr().accept(this);
		problemRequestor = pRequestor;		
		return false;
	}
	

	public boolean visit(final IsNotExpression isNotExpression) {
		
		problemRequestor.acceptProblem(
				isNotExpression,
				IProblemRequestor.IS_NOT_UNSUPPORTED,
				new String[] {});

		
		isNotExpression.getFirstExpression().accept(this);
		return false;
	}
	
	public boolean visit(ReturningToNameClause returningToNameClause) {
		try {
			bindTypeName(returningToNameClause.getName());
		}
		catch(ResolutionException e) {
		}

		return false;	
	}
	
	
	public boolean visit(final ForwardStatement forwardStatement) {
		if (forwardStatement.hasForwardTarget()){
			final Expression expr = forwardStatement.getForwardTarget();
			if(forwardStatement.isForwardToURL()) {
				expr.accept(this);
			}
			else {
				bindInvocationTarget(expr, false);
			}
		}
	
		for(Iterator iter = forwardStatement.getArguments().iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(this);
		}
		for(Iterator iter = forwardStatement.getForwardOptions().iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(this);
		}
		return false;
	}

	/*
	 * Type inference logic -- code which sets type binding for Expression ASTs
	 */
	
	public void endVisit(ParenthesizedExpression parenthesizedExpression) {
		parenthesizedExpression.setType(parenthesizedExpression.getExpression().resolveType());
	}
	
	public void endVisit(UnaryExpression unaryExpression) {
		Expression operand = unaryExpression.getExpression();
		org.eclipse.edt.mof.egl.Type operandType = operand.resolveType();
		if (operandType == null) {
			unaryExpression.setBindAttempted(true);
		}
		else {
			Operation op = IRUtils.getUnaryOperation(operandType.getClassifier(), unaryExpression.getOperator().toString());
			if (op == null) {
				unaryExpression.setBindAttempted(true);
			}
			else {
				unaryExpression.setType(op.getType());
			}
		}		
	}
	
	public void endVisit(BinaryExpression binaryExpression) {
		Expression operand1 = binaryExpression.getFirstExpression();
		Expression operand2 = binaryExpression.getSecondExpression();
		NamedElement elem1 = getOperandType(operand1);
		NamedElement elem2 = getOperandType(operand2);
		if (elem1 != null && elem2 != null) {
			Operation op = IRUtils.getBinaryOperation(elem1, elem2, binaryExpression.getOperator().toString());
			if (op != null) {
				binaryExpression.setType(op.getType());
			}
		}
	}
	
	
	public void endVisit(Assignment assignment) {}
	
	
	public void endVisit(InExpression inExpression) {		
		Expression operand1 = inExpression.getFirstExpression();
		Expression operand2 = inExpression.getSecondExpression();
		
		Operation op = IRUtils.getBinaryOperation(
									getOperandType(inExpression.getFirstExpression()), 
									getOperandType(inExpression.getSecondExpression()), 
									MultiOperandExpression.Op_IN);
		if (op != null) {
			inExpression.setType(op.getType());		
		}
		
	}
			
	public void endVisit(IsAExpression isAExpression) {
		isAExpression.setType(TypeUtils.Type_BOOLEAN);
	}
	
	public void endVisit(final AsExpression asExpression) {
		if(asExpression.hasType()) {
			org.eclipse.edt.mof.egl.Type type = asExpression.getType().resolveType();
			if (type == null) {
				asExpression.setBindAttempted(true);
			}
			else {
				asExpression.setType(type);
			}	
		}
	}
	
	public void endVisit(final IsNotExpression isNotExpression) {
		isNotExpression.setType(TypeUtils.Type_BOOLEAN);
	}
				
	public void endVisit(IntegerLiteral integerLiteral) {
		if(!integerLiteral.isBindAttempted()){
			integerLiteral.setType(TypeUtils.Type_INT);
		}
	}
	
	public void endVisit(DecimalLiteral decimalLiteral) {
		if(!decimalLiteral.isBindAttempted()){
			decimalLiteral.setType(IRUtils.getEGLPrimitiveType(Type_Decimal, getLengthFromLiteral(decimalLiteral), getDecimalsFromLiteral(decimalLiteral)));
		}
	}
	
	public void endVisit(FloatLiteral floatLiteral) {
		if(floatLiteral.resolveTypeBinding() == null){
			try {
				floatLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(
						Primitive.FLOAT));
				
				String strVal = floatLiteral.getValue();
				
				if(Double.isInfinite(Double.parseDouble(strVal))) {
					problemRequestor.acceptProblem(
						floatLiteral,
						IProblemRequestor.FLOATING_POINT_LITERAL_OUT_OF_RANGE,
						new String[] { strVal });
				}
			}
			catch( NumberFormatException e ) {
				// should be syntax error, so ignore			
			}
		}
	}
	
	public void endVisit(BooleanLiteral booleanLiteral) {
		booleanLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
	}
	
	public void endVisit(StringLiteral stringLiteral) {
		if(stringLiteral.resolveTypeBinding() == null){
			String value = stringLiteral.getValue();
			
			boolean textLiteralDefaultIsString = true;
			Primitive prim = null;
			int length = value.length();
			
			IPartBinding functionContainerBinding = currentScope.getPartBinding();
			if(functionContainerBinding != null ){
				IAnnotationBinding aBinding = functionContainerBinding.getAnnotation(new String[] {"egl", "core"}, "TextLiteralDefaultIsString");
				if(aBinding != null) {
					textLiteralDefaultIsString = Boolean.YES == aBinding.getValue();
				}
			}
			
			if(textLiteralDefaultIsString) {
				prim = Primitive.STRING;
			}
			else {
				byte[] bytes = value.getBytes();				
				
				if (bytes.length == length) {
					prim = Primitive.CHAR;
				}
				else if (bytes.length == length * 2) {
					prim = Primitive.DBCHARLIT;
				}
				else {
					prim = Primitive.MBCHAR;
				}
			}
			
			stringLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(prim, length));
			
			if(stringLiteral.isHex()) {
				if(stringLiteral.getValue().length() % 4 != 0) {
					problemRequestor.acceptProblem(stringLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_MULTIPLE_OF_FOUR, new String[] {stringLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(HexLiteral hexLiteral) {
		if(hexLiteral.resolveTypeBinding() == null) {
			hexLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.HEX, hexLiteral.getValue().length()));
			
			if(hexLiteral.getValue().length() % 2 != 0) {
				problemRequestor.acceptProblem(hexLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {hexLiteral.getValue()});
			}
		}
	}
	
	public void endVisit(CharLiteral charLiteral) {
		if(charLiteral.resolveTypeBinding() == null) {
			charLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.CHAR, charLiteral.getValue().length()));
			
			if(charLiteral.isHex()) {
				if(charLiteral.getValue().length() % 2 != 0) {
					problemRequestor.acceptProblem(charLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {charLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(DBCharLiteral dbcharLiteral) {
		if(dbcharLiteral.resolveTypeBinding() == null) {
			dbcharLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.DBCHARLIT, dbcharLiteral.getValue().length()));
			
			if(dbcharLiteral.isHex()) {
				if(dbcharLiteral.getValue().length() % 4 != 0) {
					problemRequestor.acceptProblem(dbcharLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_MULTIPLE_OF_FOUR, new String[] {dbcharLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(MBCharLiteral mbcharLiteral) {
		if(mbcharLiteral.resolveTypeBinding() == null) {
			mbcharLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.MBCHAR, mbcharLiteral.getValue().length()));
			
			if(mbcharLiteral.isHex()) {
				if(mbcharLiteral.getValue().length() % 2 != 0) {
					problemRequestor.acceptProblem(mbcharLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {mbcharLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(SQLLiteral sqlLiteral) {
		sqlLiteral.setTypeBinding(SystemPartManager.SQLSTRING_BINDING);
	}
	
	public void endVisit(TypeLiteralExpression typeLiteralExpression) {
		typeLiteralExpression.setTypeBinding(SystemPartManager.TYPEREF_BINDING);
	}
	
	private static int getLengthFromLiteral(LiteralExpression literalExpr) {
		return literalExpr.getValue().length();
	}
	
	private static int getDecimalsFromLiteral(LiteralExpression literalExpr) {
		if (literalExpr.getLiteralKind() == LiteralExpression.DECIMAL_LITERAL) {			
			return getLengthFromLiteral(literalExpr) - literalExpr.getValue().indexOf('.') + 1;
		}
		else if (literalExpr.getLiteralKind() == LiteralExpression.FLOAT_LITERAL) {			
			String literal = literalExpr.getValue();
			int length = literal.indexOf('e') +1;
			if ( length < 1 ) { // must be upper case E then
				length = literal.indexOf('E') +1;
			}
			//really length - 1 to discount the E 
			length = length - 1;
			return length - (literal.indexOf('.') + 1);
		}
		return 0;
	}
	
	protected boolean canMixTypesInArrayLiterals() {
		return true;
	}
	
	public void endVisit(ObjectExpression objExpr) {
		if (objExpr.resolveTypeBinding() == null) {
			objExpr.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.ANY));
		}
	}
	
	public void endVisit(ArrayLiteral arrayLiteral) {
		if(arrayLiteral.resolveTypeBinding() == null){
			List expressions = arrayLiteral.getExpressions();
			if(expressions.isEmpty()) {
				arrayLiteral.setTypeBinding(ArrayTypeBinding.getNonReferenceInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY)));
			}
			else {
				Expression firstElementExpr = (Expression) expressions.get(0);
				ITypeBinding firstElementType = stripPrimitiveLength((firstElementExpr).resolveTypeBinding());
				if(firstElementType instanceof AnnotationTypeBindingImpl) {
					firstElementType = ((AnnotationTypeBindingImpl) firstElementType).getAnnotationRecord();
				}
				if(firstElementType == null && firstElementExpr instanceof Name && IBinding.NOT_FOUND_BINDING == ((Name) firstElementExpr).resolveBinding())  {
					return;
				}
				if(firstElementType != null && firstElementType != IBinding.NOT_FOUND_BINDING) {
					boolean allTypesSame = true;
					Iterator iter = expressions.iterator();
					iter.next();
					while(iter.hasNext() && allTypesSame) {
						Expression nextExpr = (Expression) iter.next();
						ITypeBinding nextType = stripPrimitiveLength((nextExpr).resolveTypeBinding());
						if(nextType instanceof AnnotationTypeBindingImpl) {
							nextType = ((AnnotationTypeBindingImpl) nextType).getAnnotationRecord();
						}
						if(nextType != null && IBinding.NOT_FOUND_BINDING != nextType) {
							if(nextType != firstElementType) {
								ITypeBinding commonExternalTypeSuperType = getCommonExtendedTypeSupertype(firstElementType, nextType, compilerOptions);
								if(commonExternalTypeSuperType != null) {
									firstElementType = commonExternalTypeSuperType;
								}
								else {
									allTypesSame = false;
									
									if(!TypeCompatibilityUtil.isMoveCompatible(firstElementType, nextType, nextExpr, compilerOptions)) {
										if(!canMixTypesInArrayLiterals()) {
											problemRequestor.acceptProblem(
												nextExpr,
												IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
												new String[] {
													StatementValidator.getShortTypeString(firstElementType),
													StatementValidator.getShortTypeString(nextType),
													"[" + firstElementExpr.getCanonicalString() + ", ..., " + nextExpr.getCanonicalString() + "]"
												}
											);
										}
									}
								}
							}
						}
					}
					if(allTypesSame) {
						arrayLiteral.setTypeBinding(ArrayTypeBinding.getNonReferenceInstance(firstElementType));
					}
				}
			}
			
			if(arrayLiteral.resolveTypeBinding() == null){
				arrayLiteral.setTypeBinding(ArrayTypeBinding.getNonReferenceInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY)));
			}
		}
	}

	private ITypeBinding getCommonExtendedTypeSupertype (ITypeBinding type1, ITypeBinding type2, ICompilerOptions compilerOptions) {
		if(ITypeBinding.EXTERNALTYPE_BINDING == type1.getKind() && ITypeBinding.EXTERNALTYPE_BINDING == type2.getKind()) {
			List extendedTypes1 = ((ExternalTypeBinding) type1).getExtendedTypes();
			List extendedTypes2 = ((ExternalTypeBinding) type2).getExtendedTypes();
			if(extendedTypes1.contains(type2)) {
				return type2;
			}
			if(extendedTypes2.contains(type1)) {
				return type1;
			}
			for(Iterator iter = extendedTypes1.iterator(); iter.hasNext();) {
				ITypeBinding next = (ITypeBinding) iter.next();
				if(extendedTypes2.contains(next)) {
					return next;
				}
			}
		}

		if(ITypeBinding.INTERFACE_BINDING == type1.getKind() && ITypeBinding.INTERFACE_BINDING == type2.getKind()) {
			List extendedTypes1 = ((InterfaceBinding) type1).getExtendedTypes();
			List extendedTypes2 = ((InterfaceBinding) type2).getExtendedTypes();
			if(extendedTypes1.contains(type2)) {
				return type2;
			}
			if(extendedTypes2.contains(type1)) {
				return type1;
			}
			for(Iterator iter = extendedTypes1.iterator(); iter.hasNext();) {
				ITypeBinding next = (ITypeBinding) iter.next();
				if(extendedTypes2.contains(next)) {
					return next;
				}
			}
		}

		return null;
	}

	private ITypeBinding stripPrimitiveLength(ITypeBinding binding) {
		if(binding == null || binding == IBinding.NOT_FOUND_BINDING) {
			return binding;
		}
		switch(binding.getKind()) {
		case ITypeBinding.ARRAY_TYPE_BINDING:
			return ArrayTypeBinding.getInstance(stripPrimitiveLength(((ArrayTypeBinding) binding).getElementType()));
		case ITypeBinding.PRIMITIVE_TYPE_BINDING:
			return PrimitiveTypeBinding.getInstance(((PrimitiveTypeBinding) binding).getPrimitive());
		}
		return binding;
	}

	public void endVisit(NullLiteral nilLiteral) {
		nilLiteral.setTypeBinding(NilBinding.INSTANCE);
	}
	
	public void endVisit(AnnotationExpression annotationExpression) {
		annotationExpression.setTypeBinding(annotationExpression.getName().resolveTypeBinding());
	}
	
    protected static class SetValuesExpressionCompletor extends DefaultBinder {

        TypeScope leftHandScope;

        public SetValuesExpressionCompletor(Scope currentScope, Part currentBinding, TypeScope leftHandScope,
                IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
            super(currentScope, currentBinding, dependencyRequestor, problemRequestor, compilerOptions);
            this.leftHandScope = leftHandScope;
        }

        public boolean visit(SettingsBlock settingsBlock) {
            return true;
        }

        public boolean visit(Assignment assignment) {
            Scope saved = currentScope;
            currentScope = leftHandScope;
            assignment.getLeftHandSide().accept(this);
            currentScope = saved;
            assignment.getRightHandSide().accept(this);
            return false;
        }

        public boolean visit(SetValuesExpression setValuesExpression) {
            Scope saved = currentScope;
            currentScope = leftHandScope;
            setValuesExpression.getExpression().accept(this);
            currentScope = saved;
            final ITypeBinding typeBinding = setValuesExpression.getExpression().resolveTypeBinding();
            if (Binding.isValidBinding(typeBinding)) {
                TypeScope newScope = new TypeScope(NullScope.INSTANCE, typeBinding, setValuesExpression.getExpression()
                        .resolveDataBinding());
                setValuesExpression.getSettingsBlock().accept(
                        new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
            } else {
            	setBindAttemptedForNames(setValuesExpression.getSettingsBlock());
            }
            return false;
        }

        public boolean visit(ThisExpression thisExpression) {
            thisExpression.setTypeBinding(leftHandScope.getTypeBinding());
            return false;
        }
    }
	
	public boolean visit(SetValuesExpression setValuesExpression) {
		if(setValuesExpression.getExpression().resolveDataBinding() == IBinding.NOT_FOUND_BINDING) {
			//To prevent cascading errors, stop traversing if this is a complex annotation
			//that is not applicable.
			return false;
		}
		
        setValuesExpression.getExpression().accept(this);
        final ITypeBinding typeBinding = setValuesExpression.getExpression().resolveTypeBinding();
        if (typeBinding != null) {
            TypeScope newScope = new TypeScope(NullScope.INSTANCE, typeBinding, setValuesExpression.getExpression()
                    .resolveDataBinding());
            setValuesExpression.getSettingsBlock().accept(
                    new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
        } else if (setValuesExpression.getExpression().resolveDataBinding() != IBinding.NOT_FOUND_BINDING) {
            setValuesExpression.getSettingsBlock().accept(this);
        }
        
		return true;
	}
	
	// Will be null if not visiting a function invocation statement
	FunctionInvocation functionInvocationFromFunctionInvocationStatement;
	
	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
		functionInvocationFromFunctionInvocationStatement = functionInvocationStatement.getFunctionInvocation();
		return true;
	}
	
	public void endVisit(FunctionInvocationStatement functionInvocationStatement) {
		functionInvocationFromFunctionInvocationStatement = null;
	}
	
	public void endVisit(FunctionInvocation functionInvocation) {
		IDataBinding fInvocationDBinding = functionInvocation.getTarget().resolveDataBinding();
		if(IBinding.NOT_FOUND_BINDING != fInvocationDBinding && fInvocationDBinding != null &&
		   IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == fInvocationDBinding.getKind()) {

			FunctionResolver resolver = new FunctionResolver(compilerOptions);			
			OverloadedFunctionSet overloadedFunctionSet = (OverloadedFunctionSet) fInvocationDBinding;
			boolean isConstructor = functionInvocation.getTarget() instanceof ThisExpression;
			IDataBinding matchingFunction = resolver.findMatchingFunction(overloadedFunctionSet, getArgumentTypes(functionInvocation.getArguments()), getArgumentIsLiteralArray(functionInvocation.getArguments()), !isConstructor);
			
			if (isConstructor) {
				if(IBinding.NOT_FOUND_BINDING == matchingFunction || AmbiguousDataBinding.getInstance() == matchingFunction) {
					problemRequestor.acceptProblem(
						functionInvocation.getTarget(),
						IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
						new String[] {
							functionInvocation.getTarget().resolveTypeBinding().getCaseSensitiveName()
						});
	        		functionInvocation.getTarget().setDataBinding(IBinding.NOT_FOUND_BINDING);
	        		return;
				}
			}
			
			functionInvocation.getTarget().setAttributeOnName(Name.OVERLOADED_FUNCTION_SET, overloadedFunctionSet);
        	
        	if(AmbiguousDataBinding.getInstance() == matchingFunction) {
        		/*
        		 * If the type for at least one argument is null, an error message has already been issued.
        		 * Let the user take care of that one before putting out an error for multiple matching
        		 * overloaded functions. 
        		 */
        		boolean hasNullArgument = false;
        		for(Iterator iter = functionInvocation.getArguments().iterator(); iter.hasNext() && !hasNullArgument;) {
        			if(((Expression) iter.next()).resolveTypeBinding() == null) {
        				hasNullArgument = true;
        			}
        		}
        		if(!hasNullArgument) {
	        		problemRequestor.acceptProblem(
	        			functionInvocation.getTarget(),
	        			IProblemRequestor.MULTIPLE_OVERLOADED_FUNCTIONS_MATCH_ARGUMENTS,
	        			new String[] {
	        				fInvocationDBinding.getCaseSensitiveName()
	        			});
        		}
        		if(functionInvocation.getTarget().isName()) {
        			((Name) functionInvocation.getTarget()).setBinding(IBinding.NOT_FOUND_BINDING);
        		}
        		functionInvocation.getTarget().setDataBinding(IBinding.NOT_FOUND_BINDING);
        		functionInvocation.getTarget().setTypeBinding(null);
        	}
        	else {
        		if(functionInvocation.getTarget().isName()) {
        			((Name) functionInvocation.getTarget()).setBinding(matchingFunction);
        		}
        		functionInvocation.getTarget().setDataBinding(matchingFunction);
        		functionInvocation.getTarget().setTypeBinding(matchingFunction.getType());
        	}
        }
		
		ITypeBinding binding = functionInvocation.getTarget().resolveTypeBinding();
		if(binding != IBinding.NOT_FOUND_BINDING &&
		   binding != null &&
		   binding != AmbiguousFunctionBinding.getInstance() &&
		   (binding.isFunctionBinding() || ITypeBinding.DELEGATE_BINDING == binding.getKind())) {
			FunctionBinding functionBinding = binding.isFunctionBinding() ?
				(FunctionBinding) binding :
				createFunctionBindingFromDelegate((DelegateBinding) binding);
			ITypeBinding returnType = functionBinding.getReturnType();
			if(returnType == null) {
				// Error - function must return a type (unless this function
				// invocation is the invocation for a function invocation
				// statement, in which case it does not have to return a type)
				if(functionInvocation != functionInvocationFromFunctionInvocationStatement) {
					problemRequestor.acceptProblem(
						functionInvocation,
						IProblemRequestor.FUNCTION_MUST_RETURN_TYPE,
						new String[] {functionBinding.getName()});
				}
			}
			else {
				functionInvocation.setTypeBinding(returnType);
				dependencyRequestor.recordTypeBinding(returnType);
			}
			
			IPartBinding functionContainerBinding = currentScope.getPartBinding();
			
			checkSystemFunctionUsedInCorrectLocation(functionBinding, functionInvocation, functionContainerBinding);				
			
			functionInvocation.accept(new FunctionArgumentValidator(functionBinding, functionContainerBinding, problemRequestor, compilerOptions));
		}
	}
	
	private FunctionBinding createFunctionBindingFromDelegate(DelegateBinding binding) {
		FunctionBinding result = new FunctionBinding(binding.getCaseSensitiveName(), binding);
		result.setPrivate(binding.isPrivate());
		result.setReturnType(binding.getReturnType());
		result.setReturnTypeIsSqlNullable(binding.returnTypeIsSqlNullable());
		for(Iterator iter = binding.getParemeters().iterator(); iter.hasNext();) {
			result.addParameter((FunctionParameterBinding) iter.next());
		}
		return result;
	}
	
	private static final Set systemFunctionsOnlyAllowedInPageHandlers = new HashSet();
	static {
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "CLEARREQUESTATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "CLEARSESSIONATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "GETREQUESTATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "GETSESSIONATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "SETREQUESTATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "SETSESSIONATTR"));
	}
	
	private static final Set systemFunctionsNotAllowedInPageHandlers = new HashSet();
	static {
		systemFunctionsNotAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "ui", "console"}, "ConsoleLib", "DISPLAYLINEMODE"));
	}
	
	private static final Set systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation = new HashSet();
	static {
		systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "io", "dli"}, "DLILib", "AIBTDLI"));
		systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "io", "dli"}, "DLILib", "EGLTDLI"));
		systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "vg"}, "VGLib", "VGTDLI"));
	}
	
	private static final Set systemFunctionsNotAllowedInServices = new HashSet();
	static {
		systemFunctionsNotAllowedInServices.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "setError"));		
	}
	
	private void checkSystemFunctionUsedInCorrectLocation(IFunctionBinding functionBinding, FunctionInvocation functionInvocation, IPartBinding functionContainerBinding) {
		FunctionArgumentValidator.FunctionIdentifier fIdentifier = new FunctionArgumentValidator.FunctionIdentifier(functionBinding);
		if(functionContainerBinding == null || functionContainerBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, "JSFHandler") == null) {
			if (AbstractBinder.typeIs(functionBinding.getDeclarer(),new String[] {"egl", "java"}, "PortalLib"))	{
				problemRequestor.acceptProblem(
						functionInvocation.getTarget(),
						IProblemRequestor.INVALID_PAGEHANDLER_SYSTEM_FUNCTION_USAGE,
						new String[] {functionBinding.getCaseSensitiveName()});
				return;
			}
		}
		
		if(systemFunctionsNotAllowedInPageHandlers.contains(fIdentifier)) {
			if(functionContainerBinding != null && functionContainerBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, "JSFHandler") != null) {
				problemRequestor.acceptProblem(
					functionInvocation.getTarget(),
					IProblemRequestor.SYSTEM_FUNCTION_NOT_ALLOWED_IN_PAGEHANDLER,
					new String[] {functionBinding.getCaseSensitiveName()});
			}
		}
		else if(systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.contains(fIdentifier)) {
			IAnnotationBinding dliAnnotation = null;

			if(functionContainerBinding != null) {
				dliAnnotation = functionContainerBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLI");
			}
			
			if(dliAnnotation == null) {
				problemRequestor.acceptProblem(
					functionInvocation.getTarget(),
					IProblemRequestor.DLI_IO_ONLY_ALLOWED_IN_PROGRAM_WITH_DLI);
			}
			else {
				IAnnotationBinding callInterfaceAnnotation = (IAnnotationBinding) dliAnnotation.findData(InternUtil.intern(IEGLConstants.PROPERTY_CALLINTERFACE));
				if(callInterfaceAnnotation == IBinding.NOT_FOUND_BINDING || InternUtil.intern("AIBTDLI") == ((EnumerationDataBinding) callInterfaceAnnotation.getValue()).getName()) {
					if(!functionBindingIs(functionBinding, new String[] {"egl", "io", "dli"}, "DLILib", "AIBTDLI")) {
						problemRequestor.acceptProblem(
							functionInvocation.getTarget(),
							IProblemRequestor.DLI_SYSTEM_FUNCTION_NOT_ALLOWED_WITH_AIBTDLI_INTERFACE,
							new String[] {functionBinding.getCaseSensitiveName()});
					}
				}
			}
		}
		else if(systemFunctionsNotAllowedInServices.contains(fIdentifier)) {
			if(functionContainerBinding != null && ITypeBinding.SERVICE_BINDING == functionContainerBinding.getKind()) {
				problemRequestor.acceptProblem(
					functionInvocation.getTarget(),
					IProblemRequestor.SYSTEM_FUNCTION_NOT_ALLOWED_IN_SERVICE,
					new String[] {functionBinding.getCaseSensitiveName()});
			}
		}
	}
	
	public void endVisit(MoveStatement moveStatement) {
		//When arrays or multiply occuring items are used as targets of the
		//move statement with either the 'for <index>' or 'for all' modifiers
		//and an array index is specified, the array index is providing a
		//"starting index" for the move -- the target expression is not a
		//normal array access, in other words. If these conditions are met,
		//the following code sets the type binding for the array access to
		//the 'array' part of the array access.
		MoveModifier modifier = moveStatement.getMoveModifierOpt();
		if(modifier != null) {
			if(modifier.isFor() || modifier.isForAll()) {
				moveStatement.getTarget().accept(new DefaultASTVisitor() {
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}
					public boolean visit(ArrayAccess arrayAccess) {
						List indices = arrayAccess.getIndices();
						if(indices.size() == 1) {
							ITypeBinding indexType = ((Expression) indices.get(0)).resolveTypeBinding();
							if(indexType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == indexType.getKind()) {
								if(Primitive.isNumericType(((PrimitiveTypeBinding) indexType).getPrimitive())) {
									ITypeBinding arrayType = arrayAccess.getArray().resolveTypeBinding();
									if(arrayType != null) {
										arrayAccess.setTypeBinding(										
											compilerOptions.isVAGCompatible() && ITypeBinding.ARRAY_TYPE_BINDING != arrayType.getKind() ?
												new MultiplyOccuringItemTypeBinding(arrayType) :
												arrayType);
									}
								}
							}
						}
						return false;
					}
				});
			}
		}
	}
	
	public void endVisit(final CloseStatement closeStatement) {
		closeStatement.accept(new AbstractASTExpressionVisitor(){
			public boolean visitExpression(final Expression expression) {
				ITypeBinding typeBinding = expression.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)){

					if (typeBinding.getAnnotation(new String[] {"egl", "ui", "text"}, "PrintForm") != null){
						Scope scope = currentScope;
						while (scope != null){
							if (scope.isProgramScope()){
								break;
							}
							scope = scope.getParentScope();
						}
						
						if (scope != null){
							ProgramBinding binding = ((ProgramScope)scope).getProgramBinding();
							if (binding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGWebTransaction") != null){
								problemRequestor.acceptProblem(
										expression ,
										IProblemRequestor.CLOSE_PRINTFORM_NOT_ALLOWED_IN_WEB_TRANSACTION);
							}
							
						}
					}
				}
				return false;
			}
		});
	}

	public void endVisit(SetValuesExpression setValuesExpression) {		
		setValuesExpression.setTypeBinding(setValuesExpression.getExpression().resolveTypeBinding());
		
		setValuesExpression.getSettingsBlock().accept(new DefaultASTVisitor() {
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(Assignment assignment) {
				validateAssignment(assignment, false);
				return false;
			}
		});
		
		ITypeBinding type = setValuesExpression.getExpression().resolveTypeBinding();
		if (Binding.isValidBinding(type) && (type.getKind() == ITypeBinding.FUNCTION_BINDING || type.getKind() == ITypeBinding.DELEGATE_BINDING)) {
			problemRequestor.acceptProblem(
					setValuesExpression.getSettingsBlock(),
					IProblemRequestor.SETTINGS_BLOCK_NOT_ALLOWED,
					new String[] {});
		}
	}
			
	protected Object resolveValueRef(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {
		if (!(value instanceof String)) {
	        return value;
	    }
		
		IPartBinding partBinding = varBinding.getDeclaringPart();
		if(partBinding == null || ITypeBinding.FIXED_RECORD_BINDING != partBinding.getKind()) {
			return value;
		}

	    String selectedIndexName = (String) value;
	    Name name = new ExpressionParser(compilerOptions).parseAsName(selectedIndexName);
	    if (name == null) {
	        return IBinding.NOT_FOUND_BINDING;
	    }
	    
	    Object result = ((FixedRecordBinding) partBinding).getSimpleNamesToDataBindingsMap().get(name.getIdentifier());
	    
	    if(result == null) {
	    	result = IBinding.NOT_FOUND_BINDING;
	        problemRequestor.acceptProblem(
	        	node,
				IProblemRequestor.VALUEREF_NOT_RESOLVED,
				new String[] {
	        		name.getCanonicalName()
	        	});
	    }

	    return result;
	}
	    
    protected IDataBinding[] addElement(IDataBinding newElement, IDataBinding[] array, IDataBinding dataBinding) {
        if (array == null) {
            if (newElement == dataBinding) {
                return null;
            }
            return new IDataBinding[] { newElement };
        }
        if (newElement == array[array.length - 1]) {
            return array;
        }
        if (newElement.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING
                && array[array.length - 1].getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
            array[array.length - 1] = newElement;
            return array;
        }

        IDataBinding[] newArray = new IDataBinding[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newElement;
        return newArray;
    }

    protected Object resolveRedefines(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String redefinesName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(redefinesName);
        if (name == null) {
            problemRequestor.acceptProblem(node, IProblemRequestor.REDEFINES_MUST_BE_DECLARATION, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_REDEFINES});
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        try {
            bindExpressionName(name);
            result = name.resolveDataBinding();
            //TODO validate the attributes of the redefines record
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(node, IProblemRequestor.REDEFINES_MUST_BE_DECLARATION, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_REDEFINES});
            result = IBinding.NOT_FOUND_BINDING;
        }
        return result;
    }
    
    protected Object resolveFunctionMemberRef(Node node, IAnnotationBinding aBinding, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String functionName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(functionName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result = null;
        boolean resultValid = true;
        try {
            bindExpressionName(name);
            IDataBinding dataBinding = name.resolveDataBinding();
			result = dataBinding;
            resultValid = IDataBinding.NESTED_FUNCTION_BINDING == dataBinding.getKind();
        } catch (ResolutionException e) {
        	resultValid = false;
        }
        
        if(!resultValid) {
	        problemRequestor.acceptProblem(
            	node,
				IProblemRequestor.PROPERTY_DOESNT_RESOLVE,
				new String[] {
            		functionName,
					aBinding.getCaseSensitiveName(),
					varBinding.getCaseSensitiveName()
            	});
	        result = IBinding.NOT_FOUND_BINDING;
        }
        
        return result;
    }
    
    protected Object resolveBinding(final Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String bindingName = (String) value;
        boolean resultIsValid = false;
        IDataBinding result;

        Expression expr = new ExpressionParser(compilerOptions).parseAsLvalue(bindingName);
        if (expr == null) {
            result = IBinding.NOT_FOUND_BINDING;
        }
        else {
        	final IProblemRequestor oldProblemRequestor = problemRequestor;
        	problemRequestor = new DefaultProblemRequestor() {
				public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
			 		if (severity == IMarker.SEVERITY_ERROR) {
			 			setHasError(true);
			 		}
					oldProblemRequestor.acceptProblem(node, problemKind, severity, inserts);
				}    
				public boolean shouldReportProblem(int problemKind) {
					return oldProblemRequestor.shouldReportProblem(problemKind);
				}

        	};
	        expr.accept(this);
	        problemRequestor = oldProblemRequestor;
	        
	        resultIsValid = true;
	        
	        result = expr.resolveDataBinding();
	        if(result == null) {
	        	result = IBinding.NOT_FOUND_BINDING;
	        }	        
        }
        
        if(!resultIsValid) {
        	problemRequestor.acceptProblem(
        		node,
				IProblemRequestor.VARIABLE_NOT_FOUND,
				new String[] {value.toString()});
        }
        
        return result;
    }

    protected boolean isUIRecordItem(IDataBinding dataBinding, IDataBinding[] path) {
        if (path == null || path.length < 2) {
            return dataBinding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord") != null;
        }
        IDataBinding[] newPath = new IDataBinding[path.length - 1];
        System.arraycopy(path, 0, newPath, 0, newPath.length);
        return dataBinding.getAnnotationFor(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord", newPath) != null;
    }

	public void endVisit(ClassDataDeclaration classDataDeclaration) {
		Type type = classDataDeclaration.getType();
		if (type != null){
			while (type.isArrayType()){
				type = ((ArrayType)type).getElementType();
			}
			if (type.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)type,problemRequestor,compilerOptions);
			}
		}
		
		if (classDataDeclaration.hasInitializer()) {
			if (classDataDeclaration.hasSettingsBlock()) {
				issueErrorForInitialization(classDataDeclaration.getSettingsBlockOpt(), ((Name)classDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
			}
		}
		else {
			ITypeBinding tBinding = classDataDeclaration.getType().resolveTypeBinding();
			//Non-nullable reference types must be instantiable, because they are initialized with the default constructor
			if (Binding.isValidBinding(tBinding) && !tBinding.isNullable() && tBinding.isReference() && !tBinding.isInstantiable() && currentScope.getPartBinding() != tBinding) {
				//Don't need to throw error if the field is in an ExternalType
				if (Binding.isValidBinding(currentScope.getPartBinding()) && currentScope.getPartBinding().getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
						new String[] {type.getCanonicalName()});
				}
			}
			
			//nullable types cannot specify a settings block that contains settings for data in the field
			if (classDataDeclaration.hasSettingsBlock() && Binding.isValidBinding(tBinding) && tBinding.isNullable()) {
				issueErrorForInitialization(classDataDeclaration.getSettingsBlockOpt(), ((Name)classDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
			}
		}
		
		IPartBinding partBinding = currentScope.getPartBinding();
		if (StatementValidator.isValidBinding(partBinding) && partBinding.getKind() == ITypeBinding.LIBRARY_BINDING){
			if (partBinding.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null){
				boolean error = false;
				if (type.isPrimitiveType()){
					error = true;
				}else if (type.isNameType()){
					Name name = ((NameType)type).getName();
					IBinding binding = name.resolveBinding();
					ITypeBinding typeBinding = null;
					if(IBinding.NOT_FOUND_BINDING != binding) {
						if (binding.isDataBinding()){
							error = true;
						}else if (binding.isTypeBinding()){
							typeBinding = (ITypeBinding)binding;
							if (typeBinding.getKind() != ITypeBinding.SERVICE_BINDING &&
									typeBinding.getKind() != ITypeBinding.INTERFACE_BINDING){
								error = true;
							}
						}
					}
				}
				if (error){				
					String name = ((Expression)classDataDeclaration.getNames().get(0)).getCanonicalString();
					problemRequestor.acceptProblem(classDataDeclaration,
							classDataDeclaration.isConstant()? IProblemRequestor.NATIVE_LIBRARYS_DO_NOT_SUPPORT_CONSTANT_DECLARATIONS : IProblemRequestor.NATIVE_LIBRARYS_DO_NOT_SUPPORT_DECLARATIONS,
						new String[] {name,partBinding.getCaseSensitiveName()});
				}
			}
		}
		if (classDataDeclaration.hasSettingsBlock()) {
			IDataBinding dbinding = ((Expression)classDataDeclaration.getNames().get(0)).resolveDataBinding();
			if (Binding.isValidBinding(dbinding)) {
				issueErrorForPropertyOverrides(dbinding, classDataDeclaration.getSettingsBlockOpt());
			}
		}
	}
	
	public void endVisit(final FunctionDataDeclaration functionDataDeclaration) {
		Type type = functionDataDeclaration.getType();
		if (type != null){	
			if (type.isNameType()){
				IBinding binding = ((NameType)type).getName().resolveBinding();
				if (StatementValidator.isValidBinding(binding) && binding.isTypeBinding() && ((ITypeBinding)binding).getKind() == ITypeBinding.ARRAYDICTIONARY_BINDING){
					if (functionDataDeclaration.hasSettingsBlock()){
						functionDataDeclaration.getSettingsBlockOpt().accept(new AbstractASTExpressionVisitor(){
							int initSize = -1;
						    public boolean visit(NewExpression newExpression) {
						    	if (newExpression.getType().isArrayType()){
						    		Expression initsizeexpr = ((ArrayType)newExpression.getType()).getInitialSize();
						    		if (initsizeexpr != null){
						    			initsizeexpr.accept(new AbstractASTExpressionVisitor(){
						    				 public boolean visit(IntegerLiteral integerLiteral) {
						    				 	int size = Integer.valueOf(integerLiteral.getValue()).intValue();
						    				 	if (initSize != -1 && initSize != size){
						    				 		problemRequestor.acceptProblem(functionDataDeclaration,
						    				 				IProblemRequestor.ARRAY_DICTIONARY_HAS_INVALID_COLUMNS);
						    				 	}
						    				 	
						    				 	initSize = size;
						    				 	
						    				 	return false;
						    				 }
						    			});
						    		}
						    		
						    	}
						        return false;
						    }
						});
					}
				}
			}
			while (type.isArrayType()){
				type = ((ArrayType)type).getElementType();
			}
			if (type.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)type,problemRequestor,compilerOptions);
			}

			if (functionDataDeclaration.hasInitializer()) {
				if (functionDataDeclaration.hasSettingsBlock()) {
					issueErrorForInitialization(functionDataDeclaration.getSettingsBlockOpt(), ((Name)functionDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
				}
			}
			else {
				ITypeBinding tBinding = functionDataDeclaration.getType().resolveTypeBinding();
				//Non-nullable reference types must be instantiable, because they are initialized with the default constructor
				if (Binding.isValidBinding(tBinding) && !tBinding.isNullable() && tBinding.isReference() && !tBinding.isInstantiable() && currentScope.getPartBinding() != tBinding) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
						new String[] {type.getCanonicalName()});
				}
				//nullable types cannot specify a settings block that contains settings for data in the field
				if (functionDataDeclaration.hasSettingsBlock() && Binding.isValidBinding(tBinding) && tBinding.isNullable()) {
					issueErrorForInitialization(functionDataDeclaration.getSettingsBlockOpt(), ((Name)functionDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
				}
			}

		}
		if (functionDataDeclaration.hasSettingsBlock()) {
			IDataBinding dbinding = ((Expression)functionDataDeclaration.getNames().get(0)).resolveDataBinding();
			if (Binding.isValidBinding(dbinding)) {
				issueErrorForPropertyOverrides(dbinding, functionDataDeclaration.getSettingsBlockOpt());
			}
		}

	}
	
	public void endVisit(FunctionParameter functionParameter) {
		Type type = functionParameter.getType();
		if (type != null){		
			if (type.isArrayType()){
				String name = functionParameter.getName().getCanonicalName();
				StatementValidator.validateRecordParamDimensions(type,problemRequestor,functionParameter,name);
				type = ((ArrayType)type).getElementType();
			}
			if (type.isPrimitiveType()){
				PrimitiveTypeValidator.validateParamInFunction((PrimitiveType)type,problemRequestor,compilerOptions);
			}
		}
	}
	
	public void endVisit(ProgramParameter programParameter) {
		Type type = programParameter.getType();
		if (type != null){		
			if (type.isArrayType()){
				String name = programParameter.getName().getCanonicalName();
				StatementValidator.validateRecordParamDimensions(type,problemRequestor,programParameter,name);				
			}
			
			Type baseType = type.getBaseType();
			if (baseType.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)baseType,problemRequestor,compilerOptions);
			}
			
			if (type.isNameType()){
				if (InternUtil.intern(((NameType)type).getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.DB_PCBRECORD_STRING)){
					if (this.getDLIAnnotation() == null){
						problemRequestor.acceptProblem(programParameter,
								IProblemRequestor.DLI_PCB_PARAMETER_REQUIRES_DLI_PROPERTY_ON_PROGRAM);
					}
				}
			}
		}
	}
	
	public void endVisit(StructureItem structureItem) {
		Type type = structureItem.getType();
		if (type != null){
			Type baseType = type.getBaseType();
			if (baseType.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)baseType,problemRequestor,compilerOptions);
			}

			if (structureItem.hasInitializer()) {
				if (structureItem.hasSettingsBlock()) {
					if (structureItem.getName() != null) {
						issueErrorForInitialization(structureItem.getSettingsBlock(), structureItem.getName().getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
					}
				}
			}
			if (!structureItem.hasInitializer()) {
				ITypeBinding tBinding = type.resolveTypeBinding();
				//Non-nullable reference types must be instantiable, because they are initialized with the default constructor
				if (Binding.isValidBinding(tBinding) && !tBinding.isNullable() && tBinding.isReference() && !tBinding.isInstantiable() && currentScope.getPartBinding() != tBinding) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
						new String[] {type.getCanonicalName()});
				}
				//nullable types cannot specify a settings block that contains settings for data in the field
				if (structureItem.hasSettingsBlock() && Binding.isValidBinding(tBinding) && tBinding.isNullable() && structureItem.getName() != null) {
					issueErrorForInitialization(structureItem.getSettingsBlock(), structureItem.getName().getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
				}
			}
		}

		if (structureItem.hasSettingsBlock() && structureItem.getName() != null) {
			IDataBinding dbinding = structureItem.getName().resolveDataBinding();
			if (Binding.isValidBinding(dbinding)) {
				issueErrorForPropertyOverrides(dbinding, structureItem.getSettingsBlock());
			}
		}
	}	
	
	public void endVisit(ReturnsDeclaration returnStatement) {
		Type type = returnStatement.getType();
		if (type != null){		
			Type baseType = type.getBaseType();
			if (baseType.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)baseType,problemRequestor,compilerOptions);
			}
		}
	}	
	
	public void endVisit(CallStatement callStatement) {
		if(callStatement.hasArguments()) {
			VAGenResolutionWarningsValidator val = new VAGenResolutionWarningsValidator(problemRequestor, compilerOptions);
			for(Iterator iter = callStatement.getArguments().iterator(); iter.hasNext();) {
				val.checkItemRecordNameOverlap(currentScope, (Expression) iter.next());
			}
		}
	}
	
		            
    private boolean isArray(IDataBinding db) {
    	if(IDataBinding.STRUCTURE_ITEM_BINDING == db.getKind()) {
    		StructureItemBinding sItemBinding = (StructureItemBinding) db;
    		if(ITypeBinding.DATATABLE_BINDING == sItemBinding.getEnclosingStructureBinding().getKind() ||
    		   sItemBinding.isMultiplyOccuring()) {
    			return true;
    		}
    	}
    	ITypeBinding tBinding = db.getType();
    	return tBinding == null || ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind();
    }

    private Object resolveMQRecordProperty(Node node, Object value, IDataBinding varBinding, IDataBinding[] path, String annotationType) {

        if (!(value instanceof String)) {
            return value;
        }

        String propName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(propName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        try {
            bindExpressionName(name);
            result = name;
            if (!isBasicRecordReferece(name.resolveDataBinding())) {
                problemRequestor.acceptProblem(node, IProblemRequestor.PROPERTY_MUST_BE_BASIC_RECORD, IMarker.SEVERITY_ERROR, new String[] {propName, annotationType});
            }
        } catch (ResolutionException e) {
        	if(IProblemRequestor.VARIABLE_NOT_FOUND == e.getProblemKind()) {
	            problemRequestor.acceptProblem(
	            	node,
					IProblemRequestor.PROPERTY_DOESNT_RESOLVE,
					new String[] {
						propName,
						annotationType,
						varBinding.getName()
	            	});
        	}
        	else if(IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS == e.getProblemKind()) {
        		problemRequestor.acceptProblem(
	            	node,
					IProblemRequestor.PROPERTY_AMBIGUOUS,
					new String[] {
						propName,
						annotationType,
						varBinding.getName()
	            	});
        	}
        	else {
        		problemRequestor.acceptProblem(
	            	node,
					e.getProblemKind(),
					IMarker.SEVERITY_ERROR,
					e.getInserts());
        	}
            result = IBinding.NOT_FOUND_BINDING;
        }
        return result;
    }
    
    protected boolean isPageHandler() {
        //Override this in the Binder for pageHandler
        return false;
    }
    
    private boolean isBasicRecordReferece(IDataBinding dataBinding) {
    	if (dataBinding.getType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
    		return false;
    	}
        return dataBinding.getAnnotation(new String[] {"egl", "core"}, "BasicRecord") != null;
    }
    
    private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
    	if (!Binding.isValidBinding(aBinding)) {
    		return null;
    	}
    	
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
    
    
    private void issueErrorForPropertyOverrides(IDataBinding fieldBinding, SettingsBlock settings) {
    	if (!(fieldBinding instanceof DataBinding) || ((DataBinding)fieldBinding).getPropertyOverrides().isEmpty()) {
    		return;
    	}
    	
    	final Stack<IDataBinding> stack = new Stack();
    	stack.push(fieldBinding);
		final Node[] errorNode = new Node[1];
   	
    	settings.accept(new AbstractASTExpressionVisitor() {
    		public boolean visit(Assignment assignment) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			//if this is an annotation assignment
    			if (Binding.isValidBinding(assignment.resolveBinding())) {
    				if (stack.size() > 1) {
    					errorNode[0] = assignment;
    				}
    			}
    			return false;
    		}
    		
    		public boolean visit(AnnotationExpression annotationExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
				if (stack.size() > 1) {
					errorNode[0] = annotationExpression;
				}
				return false;
    		}
    		
    		public boolean visit(SetValuesExpression setValuesExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (Binding.isValidBinding(dBinding)) {					
					if (dBinding.getKind() == IDataBinding.ANNOTATION_BINDING) {
						if (stack.size() > 1) {
							errorNode[0] = setValuesExpression.getExpression();
						}
					}
					else {
						stack.push(dBinding);
						setValuesExpression.getSettingsBlock().accept(this);
					}
				}
				return false; 			
    		}
    		
    		public void endVisit(SetValuesExpression setValuesExpression) {
    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (Binding.isValidBinding(dBinding)) {					
					if (dBinding.getKind() == IDataBinding.ANNOTATION_BINDING) {}
					else {
						stack.pop();
					}
				}
    		}
    		
    		public boolean visitExpression(Expression expression) {
    			return false;
    		}
    		
    	});
		problemRequestor.acceptProblem(errorNode[0], IProblemRequestor.PROPERTY_OVERRIDES_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {fieldBinding.getCaseSensitiveName()});
    }
    
    private void issueErrorForInitialization(SettingsBlock settings, final String fieldName, int errorNo) {
		final Node[] errorNode = new Node[1];
    	settings.accept(new AbstractASTExpressionVisitor() {
    		public boolean visit(Assignment assignment) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			//check if it was an annotation type assignment
    			if (assignment.resolveBinding() == null) {
    				IDataBinding dBinding = assignment.getLeftHandSide().resolveDataBinding();
    				if (Binding.isValidBinding(dBinding) && (dBinding.getKind() != IDataBinding.ANNOTATION_BINDING)) {
    	        		errorNode[0] = assignment;
    				}
    			}
    			return false;
    		}
    		
    		public boolean visit(AnnotationExpression annotationExpression) {
    			return false;
    		}
    		
    		public boolean visit(SetValuesExpression setValuesExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (Binding.isValidBinding(dBinding) && (dBinding.getKind() != IDataBinding.ANNOTATION_BINDING)) {
					setValuesExpression.getSettingsBlock().accept(this);
				}
				return false; 			
    		}
    		
    		public boolean visitExpression(Expression expression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			errorNode[0] = expression;
    			return false;
    		}
    		
    	});
    	if (errorNode[0] != null) {
    		problemRequestor.acceptProblem(errorNode[0], errorNo, IMarker.SEVERITY_ERROR, new String[] {fieldName});
    	}
    }
 }
