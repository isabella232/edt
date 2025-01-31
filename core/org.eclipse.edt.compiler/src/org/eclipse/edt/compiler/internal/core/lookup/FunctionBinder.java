/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhenClause;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Part;


/**
 * @author winghong
 */
public class FunctionBinder extends DefaultBinder {

    private FunctionMember functionBinding;

    private Part partBinding;
    
    private List<FunctionDataDeclaration> functionDataDecls = new ArrayList<FunctionDataDeclaration>();
    
    private String canonicalFunctionName;
    
    public FunctionBinder(Part partBinding, FunctionMember functionBinding, Scope scope,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.partBinding = partBinding;
        this.functionBinding = functionBinding;
    }
    
    @Override
    public boolean visit(NestedFunction function) {
    	canonicalFunctionName = function.getName().getCanonicalName();
    	
        FunctionScope functionScope = new FunctionScope((FunctionContainerScope) currentScope, functionBinding);
        currentScope = functionScope;
        
        // For nested functions, the functionBinding passed in should already
        // have been completed by the FunctionContainerBinder
        for (org.eclipse.edt.mof.egl.FunctionParameter parm : functionBinding.getParameters()) {
        	functionScope.addDeclaredDataName(parm.getName());
        }

        return true;
    }
    
    @Override
    public boolean visit(Constructor constructor) {
    	canonicalFunctionName = IEGLConstants.KEYWORD_CONSTRUCTOR;
    	
        FunctionScope functionScope = new FunctionScope((FunctionContainerScope) currentScope, functionBinding);
        currentScope = functionScope;
        
        // For constructors, the constructor passed in should already
        // have been completed by the FunctionContainerBinder
        for (org.eclipse.edt.mof.egl.FunctionParameter parm : functionBinding.getParameters()) {
        	functionScope.addDeclaredDataName(parm.getName());
        }

        return true;
    }

    @Override
    public boolean visit(FunctionParameter functionParameter) {
        return false;
    }
    
    @Override
    public boolean visit(FunctionDataDeclaration dataDecl) {
    	functionDataDecls.add(dataDecl);
    	processDataDeclaration(dataDecl.getNames(), dataDecl.getType(), dataDecl.isNullable(), dataDecl.getSettingsBlockOpt(), dataDecl.isConstant(), dataDecl.getInitializer());
    	return true;
    }
    
    private void processDataDeclaration(List<Name> names, Type type, boolean isNullable, SettingsBlock settingsBlock, boolean isConstantDeclaration, Expression initializer) {

        org.eclipse.edt.mof.egl.Type typeBinding = null;
        try {
            typeBinding = bindType(type);
            
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
                    .getInserts());
            if(settingsBlock != null) {
            	AbstractBinder.setBindAttemptedForNames(settingsBlock);
            }
            for(Name name : names) {
            	name.setBindAttempted(true);
            }
            return; // Do not create the class field bindings if the
            // type cannot be resolved
        }
    	        
        for (Name name : names) {
            String dataName = name.getIdentifier();
            
            Field field;
            if (isConstantDeclaration) {
            	field = IrFactory.INSTANCE.createConstantField();
            }
            else {
            	field = IrFactory.INSTANCE.createField();
            }

            field.setContainer(functionBinding);
            field.setType(typeBinding);
            field.setIsNullable(isNullable);
            field.setName(name.getCaseSensitiveIdentifier());
            
        	if (((ILocalVariableScope) currentScope).hasDeclaredDataName(dataName)) {
                problemRequestor.acceptProblem(name, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] {
                        name.getCanonicalName(), canonicalFunctionName });
            }
        	else {            	
        		((ILocalVariableScope) currentScope).addLocalVariable(field);
        		((ILocalVariableScope) currentScope).addDeclaredDataName(dataName);
        	}
            
            name.setMember(field);
            name.setType(typeBinding);
            
