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

import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTStatementVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.AttrType;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class LibraryValidator extends FunctionContainerValidator {
	String libraryName = "";
	Library library = null;
	
	public LibraryValidator(IProblemRequestor problemRequestor, LibraryBinding partBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, partBinding, compilerOptions);
		libraryName = partBinding.getCaseSensitiveName();
	}
	
	public boolean visit(Library alibrary) {
		this.library = alibrary;
		partNode = alibrary;
		EGLNameValidator.validate(library.getName(), EGLNameValidator.LIBRARY, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(alibrary);
		checkCallInterface(alibrary.getName(), (LibraryBinding) alibrary.getName().resolveBinding());
		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		validateLibraryFunctions(nestedFunction);
		return false;
	}
	
	
	private void validateLibraryFunctions(final NestedFunction nestedFunction) {
		final LibraryBinding libBinding = (LibraryBinding)partBinding;
		
		if (InternUtil.intern(nestedFunction.getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(library.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {libraryName});
			return;
		}
		nestedFunction.accept(new AbstractASTStatementVisitor(){
			public void visitStatement(Statement stmt){
				 if (libBinding.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null){
				 	problemRequestor.acceptProblem(stmt,
				 			IProblemRequestor.NATIVE_LIBRARY_FUNCTIONS_DO_NOT_SUPPORT_STATEMENTS);
				 }
			}
			
			public boolean visit(FunctionDataDeclaration functionDataDeclaration){
				if (libBinding.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null){
					problemRequestor.acceptProblem(functionDataDeclaration,
							functionDataDeclaration.isConstant()? IProblemRequestor.NATIVE_LIBRARY_FUNCTIONS_DO_NOT_SUPPORT_CONSTANT_DECLARATIONS :
							IProblemRequestor.NATIVE_LIBRARY_FUNCTIONS_DO_NOT_SUPPORT_DECLARATIONS,
							new String[] {
									((SimpleName)functionDataDeclaration.getNames().get(0)).getCanonicalName(),
									nestedFunction.getName().getCanonicalName()});
				}
				return false;
			}
		});
		
		nestedFunction.accept(new AbstractASTVisitor(){
			public boolean visit(FunctionParameter functionParameter) {
				
		        Type parmType = functionParameter.getType();
		        ITypeBinding binding =  parmType.resolveTypeBinding();
		        if (parmType.isPrimitiveType()){
		        	PrimitiveType pType = (PrimitiveType)parmType;
		        	int primitiveInt = pType.getPrimitive().getType();
					
					int primitiveLength = ((PrimitiveTypeBinding)binding).getLength();
					
			        	
		        	if(primitiveInt == Primitive.BIN_PRIMITIVE || 
		        			primitiveInt == Primitive.NUM_PRIMITIVE || 
		        			primitiveInt == Primitive.NUMC_PRIMITIVE || 
		        			primitiveInt == Primitive.PACF_PRIMITIVE ||
		        			primitiveInt == Primitive.CHAR_PRIMITIVE ||
		        			primitiveInt == Primitive.DBCHAR_PRIMITIVE ||
		        			primitiveInt == Primitive.MBCHAR_PRIMITIVE ||
		        			primitiveInt == Primitive.HEX_PRIMITIVE ||
		        			primitiveInt == Primitive.UNICODE_PRIMITIVE){
		        		if (!pType.hasPrimLength() ||
		        				(pType.hasPrimLength() && primitiveLength == 0)){
		        			problemRequestor.acceptProblem(parmType,
		        					IProblemRequestor.LIBRARY_PARAMETER_TYPES_MUST_SPECIFY_LENGTH,
									 new String[] {
		        					functionParameter.getName().getCanonicalName(),
		        					parmType.getCanonicalName(), 
									nestedFunction.getName().getCanonicalName(),
		                            libraryName});
		        		}
		        		
		        	}
		        	
		        	if (functionParameter.getAttrType() == AttrType.FIELD){
		        		problemRequestor.acceptProblem(functionParameter,
		        				IProblemRequestor.FUNCTION_PARAMETERS_DO_NOT_SUPPORT_NULLABLE_AND_FIELD,
								new String[] {
		        				functionParameter.getName().getCanonicalName(),
		        				nestedFunction.getName().getCanonicalName(),
		        				libraryName,
		        				IEGLConstants.KEYWORD_FIELD});
		        	}

		        }
		        
		        if (libBinding.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null){
					if (StatementValidator.isValidBinding(binding) &&( binding.getKind() == ITypeBinding.FIXED_RECORD_BINDING ||
							binding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING)){
							
								problemRequestor.acceptProblem(parmType,
										IProblemRequestor.NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_RECORDS,
										new String[] {
										functionParameter.getName().getCanonicalName(),
			                            nestedFunction.getName().getCanonicalName(),
			                            libraryName});
						}
						
	        		if (parmType.isArrayType()){
	           			problemRequestor.acceptProblem(functionParameter,
		        				IProblemRequestor.NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_ARRAYS,
					              new String[] {
		        				functionParameter.getName().getCanonicalName(),
		        				nestedFunction.getName().getCanonicalName(),
								libraryName});
		        	}
	        		
	        		if (functionParameter.isParmConst()) {
	           			problemRequestor.acceptProblem(functionParameter,
		        				IProblemRequestor.NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_REF,
					              new String[] {
		        				functionParameter.getName().getCanonicalName(),
		        				nestedFunction.getName().getCanonicalName(),
								libraryName,
								IEGLConstants.KEYWORD_CONST});
	        		}
      	

		        }
		        return true;
			}
		
		});
		
		
	}

	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
		super.visit(useStatement);
		final LibraryBinding libBinding = (LibraryBinding)partBinding;
		
		if (libBinding.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null){
			problemRequestor.acceptProblem(useStatement, 
					IProblemRequestor.NATIVE_LIBRARYS_DO_NOT_SUPPORT_USE_STATEMENTS,
			new String[] {((Name)useStatement.getNames().get(0)).getCanonicalName(),libBinding.getCaseSensitiveName()});
		}
		return false;
	}
	
	private void checkCallInterface(Name libraryNameNode, LibraryBinding libraryBinding) {
		IAnnotationBinding aBinding = libraryBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLI");
		if(aBinding != null) {
			aBinding = (IAnnotationBinding) aBinding.findData(InternUtil.intern(IEGLConstants.PROPERTY_CALLINTERFACE));
			if(aBinding != IBinding.NOT_FOUND_BINDING && InternUtil.intern("CBLTDLI") == ((EnumerationDataBinding) aBinding.getValue()).getName()) {
				problemRequestor.acceptProblem(
					libraryNameNode,
					IProblemRequestor.DLI_CBLTDLI_NOT_ALLOWED_IN_LIBRARY);
			}
		}
	}
}
