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
	
	import java.util.Iterator;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;

	
public class FunctionDataDeclarationValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
    private ICompilerOptions compilerOptions;
	private IPartBinding enclosingPart;

	
	public FunctionDataDeclarationValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.enclosingPart = enclosingPart;
	}
	
	public boolean visit(final FunctionDataDeclaration functionDataDeclaration) {
		for(Iterator iter = functionDataDeclaration.getNames().iterator(); iter.hasNext();) {
			EGLNameValidator.validate((Name) iter.next(), EGLNameValidator.PART, problemRequestor, compilerOptions);
		}
		
		//TODO StatementValidator has many errors
//		if (functionDataDeclaration.isConstant()){
//			StatementValidator.validatePrimitiveConstant(functionDataDeclaration.getType(),problemRequestor);
//		}
		
		TypeValidator.validateTypeDeclaration(functionDataDeclaration.getType(), enclosingPart, problemRequestor);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(functionDataDeclaration);
		functionDataDeclaration.accept(new FieldValidator(problemRequestor, compilerOptions, enclosingPart));

		return false;
	}
}
