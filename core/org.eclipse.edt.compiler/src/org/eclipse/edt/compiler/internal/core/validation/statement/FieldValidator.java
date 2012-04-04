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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class FieldValidator extends DefaultASTVisitor{
	
	private IProblemRequestor problemRequestor;
    private ICompilerOptions compilerOptions;
	
	public FieldValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(final ClassDataDeclaration classDataDeclaration) {
		validateNoEmptySettingsBlockForServiceOrInterface(classDataDeclaration.getType(), classDataDeclaration.getSettingsBlockOpt());
		return false;
	}
	
	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
		
		validateNoEmptySettingsBlockForServiceOrInterface(functionDataDeclaration.getType(), functionDataDeclaration.getSettingsBlockOpt());

		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.StructureItem structureItem) {
		if (structureItem.hasType()) {
			validateNoEmptySettingsBlockForServiceOrInterface(structureItem.getType(), structureItem.getSettingsBlock());
		}
		return false;
	}
	
	private boolean isServiceOrInterfaceType(Type type) {
		ITypeBinding typeBinding = type.resolveTypeBinding();
		if (Binding.isValidBinding(typeBinding) && (Binding.isValidBinding(typeBinding.getBaseType()))) {
			return (typeBinding.getBaseType().getKind() == ITypeBinding.SERVICE_BINDING) || (typeBinding.getBaseType().getKind() == ITypeBinding.INTERFACE_BINDING);
		}
		return false;
	}
	
	private void validateNoEmptySettingsBlockForServiceOrInterface (Type type, SettingsBlock block) {
		if (block == null || !isServiceOrInterfaceType(type)) {
			return;
		}	
		
		if (block.getSettings().size() == 0) {
			problemRequestor.acceptProblem(type,
					IProblemRequestor.SERVICE_AND_INTERFACE_EMPTY_BLOCK,
					new String[]{type.resolveTypeBinding().getName()});
		}
	}



}
