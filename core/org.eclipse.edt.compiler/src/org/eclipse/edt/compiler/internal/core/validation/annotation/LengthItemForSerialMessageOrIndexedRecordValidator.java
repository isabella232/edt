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

import java.util.HashSet;

import org.eclipse.edt.compiler.binding.DataBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author svihovec
 *
 */
public class LengthItemForSerialMessageOrIndexedRecordValidator implements IValueValidationRule {

	private static final HashSet validLengthItemTypes = new HashSet();
	
	static{
		validLengthItemTypes.add(Primitive.BIN);
		validLengthItemTypes.add(Primitive.NUM);
		validLengthItemTypes.add(Primitive.NUMC);
		validLengthItemTypes.add(Primitive.PACF);
		validLengthItemTypes.add(Primitive.DECIMAL);
		validLengthItemTypes.add(Primitive.INT);
		validLengthItemTypes.add(Primitive.SMALLINT);
	}
	
	/**
	 * Validate that the length items type is one of: bin, num, numc, packf, decimal, int, smallint
	 * Validate that the length item has a length <= 9
	 * Validate that the length item has no decimals
	 */
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		
		String canonicalRecordName = TypeNameUtility.getName(target);
			
		DataBinding dataBinding = null;

		if (annotationBinding.getValue() instanceof DataBinding) {
			dataBinding = (DataBinding) annotationBinding.getValue();
		}
		else {
			if (annotationBinding.getValue() instanceof Expression) {
				if (((Expression)annotationBinding.getValue()).resolveDataBinding() instanceof DataBinding) {
					dataBinding = (DataBinding) ((Expression)annotationBinding.getValue()).resolveDataBinding();
				}
			}
		}
		
		if(dataBinding != null){
			if(dataBinding.getType().getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
				PrimitiveTypeBinding primitiveTypeBinding = ((PrimitiveTypeBinding)dataBinding.getType());
				
				if(!validLengthItemTypes.contains(primitiveTypeBinding.getPrimitive())){
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PROPERTY_INVALID_TYPE_FOR_RECORD_ITEM_PROPERTY,
						new String[] {
							canonicalRecordName,
							IEGLConstants.PROPERTY_LENGTHITEM,
							"\"" + dataBinding.getCaseSensitiveName() + "\"",
	            			canonicalRecordName,
							primitiveTypeBinding.getPrimitive().getName()
						});
				}
				else {
					if(primitiveTypeBinding.getLength() > 9){
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.RECORD_ITEM_PROPERTY_VALUE_TOO_LONG,
							new String[] {
								canonicalRecordName,
								IEGLConstants.PROPERTY_LENGTHITEM,
		            			"\"" + dataBinding.getCaseSensitiveName() + "\"",
		            			canonicalRecordName,
								Integer.toString(primitiveTypeBinding.getLength())
							});
					}
					
					if(primitiveTypeBinding.getDecimals() > 0){
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.RECORD_ITEM_PROPERTY_VALUE_HAS_DECIMALS,
							new String[] {
								canonicalRecordName,
								IEGLConstants.PROPERTY_LENGTHITEM,
	        					"\"" + dataBinding.getCaseSensitiveName() + "\"",
								canonicalRecordName
							});
					}
				}
			}
			else{
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.PROPERTY_INVALID_UNKNOWN_TYPE_FOR_RECORD_ITEM_PROPERTY,
					new String[] {
						canonicalRecordName,
						IEGLConstants.PROPERTY_LENGTHITEM,
            			"\"" + dataBinding.getCaseSensitiveName() + "\"",
            			canonicalRecordName
					});
			}
		}
	}
}
