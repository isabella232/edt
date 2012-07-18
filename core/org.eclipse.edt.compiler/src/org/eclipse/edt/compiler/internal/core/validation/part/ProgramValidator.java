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
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
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
	
	public boolean visit(Program aprogram) {
		program = aprogram;
		partNode = aprogram;
		EGLNameValidator.validate(program.getName(), EGLNameValidator.PROGRAM, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(program); TODO JZS
		validateProgramFunctions();
		validateProgramParameters();
		
		if (program.isCallable()) {
			problemRequestor.acceptProblem(program.getName(),
					IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
					new String[] {program.getIdentifier()});
		}

		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
//		super.visit(classDataDeclaration); TODO JZS
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
//		super.visit(settingsBlock); TODO JZS
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
//		super.visit(useStatement); TODO JZS
		return false;
	}
	
	protected void validateProgramParameters(){
		//TODO port this to the new framework when called programs are supported
//		program.accept(new AbstractASTVisitor(){
//			int parmcount = 0;
//			public boolean visit (ProgramParameter programParameter){
//				programParameter.getName().resolveElement();
//				IDataBinding binding = (IDataBinding)programParameter.getName().resolveBinding();
//				if (StatementValidator.isValidBinding(binding)){
//					ITypeBinding typeBinding = binding.getType();
//					if (StatementValidator.isValidBinding(typeBinding)){
//						if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING ||
//							typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING){
//							boolean error = false;
//							if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING &&
//								((FlexibleRecordBinding)typeBinding).getDeclaredFields().size() == 0 &&
//								((FlexibleRecordBinding)typeBinding).getDefaultSuperType() == null){
//								error = true;
//							}
//							
//							if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING &&
//									((FixedRecordBinding)typeBinding).getStructureItems().size() == 0 &&
//									((FixedRecordBinding)typeBinding).getDefaultSuperType() == null ){
//									error = true;
//							}
//							
//							if (error){
//								problemRequestor.acceptProblem(programParameter,
//										IProblemRequestor.RECORD_PARAMETER_WITH_NO_CONTENTS,
//										new String[] {programParameter.getName().getCanonicalName(),programParameter.getName().getCanonicalName()});
//							}
//						}
//						else if (typeBinding.getKind() == ITypeBinding.FORM_BINDING){
//							if (InternUtil.intern(typeBinding.getName()) != InternUtil.intern(programParameter.getName().getCanonicalName())){
//								problemRequestor.acceptProblem(programParameter,
//										IProblemRequestor.INVALID_FORM_TYPEDEF,
//										new String[] {programParameter.getName().getCanonicalName(),typeBinding.getCaseSensitiveName()});
//							}
//						}
//						new ProgramParameterValidator(problemRequestor).validate(typeBinding, programParameter.getType());
//						
//						checkParmTypeNotStaticArray(programParameter, programParameter.getType());
//					}
//					
//				}
//				return false;
//			}
//			
//
//		});
	}
	
	
//	private void checkParmTypeNotStaticArray(ProgramParameter parm, Type parmType) {
//
//		if (parmType.isNullableType()) {
//			checkParmTypeNotStaticArray(parm, parmType.getBaseType());
//		}
//		
//		if(parmType.isArrayType()) {
//			if (((ArrayType) parmType).hasInitialSize()) {
//        	problemRequestor.acceptProblem(
//        		parmType,
//				IProblemRequestor.STATIC_ARRAY_PGM_PARAMETER_DEFINITION,
//				new String[] {parm.getName().getCanonicalName()});   
//			}
//			else {
//				checkParmTypeNotStaticArray(parm, ((ArrayType) parmType).getElementType());
//			}
//        }
//			
//	}

	
	protected void validateProgramFunctions(){
		program.accept(new AbstractASTVisitor(){
			boolean main = false;
			public boolean visit (NestedFunction nestedFunction){
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
