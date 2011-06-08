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
package org.eclipse.edt.compiler.internal.core.validation.part;

import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class InterfaceValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	private InterfaceBinding partBinding;
    private ICompilerOptions compilerOptions;
    protected Interface iFaceNode = null;
	
	public InterfaceValidator(IProblemRequestor problemRequestor, InterfaceBinding partBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.partBinding = partBinding;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(Interface interfaceNode) {
		iFaceNode = interfaceNode;
		EGLNameValidator.validate(interfaceNode.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(interfaceNode);
		return true;
	}

	public boolean visit(NestedFunction nestedFunction) {
		nestedFunction.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		if (nestedFunction.isPrivate()){
			problemRequestor.acceptProblem(nestedFunction.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_CANNOT_BE_PRIVATE);
		}

		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedFunction);

		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction,false,problemRequestor);
		
		if (InternUtil.intern(nestedFunction.getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(iFaceNode.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {partBinding.getCaseSensitiveName()});
		}
		
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
}
