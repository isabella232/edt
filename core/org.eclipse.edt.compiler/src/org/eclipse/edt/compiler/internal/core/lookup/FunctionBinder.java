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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.CallStatementBinding;
import org.eclipse.edt.compiler.binding.ExitStatementBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionBindingCompletor;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.IsNotStateBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.binding.OpenUIStatementBinding;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.binding.ShowStatementBinding;
import org.eclipse.edt.compiler.binding.TransferStatementBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLIsSystemPartAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.IDliIOStatement;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.InlineDLIStatement;
import org.eclipse.edt.compiler.core.ast.InlineSQLStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnEventBlock;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.core.ast.WhenClause;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.core.ast.WithIDClause;
import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.dli.DLIInfo;
import org.eclipse.edt.compiler.internal.dli.DLIModel;
import org.eclipse.edt.compiler.internal.dli.DLIParser;
import org.eclipse.edt.compiler.internal.dli.IBooleanExpression;
import org.eclipse.edt.compiler.internal.dli.IBooleanExpressionSSAConditions;
import org.eclipse.edt.compiler.internal.dli.IBooleanOperatorExpression;
import org.eclipse.edt.compiler.internal.dli.ICondition;
import org.eclipse.edt.compiler.internal.dli.IHostVariableValue;
import org.eclipse.edt.compiler.internal.dli.ISSAConditions;
import org.eclipse.edt.compiler.internal.dli.ISegmentSearchArgument;
import org.eclipse.edt.compiler.internal.dli.IStatement;
import org.eclipse.edt.compiler.internal.dli.IValue;
import org.eclipse.edt.compiler.internal.dli.IValueExpressionSSAConditions;
import org.eclipse.edt.compiler.internal.dli.stmtFactory.DLIDefaultStatementFactory;
import org.eclipse.edt.compiler.internal.sql.EGLSQLGenerationTokens;
import org.eclipse.edt.compiler.internal.sql.ItemNameToken;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.compiler.internal.sql.SQLInfo;
import org.eclipse.edt.compiler.internal.sql.Token;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLAddStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLDeleteStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLGetByKeyForUpdateStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLGetByKeyStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLOpenForUpdateStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLOpenStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLReplaceStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLStatementFactory;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLParser;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class FunctionBinder extends DefaultBinder {

    private IFunctionBinding functionBinding;

    private IPartBinding partBinding;

    private IBinding statementBinding = null;
    
    private List functionDataDecls = new ArrayList();
    
    private String canonicalFunctionName;
    
    private Set functionIOObjects;

    public FunctionBinder(IPartBinding partBinding, IFunctionBinding functionBinding, Scope scope,
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
        for (Iterator iter = functionBinding.getParameters().iterator(); iter.hasNext();) {
        	((ILocalVariableScope) currentScope).addDeclaredDataName(((IDataBinding) iter.next()).getName());
        }

        LocalVariableAndIOObjectBinder localVariableAndIOObjectBinder = new LocalVariableAndIOObjectBinder(functionScope, problemRequestor, this, partBinding);
		function.accept(localVariableAndIOObjectBinder);
		functionIOObjects = localVariableAndIOObjectBinder.getIOObjects();
		addNonLocalVariableIOObjectsToScope();		

        return true;
    }
    
    public boolean visit(Constructor constructor) {
    	canonicalFunctionName = IEGLConstants.KEYWORD_CONSTRUCTOR;
    	
        FunctionScope functionScope = new FunctionScope((FunctionContainerScope) currentScope, functionBinding);
        currentScope = functionScope;
        
        // For nested functions, the functionBinding passed in should already
        // have been completed by the FunctionContainerBinder
        for (Iterator iter = functionBinding.getParameters().iterator(); iter.hasNext();) {
        	((ILocalVariableScope) currentScope).addDeclaredDataName(((IDataBinding) iter.next()).getName());
        }

        LocalVariableAndIOObjectBinder localVariableAndIOObjectBinder = new LocalVariableAndIOObjectBinder(functionScope, problemRequestor, this, partBinding);
		constructor.accept(localVariableAndIOObjectBinder);
		functionIOObjects = localVariableAndIOObjectBinder.getIOObjects();
		addNonLocalVariableIOObjectsToScope();		

        return true;
    }


    public boolean visit(TopLevelFunction function) {
    	canonicalFunctionName = function.getName().getCanonicalName();
    	
        function.accept(new FunctionBindingCompletor(partBinding, currentScope, (FunctionBinding) functionBinding, dependencyRequestor,
                problemRequestor, compilerOptions));

        FunctionScope functionScope = new FunctionScope(currentScope, functionBinding);
        currentScope = functionScope;
        
        for (Iterator iter = functionBinding.getParameters().iterator(); iter.hasNext();) {
        	((ILocalVariableScope) currentScope).addDeclaredDataName(((IDataBinding) iter.next()).getName());
        }
        
        LocalVariableAndIOObjectBinder localVariableAndIOObjectBinder = new LocalVariableAndIOObjectBinder(functionScope, problemRequestor, this, partBinding);
		function.accept(localVariableAndIOObjectBinder);
		functionIOObjects = localVariableAndIOObjectBinder.getIOObjects();
		addNonLocalVariableIOObjectsToScope();

        IAnnotationBinding annotation = functionBinding.getAnnotation(new String[] {"egl", "core"}, "ContainerContextDependent");
        if (annotation == null || annotation.getValue() == Boolean.NO) {
            currentScope.stopReturningFunctionContainerFunctions();
        }

        return true;
    }
    
    private void addNonLocalVariableIOObjectsToScope() {
    	for(Iterator iter = functionIOObjects.iterator(); iter.hasNext();) {
    		IDataBinding dBinding = (IDataBinding) iter.next();
    		if(IDataBinding.LOCAL_VARIABLE_BINDING != dBinding.getKind()) {
    			((ILocalVariableScope) currentScope).addIOObject(dBinding);
    		}
    	}
    }

    public boolean visit(FunctionParameter functionParameter) {
        //Parameters have already been processed by the binding completor.
        //(With the EGL language as it currently is) there's nothing left to
        // do but bind the resolvable properties
        processResolvableProperties(functionParameter.getName());
        return false;
    }
    
    public boolean visit(FunctionDataDeclaration dataDecl) {
    	functionDataDecls.add(dataDecl);
    	processDataDeclaration(dataDecl.getNames(), dataDecl.getSettingsBlockOpt());
    	processResolvableProperties(dataDecl);
    	return true;
    }
    
    private void processDataDeclaration(List names, SettingsBlock settingsBlock) {
    	Iterator i = names.iterator();
        boolean annotationFoundUsingScope = false;

        while (i.hasNext()) {
            Name name = (Name) i.next();
            String dataName = name.getIdentifier();
            IDataBinding dBinding = (IDataBinding) name.resolveBinding();
            
            if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
            	LocalVariableBinding varBinding = (LocalVariableBinding) name.resolveBinding();
            	if (((ILocalVariableScope) currentScope).hasDeclaredDataName(dataName)) {
                    problemRequestor.acceptProblem(name, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] {
                            name.getCanonicalName(), canonicalFunctionName });
                }
            	else {            	
            		((ILocalVariableScope) currentScope).addLocalVariable(varBinding);
            		((ILocalVariableScope) currentScope).addDeclaredDataName(dataName);
            	}
            	
	            if (settingsBlock != null) {
	                Scope scope = new DataBindingScope(getSystemScope(currentScope), varBinding);
	                AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(scope, varBinding, varBinding.getType(),
	                        varBinding, -1, functionBinding.getDeclarer());
	                annotationScope.setAnnotationFoundUsingThisScope(annotationFoundUsingScope);
	                settingsBlock.accept(
	                        new SettingsBlockAnnotationBindingsCompletor(currentScope, functionBinding.getDeclarer(), annotationScope,
	                                dependencyRequestor, problemRequestor, compilerOptions));
                    annotationFoundUsingScope = annotationScope.isAnnotationFoundUsingThisScope();
	            }
	            
	            if(functionIOObjects.contains(varBinding)) {
	            	((ILocalVariableScope) currentScope).addIOObject(varBinding);
	            }
            }
        }    	
    }
    
    private Scope getSystemScope(Scope scope) {
		while(!(scope instanceof SystemScope)) {
			scope = scope.getParentScope();
		}
		return scope;
	}

	public void endVisit(TopLevelFunction topLevelFunction) {
        currentScope.startReturningFunctionContainerFunctions();
    }
    
    public void endVisit(NestedFunction nestedFunction) {
    }

    public boolean visit(SetValuesExpression setValuesExpression) {
        setValuesExpression.getExpression().accept(this);
        final ITypeBinding typeBinding = setValuesExpression.getExpression().resolveTypeBinding();
        if (typeBinding != null) {
            TypeBindingScope newScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, setValuesExpression.getExpression()
                    .resolveDataBinding());
            setValuesExpression.getSettingsBlock().accept(
                    new SetValuesExpressionCompletor(currentScope, functionBinding.getDeclarer(), newScope, dependencyRequestor, problemRequestor, compilerOptions));
        } else if (setValuesExpression.getExpression().resolveDataBinding() != IBinding.NOT_FOUND_BINDING) {
            setValuesExpression.getSettingsBlock().accept(this);
        }
        return false;
    }

    public boolean visit(OpenUIStatement openUIStatement) {
    	OpenUIStatementBinding openUIStatementBinding = new OpenUIStatementBinding(InternUtil.internCaseSensitive(""));
		statementBinding = openUIStatementBinding;
        openUIStatement.setStatementBinding(openUIStatementBinding);
        
        if(openUIStatement.hasOpenAttributes()) {
        	openUIStatement.getOpenAttributes().accept(this);
        }
        
        for(Iterator iter = openUIStatement.getOpenableElements().iterator(); iter.hasNext();) {
        	((Node) iter.next()).accept(this);
        }
        
        for(Iterator iter = openUIStatement.getBindClauseVariables().iterator(); iter.hasNext();) {
        	((Node) iter.next()).accept(this);
        }
        
        for(Iterator iter = openUIStatement.getEventBlocks().iterator(); iter.hasNext();) {
        	OnEventBlock nextBlock = (OnEventBlock) iter.next();
        	
        	nextBlock.getEventTypeExpr().accept(this);
        	
        	if(nextBlock.hasStringList()) {
        		for(Iterator iter2 = nextBlock.getStringList().iterator(); iter2.hasNext();) {
        			((Node) iter2.next()).accept(this);
        		}
        	}
        	
        	currentScope = new StatementBlockScope(currentScope);
        	for(Iterator iter2 = nextBlock.getStatements().iterator(); iter2.hasNext();) {
    			((Node) iter2.next()).accept(this);
    		}
        	currentScope = currentScope.getParentScope();
        }
        
		return false;
    }
    
    public boolean visit(CallStatement callStatement) {
    	CallStatementBinding callStatementBinding = new CallStatementBinding(InternUtil.internCaseSensitive(""));
		statementBinding = callStatementBinding;
        callStatement.setStatementBinding(callStatementBinding);
        
		try {
			bindingCallTarget = true;
			bindInvocationTarget(callStatement.getInvocationTarget(), true);
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		} finally {
			bindingCallTarget = false;
		}
		
		if(callStatement.hasArguments()) {
			for(Iterator iter = callStatement.getArguments().iterator(); iter.hasNext();) {
				((Node) iter.next()).accept(this);
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
		
		statementBinding = null;
	    
		
		return false;
    }
    
    public boolean visit(TransferStatement transferStatement) {
    	TransferStatementBinding transferStatementBinding = new TransferStatementBinding(InternUtil.internCaseSensitive(""));
		statementBinding = transferStatementBinding;
		transferStatement.setStatementBinding(transferStatementBinding);
        
		try {
			bindInvocationTarget(transferStatement.getInvocationTarget(), false);
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		
		if(transferStatement.hasPassingRecord()){
			transferStatement.getPassingRecord().accept(this);
		}
		
		if(transferStatement.hasSettingsBlock()) {
			transferStatement.getSettingsBlock().accept(this);
		}
		
		statementBinding = null;
		
		return false;
	}
    
    public boolean visit(ShowStatement showStatement) {
    	ShowStatementBinding showStatementBinding = new ShowStatementBinding(InternUtil.internCaseSensitive(""));
		statementBinding = showStatementBinding;
		showStatement.setStatementBinding(showStatementBinding);
		
		showStatement.getPageRecordOrForm().accept(this);
		
		final DefaultBinder thisBinder = this;
		for(Iterator iter = showStatement.getShowOptions().iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(new DefaultASTVisitor() {
				public boolean visit(org.eclipse.edt.compiler.core.ast.PassingClause passingClause) {
					passingClause.accept(thisBinder);
					return false;
				}
				public boolean visit(org.eclipse.edt.compiler.core.ast.ReturningToInvocationTargetClause returningToInvocationTargetClause) {
					try {
						bindInvocationTarget(returningToInvocationTargetClause.getExpression(), false);
					} catch (ResolutionException e) {
						problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
					}
					return false;
				}
			});
		}
		
		if(showStatement.hasSettingsBlock()) {
			showStatement.getSettingsBlock().accept(this);
		}
		statementBinding = null;
		
		return false;
	}
    
    public boolean visit(ExitStatement exitStatement) {
    	ExitStatementBinding exitStatementBinding = new ExitStatementBinding(InternUtil.internCaseSensitive(""));
		statementBinding = exitStatementBinding;
        exitStatement.setStatementBinding(exitStatementBinding);
        
        if (exitStatement.getExitModifierOpt() != null) {
        	exitStatement.getExitModifierOpt().accept(this);
        }
		
		if(exitStatement.hasSettingsBlock()) {
			exitStatement.getSettingsBlock().accept(this);
		}
		statementBinding = null;
		
		return false;
    }

    public void endVisit(OpenUIStatement openUIStatement) {
    	statementBinding = null;
    };

    public boolean visit(SettingsBlock settingsBlock) {
        if (statementBinding == null) {
            return super.visit(settingsBlock);
        }
        AnnotationLeftHandScope lhScope = new AnnotationLeftHandScope(currentScope, statementBinding, null,
        		statementBinding, -1, functionBinding.getDeclarer());
        SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope, functionBinding
                .getDeclarer(), lhScope, dependencyRequestor, problemRequestor, compilerOptions);
        settingsBlock.accept(completor);
        
        //In order to get content assist to work correctly (among other things), we must
        // set the typeBinding on all of the setValueExpressions in this settings block
        
        AbstractASTVisitor sveVisitor =  new AbstractASTVisitor() {
        	public boolean visit(SetValuesExpression setValuesExpression) {
        		setValuesExpression.setTypeBinding(setValuesExpression.getExpression().resolveTypeBinding());        		
        		return true;
        	}
        };
        
        settingsBlock.accept(sveVisitor);
        
        
        return false;
        
    }

    private void processDLIStatement(final IDliIOStatement statement) {

        final IDataBinding[] pcbBinding = new IDataBinding[1];
        statement.accept(new AbstractASTVisitor() {
            public boolean visit(UsingPCBClause usingPCBClause) {
                pcbBinding[0] = usingPCBClause.getPCB().resolveDataBinding();
                return false;
            }

            public boolean visit(InlineDLIStatement inlineDLIStatement) {
                try {
                    DLIInfo info = new DLIInfo();
                    info.setInlineValueStart(inlineDLIStatement.getValueOffset());
                    info.setHasDLICall(true);
                    statement.setDliInfo(info);
                    info.setModel(new DLIParser(compilerOptions).parse(inlineDLIStatement.getValue()));
                    reportParseErrorsForInlineDLI(info.getModel(), inlineDLIStatement);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        List tBindings = new ArrayList();
        List dBindings = new ArrayList();
        collectDataAndTypeBindings(statement.getTargets(), tBindings, dBindings);

        boolean allDLISegments = allDliSegments(dBindings);
        if (allDLISegments || statement.getDliInfo() != null) {
            ITypeBinding[] types = (ITypeBinding[]) tBindings.toArray(new ITypeBinding[tBindings.size()]);
            IDataBinding[] datas = (IDataBinding[]) dBindings.toArray(new IDataBinding[dBindings.size()]);

            DLIInfo info = statement.getDliInfo();

            if (info == null) {
                info = new DLIInfo();
                statement.setDliInfo(info);
                info.setDefaultStatement(true);
            }

            DLIDefaultStatementFactory factory = new DLIDefaultStatementFactory(getPSB(), datas, types, pcbBinding[0], getPCBParams(),
                    NullProblemRequestor.getInstance());
            info.setStatementFactory(factory);
            if (info.isDefaultStatement()) {
                String callString = factory.getDLICallForStatement(statement);
                if (callString != null) {
                    info.setHasDLICall(true);
                    try {
                        info.setModel(new DLIParser(compilerOptions).parse(callString));
                        reportParseErrorsForDefaultDLI(info.getModel(), (Statement) statement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (allDLISegments) {
                info.setTypeBindings(types);
            }
        }
        if (statement.getDliInfo() != null) {
            bindDLIHostVariables(statement);
        }
    }

    private void bindDLIHostVariables(IDliIOStatement dliStatement) {
        if (hasDLICall(dliStatement)) {
            Iterator i = dliStatement.getDliInfo().getModel().getStatements().iterator();
            while (i.hasNext()) {
                IStatement stmt = (IStatement) i.next();
                bindDLIHostVariables(stmt, dliStatement);
            }
        }
    }

    private void bindDLIHostVariables(IStatement stmt, IDliIOStatement dliStatement) {
        Iterator i = stmt.getSegmentSearchArguments().iterator();
        while (i.hasNext()) {
            bindDLIHostVariables((ISegmentSearchArgument) i.next(), dliStatement);
        }
    }

    private void bindDLIHostVariables(ISegmentSearchArgument ssa, IDliIOStatement dliStatement) {
        ISSAConditions conditions = ssa.getSSAConditions();
        if (conditions == null) {
            return;
        }

        if (conditions.isBooleanExpressionSSAConditions()) {
            bindDLIHostVariables(((IBooleanExpressionSSAConditions) conditions).getBooleanExpression(), dliStatement);

        }
        if (conditions.isValueExpressionSSAConditions()) {
            bindDLIHostVariables(((IValueExpressionSSAConditions) conditions).getValue(), dliStatement);
        }
    }

    private void bindDLIHostVariables(IBooleanExpression booleanExpr, IDliIOStatement dliStatement) {
        if (booleanExpr == null) {
            return;
        }
        if (booleanExpr.isBooleanOperatorExpression()) {
            bindDLIHostVariables(((IBooleanOperatorExpression) booleanExpr).getLeftOperand(), dliStatement);
            bindDLIHostVariables(((IBooleanOperatorExpression) booleanExpr).getRightOperand(), dliStatement);
            return;
        }
        if (booleanExpr.isCondition()) {
            ICondition condition = (ICondition) booleanExpr;
            bindDLIHostVariables(condition.getValue(), dliStatement);
            return;
        }
    }

    private void bindDLIHostVariables(IValue value, IDliIOStatement dliStatement) {
        if (value == null) {
            return;
        }
        if (value.isHostVariableValue()) {
            IHostVariableValue hostVar = (IHostVariableValue) value;
            if (hostVar.getText() != null) {
                Expression expr = new ExpressionParser(compilerOptions).parseAsLvalue(hostVar.getText());
                if (expr == null) {
                    return;
                }
                hostVar.setExpression(expr);
                IProblemRequestor oldProbReq = problemRequestor;
                AccumulatingProblemrRequestor newProbReq = new AccumulatingProblemrRequestor();
                problemRequestor = newProbReq;
                expr.accept(this);
                problemRequestor = oldProbReq;
                Iterator i = newProbReq.getProblems().iterator();

                boolean putErrorOnStatement = false;
                int startOffset = 0;
                if (dliStatement.getDliInfo().isDefaultStatement()) {
                    putErrorOnStatement = true;
                } else {
                    startOffset = dliStatement.getDliInfo().getInlineValueStart() + hostVar.getOffset() - expr.getOffset();
                }

                while (i.hasNext()) {
                    Problem problem = (Problem) i.next();
                    if (putErrorOnStatement) {
                        problemRequestor.acceptProblem((Statement) dliStatement, problem.getProblemKind(), problem.getSeverity(), problem
                                .getInserts());
                    } else {
                        int probStart = startOffset + problem.getStartOffset() + 1;
                        int probLen = problem.getEndOffset() - problem.getStartOffset();
                        problemRequestor.acceptProblem(probStart, probStart + probLen, problem.getSeverity(), problem.getProblemKind(),
                                problem.getInserts());
                    }
                }

            }
        }

    }

    private boolean hasDLICall(IDliIOStatement dliStatement) {
        if (dliStatement.getDliInfo() == null) {
            return false;
        }
        if (dliStatement.getDliInfo().getModel() == null) {
            return false;
        }
        if (dliStatement.getDliInfo().getModel().getStatements() == null) {
            return false;
        }
        return true;
    }

    private void collectDataAndTypeBindings(List exprList, List typeBindings, List dataBindings) {
        if (exprList == null) {
            return;
        }

        Iterator i = exprList.iterator();
        while (i.hasNext()) {
            Expression expr = (Expression) i.next();
            dataBindings.add(expr.resolveDataBinding());
            typeBindings.add(expr.resolveTypeBinding());
        }
    }

    private boolean allDliSegments(List dataBindings) {
    	if (dataBindings == null || dataBindings.size() == 0) {
    		return false;
    	}
    	
        Iterator i = dataBindings.iterator();
        while (i.hasNext()) {
            IDataBinding binding = (IDataBinding) i.next();
            if (binding == null || binding == IBinding.NOT_FOUND_BINDING) {
                return false;
            }
            return binding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLISegment") != null;
        }
        return true;
    }

    public void endVisit(OpenStatement statement) {
 //       processSQLStatement(statement);
        super.endVisit(statement);
    }

    public void endVisit(AddStatement statement) {
        processDLIStatement(statement);
//       processSQLStatement(statement);
        super.endVisit(statement);
    }

    public void endVisit(DeleteStatement statement) {
        processDLIStatement(statement);
//        processSQLStatement(statement);
        super.endVisit(statement);
    }

    public void endVisit(final GetByKeyStatement statement) {
        processDLIStatement(statement);
//        processSQLStatement(statement);
        super.endVisit(statement);
    }

    public void endVisit(final GetByPositionStatement statement) {
        processDLIStatement(statement);
        super.endVisit(statement);
    }

    public void endVisit(final ReplaceStatement statement) {
        processDLIStatement(statement);
 //       processSQLStatement(statement);
        super.endVisit(statement);
    }

    private boolean reportParseErrorsForInlineDLI(DLIModel model, InlineDLIStatement inlineDLIStatement) {
        if (model.getProblems() == null || model.getProblems().size() == 0) {
            if (model.getStatements().size() == 0) {
                problemRequestor.acceptProblem(inlineDLIStatement, IProblemRequestor.DLI_MODIFIED_CALL_MUST_BE_SPECIFIED);
            }
            return false;
        }

        Iterator i = model.getProblems().iterator();
        while (i.hasNext()) {
            Problem problem = (Problem) i.next();
            int start = inlineDLIStatement.getValueOffset() + problem.getStartOffset();
            int end = inlineDLIStatement.getValueOffset() + problem.getEndOffset();
            int id = problem.getProblemKind();
            problemRequestor.acceptProblem(start, end, IMarker.SEVERITY_ERROR, id, problem.getInserts());
        }

        return true;
    }

    private boolean reportParseErrorsForDefaultDLI(DLIModel model, Statement statement) {
        if (model.getProblems() == null || model.getProblems().size() == 0) {
            return false;
        }

        Iterator i = model.getProblems().iterator();
        while (i.hasNext()) {
            Problem problem = (Problem) i.next();
            problemRequestor.acceptProblem(statement, IProblemRequestor.DLI_PARSE_ERROR, new String[] { problem.toString() });
        }
        return true;
    }

    private boolean reportErrorsForDefaultSQL(EGLSQLStatementFactory factory, Statement statement) {
        if (factory.getErrorMessages() == null || factory.getErrorMessages().size() == 0) {
            return false;
        }

        Iterator i = factory.getErrorMessages().iterator();
        while (i.hasNext()) {
            Problem problem = (Problem) i.next();
            problemRequestor.acceptProblem(statement, problem.getProblemKind(), problem.getInserts());
        }
        return true;
    }

    private void processResolvableProperties(FunctionDataDeclaration functionDataDeclaration) {
        Iterator i = functionDataDeclaration.getNames().iterator();
        while (i.hasNext()) {
            Name name = (Name) i.next();
            if (name.resolveBinding() == IBinding.NOT_FOUND_BINDING) {
                break;
            }

            if (functionDataDeclaration.hasSettingsBlock()) {
                processResolvableProperties(functionDataDeclaration.getSettingsBlockOpt(), name.resolveDataBinding(), null);
            }
            processResolvableProperties(name);
        }
    }

    public void endVisit(final ExecuteStatement statement) {
 //       processSQLStatement(statement);
        super.endVisit(statement);
    }

    public void processSQLStatement(ExecuteStatement statement) {

//        IDataBinding sqlRecordData = null;
//        IDataBinding[] dataArr = new IDataBinding[1];
//        getSQLRecordData(statement.getIOObjects(), dataArr);
//        if (dataArr[0] != null) {
//            sqlRecordData = dataArr[0];
//        }
//
//        EGLSQLParser sqlParser = null;
//        if (statement.hasInlineSQLStatement()) {
//            SQLInfo info = new SQLInfo();
//            info.setInlineValueStart(statement.getInlineSQLStatement().getValueOffset());
//            sqlParser = new EGLSQLParser(statement.getInlineSQLStatement().getValue(), "ANY", compilerOptions);
//            info.setParser(sqlParser); //$NON-NLS-1$
//            statement.setSqlInfo(info);
//        } else {
//            if (sqlRecordData != null && !statement.isPreparedStatement()) {
//                EGLSQLExecuteStatementFactory sqlStatementFactory = new EGLSQLExecuteStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), statement.isUpdate(), statement.isDelete(), statement.isInsert(), compilerOptions);
//        		String sqlStatement = sqlStatementFactory.buildDefaultSQLStatement();
//        		reportErrorsForDefaultSQL(sqlStatementFactory, statement);
//        		if (sqlStatement != null) {
//                    SQLInfo info = new SQLInfo();
//                    info.setDefaultStatment(true);
//                    sqlParser = new EGLSQLParser(sqlStatement, "ANY", compilerOptions);
//                    info.setParser(sqlParser); //$NON-NLS-1$
//                    statement.setSqlInfo(info);
//        		}
//
//             }
//        }
//
//        if (sqlParser != null && sqlParser.hasErrors()) {
//        	Iterator i = sqlParser.getErrors().iterator();
//        	while (i.hasNext()) {
//                Problem msg = (Problem) i.next();
//                problemRequestor.acceptProblem(statement, msg.getProblemKind(), msg.getInserts());
//        	}
//        }
//        
//        if (statement.getSqlInfo() != null) {
//            if (sqlRecordData == null) {
//                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser()));
//            } else {
//                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser(), sqlRecordData, true));
//            }
//            statement.getSqlInfo().getClauseMap().put(SQLConstants.EXECUTE, statement.getSqlInfo().getGenTokens().getAllTokens());
//            bindSqlHostVariables(statement, statement.getSqlInfo());
//        }

     }
    
    private final Set PROBLEM_KINDS_IGNORED_FOR_DEFAULT_SQL_HOST_VARIABLES = new HashSet(Arrays.asList(new Integer[] {
    	new Integer(IProblemRequestor.ARRAY_ACCESS_NOT_SUBSCRIPTED)	
    }));

    private void bindSqlHostVariables(Statement statement, SQLInfo sqlInfo) {
        Iterator i = sqlInfo.getClauseMap().values().iterator();
        while (i.hasNext()) {
            Token[] tokens = (Token[]) i.next();
            bindSqlHostVariables(statement, sqlInfo, tokens);
        }
    }

    private void bindSqlHostVariables(Statement statement, SQLInfo sqlInfo, Token[] tokens) {
        if (tokens == null) {
            return;
        }
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].isItemNameToken()) {
                bindSqlHostVariable(statement, sqlInfo, (ItemNameToken) tokens[i]);
            }
        }
    }

    private void bindSqlHostVariable(Statement statement, SQLInfo sqlInfo, ItemNameToken token) {
        String str = token.isInputHostVariableToken() || token.isOutputHostVariableToken() ? token.getString().substring(1) : token.getString();
        Expression expr = new ExpressionParser(compilerOptions).parseAsNameOrAccess(str);
        if (expr == null) {
            return;
        }
        token.setExpression(expr);
        IProblemRequestor oldProbReq = problemRequestor;
        AccumulatingProblemrRequestor newProbReq = new AccumulatingProblemrRequestor();
        problemRequestor = newProbReq;
        expr.accept(this);
        validateSqlHostVariable(expr);
        problemRequestor = oldProbReq;
        Iterator i = newProbReq.getProblems().iterator();

        boolean putErrorOnStatement = false;
        int startOffset = 0;
        if (sqlInfo.isDefaultStatement()) {
            putErrorOnStatement = true;
        } else {
            startOffset = sqlInfo.getInlineValueStart() + token.getStartOffset() - expr.getOffset();
        }

        while (i.hasNext()) {
            Problem problem = (Problem) i.next();

            if(sqlInfo.isDefaultStatement() && PROBLEM_KINDS_IGNORED_FOR_DEFAULT_SQL_HOST_VARIABLES.contains(new Integer(problem.getProblemKind()))) {
            	continue;
            }
            
            if (putErrorOnStatement) {
                problemRequestor.acceptProblem(statement, problem.getProblemKind(), problem.getSeverity(), problem.getInserts());
            } else {
                int probStart = startOffset + problem.getStartOffset() + 1;
                int probLen = problem.getEndOffset() - problem.getStartOffset();
                problemRequestor.acceptProblem(probStart, probStart + probLen, problem.getSeverity(), problem.getProblemKind(), problem
                        .getInserts());
            }
        }

    }
    
    private void validateSqlHostVariable(Expression expr) {
        IDataBinding dataBinding = expr.resolveDataBinding();
        if (dataBinding == null || dataBinding == IBinding.NOT_FOUND_BINDING) {
            return;
        }
        if (dataBinding.getDeclaringPart().getAnnotation(EGLIsSystemPartAnnotationTypeBinding.getInstance()) != null) {
            problemRequestor.acceptProblem(expr, IProblemRequestor.INVALID_USAGE_LOCATION, new String[] {expr.getCanonicalString()});
            return;
        }
        
        ITypeBinding typeBinding = expr.resolveTypeBinding();
        if (typeBinding == null || typeBinding == IBinding.NOT_FOUND_BINDING) {
            return;
        }
        
        if(ITypeBinding.MULTIPLY_OCCURING_ITEM == typeBinding.getKind()) {
        	typeBinding = typeBinding.getBaseType();
        }
        
        if (typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
            problemRequestor.acceptProblem(expr, IProblemRequestor.HOST_VARIABLE_MUST_BE_ITEM, new String[] {expr.getCanonicalString()});
        }
    }

    private void getSQLRecordData(List exprList, IDataBinding[] data) {
        if (exprList == null || exprList.size() != 1) {
            return;
        }
        IDataBinding exprData = ((Expression) exprList.get(0)).resolveDataBinding();
        if (exprData == null || exprData == IBinding.NOT_FOUND_BINDING) {
            return;
        }
        if (exprData.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") == null) {
            return;
        }
        data[0] = exprData;
    }

    private void processSQLStatement(AddStatement statement) {

        IDataBinding sqlRecordData = null;
        IDataBinding[] dataArr = new IDataBinding[1];
        getSQLRecordData(statement.getIOObjects(), dataArr);
        if (dataArr[0] != null) {
            sqlRecordData = dataArr[0];
        }

        InlineSQLStatement inline = getInlineSQLStatement(statement);
        EGLSQLParser sqlParser = null;
        if (inline != null) {
            SQLInfo info = new SQLInfo();
            info.setInlineValueStart(inline.getValueOffset());
            sqlParser = new EGLSQLParser(inline.getValue(), "ANY", compilerOptions);
            info.setParser(sqlParser); //$NON-NLS-1$
            statement.setSqlInfo(info);
        } else {
            if (sqlRecordData != null) {
                EGLSQLAddStatementFactory sqlStatementFactory = new EGLSQLAddStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), compilerOptions);
        		String sqlStatement = sqlStatementFactory.buildDefaultSQLStatement();
        		reportErrorsForDefaultSQL(sqlStatementFactory, statement);
        		if (sqlStatement != null) {
                    SQLInfo info = new SQLInfo();
                    info.setDefaultStatment(true);
                    sqlParser = new EGLSQLParser(sqlStatement, "ANY", compilerOptions);
                    info.setParser(sqlParser); //$NON-NLS-1$
                    statement.setSqlInfo(info);
        		}

             }
        }

        if (sqlParser != null && sqlParser.hasErrors()) {
        	Iterator i = sqlParser.getErrors().iterator();
        	while (i.hasNext()) {
                Problem msg = (Problem) i.next();
                problemRequestor.acceptProblem(statement, msg.getProblemKind(), msg.getInserts());
        	}
        }
        
        if (statement.getSqlInfo() != null) {
            if (sqlRecordData == null) {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser()));
            } else {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser(), sqlRecordData, true));
            }
            statement.getSqlInfo().getClauseMap().put(SQLConstants.COLUMNS, statement.getSqlInfo().getGenTokens().getColumnsTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.INSERT_INTO, statement.getSqlInfo().getGenTokens().getInsertIntoTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.VALUES, statement.getSqlInfo().getGenTokens().getValuesTokens());
            bindSqlHostVariables(statement, statement.getSqlInfo());
        }

    }

    private void processSQLStatement(DeleteStatement statement) {

        IDataBinding sqlRecordData = null;
        IDataBinding[] dataArr = new IDataBinding[1];
        getSQLRecordData(statement.getIOObjects(), dataArr);
        if (dataArr[0] != null) {
            sqlRecordData = dataArr[0];
        }

        final List usingKeysList = new ArrayList();
        final boolean[] hasUsingKeys = new boolean[] {false};
        final boolean[] noCursor = new boolean[] {false};
        statement.accept(new AbstractASTVisitor() {
        	public boolean visit(org.eclipse.edt.compiler.core.ast.NoCursorClause noCursorClause) {
        		noCursor[0] = true;
        		return false;
        	}
	        public boolean visit(UsingKeysClause usingKeysClause) {
	        	noCursor[0] = true;
	        	hasUsingKeys[0] = true;
	            Iterator i = usingKeysClause.getExpressions().iterator();
	            while (i.hasNext()) {
	                Expression expr = (Expression) i.next();
	                String colName = SQLUtility.getColumnName(expr);
	                if (colName != null) {
	                    usingKeysList.add(new String[] {expr.getCanonicalString(), colName});
	                }
	            }
	            return false;
	        }
        });

        InlineSQLStatement inline = getInlineSQLStatement(statement);
        EGLSQLParser sqlParser = null;
        if (inline != null) {
            SQLInfo info = new SQLInfo();
            info.setInlineValueStart(inline.getValueOffset());
            sqlParser = new EGLSQLParser(inline.getValue(), "ANY", compilerOptions);
            info.setParser(sqlParser); //$NON-NLS-1$
            statement.setSqlInfo(info);
        } else {
            if (sqlRecordData != null) {
                String[][] usingKeys = null;
                if (hasUsingKeys[0]) {
                	usingKeys = (String[][])usingKeysList.toArray(new String[usingKeysList.size()][]);
                }    		    	
		    	
                EGLSQLDeleteStatementFactory sqlStatementFactory = new EGLSQLDeleteStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), usingKeys, noCursor[0], compilerOptions);
        		String sqlStatement = sqlStatementFactory.buildDefaultSQLStatement();
        		reportErrorsForDefaultSQL(sqlStatementFactory, statement);
        		if (sqlStatement != null) {
                    SQLInfo info = new SQLInfo();
                    info.setDefaultStatment(true);
                    sqlParser = new EGLSQLParser(sqlStatement, "ANY", compilerOptions);
                    info.setParser(sqlParser); //$NON-NLS-1$
                    statement.setSqlInfo(info);
        		}

             }
        }

        if (sqlParser != null && sqlParser.hasErrors()) {
        	Iterator i = sqlParser.getErrors().iterator();
        	while (i.hasNext()) {
                Problem msg = (Problem) i.next();
                problemRequestor.acceptProblem(statement, msg.getProblemKind(), msg.getInserts());
        	}
        }
        
        if (statement.getSqlInfo() != null) {
            if (sqlRecordData == null) {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser()));
            } else {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser(), sqlRecordData, true));
            }
            statement.getSqlInfo().getClauseMap().put(SQLConstants.DELETE, statement.getSqlInfo().getGenTokens().getDeleteTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.FROM, statement.getSqlInfo().getGenTokens().getFromTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.WHERE, statement.getSqlInfo().getGenTokens().getWhereTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.WHERE_CURRENT_OF_CLAUSE, statement.getSqlInfo().getGenTokens().getWhereCurrentOfTokens());
            bindSqlHostVariables(statement, statement.getSqlInfo());
        }

    }

    private void processSQLStatement(ReplaceStatement statement) {

        IDataBinding sqlRecordData = null;
        IDataBinding[] dataArr = new IDataBinding[1];
        getSQLRecordData(statement.getIOObjects(), dataArr);
        if (dataArr[0] != null) {
            sqlRecordData = dataArr[0];
        }

        final List usingKeysList = new ArrayList();
        final boolean[] hasUsingKeys = new boolean[] {false};
        final boolean[] noCursor = new boolean[] {false};
        statement.accept(new AbstractASTVisitor() {
        	public boolean visit(org.eclipse.edt.compiler.core.ast.NoCursorClause noCursorClause) {
        		noCursor[0] = true;
        		return false;
        	}
	        public boolean visit(UsingKeysClause usingKeysClause) {
	        	noCursor[0] = true;
	        	hasUsingKeys[0] = true;
	            Iterator i = usingKeysClause.getExpressions().iterator();
	            while (i.hasNext()) {
	                Expression expr = (Expression) i.next();
	                String colName = SQLUtility.getColumnName(expr);
	                if (colName != null) {
	                    usingKeysList.add(new String[] {expr.getCanonicalString(), colName});
	                }
	            }
	            return false;
	        }
        });
        
        InlineSQLStatement inline = getInlineSQLStatement(statement);
        EGLSQLParser sqlParser = null;
        if (inline != null) {
            SQLInfo info = new SQLInfo();
            info.setInlineValueStart(inline.getValueOffset());
            sqlParser = new EGLSQLParser(inline.getValue(), "ANY", compilerOptions);
            info.setParser(sqlParser); //$NON-NLS-1$
            statement.setSqlInfo(info);
        } else {
            if (sqlRecordData != null) {
    		    ITypeBinding type = ((Expression)statement.getIOObjects().get(0)).resolveTypeBinding();
    		    boolean isArray = type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING;
    		    if (!isArray) {
    		    	
                    String[][] usingKeys = null;
                    if (hasUsingKeys[0]) {
                    	usingKeys = (String[][])usingKeysList.toArray(new String[usingKeysList.size()][]);
                    }    		    	
    		    	
	                EGLSQLReplaceStatementFactory sqlStatementFactory = new EGLSQLReplaceStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), usingKeys, noCursor[0], compilerOptions);
	        		String sqlStatement = sqlStatementFactory.buildDefaultSQLStatement();
	        		reportErrorsForDefaultSQL(sqlStatementFactory, statement);
	        		if (sqlStatement != null) {
	                    SQLInfo info = new SQLInfo();
	                    info.setDefaultStatment(true);
	                    sqlParser = new EGLSQLParser(sqlStatement, "ANY", compilerOptions);
	                    info.setParser(sqlParser); //$NON-NLS-1$
	                    statement.setSqlInfo(info);
	        		}
    		    }

             }
        }

        if (sqlParser != null && sqlParser.hasErrors()) {
        	Iterator i = sqlParser.getErrors().iterator();
        	while (i.hasNext()) {
                Problem msg = (Problem) i.next();
                problemRequestor.acceptProblem(statement, msg.getProblemKind(), msg.getInserts());
        	}
        }
        
        if (statement.getSqlInfo() != null) {
            if (sqlRecordData == null) {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser()));
            } else {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser(), sqlRecordData, true));
            }
            statement.getSqlInfo().getClauseMap().put(SQLConstants.UPDATE, statement.getSqlInfo().getGenTokens().getUpdateTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.SET, statement.getSqlInfo().getGenTokens().getSetTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.WHERE, statement.getSqlInfo().getGenTokens().getWhereTokens());
            statement.getSqlInfo().getClauseMap().put(SQLConstants.WHERE_CURRENT_OF_CLAUSE, statement.getSqlInfo().getGenTokens().getWhereCurrentOfTokens());
            bindSqlHostVariables(statement, statement.getSqlInfo());
        }
    }

    private void processSQLStatement(OpenStatement statement) {

        IDataBinding sqlRecordData = null;
        IDataBinding[] dataArr = new IDataBinding[1];
        getSQLRecordData(statement.getIOObjects(), dataArr);
        if (dataArr[0] != null) {
            sqlRecordData = dataArr[0];
        }

        final boolean[] hasPrepared = new boolean[1];
        final boolean[] isForUpdate = new boolean[1];
        final List intoNames = new ArrayList();
        final List usingKeysList = new ArrayList();
        final boolean[] hasUsingKeys = new boolean[] {false};
        statement.accept(new AbstractASTVisitor() {
            public boolean visit(WithIDClause withIDClause) {
                hasPrepared[0] = true;
                return false;
            }
           public boolean visit(ForUpdateClause forUpdateClause) {
               isForUpdate[0] = true;
               return false;
            }
	        public boolean visit(IntoClause intoClause) {
	            Iterator i = intoClause.getExpressions().iterator(); 
	            while (i.hasNext()) {
	                intoNames.add(((Expression) i.next()).getCanonicalString());
	            }
	            return false;
	        }
	        public boolean visit(UsingKeysClause usingKeysClause) {
	        	hasUsingKeys[0] = true;
	            Iterator i = usingKeysClause.getExpressions().iterator();
	            while (i.hasNext()) {
	                Expression expr = (Expression) i.next();
	                String colName = SQLUtility.getColumnName(expr);
	                if (colName != null) {
	                    usingKeysList.add(new String[] {expr.getCanonicalString(), colName});
	                }
	            }
	            return false;
	        }
        });
        
        InlineSQLStatement inline = getInlineSQLStatement(statement);
        EGLSQLParser sqlParser = null;
        if (inline != null) {
            SQLInfo info = new SQLInfo();
            info.setInlineValueStart(inline.getValueOffset());
            sqlParser = new EGLSQLParser(inline.getValue(), "ANY", compilerOptions);
            info.setParser(sqlParser); //$NON-NLS-1$
            statement.setSqlInfo(info);
        } else {
            if (sqlRecordData != null && !hasPrepared[0]) {
                String[][] usingKeys = null;
                if (hasUsingKeys[0]) {
                	usingKeys = (String[][])usingKeysList.toArray(new String[usingKeysList.size()][]);
                }
                List into = intoNames;
                if (into.size() == 0) {
                    into = null;
                }
                EGLSQLStatementFactory sqlStatementFactory;
        		if (isForUpdate[0]) {
        		    sqlStatementFactory = new EGLSQLOpenForUpdateStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), into, usingKeys, compilerOptions);
        		} else {
        		    sqlStatementFactory = new EGLSQLOpenStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), into, usingKeys, compilerOptions);
        		}
                
        		String sqlStatement = sqlStatementFactory.buildDefaultSQLStatement();
        		reportErrorsForDefaultSQL(sqlStatementFactory, statement);
        		if (sqlStatement != null) {
                    SQLInfo info = new SQLInfo();
                    info.setDefaultStatment(true);
                    sqlParser = new EGLSQLParser(sqlStatement, "ANY", compilerOptions);
                    info.setParser(sqlParser); //$NON-NLS-1$
                    statement.setSqlInfo(info);
        		}

             }
        }
        
        if (sqlParser != null && sqlParser.hasErrors()) {
        	Iterator i = sqlParser.getErrors().iterator();
        	while (i.hasNext()) {
                Problem msg = (Problem) i.next();
                problemRequestor.acceptProblem(statement, msg.getProblemKind(), msg.getInserts());
        	}
        }
        
        if (statement.getSqlInfo() != null) {
            if (sqlRecordData == null) {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser()));
            } else {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser(), sqlRecordData, true));
            }
            
    		if (!hasPrepared[0]) {
    		    Token[] callTokens = statement.getSqlInfo().getGenTokens().getCallTokens();
    			if (callTokens == null) {
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.SELECT, statement.getSqlInfo().getGenTokens().getSelectTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.FROM, statement.getSqlInfo().getGenTokens().getFromTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.WHERE, statement.getSqlInfo().getGenTokens().getWhereTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.GROUP_BY, statement.getSqlInfo().getGenTokens().getGroupByTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.HAVING, statement.getSqlInfo().getGenTokens().getHavingTokens());
//    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.INTO, createIntoToken());
    				if (isForUpdate[0]) {
    					Token[] updateTokens = statement.getSqlInfo().getGenTokens().getForUpdateOfTokens();
    					if (updateTokens != null) {
    						if (isForUpdateOf(updateTokens)) {
    							statement.getSqlInfo().getClauseMap().put(SQLConstants.FOR_UPDATE_OF, statement.getSqlInfo().getGenTokens().getForUpdateOfTokens());
    						}
    						else {
    							statement.getSqlInfo().getClauseMap().put(SQLConstants.FOR_UPDATE, statement.getSqlInfo().getGenTokens().getForUpdateOfTokens());
    						}
    					}
    				} else {
        	            statement.getSqlInfo().getClauseMap().put(SQLConstants.ORDER_BY, statement.getSqlInfo().getGenTokens().getOrderByTokens());
    				}
    	            bindSqlHostVariables(statement, statement.getSqlInfo());
    			} else {
    	            statement.getSqlInfo().getClauseMap().put(IEGLConstants.KEYWORD_CALL, callTokens);
    	            bindSqlHostVariables(statement, statement.getSqlInfo());
    			}
    		} else {
//	            statement.getSqlInfo().getClauseMap().put(SQLConstants.INTO, createIntoToken());
    		}
        }
        else {
            if (hasPrepared[0] && intoNames.size() > 0) {
                SQLInfo info = new SQLInfo();
                info.setDefaultStatment(true);
                statement.setSqlInfo(info);
//	            statement.getSqlInfo().getClauseMap().put(SQLConstants.INTO, createIntoToken());
            }
        }

    }
    
    private boolean isForUpdateOf(Token[] tokens) {
    	if (tokens == null || tokens.length == 0) {
    		return false;
    	}
    	if (tokens[0].getString() == null) {
    		return false;
    	}
    	return tokens[0].getString().toUpperCase().toLowerCase().startsWith(SQLConstants.FOR_UPDATE_OF);
    }

    private void processSQLStatement(GetByKeyStatement statement) {

        IDataBinding sqlRecordData = null;
        IDataBinding[] dataArr = new IDataBinding[1];
        getSQLRecordData(statement.getIOObjects(), dataArr);
        if (dataArr[0] != null) {
            sqlRecordData = dataArr[0];
        }

        final boolean[] hasPrepared = new boolean[1];
        final boolean[] isForUpdate = new boolean[1];
        final List intoNames = new ArrayList();
        final List usingKeysList = new ArrayList();
        final boolean[] hasUsingKeys = new boolean[] {false};
        statement.accept(new AbstractASTVisitor() {
            public boolean visit(WithIDClause withIDClause) {
                hasPrepared[0] = true;
                return false;
            }
           public boolean visit(ForUpdateClause forUpdateClause) {
               isForUpdate[0] = true;
               return false;
            }
	        public boolean visit(IntoClause intoClause) {
	            Iterator i = intoClause.getExpressions().iterator(); 
	            while (i.hasNext()) {
	                intoNames.add(((Expression) i.next()).getCanonicalString());
	            }
	            return false;
	        }
	        public boolean visit(UsingKeysClause usingKeysClause) {
	        	hasUsingKeys[0] = true;
	            Iterator i = usingKeysClause.getExpressions().iterator();
	            while (i.hasNext()) {
	                Expression expr = (Expression) i.next();
	                String colName = SQLUtility.getColumnName(expr);
	                if (colName != null) {
	                    usingKeysList.add(new String[] {expr.getCanonicalString(), colName});
	                }
	            }
	            return false;
	        }
        });
        
        InlineSQLStatement inline = getInlineSQLStatement(statement);
        EGLSQLParser sqlParser = null;
        if (inline != null) {
            SQLInfo info = new SQLInfo();
            info.setInlineValueStart(inline.getValueOffset());
            sqlParser = new EGLSQLParser(inline.getValue(), "ANY", compilerOptions);
            info.setParser(sqlParser); //$NON-NLS-1$
            statement.setSqlInfo(info);
        } else {
            if (sqlRecordData != null && !hasPrepared[0]) {
                String[][] usingKeys = null;
                if (hasUsingKeys[0]) {
                	usingKeys = (String[][])usingKeysList.toArray(new String[usingKeysList.size()][]);
                }
                List into = intoNames;
                if (into.size() == 0) {
                    into = null;
                }
                EGLSQLStatementFactory sqlStatementFactory;
        		if (isForUpdate[0]) {
        		    sqlStatementFactory = new EGLSQLGetByKeyForUpdateStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), into, usingKeys, false, compilerOptions);
        		} else {
        		    ITypeBinding type = ((Expression)statement.getIOObjects().get(0)).resolveTypeBinding();
        		    boolean isArray = type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING;
        		    sqlStatementFactory = new EGLSQLGetByKeyStatementFactory(sqlRecordData, ((Expression)statement.getIOObjects().get(0)).getCanonicalString(), into, usingKeys, isArray, compilerOptions);
        		}
                
        		String sqlStatement = sqlStatementFactory.buildDefaultSQLStatement();
        		reportErrorsForDefaultSQL(sqlStatementFactory, statement);
        		if (sqlStatement != null) {
                    SQLInfo info = new SQLInfo();
                    info.setDefaultStatment(true);
                    sqlParser = new EGLSQLParser(sqlStatement, "ANY", compilerOptions);
                    info.setParser(sqlParser); //$NON-NLS-1$
                    statement.setSqlInfo(info);
        		}

             }
        }
        
        if (sqlParser != null && sqlParser.hasErrors()) {
        	Iterator i = sqlParser.getErrors().iterator();
        	while (i.hasNext()) {
                Problem msg = (Problem) i.next();
                problemRequestor.acceptProblem(statement, msg.getProblemKind(), msg.getInserts());
        	}
        }
        
        if (statement.getSqlInfo() != null) {
            if (sqlRecordData == null) {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser()));
            } else {
                statement.getSqlInfo().setGenTokens(new EGLSQLGenerationTokens(statement.getSqlInfo().getParser(), sqlRecordData, true));
            }
            
    		if (!hasPrepared[0]) {
    		    Token[] callTokens = statement.getSqlInfo().getGenTokens().getCallTokens();
    			if (callTokens == null) {
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.SELECT, statement.getSqlInfo().getGenTokens().getSelectTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.FROM, statement.getSqlInfo().getGenTokens().getFromTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.WHERE, statement.getSqlInfo().getGenTokens().getWhereTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.GROUP_BY, statement.getSqlInfo().getGenTokens().getGroupByTokens());
    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.HAVING, statement.getSqlInfo().getGenTokens().getHavingTokens());