            if (settingsBlock != null) {
                Scope scope = new MemberScope(NullScope.INSTANCE, field);
                AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(scope, field, typeBinding, field);
                settingsBlock.accept(
                        new SettingsBlockAnnotationBindingsCompletor(currentScope, partBinding, annotationScope,
                                dependencyRequestor, problemRequestor, compilerOptions));
            }
        }
    }
    
    @Override
    public void endVisit(NestedFunction nestedFunction) {
    }
    
    @Override
    public boolean visit(SetValuesExpression setValuesExpression) {
        setValuesExpression.getExpression().accept(this);
        final org.eclipse.edt.mof.egl.Type type = setValuesExpression.getExpression().resolveType();
        if (type != null) {
            TypeScope newScope = new TypeScope(NullScope.INSTANCE, type);
            setValuesExpression.getSettingsBlock().accept(
                    new SetValuesExpressionCompletor(currentScope, (Part)functionBinding.getContainer(), newScope, dependencyRequestor, problemRequestor, compilerOptions));
        } else if (setValuesExpression.getExpression().resolveElement() != null) {
            setValuesExpression.getSettingsBlock().accept(this);
        }
        return false;
    }
    
    @Override
    public boolean visit(CallStatement callStatement) {
        
		bindInvocationTarget(callStatement.getInvocationTarget());
		
		if(callStatement.hasArguments()) {
			for(Node node : callStatement.getArguments()) {
				node.accept(this);
			}
		}
		
		if(callStatement.hasSettingsBlock()) {
			callStatement.getSettingsBlock().accept(this);
		}
		
		if (callStatement.getUsing() != null) {
			callStatement.getUsing().accept(this);
		}
		
		if (callStatement.getCallSynchronizationValues() != null) {
			if (callStatement.getCallSynchronizationValues().getReturns() != null) {
				callStatement.getCallSynchronizationValues().getReturns().accept(this);
			}
			
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				callStatement.getCallSynchronizationValues().getReturnTo().accept(this);
			}

			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				callStatement.getCallSynchronizationValues().getOnException().accept(this);
			}
		}
				
		return false;
    }
    
    @Override
    public boolean visit(ExitStatement exitStatement) {
        
        if (exitStatement.getExitModifierOpt() != null) {
        	exitStatement.getExitModifierOpt().accept(this);
        }
		
		if(exitStatement.hasSettingsBlock()) {
			exitStatement.getSettingsBlock().accept(this);
		}
		
		return false;
    }
    
    private void visitStatementBlocks(Statement statement) {
    	for(List<Node> block : statement.getStatementBlocks()) {
			currentScope = new StatementBlockScope(currentScope);
			for(Node node : block) {
				node.accept(this);
			}
			currentScope = currentScope.getParentScope();
		}
    }

    @Override
    public boolean visit(IfStatement ifStatement) {
    	ifStatement.getCondition().accept(this);
    	visitStatementBlocks(ifStatement);
    	return false;
    }
    
    @Override
	public boolean visit(CaseStatement caseStatement) {
		if(caseStatement.hasCriterion()) {
			Expression expr = caseStatement.getCriterion();
			expr.accept(this);
		}
		
		for(WhenClause whenClause : caseStatement.getWhenClauses()) {
			for(Expression expr : whenClause.getExpr_plus()) {
				expr.accept(this);
			}
		}
		visitStatementBlocks(caseStatement);
		return false;
	}
    
    @Override
    public boolean visit(ForStatement forStatement) {
    	currentScope = new StatementBlockScope(currentScope);
    	
    	if(forStatement.hasVariableDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {forStatement.getVariableDeclarationName()}),
    			forStatement.getVariableDeclarationType(),
    			forStatement.isNullable(),
    			null,
    			false,
    			null);
    	}
    	else {
    		forStatement.getCounterVariable().accept(this);	
    	}
    	
    	if(forStatement.hasFromIndex()) {
    		forStatement.getFromIndex().accept(this);
    	}
		
    	forStatement.getEndIndex().accept(this);
		
		if(forStatement.hasPositiveDelta() || forStatement.hasNegativeDelta()) {
			forStatement.getDeltaExpression().accept(this);
		}
		
		for(Node node : forStatement.getStmts()) {
    		node.accept(this);
    	}
		
    	currentScope = currentScope.getParentScope();
    	
    	return false;
    }
    
    @Override
    public boolean visit(ForEachStatement forEachStatement) {
    	currentScope = new StatementBlockScope(currentScope);
    	
    	if(forEachStatement.hasVariableDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {forEachStatement.getVariableDeclarationName()}),
    			forEachStatement.getVariableDeclarationType(),
    			forEachStatement.isNullable(),
    			null,
    			false,
    			null);
    	}
    	else {
	    	for (Node n : (List<Node>)forEachStatement.getTargets()) {
	    		n.accept(this);
	    	}
    	}
    	
    	forEachStatement.getResultSet().getExpression().accept(this);
    	
    	for(Node node : forEachStatement.getStmts()) {
    		node.accept(this);
    	}
    	
    	currentScope = currentScope.getParentScope();
    	
    	return false;
    }
    
    @Override
    public boolean visit(WhileStatement whileStatement) {
    	whileStatement.getExpr().accept(this);    	
    	visitStatementBlocks(whileStatement);
    	return false;
    }
    
    @Override
    public boolean visit(TryStatement tryStatement) {
    	currentScope = new StatementBlockScope(currentScope);
    	for(Node node : tryStatement.getStmts()) {
    		node.accept(this);
    	}
    	currentScope = currentScope.getParentScope();
    	
    	for(Node node : tryStatement.getOnExceptionBlocks()) {
    		node.accept(this);
    	}
    	return false;
    }
    
    public boolean visit(OnExceptionBlock onExceptionBlock) {
    	currentScope = new StatementBlockScope(currentScope);
		processDataDeclaration(
				Arrays.asList(new Name[] {onExceptionBlock.getExceptionName()}),
				onExceptionBlock.getExceptionType(),
				onExceptionBlock.isNullable(),
				null,
				false,
				null);
    	
    	for(Node node : onExceptionBlock.getStmts()) {
    		node.accept(this);
    	}
    	currentScope = currentScope.getParentScope();
    	return false;
    }
}
