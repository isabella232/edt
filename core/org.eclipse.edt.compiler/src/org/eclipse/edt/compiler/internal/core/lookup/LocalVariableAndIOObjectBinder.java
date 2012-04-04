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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LocalConstantBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTStatementVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * Class to visit statements and bind targets in I/O statements, such as "myForm"
 * in the statement: "print myForm;". The resolved bindings for these targets
 * are added to the list IOObjects which can be accessed through the method
 * getIOObjects().
 * 
 * To bind I/O targets which are defined as local variables in the function, it
 * is neccesary to visit and analyze function data declarations as well. To avoid
 * duplicating work, this class creates data bindings for and binds the AST nodes
 * for the names and types in the variable declarations. The data bindings are not
 * added to the functionScope that is passed in, however. The class FunctionBinder
 * adds them as it visits their declarations, so that forward references are
 * restricted.
 * 
 * @author Dave Murray
 */
public class LocalVariableAndIOObjectBinder extends AbstractASTStatementVisitor {
	
	Scope functionScope;
	IProblemRequestor problemRequestor;
	private AbstractBinder binder;
	private List functionDataDecls = new ArrayList();
	private IPartBinding declaringPartBinding;
	private Set IOObjects = new HashSet();
	private boolean i4GLItemsNullableIsSet;
	
	public LocalVariableAndIOObjectBinder(Scope functionScope, IProblemRequestor problemRequestor, AbstractBinder binder, IPartBinding declaringPartBinding) {
		this.functionScope = new StatementBlockScope(functionScope);
		this.problemRequestor = problemRequestor;
		this.binder = binder;
		this.declaringPartBinding = declaringPartBinding;
		i4GLItemsNullableIsSet = functionScope.getParentScope().I4GLItemsNullableIsEnabled();
	}
	
	public void visitStatement(Statement stmt) {
	}
	
	public boolean internalVisitStatement(Statement statement) {
		for(Iterator iter = statement.getIOObjects().iterator(); iter.hasNext();) {
			Expression nextExpr = (Expression) iter.next();
			nextExpr.accept(new DefaultASTVisitor() {
				public boolean visit(SimpleName simpleName) {
					IDataBinding ioTargetDataBinding = functionScope.findIOTargetData(simpleName.getIdentifier());
					if(ioTargetDataBinding != IBinding.NOT_FOUND_BINDING) {
						simpleName.setBinding(ioTargetDataBinding);
						simpleName.setTypeBinding(ioTargetDataBinding.getType());
						
						if(ioTargetDataBinding.getKind() == IDataBinding.AMBIGUOUS_BINDING) {
							problemRequestor.acceptProblem(simpleName, IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS, IMarker.SEVERITY_ERROR, new String[] {simpleName.getIdentifier()});
						}
						else {
							IOObjects .add(ioTargetDataBinding);
						}
					}
					return false;
				}
			});			
		}
		
		if(statement.canIncludeOtherStatements()) {
			for(Iterator iter = statement.getStatementBlocks().iterator(); iter.hasNext();) {
				functionScope = new StatementBlockScope(functionScope);
				for(Iterator stmtIter = ((List) iter.next()).iterator(); stmtIter.hasNext();) {
					((Node) stmtIter.next()).accept(this);
				}
				functionScope = functionScope.getParentScope();
			}
		}
		
		return false;
	}
	
	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
    	functionDataDecls.add(functionDataDeclaration);
    	processDataDeclaration(functionDataDeclaration.getNames(), functionDataDeclaration.getType(), functionDataDeclaration.getSettingsBlockOpt(), functionDataDeclaration.isConstant(), functionDataDeclaration.getInitializer());
    	return true;
    }
	
	public boolean visit(ForStatement forStatement) {
		functionScope = new StatementBlockScope(functionScope);
		
		if(forStatement.hasVariableDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {forStatement.getVariableDeclarationName()}),
    			forStatement.getVariableDeclarationType(),
    			null,
    			false,
    			null);
    	}
    	
    	for(Iterator stmtIter = forStatement.getStmts().iterator(); stmtIter.hasNext();) {
			((Node) stmtIter.next()).accept(this);
		}
		
    	functionScope = functionScope.getParentScope();
    	return false;
	}
	
	public boolean visit(TryStatement tryStatement) {
		functionScope = new StatementBlockScope(functionScope);
		
		for(Iterator stmtIter = tryStatement.getStmts().iterator(); stmtIter.hasNext();) {
			((Node) stmtIter.next()).accept(this);
		}
		
    	functionScope = functionScope.getParentScope();
    	
    	for(Iterator iter = tryStatement.getOnExceptionBlocks().iterator(); iter.hasNext();) {
    		((Node) iter.next()).accept(this);
    	}
    	
    	return false;
	}
	
	public boolean visit(OnExceptionBlock onExceptionBlock) {
		functionScope = new StatementBlockScope(functionScope);
		
		if(onExceptionBlock.hasExceptionDeclaration()) {
    		processDataDeclaration(
    			Arrays.asList(new Name[] {onExceptionBlock.getExceptionName()}),
    			onExceptionBlock.getExceptionType(),
    			null,
    			false,
    			null);
    	}
    	
    	for(Iterator stmtIter = onExceptionBlock.getStmts().iterator(); stmtIter.hasNext();) {
			((Node) stmtIter.next()).accept(this);
		}
		
    	functionScope = functionScope.getParentScope();
    	return false;
	}
    
    private void processDataDeclaration(List names, Type type, SettingsBlock settingsBlock, boolean isConstantDeclaration, Expression initializer) {
        ITypeBinding typeBinding = null;
        try {
            typeBinding = binder.bindType(type);
            
            if(i4GLItemsNullableIsSet) {
            	if(ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getBaseType().getKind()) {
            		typeBinding = typeBinding.getNullableInstance();
            	}
            }
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
                    .getInserts());
            if(settingsBlock != null) {
            	AbstractBinder.bindNamesToNotFound(settingsBlock);
            }
            for(Iterator iter = names.iterator(); iter.hasNext();) {
            	((Name) iter.next()).setBinding(IBinding.NOT_FOUND_BINDING);
            }
            return; // Do not create the class field bindings if the
            // type cannot be resolved
        }

        Object constantValue = null;
        if (isConstantDeclaration) {
            constantValue = AbstractBinder.getConstantValue(initializer, typeBinding, ((Name) names.get(0)).getCanonicalName(), problemRequestor);
        }
        
        Iterator i = names.iterator();
        while (i.hasNext()) {
            Name name = (Name) i.next();
            String dataName = name.getIdentifier();

            LocalVariableBinding varBinding = isConstantDeclaration ?
            	new LocalConstantBinding(name.getCaseSensitiveIdentifier(), declaringPartBinding, typeBinding, constantValue) :
            	new LocalVariableBinding(name.getCaseSensitiveIdentifier(), declaringPartBinding, typeBinding);
            	
        	if(!((ILocalVariableScope) functionScope).hasDeclaredDataName(dataName)) {
	            ((ILocalVariableScope) functionScope).addLocalVariable(varBinding);
	            ((ILocalVariableScope) functionScope).addDeclaredDataName(dataName);
        	}
            
            name.setBinding(varBinding);
        }
    }
    
    public Set getIOObjects() {
    	return IOObjects;
    }
}
