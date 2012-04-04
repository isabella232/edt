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
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author Dave Murray
 */
public class SQLDataCodeAnnotationValidator extends AnnotationValidationAnnotationTypeBinding {
	
	public static AnnotationValidationAnnotationTypeBinding INSTANCE = new SQLDataCodeAnnotationValidator();
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;
	
	private SQLDataCodeAnnotationValidator() {
		super(InternUtil.internCaseSensitive("SQLDataCodeAnnotationValidator"));
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){

		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SQLDATACODE));
		
		// TODO Remove this null check and possibly the not found check?
		if(annotationBinding.getValue() != null  && annotationBinding.getValue()!= IBinding.NOT_FOUND_BINDING){
			
			if(targetTypeBinding != null && IBinding.NOT_FOUND_BINDING != targetTypeBinding) {
				String typeName = targetTypeBinding.getName();
			
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == targetTypeBinding.getKind()) {
					Primitive prim = ((PrimitiveTypeBinding) targetTypeBinding).getPrimitive();
					int primType = prim.getType();
					typeName = prim.getName();
					boolean primTypeIsValid = true;
					
					try {
						int value = Integer.parseInt(annotationBinding.getValue().toString());
					
						switch(primType) {
							//char primitives must match with sqlDataCodes 448,449,452,453,456,457					
							case Primitive.CHAR_PRIMITIVE:
								primTypeIsValid = value == 448 || value == 449 || value == 452 ||
								                  value == 453 || value == 456 || value == 457 ||
								                  value == 384 || value == 385 || value == 388 ||
								                  value == 389 || value == 392 || value == 393;
								break;
								
							//dbchar primitives must match with sqlDataCodes 464,465,468,469,472,473
							case Primitive.DBCHAR_PRIMITIVE:
								primTypeIsValid = value == 464 || value == 465 || value == 468 ||
								                  value == 469 || value == 472 || value == 473;
								break;
								
							//dbchar primitives must match with sqlDataCodes 464,465,468,469,472,473
							case Primitive.UNICODE_PRIMITIVE:
								primTypeIsValid = value == 464 || value == 465 || value == 468 ||
								                  value == 469 || value == 472 || value == 473 ||
								                  value == 385 || value == 389 || value == 393 ||
								                  value == 384 || value == 388 || value == 392;
								break;
						
							//hex primitives must match with sqlDataCodes:		
							//500,501 496,497 492,493 480,481 484,485 452,453 448,449
							//456,457 (448,449) 468,469 464,465 472,473 400,401 384,385
							//388,389 392,393
							case Primitive.HEX_PRIMITIVE:
								break;
							//String primitives must match with sqlDataCodes:		
							//468, 469 384,385 388,389 392,393
							case Primitive.STRING_PRIMITIVE:
								primTypeIsValid = value == 468 || value == 469 || value == 385 || 
								                  value == 389 || value == 393 || value == 384 || 
								                  value == 388 || value == 392;
								break;
								
							
							default:
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.PROPERTY_INVALID_PRIMITIVE_USED_WITH_SQLDATACODE_PROPERTY,
									new String[] {StatementValidator.getShortTypeString(targetTypeBinding)});
						}
					}
					catch(NumberFormatException e) {
						primTypeIsValid = false;
					}
					
					if(!primTypeIsValid) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.PROPERTY_INVALID_SQLDATACODE_AND_PRIMITIVE_COMBINATION,
							new String[] {
								annotationBinding.getValue().toString(),
								StatementValidator.getShortTypeString(targetTypeBinding)
							});
					}
				}
				else {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PROPERTY_INVALID_PRIMITIVE_USED_WITH_SQLDATACODE_PROPERTY,
						new String[] {StatementValidator.getShortTypeString(targetTypeBinding)});
				}
			}
		}
	}
}
