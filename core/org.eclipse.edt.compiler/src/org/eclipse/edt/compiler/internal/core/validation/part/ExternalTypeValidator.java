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

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ClassDataDeclarationValidator;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Stereotype;
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
	
	public boolean visit(ExternalType externalType) {
		this.externalType = externalType;
		EGLNameValidator.validate(externalType.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(externalType); TODO
		
		if(checkHasSubtype(externalType)) {
			checkExtendedTypes(externalType);
		}
		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		classDataDeclaration.accept(new ClassDataDeclarationValidator(problemRequestor, compilerOptions, partBinding));
		
//		if(classDataDeclaration.hasInitializer()) {
//			problemRequestor.acceptProblem(
//				classDataDeclaration.getInitializer(),
//				IProblemRequestor.INITIALIZER_NOT_ALLOWED_FOR_EXTERNALTYPE_FIELD);
//		}
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		checkParameters(nestedFunction.getFunctionParameters());
		return false;
	}
	
	public boolean visit(Constructor constructor) {
		super.visit(constructor);
		checkParameters(constructor.getParameters());
		return false;
	}
	
	private void checkParameters(List parameters) {
		
		Stereotype subtype = externalTypeBinding.getSubType();
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
	
	public boolean visit(SettingsBlock settingsBlock) {
		// The only code that did anything was commented out. if it ends up being needed, this needs to be ported to the new binding model.
//		settingsBlock.accept(new DefaultASTVisitor() {
//			public boolean visit(SettingsBlock settingsBlock) {
//				return true;
//			}
//			
//			public boolean visit(Assignment assignment) {
//				IDataBinding lhDBinding = assignment.getLeftHandSide().resolveDataBinding();
//				if(lhDBinding != null && IBinding.NOT_FOUND_BINDING != lhDBinding && IDataBinding.CLASS_FIELD_BINDING == lhDBinding.getKind()) {
////					problemRequestor.acceptProblem(
////						assignment,
////						IProblemRequestor.INITIALIZER_NOT_ALLOWED_FOR_EXTERNALTYPE_FIELD);
//				}
//				return false;
//			}
//		});
		return false;
	}
	
	private void checkExtendedTypes(ExternalType externalType) {
		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
			Name nameAST = (Name) iter.next();
			Type extendedType = nameAST.resolveType();
			if(extendedType != null) {
				if(!(extendedType instanceof org.eclipse.edt.mof.egl.ExternalType)) {
					problemRequestor.acceptProblem(
							nameAST,
						IProblemRequestor.EXTERNALTYPE_MUST_EXTEND_EXTERNALTYPE,
						new String[] {
							extendedType.getTypeSignature()
						});
				}
			}
		}
	}

	private boolean checkHasSubtype(ExternalType externalType) {
		boolean subtypeValid;
		if(externalType.hasSubType()) {
			subtypeValid = externalTypeBinding.getSubType() != null;
			//TODO is this check still valid? there's no egl.core.NativeType, and eglx.lang.NativeType is valid
			if (externalTypeBinding.getSubType() != null && NameUtile.equals(externalTypeBinding.getSubType().getEClass().getName(), IEGLConstants.PROPERTY_NATIVETYPE) &&
					NameUtile.equals(externalTypeBinding.getSubType().getEClass().getPackageName(), "egl.core")) {
				problemRequestor.acceptProblem(
						externalType.getSubType(),
						IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
						new String[] {
							IEGLConstants.PROPERTY_NATIVETYPE.toUpperCase()
						});
					return false;
				}
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
}
