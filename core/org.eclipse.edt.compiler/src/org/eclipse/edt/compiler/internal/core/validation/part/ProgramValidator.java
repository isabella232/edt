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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.ProgramParameterValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class ProgramValidator extends FunctionContainerValidator {
	ProgramBinding programBinding = null;
	Program program = null;
	
	public ProgramValidator(IProblemRequestor problemRequestor, ProgramBinding partBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, partBinding, compilerOptions);
		programBinding = partBinding;
	}
	
	public boolean visit(Program aprogram) {
		program = aprogram;
		partNode = aprogram;
		EGLNameValidator.validate(program.getName(), EGLNameValidator.PROGRAM, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(program);
		validateProgramFunctions();
		validateProgramParameters();
		
		if (program.isCallable()) {
			problemRequestor.acceptProblem(program.getName(),
					IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
					new String[] {program.getIdentifier()});
		}

		IAnnotationBinding aBinding = programBinding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGWebTransaction");
		if (aBinding != null){
			validateWebtransName(program);
		}
		
		return true;
	}
	
	private void validateWebtransName(Program program) {
		String name;
		boolean hasAlias = false;
		IAnnotationBinding aliasBinding = programBinding.getAnnotation(new String[] {"egl", "core"}, IEGLConstants.PROPERTY_ALIAS);
		
		if (Binding.isValidBinding(aliasBinding)) {
			name = (String)aliasBinding.getValue();
			hasAlias = true;
		}
		else {
			name = program.getName().getCanonicalName();
		}
		
//		String aliasedName = Aliaser.getAlias(name);
		String aliasedName = name;
		
		if (aliasedName.length() > 8) {
			Node node = program.getName();
			if (hasAlias) {
				final Node[] aliasNode = new Node[1];
				DefaultASTVisitor sbVisitor = new DefaultASTVisitor() {
					public boolean visit(Program program) {
						return true;
					}
					public boolean visit(SettingsBlock settingsBlock) {
						if (aliasNode[0] == null) {
							AbstractASTVisitor assVisitor = new AbstractASTVisitor() {
								public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
									IAnnotationBinding annBinding = assignment.resolveBinding();
									if (annBinding != null) {
										if (annBinding.getName() == InternUtil.intern(IEGLConstants.PROPERTY_ALIAS)) {
											aliasNode[0] = assignment.getRightHandSide();
										}
									}
									return false;
								}
							};
							settingsBlock.accept(assVisitor);
						}
						return false;
					}
				};
				
				program.accept(sbVisitor);
				if (aliasNode[0] != null) {
					node = aliasNode[0];
				}
				
			}
			
			problemRequestor.acceptProblem(node,
					IProblemRequestor.RUNTIME_NAME_OF_WEB_PROGRAM_EXCEEDS_8_CHARACTERS,
					new String[] {name});
		
		}
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
	
	protected void validateProgramParameters(){
		program.accept(new AbstractASTVisitor(){
			int parmcount = 0;
			public boolean visit (ProgramParameter programParameter){

				IDataBinding binding = (IDataBinding)programParameter.getName().resolveBinding();
				if (StatementValidator.isValidBinding(binding)){
					ITypeBinding typeBinding = binding.getType();
					if (StatementValidator.isValidBinding(typeBinding)){
						if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING ||
							typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING){
							boolean error = false;
							if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING &&
								((FlexibleRecordBinding)typeBinding).getDeclaredFields().size() == 0 &&
								((FlexibleRecordBinding)typeBinding).getDefaultSuperType() == null){
								error = true;
							}
							
							if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING &&
									((FixedRecordBinding)typeBinding).getStructureItems().size() == 0 &&
									((FixedRecordBinding)typeBinding).getDefaultSuperType() == null ){
									error = true;
							}
							
							if (error){
								problemRequestor.acceptProblem(programParameter,
										IProblemRequestor.RECORD_PARAMETER_WITH_NO_CONTENTS,
										new String[] {programParameter.getName().getCanonicalName(),programParameter.getName().getCanonicalName()});
							}
						}
						else if (typeBinding.getKind() == ITypeBinding.FORM_BINDING){
							if (InternUtil.intern(typeBinding.getName()) != InternUtil.intern(programParameter.getName().getCanonicalName())){
								problemRequestor.acceptProblem(programParameter,
										IProblemRequestor.INVALID_FORM_TYPEDEF,
										new String[] {programParameter.getName().getCanonicalName(),typeBinding.getCaseSensitiveName()});
							}
						}
						new ProgramParameterValidator(problemRequestor).validate(typeBinding, programParameter.getType());
						
						checkParmTypeNotStaticArray(programParameter, programParameter.getType());
					}
					
				}
				return false;
			}
			

		});
	}
	
	
	private void checkParmTypeNotStaticArray(ProgramParameter parm, Type parmType) {

		if (parmType.isNullableType()) {
			checkParmTypeNotStaticArray(parm, parmType.getBaseType());
		}
		
		if(parmType.isArrayType()) {
			if (((ArrayType) parmType).hasInitialSize()) {
        	problemRequestor.acceptProblem(
        		parmType,
				IProblemRequestor.STATIC_ARRAY_PGM_PARAMETER_DEFINITION,
				new String[] {parm.getName().getCanonicalName()});   
			}
			else {
				checkParmTypeNotStaticArray(parm, ((ArrayType) parmType).getElementType());
			}
        }
			
	}

	
	protected void validateProgramFunctions(){
		program.accept(new AbstractASTVisitor(){
			boolean main = false;
			public boolean visit (NestedFunction nestedFunction){
				if (InternUtil.intern(nestedFunction.getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)){
					main = true;
					if (nestedFunction.getFunctionParameters().size() > 0){
						problemRequestor.acceptProblem((Node)nestedFunction.getFunctionParameters().get(0),
								IProblemRequestor.MAIN_FUNCTION_HAS_PARAMETERS,
								new String[] {program.getName().getCanonicalName()});
					}
				}
				return false;
			}
			
			public void endVisit(Program aprogram){
				if (!main){
					problemRequestor.acceptProblem(aprogram.getName(),
							IProblemRequestor.PROGRAM_MAIN_FUNCTION_REQUIRED,
							new String[] {aprogram.getName().getCanonicalName()});
				}
			}
		});
	}
}
