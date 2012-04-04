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

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ClassDataDeclarationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class ExternalTypeValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	private ExternalTypeBinding partBinding;
    private ICompilerOptions compilerOptions;

	public ExternalTypeValidator(IProblemRequestor problemRequestor, ExternalTypeBinding partBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.partBinding = partBinding;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(ExternalType externalType) {
		EGLNameValidator.validate(externalType.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(externalType);
		
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
		nestedFunction.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		
		checkParameters(nestedFunction.getFunctionParameters());
		
		return false;
	}
	
	public boolean visit(Constructor constructor) {
		constructor.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		checkParameters(constructor.getParameters());
		
		return false;
	}
	
	private void checkParameters(List parameters) {
		
		if (partBinding != null && partBinding.getSubType() != null) {
			
			// do not validate the parms for NativeType
			if (AbstractBinder.annotationIs(partBinding.getSubType(), new String[] {"eglx", "lang"}, "NativeType")) {
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
		settingsBlock.accept(new DefaultASTVisitor() {
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(Assignment assignment) {
				IDataBinding lhDBinding = assignment.getLeftHandSide().resolveDataBinding();
				if(lhDBinding != null && IBinding.NOT_FOUND_BINDING != lhDBinding && IDataBinding.CLASS_FIELD_BINDING == lhDBinding.getKind()) {
//					problemRequestor.acceptProblem(
//						assignment,
//						IProblemRequestor.INITIALIZER_NOT_ALLOWED_FOR_EXTERNALTYPE_FIELD);
				}
				return false;
			}
		});
		return false;
	}
	
	private void checkExtendedTypes(ExternalType externalType) {
		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
			Name nameAST = (Name) iter.next();
			ITypeBinding extendedType = (ITypeBinding) (nameAST).resolveBinding();
			if(extendedType != null && IBinding.NOT_FOUND_BINDING != extendedType) {
				boolean typeIsValid = false;
				
				if(ITypeBinding.EXTERNALTYPE_BINDING == extendedType.getKind()) {
					typeIsValid = true;
				}
				
				if(!typeIsValid) {
					problemRequestor.acceptProblem(
							nameAST,
						IProblemRequestor.EXTERNALTYPE_MUST_EXTEND_EXTERNALTYPE,
						new String[] {
							extendedType.getCaseSensitiveName()
						});
				}
			}
		}
	}

	private boolean checkHasSubtype(ExternalType externalType) {
		boolean subtypeValid;
		if(externalType.hasSubType()) {
			subtypeValid = partBinding.getSubType() != null;
			if (partBinding.getSubType() != null && partBinding.getSubType().getName() == InternUtil.intern(IEGLConstants.PROPERTY_NATIVETYPE) &&
				partBinding.getSubType().getPackageName() == InternUtil.intern(new String[] {"egl", "core"})) {
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
