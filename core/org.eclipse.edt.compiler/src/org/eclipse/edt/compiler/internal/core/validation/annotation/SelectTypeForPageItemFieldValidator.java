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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class SelectTypeForPageItemFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public static final Set STRINGS_OR_CHARS = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.STRING,
		Primitive.CHAR
	}));

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SELECTTYPE));
		EnumerationDataBinding value = null;
		if(annotationBinding != null) {
			value = (EnumerationDataBinding) annotationBinding.getValue();
		}
		
		IAnnotationBinding selectFromListItemABinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SELECTFROMLISTITEM));
		
		if(selectFromListItemABinding == null) {
			if(value != null) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.SELECTTYPE_REQUIRES_SELECTFROMLIST);
			}
		}
		else if(value == null || InternUtil.intern("INDEX") == value.getName()) {
			ITypeBinding containerType = containerBinding.getType();
			if(containerType != null && IBinding.NOT_FOUND_BINDING != containerType) {
				boolean compatible =
					TypeCompatibilityUtil.typesAreIdentical(
						containerType.getBaseType(),
						PrimitiveTypeBinding.getInstance(Primitive.INT),
						compilerOptions);
					
				if(!compatible){
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.SELECTTYPE_TARGET_MUST_BE_INT,
						new String[] {
							containerBinding.getCaseSensitiveName()
						});
				}
			}
		}
		else if(InternUtil.intern("VALUE") == value.getName()) {
			Object selectFromListItemValue = selectFromListItemABinding.getValue();
			if(selectFromListItemValue instanceof Name) {
				IDataBinding selectFromListItemDBinding = ((Name) selectFromListItemABinding.getValue()).resolveDataBinding();
				if(selectFromListItemDBinding != IBinding.NOT_FOUND_BINDING && selectFromListItemDBinding != null) {
					ITypeBinding selectFromListItemType = selectFromListItemDBinding.getType();
					if(selectFromListItemType != null && containerBinding.getType() != null) {
						
						ITypeBinding containerType = containerBinding.getType();
						if(containerType != null && IBinding.NOT_FOUND_BINDING != containerType) {
							boolean compatible = typesIdenticalOrAreCharAndString(
								containerType.getBaseType(),
								selectFromListItemType.getBaseType(),
								compilerOptions);
								
							if(!compatible){
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.SELECTTYPE_ITEM_MUST_MATCH_ARRAY,
									new String[] {
										((Name) selectFromListItemValue).getCanonicalName(),
										containerBinding.getCaseSensitiveName()
									});
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean typesIdenticalOrAreCharAndString(ITypeBinding type1, ITypeBinding type2, ICompilerOptions compilerOptions) {
		return TypeCompatibilityUtil.typesAreIdentical(type1, type2, compilerOptions) ||
		       typesInPrimitiveClass(new ITypeBinding[] {type1, type2}, STRINGS_OR_CHARS);
	}

	public static boolean typesInPrimitiveClass(ITypeBinding[] bindings, Set primitives) {
		for(int i = 0; i < bindings.length; i++) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING != bindings[i].getKind() ||
			   !primitives.contains(((PrimitiveTypeBinding) bindings[i]).getPrimitive())) {
				return false;
			}
		}
		return true;
	}		
}
