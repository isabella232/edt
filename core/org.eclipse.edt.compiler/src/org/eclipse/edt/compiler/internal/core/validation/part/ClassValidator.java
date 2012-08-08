/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.Type;

public class ClassValidator extends FunctionContainerValidator {
	
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.EGLClass classBinding;
	EGLClass clazz;
	
	public ClassValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		this.classBinding = (org.eclipse.edt.mof.egl.EGLClass)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(EGLClass clazz) {
		this.clazz = clazz;
		partNode = clazz;
		EGLNameValidator.validate(clazz.getName(), EGLNameValidator.CLASS, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(program); TODO
		
		checkImplements(clazz.getImplementedInterfaces());
		checkInterfaceFunctionsOverriden(classBinding);
		checkExtends();
		
		return true;
	}
	
	private void checkExtends() {
		Name extended = clazz.getExtends();
		if (extended != null) {
			Type type = extended.resolveType();
			if (type != null && !(type instanceof org.eclipse.edt.mof.egl.EGLClass)) {
				problemRequestor.acceptProblem(
						extended,
						IProblemRequestor.CLASS_MUST_EXTEND_CLASS,
						new String[] {
								type.getTypeSignature()
						});
			}
			else if (type != null && classBinding.equals(type)) {
				problemRequestor.acceptProblem(
						extended,
						IProblemRequestor.PART_CANNOT_EXTEND_ITSELF,
						new String[] {
								type.getTypeSignature()
						});
			}
		}
	}
}
