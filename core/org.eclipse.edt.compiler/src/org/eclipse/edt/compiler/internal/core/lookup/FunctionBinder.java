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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.compiler.binding.FunctionBindingCompletor;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
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
import org.eclipse.edt.compiler.core.ast.OnEventBlock;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhenClause;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;


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


    public boolean visit(TopLevelFunction function) {
    	canonicalFunctionName = function.getName().getCanonicalName();
    	
        function.accept(new FunctionBindingCompletor(partBinding, currentScope, functionBinding, dependencyRequestor,
                problemRequestor, compilerOptions));

        FunctionScope functionScope = new FunctionScope(currentScope, functionBinding);
        currentScope = functionScope;
        
        for (org.eclipse.edt.mof.egl.FunctionParameter parm : functionBinding.getParameters()) {
        	functionScope.addDeclaredDataName(parm.getName());
        }
        

        return true;
    }
    
    public boolean visit(FunctionParameter functionParameter) {
        return false;
    }
    
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
    	        
        PrimitiveTypeLiteral constantValue = null;
        if (isConstantDeclaration) {
            constantValue = getConstantValue(initializer); 
        }
        
        for (Name name : names) {
            String dataName = name.getIdentifier();
            
            Field field;
            if (isConstantDeclaration) {
            	ConstantField cons = IrFactory.INSTANCE.createConstantField();
            	cons.setValue(constantValue);
            	field = cons;
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
    
	public void endVisit(TopLevelFunction topLevelFunction) {
	}
    
    public void endVisit(NestedFunction nestedFunction) {
    }

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

    public boolean visit(OpenUIStatement openUIStatement) {
        
        if(openUIStatement.hasOpenAttributes()) {
        	openUIStatement.getOpenAttributes().accept(this);
        }
        
        for(Node node : openUIStatement.getOpenableElements()) {
        	node.accept(this);
        }
        
        for(Node node : openUIStatement.getBindClauseVariables()) {
        	node.accept(this);
        }
        
        for(OnEventBlock nextBlock : openUIStatement.getEventBlocks()) {
        	
        	nextBlock.getEventTypeExpr().accept(this);
        	
        	if(nextBlock.hasStringList()) {
        		for(Node node : nextBlock.getStringList()) {
        			node.accept(this);
        		}
        	}
        	
        	currentScope = new StatementBlockScope(currentScope);
        	for(Node node : nextBlock.getStatements()) {
    			node.accept(this);
    		}
        	currentScope = currentScope.getParentScope();
        }
        
		return false;
    }
    
    public boolean visit(CallStatement callStatement) {
        
		bindingCallTarget = true;
		bindInvocationTarget(callStatement.getInvocationTarget());
		bindingCallTarget = false;
		
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
    
    public boolean visit(TransferStatement transferStatement) {
        
		bindInvocationTarget(transferStatement.getInvocationTarget());
		
		if(transferStatement.hasPassingRecord()){
			transferStatement.getPassingRecord().accept(this);
		}
		
		if(transferStatement.hasSettingsBlock()) {
			transferStatement.getSettingsBlock().accept(this);
		}
				
		return false;
	}
    
    public boolean visit(ShowStatement showStatement) {
		
		showStatement.getPageRecordOrForm().accept(this);
		
		final DefaultBinder thisBinder = this;
		for(Node node : showStatement.getShowOptions()) {
			node.accept(new DefaultASTVisitor() {
				public boolean visit(org.eclipse.edt.compiler.core.ast.PassingClause passingClause) {
					passingClause.accept(thisBinder);
					return false;
				}
				public boolean visit(org.eclipse.edt.compiler.core.ast.ReturningToInvocationTargetClause returningToInvocationTargetClause) {
					bindInvocationTarget(returningToInvocationTargetClause.getExpression());
					return false;
				}
			});
		}
		
		if(showStatement.hasSettingsBlock()) {
			showStatement.getSettingsBlock().accept(this);
		}
		
		return false;
	}
    
    public boolean visit(ExitStatement exitStatement) {
        
        if (exitStatement.getExitModifierOpt() != null) {
        	exitStatement.getExitModifierOpt().accept(this);
        }
		
		if(exitStatement.hasSettingsBlock()) {
			exitStatement.getSettingsBlock().accept(this);
		}
		
		return false;
    }

    public void endVisit(OpenUIStatement openUIStatement) {
    };
    
    private void visitStatementBlocks(Statement statement) {
    	for(List<Node> block : statement.getStatementBlocks()) {
			currentScope = new StatementBlockScope(currentScope);
			for(Node node : block) {
				node.accept(this);
			}
			currentScope = currentScope.getParentScope();
		}
    }
    
    public boolean visit(IfStatement ifStatement) {
    	ifStatement.getCondition().accept(this);
    	visitStatementBlocks(ifStatement);
    	return false;
    }
    
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
    
    public boolean visit(WhileStatement whileStatement) {
    	whileStatement.getExpr().accept(this);    	
    	visitStatementBlocks(whileStatement);
    	return false;
    }
    
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
    	if(onExceptionBlock.hasExceptionDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {onExceptionBlock.getExceptionName()}),
    			onExceptionBlock.getExceptionType(),
    			onExceptionBlock.isNullable(),
    			null,
    			false,
    			null);
    	}
    	
    	for(Node node : onExceptionBlock.getStmts()) {
    		node.accept(this);
    	}
    	currentScope = currentScope.getParentScope();
    	return false;
    }
}
