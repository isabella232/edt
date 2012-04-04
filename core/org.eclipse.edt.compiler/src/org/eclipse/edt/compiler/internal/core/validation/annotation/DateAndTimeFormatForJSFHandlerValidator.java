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
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class DateAndTimeFormatForJSFHandlerValidator extends DefaultFieldContentAnnotationValidationRule {
	
	private static final int MIN_LENGTH_FOR_TEXT_ITEM_WITH_DATEFORMAT = 10;
	private static final int MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_DDD_YYYY_DATEFORMAT = 7;
	private static final int MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_DDD_YY_DATEFORMAT = 5;
	private static final int MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_YYYY_DATEFORMAT = 8;
	private static final int MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_YY_DATEFORMAT = 6;
	private static final int MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_OTHER_DATEFORMAT = 8;
	
	private static final int MIN_LENGTH_FOR_TEXT_ITEM_WITH_TIMEFORMAT = 8;
	private static final int MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_TIMEFORMAT = 6;

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		ITypeBinding targetType = containerBinding.getType();
		if(targetType != null && IBinding.NOT_FOUND_BINDING != targetType && ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind()) {
			PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) targetType;
			if(primTypeBinding.getDecimals() == 0) {
				Primitive prim = primTypeBinding.getPrimitive();
				
				if (prim == Primitive.STRING) {
					return;
				}
				
				int length = primTypeBinding.getLength();
				
				IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_DATEFORMAT));
				if(annotationBinding != null && annotationBinding.getValue() != null) {
					if(Primitive.isStringType(prim)) {
						if(length < MIN_LENGTH_FOR_TEXT_ITEM_WITH_DATEFORMAT) {
							problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.LENGTH_OF_CHARACTER_ITEM_TOO_SHORT_FOR_DATETIMEFORMAT,
								new String[] {
									containerBinding.getCaseSensitiveName(),
									Integer.toString(MIN_LENGTH_FOR_TEXT_ITEM_WITH_DATEFORMAT),
									IEGLConstants.PROPERTY_DATEFORMAT
								});
						}
					}
					else if(Primitive.isNumericType(prim)) {
						String value = (String) annotationBinding.getValue();
						int minLength = 0;
						if(value.indexOf("DDD") != -1) {
							if(value.indexOf("yyyy") != -1) {
								minLength = MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_DDD_YYYY_DATEFORMAT;
							}
							else if(value.indexOf("yy") != -1) {
								minLength = MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_DDD_YY_DATEFORMAT;
							}
						}
						else {
							if(value.indexOf("yyyy") != -1) {
								minLength = MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_YYYY_DATEFORMAT;
							}
							else if(value.indexOf("yy") != -1) {
								minLength = MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_YY_DATEFORMAT;
							}
						}
						if(minLength == 0) {
							minLength = MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_OTHER_DATEFORMAT;
						}
						if(length < minLength) {
							problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.LENGTH_TOO_SHORT_FOR_DATEFORMAT_MASK,
								new String[] {
									containerBinding.getCaseSensitiveName(),
									Integer.toString(minLength),
									value
								});
						}
					}
				}
				else {
					annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_TIMEFORMAT));
					if(annotationBinding != null && annotationBinding.getValue() != null) {
						if(Primitive.isStringType(prim)) {
							if(length < MIN_LENGTH_FOR_TEXT_ITEM_WITH_TIMEFORMAT) {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.LENGTH_OF_CHARACTER_ITEM_TOO_SHORT_FOR_DATETIMEFORMAT,
									new String[] {
										containerBinding.getCaseSensitiveName(),
										Integer.toString(MIN_LENGTH_FOR_TEXT_ITEM_WITH_TIMEFORMAT),
										IEGLConstants.PROPERTY_TIMEFORMAT
									});
							}
						}
						else if(Primitive.isNumericType(prim)) {
							if(length < MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_TIMEFORMAT) {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.LENGTH_OF_NUMERIC_ITEM_TOO_SHORT_FOR_DATETIMEFORMAT,
									new String[] {
										containerBinding.getCaseSensitiveName(),
										Integer.toString(MIN_LENGTH_FOR_NUMERIC_ITEM_WITH_TIMEFORMAT),
										IEGLConstants.PROPERTY_TIMEFORMAT
									});
							}
						}
					}
				}
			}
		}
	}
}