//    	            statement.getSqlInfo().getClauseMap().put(SQLConstants.INTO, createIntoToken());
    				if (isForUpdate[0]) {
        	            statement.getSqlInfo().getClauseMap().put(SQLConstants.FOR_UPDATE_OF, statement.getSqlInfo().getGenTokens().getForUpdateOfTokens());
    				} else {
        	            statement.getSqlInfo().getClauseMap().put(SQLConstants.ORDER_BY, statement.getSqlInfo().getGenTokens().getOrderByTokens());
    				}
    	            bindSqlHostVariables(statement, statement.getSqlInfo());
    			} else {
    	            statement.getSqlInfo().getClauseMap().put(IEGLConstants.KEYWORD_CALL, callTokens);
    	            bindSqlHostVariables(statement, statement.getSqlInfo());
    			}
    		} else {
//	            statement.getSqlInfo().getClauseMap().put(SQLConstants.INTO, createIntoToken());
    		}
        }
        else {
            if (hasPrepared[0] && intoNames.size() > 0) {
                SQLInfo info = new SQLInfo();
                info.setDefaultStatment(true);
                statement.setSqlInfo(info);
//	            statement.getSqlInfo().getClauseMap().put(SQLConstants.INTO, createIntoToken());
            }
        }

    }

    private InlineSQLStatement getInlineSQLStatement(Statement stmt) {
        final InlineSQLStatement[] inline = new InlineSQLStatement[1];
        stmt.accept(new AbstractASTVisitor() {
            public boolean visit(InlineSQLStatement inlineSQLStatement) {
                inline[0] = inlineSQLStatement;
                return false;
            }
        });
        return inline[0];
    }
    
    private void visitStatementBlocks(Statement statement) {
    	for(Iterator iter = statement.getStatementBlocks().iterator(); iter.hasNext();) {
			currentScope = new StatementBlockScope(currentScope);
			for(Iterator stmtIter = ((List) iter.next()).iterator(); stmtIter.hasNext();) {
				((Node) stmtIter.next()).accept(this);
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
		boolean criterionIsSysType = false;
		boolean criterionIsEventKey = false;
		if(caseStatement.hasCriterion()) {
			Expression expr = caseStatement.getCriterion();
			expr.accept(this);
			IDataBinding dBinding = expr.resolveDataBinding();
			if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "SysVar", "SYSTEMTYPE")) {
				criterionIsSysType = true;
			}
			
			if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "ui", "text"}, "ConverseVar", "EVENTKEY")) {
				criterionIsEventKey = true;
			}
		}
		for(Iterator iter = caseStatement.getWhenClauses().iterator(); iter.hasNext();) {
			WhenClause whenClause = (WhenClause) iter.next();
			for(Iterator iter2 = whenClause.getExpr_plus().iterator(); iter2.hasNext();) {
				Expression expr = (Expression) iter2.next();
				if(criterionIsSysType) {
					expr.accept(new AbstractASTExpressionVisitor() {						
						public void endVisit(SimpleName simpleName) {
							IsNotStateBinding dBinding = IsNotStateBinding.toIsNotStateBinding(simpleName.getIdentifier());
							if(dBinding == null || dBinding.getIsNotStateKind() != IsNotStateBinding.SYSTEM_TYPE) {
								endVisitExpression(simpleName);
							}
							else {
								simpleName.setBinding(dBinding);
							}
						}
						
						public void endVisitExpression(Expression expression) {
							problemRequestor.acceptProblem(expression, IProblemRequestor.INVALID_SYSTEM_TYPE_VALUE, new String[]{expression.getCanonicalString()});
						}
					});
				} else if (criterionIsEventKey){
					expr.accept(new AbstractASTExpressionVisitor() {						
						public void endVisit(SimpleName simpleName) {
							IsNotStateBinding dBinding = IsNotStateBinding.toIsNotStateBinding(simpleName.getIdentifier());
							if(dBinding == null || dBinding.getIsNotStateKind() != IsNotStateBinding.EVENT_KEY) {
								endVisitExpression(simpleName);
							}
							else {
								simpleName.setBinding(dBinding);
							}
						}
						
						public void endVisitExpression(Expression expression) {
							problemRequestor.acceptProblem(expression, IProblemRequestor.INVALID_EVENT_KEY_VALUE, new String[]{expression.getCanonicalString()});
						}
					});
				}else {
					expr.accept(this);
				}
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
		
		for(Iterator iter = forStatement.getStmts().iterator(); iter.hasNext();) {
    		((Node) iter.next()).accept(this);
    	}
		
    	currentScope = currentScope.getParentScope();
    	
    	return false;
    }
    
    public boolean visit(ForEachStatement forEachStatement) {
    	currentScope = new StatementBlockScope(currentScope);
    	
    	if(forEachStatement.hasVariableDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {forEachStatement.getVariableDeclarationName()}),
    			null);
    	}
    	else {
	    	for (Node n : (List<Node>)forEachStatement.getTargets()) {
	    		n.accept(this);
	    	}
    	}
    	
    	forEachStatement.getResultSet().getExpression().accept(this);
    	
    	for(Iterator iter = forEachStatement.getStmts().iterator(); iter.hasNext();) {
    		((Node) iter.next()).accept(this);
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
    	for(Iterator iter = tryStatement.getStmts().iterator(); iter.hasNext();) {
    		((Node) iter.next()).accept(this);
    	}
    	currentScope = currentScope.getParentScope();
    	
    	for(Iterator iter = tryStatement.getOnExceptionBlocks().iterator(); iter.hasNext();) {
    		((Node) iter.next()).accept(this);
    	}
    	return false;
    }
    
    public boolean visit(OnExceptionBlock onExceptionBlock) {
    	currentScope = new StatementBlockScope(currentScope);
    	if(onExceptionBlock.hasExceptionDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {onExceptionBlock.getExceptionName()}),
    			null);
    	}
    	
    	for(Iterator iter = onExceptionBlock.getStmts().iterator(); iter.hasNext();) {
    		((Node) iter.next()).accept(this);
    	}
    	currentScope = currentScope.getParentScope();
    	return false;
    }
}
