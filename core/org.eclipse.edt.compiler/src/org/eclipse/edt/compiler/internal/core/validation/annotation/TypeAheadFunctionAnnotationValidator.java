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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class TypeAheadFunctionAnnotationValidator extends DefaultFieldContentAnnotationValidationRule {
	
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
 		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_TYPEAHEADFUNCTION));
		if(annotationBinding != null && annotationBinding.getValue() != null && containerBinding != null && containerBinding != IBinding.NOT_FOUND_BINDING && containerBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING) {
			internalValidate(annotationBinding,errorNode,container, containerBinding,canonicalContainerName, allAnnotations, problemRequestor, compilerOptions);
		}
	}
	
	protected void internalValidate(IAnnotationBinding annotationBinding, Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IFunctionBinding value = (IFunctionBinding)annotationBinding.getValue();
	
		if(value != null && value != IBinding.NOT_FOUND_BINDING){
			boolean isValidSignature = false;
			if (value.getParameters().size() == 1) {
			List params = value.getParameters();
			FunctionParameterBinding paramBinding = (FunctionParameterBinding)params.get(0);
			ITypeBinding paramTypeBinding = paramBinding.getType();
			if (paramTypeBinding != null && paramTypeBinding != IBinding.NOT_FOUND_BINDING){
				if (paramTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
					Primitive prim = ((PrimitiveTypeBinding)paramTypeBinding).getPrimitive();
					if (Primitive.isStringType(prim)){
						//check return type
						ITypeBinding retTypeBinding = value.getReturnType(); 
						if (retTypeBinding != null && retTypeBinding!= IBinding.NOT_FOUND_BINDING){
							if (retTypeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
								ITypeBinding baseType = retTypeBinding.getBaseType();
								if (baseType != null && baseType != IBinding.NOT_FOUND_BINDING){
									if (baseType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
										prim = ((PrimitiveTypeBinding)baseType).getPrimitive();
										if (Primitive.isStringType(prim)){
											isValidSignature = true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (!isValidSignature){
			problemRequestor.acceptProblem(
			errorNode,
			IProblemRequestor.TYPEAHEAD_FUNCTION_BAD_SIGNATURE,
			new String[] {value.getCaseSensitiveName()});
		}
		
		if (containerBinding != null && containerBinding != IBinding.NOT_FOUND_BINDING){
			if (containerBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
				if (((StructureItemBinding)containerBinding).getChildren().size()> 0 && ((StructureItem)container).hasLevel()){
					problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.TYPEAHEAD_MUST_BE_ON_LEAF_ITEM,
							new String[] {IEGLConstants.PROPERTY_TYPEAHEADFUNCTION});
				}
			}
			
			ITypeBinding typeBinding = containerBinding.getType();
			if (typeBinding != null && typeBinding != IBinding.NOT_FOUND_BINDING ){
				if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
					typeBinding = typeBinding.getBaseType();
				}
				
				if (typeBinding.getKind() != typeBinding.PRIMITIVE_TYPE_BINDING){
					problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.TYPE_INVALID_FOR_TYPEAHEAD,
							new String[] {IEGLConstants.PROPERTY_TYPEAHEADFUNCTION,typeBinding.getCaseSensitiveName()});
				}
			}
		}
		
	}
		
	}
}
