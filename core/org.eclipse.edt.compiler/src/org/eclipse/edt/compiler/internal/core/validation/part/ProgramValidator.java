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

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.ExpressionValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.utils.NameUtile;

public class ProgramValidator extends FunctionContainerValidator {
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.Program programBinding;
	Program program;
	
	public ProgramValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		this.programBinding = (org.eclipse.edt.mof.egl.Program)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(Program aprogram) {
		program = aprogram;
		partNode = aprogram;
		EGLNameValidator.validate(program.getName(), EGLNameValidator.PROGRAM, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(program);
		aprogram.accept(new ExpressionValidator(partBinding, problemRequestor, compilerOptions));
		validateProgramFunctions();
		
		if (program.isCallable()) {
			problemRequestor.acceptProblem(program.getName(),
					IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
					new String[] {program.getIdentifier()});
		}

		return true;
	}
	
	protected void validateProgramFunctions() {
		program.accept(new AbstractASTVisitor() {
			boolean main = false;
			@Override
			public boolean visit (NestedFunction nestedFunction) {
				if (NameUtile.equals(nestedFunction.getName().getCanonicalName(), IEGLConstants.MNEMONIC_MAIN)){
					main = true;
					if (nestedFunction.getFunctionParameters().size() > 0){
						problemRequestor.acceptProblem((Node)nestedFunction.getFunctionParameters().get(0),
								IProblemRequestor.MAIN_FUNCTION_HAS_PARAMETERS,
								new String[] {program.getName().getCanonicalName()});
					}
				}
				return false;
			}
			
			@Override
			public void endVisit(Program aprogram) {
				if (!main){
					problemRequestor.acceptProblem(aprogram.getName(),
							IProblemRequestor.PROGRAM_MAIN_FUNCTION_REQUIRED,
							new String[] {aprogram.getName().getCanonicalName()});
				}
			}
		});
	}
}
