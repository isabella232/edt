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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.part.FunctionContainerValidator.StaticReferenceChecker;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;

	
public class ClassDataDeclarationValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
    private ICompilerOptions compilerOptions;
    private IPartBinding declaringPart;
	
	public ClassDataDeclarationValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IPartBinding declaringPart) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.declaringPart = declaringPart;
	}
	
	@Override
	public boolean visit(final ClassDataDeclaration classDataDeclaration) {
		for(Iterator iter = classDataDeclaration.getNames().iterator(); iter.hasNext();) {
			EGLNameValidator.validate((Name) iter.next(), EGLNameValidator.PART, problemRequestor, compilerOptions);
		}
		
		TypeValidator.validateTypeDeclaration(classDataDeclaration.getType(), declaringPart, problemRequestor);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(classDataDeclaration);
		classDataDeclaration.accept(new FieldValidator(problemRequestor, compilerOptions, declaringPart));
		
		if (classDataDeclaration.isStatic() && classDataDeclaration.hasInitializer()) {
			classDataDeclaration.getInitializer().accept(new StaticReferenceChecker(problemRequestor));
		}
		
		return false;
	}
}
