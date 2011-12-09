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
package org.eclipse.edt.compiler.binding;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.EnumerationField;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UnaryExpression.Operator;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author demurray
 */
public class EnumerationBindingCompletor extends AbstractBinder {

    private EnumerationTypeBinding enumerationBinding;
    private IProblemRequestor problemRequestor;
    
    private Set fieldNames = new HashSet();

    public EnumerationBindingCompletor(Scope currentScope, EnumerationTypeBinding enumerationBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, enumerationBinding, dependencyRequestor, compilerOptions);
        this.enumerationBinding = enumerationBinding;
        this.problemRequestor = problemRequestor;
    }
    
    public boolean visit(Enumeration enumeration) {
    	enumeration.getName().setBinding(enumerationBinding);
    	enumerationBinding.setPrivate(enumerationBinding.isPrivate());
    	
    	return true;
    }
    
	public void endVisit(Enumeration enumeration) {
		enumerationBinding.setValid(true);
	}
    
	public boolean visit(EnumerationField enumerationField) {
		int constantValue = -1;
		if(enumerationField.hasConstantValue()) {
			final int constantValueAry[] = new int[] {-1};
			enumerationField.getConstantValue().accept(new AbstractASTExpressionVisitor() {
				public void endVisit(IntegerLiteral integerLiteral) {
					try {
						constantValueAry[0] = Integer.parseInt(integerLiteral.getValue());
					} catch (NumberFormatException e) {
					}
				}
				public boolean visit(org.eclipse.edt.compiler.core.ast.UnaryExpression unaryExpression) {
					return true;
				}
				public void endVisit(org.eclipse.edt.compiler.core.ast.UnaryExpression unaryExpression) {
					if (unaryExpression.getOperator() == Operator.MINUS) {
						constantValueAry[0] = constantValueAry[0] * -1;
					}
				}
				public void endVisitExpression(Expression expression) {
				}
			});
			constantValue = constantValueAry[0];
		}
		else {
			constantValue = fieldNames.size();
		}
		EnumerationDataBinding enumDataBinding = new EnumerationDataBinding(enumerationField.getName().getCaseSensitiveIdentifier(), enumerationBinding, enumerationBinding, constantValue);
		enumerationField.getName().setBinding(enumDataBinding);
		
		if(fieldNames.contains(enumerationField.getName().getIdentifier())) {
    		problemRequestor.acceptProblem(
    			enumerationField.getName(),
				IProblemRequestor.DUPLICATE_VARIABLE_NAME,
				new String[] {
    				enumerationField.getName().getCanonicalName(),
					enumerationBinding.getCaseSensitiveName()
				}
			);
		}
		else {
			fieldNames.add(enumerationField.getName().getIdentifier());
			enumerationBinding.addEnumeration(enumDataBinding);
		}
		return false;
	}
	
    public boolean visit(SettingsBlock settingsBlock) {
        AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, enumerationBinding, enumerationBinding, enumerationBinding, -1, enumerationBinding);
        SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, enumerationBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
        settingsBlock.accept(blockCompletor);
        return false;
    }}
