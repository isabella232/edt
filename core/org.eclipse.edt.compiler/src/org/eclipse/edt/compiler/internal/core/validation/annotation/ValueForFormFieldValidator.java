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

import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class ValueForFormFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALUE));		
		if(annotationBinding != null) {
			Object value = annotationBinding.getValue();
			if(containerBinding != null && IDataBinding.FORM_FIELD == containerBinding.getKind()) {
				FormFieldBinding fieldBinding = (FormFieldBinding) containerBinding;
				
				ITypeBinding targetTypeBinding = null;
				if(fieldBinding.isConstant()) {
					targetTypeBinding = PrimitiveTypeBinding.getInstance(Primitive.CHAR, Integer.MAX_VALUE);				 
				}
				else {
					targetTypeBinding = fieldBinding.getType();
				}
				
				if(targetTypeBinding != null) {
					if(ITypeBinding.PRIMITIVE_TYPE_BINDING == targetTypeBinding.getKind()) {
						PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) targetTypeBinding;
						Primitive prim = primTypeBinding.getPrimitive();
						
						if(Primitive.isStringType(prim) || Primitive.NUM == prim || Primitive.NUMC == prim) {
							if(value instanceof String || (!Primitive.isStringType(prim) && value instanceof Number)) {
								if(Primitive.STRING != prim) {
									if(!fieldBinding.isConstant()) {
										int maxLength;
										IAnnotationBinding aBinding = fieldBinding.getAnnotation(new String[] {"egl", "ui"}, "FieldLen");
										if(aBinding != null) {
											maxLength = ((Integer) aBinding.getValue()).intValue();
										}
										else {
											maxLength = primTypeBinding.getLength();
										}
										
										if(lengthWithoutBogusChars(value.toString()) > maxLength) {
											problemRequestor.acceptProblem(
												errorNode,
												IProblemRequestor.PROPERTY_LENGTH_EXCEEDS_DEFINED_LENGTH,
												new String[] {
													value.toString(),
													IEGLConstants.PROPERTY_VALUE,
													String.valueOf(maxLength)});
										}
									}
								}								
							}
							else {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.INVALID_FORM_FIELD_VALUE_PROPERTY_VALUE,
									new String[] {IEGLConstants.PROPERTY_VALUE, canonicalContainerName, fieldBinding.getDeclaringPart().getCaseSensitiveName()});
							}
						}
						else if(Primitive.isIntegerType(prim)) {
							if(value instanceof String ||
							   value instanceof Object[] ||
							   value instanceof IBinding) {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.PROPERTY_REQUIRES_INTEGER,
									new String[] {IEGLConstants.PROPERTY_VALUE});
							}
						}
					}
				}
			}
		}
	}
		
	private int lengthWithoutBogusChars(String input) {
		
		int len = 0;
		
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			//Strip out bidi marks
			if (chars[i] != '\u200E' && chars[i] != '\u200F') {
				len = len + 1;
			}
		}
		
		return len;
		
	}
}
