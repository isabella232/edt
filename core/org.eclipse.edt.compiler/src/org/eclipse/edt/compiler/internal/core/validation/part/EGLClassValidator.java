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

import org.eclipse.edt.compiler.binding.EGLClassBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class EGLClassValidator extends FunctionContainerValidator {
	String className = "";
	EGLClass eglClass = null;
	
	public EGLClassValidator(IProblemRequestor problemRequestor, EGLClassBinding partBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, partBinding, compilerOptions);
		className = partBinding.getCaseSensitiveName();
	}
	
	public boolean visit(EGLClass aClass) {
		this.eglClass = aClass;
		partNode = aClass;
		EGLNameValidator.validate(eglClass.getName(), EGLNameValidator.LIBRARY, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(aClass);
		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		return false;
	}
	
	
	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
		super.visit(useStatement);
		return false;
	}
	
}
