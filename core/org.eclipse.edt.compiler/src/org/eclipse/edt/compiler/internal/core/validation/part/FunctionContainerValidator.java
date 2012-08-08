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
package org.eclipse.edt.compiler.internal.core.validation.part;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.ClassDataDeclarationValidator;


/**
 * @author Dave Murray
 */
public abstract class FunctionContainerValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	protected IPartBinding partBinding;
	protected Part partNode;
    protected ICompilerOptions compilerOptions;
	
	public FunctionContainerValidator(IProblemRequestor problemRequestor, IPartBinding partBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.partBinding = partBinding;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		classDataDeclaration.accept(new ClassDataDeclarationValidator(problemRequestor, compilerOptions, partBinding));
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		nestedFunction.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		return false;
	}
	
	public boolean visit(Constructor constructor) {
		constructor.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
		//TODO Validator blows up right now
//		useStatement.accept(new UseStatementValidator(partBinding,partNode.getName().getCanonicalName(),problemRequestor, compilerOptions));
		return false;
	}
}
