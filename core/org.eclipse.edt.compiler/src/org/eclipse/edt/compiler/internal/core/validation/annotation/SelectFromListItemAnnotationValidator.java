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

import org.eclipse.edt.compiler.binding.AnnotationValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
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
public class SelectFromListItemAnnotationValidator extends AnnotationValidationAnnotationTypeBinding {
	
	public SelectFromListItemAnnotationValidator() {
		super(InternUtil.internCaseSensitive("selectFromListItem"));
	}
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SELECTFROMLISTITEM));
		if (annotationBinding.getValue() != null) {
			if (annotationBinding.getValue() instanceof Name){
				IDataBinding value = ((Name) annotationBinding.getValue()).resolveDataBinding();
				
				if(IDataBinding.STRUCTURE_ITEM_BINDING == value.getKind()) {
					StructureItemBinding sItemBinding = (StructureItemBinding) value;
					if(sItemBinding.getParentItem() != null || !sItemBinding.getChildren().isEmpty()) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.SELECTION_ITEM_MUST_BE_TOP_LEVEL_AND_LEAF,
							new String[] {
								value.getCaseSensitiveName(),
								IEGLConstants.PROPERTY_SELECTFROMLISTITEM
							}
						);
					}
				}
				
				if(IDataBinding.CLASS_FIELD_BINDING == value.getKind() &&
				   ITypeBinding.LIBRARY_BINDING == value.getDeclaringPart().getKind()) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.SELECTION_ITEM_MAY_NOT_BE_LIBRARY_FIELD,
						new String[] {
							value.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_SELECTFROMLISTITEM
						}
					);
				}
				
				ITypeBinding valueType = value.getType();
				if(Binding.isValidBinding(valueType)) {
					if(ITypeBinding.ARRAY_TYPE_BINDING == valueType.getKind()) {
						ITypeBinding valueElementType = ((ArrayTypeBinding) valueType).getElementType();
						if(ITypeBinding.PRIMITIVE_TYPE_BINDING == valueElementType.getKind()) {
							int prim = ((PrimitiveTypeBinding) valueElementType).getPrimitive().getType();
							if(Primitive.HEX_PRIMITIVE == prim || Primitive.BLOB_PRIMITIVE == prim) {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.BLOB_OR_HEX_USED_WITH_SELECTION_PROPERTY,
									new String[] {
										IEGLConstants.PROPERTY_SELECTFROMLISTITEM,
										StatementValidator.getTypeString(valueType)
									});
							}
						}
					}
				}
			}	
		}
	}
}
