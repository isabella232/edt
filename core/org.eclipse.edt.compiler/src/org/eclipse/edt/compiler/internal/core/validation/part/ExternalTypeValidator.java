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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ClassDataDeclarationValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author Dave Murray
 */
public class ExternalTypeValidator extends FunctionContainerValidator {
	
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.ExternalType externalTypeBinding;
    ExternalType externalType;

	public ExternalTypeValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		externalTypeBinding = (org.eclipse.edt.mof.egl.ExternalType)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(ExternalType externalType) {
		this.externalType = externalType;
		EGLNameValidator.validate(externalType.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(externalType);
		
		if (checkHasSubtype()) {
			checkExtendedTypes(externalTypeBinding.getStereotype().getEClass());
			checkCycles();
		}
		return true;
	}
	
	@Override
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		classDataDeclaration.accept(new ClassDataDeclarationValidator(problemRequestor, compilerOptions, partBinding));
		
//		if(classDataDeclaration.hasInitializer()) {
//			problemRequestor.acceptProblem(
//				classDataDeclaration.getInitializer(),
//				IProblemRequestor.INITIALIZER_NOT_ALLOWED_FOR_EXTERNALTYPE_FIELD);
//		}
		return false;
	}
	
	@Override
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		checkParameters(nestedFunction.getFunctionParameters());
		return false;
	}
	
	@Override
	public boolean visit(Constructor constructor) {
		super.visit(constructor);
		checkParameters(constructor.getParameters());
		return false;
	}
	
	private void checkParameters(List parameters) {
		
		Stereotype subtype = externalTypeBinding.getStereotype();
		if (subtype != null) {
			// do not validate the parms for NativeType
			EClass clazz = subtype.getEClass();
			if (clazz != null && NameUtile.equals(clazz.getName(), NameUtile.getAsName(IEGLConstants.PROPERTY_NATIVETYPE))
					&& NameUtile.equals(clazz.getPackageName(), NameUtile.getAsName("eglx.lang"))) {
				return;
			}
		}
		
		for(Iterator iter = parameters.iterator(); iter.hasNext();) {
			FunctionParameter parm = (FunctionParameter) iter.next();
			if (parm.isParmConst()) {
				problemRequestor.acceptProblem(
						parm,
						IProblemRequestor.EXTERNALTYPE_PARM_CANNOT_BE_CONST,
						new String[] {parm.getName().getCanonicalName()});
			}
		}
	}
	
	private void checkExtendedTypes(EClass expectedSubtype) {
		for (Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
			Name nameAST = (Name) iter.next();
			Type extendedType = nameAST.resolveType();
			if (extendedType != null && !BindingUtil.isEClassProxy(extendedType)) {
				if (!(extendedType instanceof org.eclipse.edt.mof.egl.ExternalType)) {
					problemRequestor.acceptProblem(
							nameAST,
							IProblemRequestor.EXTERNALTYPE_MUST_EXTEND_EXTERNALTYPE,
							new String[] {
									extendedType.getTypeSignature()
							});
				}
				else {
					// Super type must have the same subtype.
					Stereotype superSubtype = ((org.eclipse.edt.mof.egl.ExternalType)extendedType).getStereotype();
					if (superSubtype == null || (!expectedSubtype.equals(superSubtype.getEClass()) && !isMofClass((org.eclipse.edt.mof.egl.ExternalType)extendedType))) {
						problemRequestor.acceptProblem(
								nameAST,
								IProblemRequestor.EXTERNAL_TYPE_SUPER_SUBTYPE_MISMATCH,
								new String[] {
										extendedType.getTypeSignature()
								});
					}
				}
			}
		}
	}
	
	private boolean isMofClass(org.eclipse.edt.mof.egl.ExternalType et) {
		
		if (BindingUtil.getAnnotationWithSimpleName(et, "ClassType") != null) {
			return true;
		}
		if (BindingUtil.getAnnotationWithSimpleName(et, "MofClass") != null) {
			return true;
		}
		
		return false;
	}

	private boolean checkHasSubtype() {
		boolean subtypeValid;
		if(externalType.hasSubType()) {
			subtypeValid = externalTypeBinding.getStereotype() != null;
		}
		else {
			problemRequestor.acceptProblem(
				externalType.getName(),
				IProblemRequestor.PART_DEFINITION_REQUIRES_TYPE_CLAUSE,
				new String[] {
					externalType.getName().getCanonicalName()
				});
			subtypeValid = false;
		}
		return subtypeValid;
	}
	
	private void checkCycles() {
		for (Name name : externalType.getExtendedTypes()) {
			Type type = name.resolveType();
			if (type instanceof org.eclipse.edt.mof.egl.ExternalType
					&& checkCycles((org.eclipse.edt.mof.egl.ExternalType)type, new HashSet<org.eclipse.edt.mof.egl.ExternalType>())) {
				problemRequestor.acceptProblem(
	    				name,
	    				IProblemRequestor.RECURSIVE_LOOP_IN_EXTENDS,
	    				new String[] {externalTypeBinding.getCaseSensitiveName(), name.toString()});
			}
		}
	}
	
	private boolean checkCycles(org.eclipse.edt.mof.egl.ExternalType externalType, Set<org.eclipse.edt.mof.egl.ExternalType> seen) {
		// Sometimes the binding won't be resolved yet.
		externalType = (org.eclipse.edt.mof.egl.ExternalType)BindingUtil.realize(externalType);
		
		if (seen.contains(externalType)) {
			return false;
		}
		
		if (externalTypeBinding.equals(externalType)) {
			return true;
		}
		
		seen.add(externalType);
		for (StructPart superType : externalType.getSuperTypes()) {
			if (superType instanceof org.eclipse.edt.mof.egl.ExternalType) {
				if (checkCycles((org.eclipse.edt.mof.egl.ExternalType)superType, seen)) {
					return true;
				}
			}
		}
		return false;
	}
}
