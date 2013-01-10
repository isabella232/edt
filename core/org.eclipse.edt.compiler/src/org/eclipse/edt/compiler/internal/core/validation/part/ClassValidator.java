/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Class;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.ExpressionValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;

public class ClassValidator extends FunctionContainerValidator {
	
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.EGLClass classBinding;
	Class clazz;
	
	public ClassValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		this.classBinding = (org.eclipse.edt.mof.egl.EGLClass)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(Class clazz) {
		this.clazz = clazz;
		partNode = clazz;
		EGLNameValidator.validate(clazz.getName(), EGLNameValidator.CLASS, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(clazz);
		clazz.accept(new ExpressionValidator(partBinding, problemRequestor, compilerOptions));
		
		checkImplements(clazz.getImplementedInterfaces());
		checkInterfaceFunctionsOverriden(classBinding);
		checkAbstractFunctionsOverriden(classBinding);
		checkImplicitConstructor(clazz);
		if (checkExtends()) {
			checkCycles();
		}
		
		return true;
	}
	
	private boolean checkExtends() {
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
				return false;
			}
			else if (type != null && classBinding.equals(type)) {
				problemRequestor.acceptProblem(
						extended,
						IProblemRequestor.PART_CANNOT_EXTEND_ITSELF,
						new String[] {
								type.getTypeSignature()
						});
				return false;
			}
		}
		return true;
	}
	
	private void checkCycles() {
		Name name = clazz.getExtends();
		if (name != null) {
			Type type = name.resolveType();
			if (type instanceof org.eclipse.edt.mof.egl.EGLClass
					&& checkCycles((org.eclipse.edt.mof.egl.EGLClass)type, new HashSet<org.eclipse.edt.mof.egl.EGLClass>())) {
				problemRequestor.acceptProblem(
	    				name,
	    				IProblemRequestor.RECURSIVE_LOOP_IN_EXTENDS,
	    				new String[] {classBinding.getCaseSensitiveName(), name.toString()});
			}
		}
	}
	
	private boolean checkCycles(org.eclipse.edt.mof.egl.EGLClass classType, Set<org.eclipse.edt.mof.egl.EGLClass> seen) {
		// Sometimes the binding won't be resolved yet.
		classType = (org.eclipse.edt.mof.egl.EGLClass)BindingUtil.realize(classType);
		
		if (seen.contains(classType)) {
			return false;
		}
		
		if (classBinding.equals(classType)) {
			return true;
		}
		
		seen.add(classType);
		for (StructPart superType : classType.getSuperTypes()) {
			if (superType instanceof org.eclipse.edt.mof.egl.EGLClass) {
				if (checkCycles((org.eclipse.edt.mof.egl.EGLClass)superType, seen)) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public boolean visit(NestedFunction nestedFunction) {
		Function function = (Function)nestedFunction.getName().resolveElement();
		
		if (function != null) {
			Function override = getOverriddenFunction(function, classBinding);
			if (override != null) {
				//Check that the function does not reduce visibility of the overridden function
				if (BindingUtil.isPrivate(function) && !BindingUtil.isPrivate(override)) {
					problemRequestor.acceptProblem(
		    				nestedFunction.getName(),
		    				IProblemRequestor.CANNOT_REDUCE_VISIBILITY,
		    				new String[] {override.getCaseSensitiveName(), ((NamedElement)override.getContainer()).getCaseSensitiveName()});
				}
				
				//Check that the function returns the same type as the overridden function
				if (!BindingUtil.typesAreIdentical(function.getReturnType(), override.getReturnType()) || function.isNullable() != override.isNullable()) {
					problemRequestor.acceptProblem(
							nestedFunction.getName(),
		    				IProblemRequestor.RETURN_TYPES_NOT_COMPATIBLE,
		    				new String[] {override.getCaseSensitiveName(), ((NamedElement)override.getContainer()).getCaseSensitiveName()});
				}
			}
		}
		return super.visit(nestedFunction);
	}
	
	private Function getOverriddenFunction(Function function, EGLClass classBinding) {
		//Check the hierarchy for a function with the same parm types as the one provided
		
		if (classBinding == null) {
			return null;
		}
		
		for (StructPart superType : classBinding.getSuperTypes()) {
			if (superType instanceof org.eclipse.edt.mof.egl.EGLClass) {
				Function override = getOverriddenFunction(function, ((EGLClass)superType).getFunctions());
				if (override != null) {
					return override;
				}
				return getOverriddenFunction(function, (EGLClass)superType);
			}
		}
		return null;		
	}
	
	private Function getOverriddenFunction(Function function, List<Function> functions) {
		for (Function newFunc : functions) {
			if (BindingUtil.functionSignituresAreIdentical(function, newFunc, false, false)) {
				return newFunc;
			}
		}
		return null;
	}	
}
