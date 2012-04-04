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

import java.util.Map;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public abstract class AbstractSelectedItemAnnotationValidator extends DefaultFieldContentAnnotationValidationRule implements IFieldContentAnnotationValidationRule, IAnnotationValidationRule {
	
	private String propertyName;
	
	public AbstractSelectedItemAnnotationValidator(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		doValidate(errorNode, target, null, targetTypeBinding, null, allAnnotations, problemRequestor, compilerOptions);		
	}
	
	public void validate(Node errorNode, Node target, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		ITypeBinding targetTypeBinding = containerBinding.getType();
		if(targetTypeBinding != null && IBinding.NOT_FOUND_BINDING != targetTypeBinding) {
			doValidate(errorNode, target, containerBinding, targetTypeBinding, canonicalContainerName, allAnnotations, problemRequestor, compilerOptions);
		}
	}
	
	public void doValidate(Node errorNode, Node target, IBinding targetBinding, ITypeBinding targetTypeBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(propertyName));
		if(annotationBinding != null) {
			if (annotationBinding.getValue() != null) {
				if (annotationBinding.getValue() instanceof Name){
					IDataBinding value = ((Name) annotationBinding.getValue()).resolveDataBinding();
					if(value != null && IBinding.NOT_FOUND_BINDING != value) {
						ITypeBinding tBinding = value.getType();
						if(tBinding != null && IBinding.NOT_FOUND_BINDING != tBinding) {
							if(ITypeBinding.DATATABLE_BINDING == targetTypeBinding.getKind() ||
							   ITypeBinding.ARRAY_TYPE_BINDING == targetTypeBinding.getKind() &&
							   (ITypeBinding.FLEXIBLE_RECORD_BINDING == targetTypeBinding.getBaseType().getKind() ||
							    ITypeBinding.FIXED_RECORD_BINDING == targetTypeBinding.getBaseType().getKind())) {
								targetIsDataTableOrRecordArray(errorNode, targetBinding, targetTypeBinding, tBinding, value, allAnnotations, problemRequestor, compilerOptions);		
							}
							else if(ITypeBinding.ARRAY_TYPE_BINDING == targetTypeBinding.getKind() &&
									ITypeBinding.PRIMITIVE_TYPE_BINDING == targetTypeBinding.getBaseType().getKind()) {
								int targetPrim = ((PrimitiveTypeBinding) targetTypeBinding.getBaseType()).getPrimitive().getType();
								switch(targetPrim) {
									case Primitive.HEX_PRIMITIVE:
									case Primitive.BLOB_PRIMITIVE:
										problemRequestor.acceptProblem(
											errorNode,
											IProblemRequestor.BLOB_OR_HEX_USED_WITH_SELECTION_PROPERTY,
											new String[] {
												propertyName,
												StatementValidator.getTypeString(targetTypeBinding)
											});				
										break;
									default:
										targetIsPrimitiveArray(errorNode, targetBinding, targetTypeBinding, tBinding, value, allAnnotations, problemRequestor, compilerOptions);
								}
							}
							else {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.PROPERTY_ONLY_VALID_FOR_ARRAYS,
									new String[] {
										propertyName	
									});
							}
						}
					}
				}
			}
		}
	}

	protected abstract void targetIsDataTableOrRecordArray(Node errorNode, IBinding targetBinding, ITypeBinding targetTypeBinding, ITypeBinding selectedItemType, IDataBinding value, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
	protected abstract void targetIsPrimitiveArray(Node errorNode, IBinding targetBinding, ITypeBinding targetTypeBinding, ITypeBinding selectedItemType, IDataBinding value, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
}
