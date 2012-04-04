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

import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author svihovec
 *
 */
public class NumElementsItemForSerialMessageOrIndexedRecordValidator implements IValueValidationRule{
	
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
	 *
	 */
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		String canonicalRecordName = TypeNameUtility.getName(target);

		StructureItemBinding structureItemBinding = null;

		if (annotationBinding.getValue() instanceof StructureItemBinding) {
			structureItemBinding = (StructureItemBinding) annotationBinding.getValue();
		}
		else {
			if (annotationBinding.getValue() instanceof Expression) {
				if (((Expression)annotationBinding.getValue()).resolveDataBinding() instanceof StructureItemBinding) {
					structureItemBinding = (StructureItemBinding) ((Expression)annotationBinding.getValue()).resolveDataBinding();
				}
			}
		}
		
		if(structureItemBinding != null){
			if(structureItemBinding.getType().getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
				PrimitiveTypeBinding primitiveTypeBinding = ((PrimitiveTypeBinding)structureItemBinding.getType());
				
				if(!validLengthItemTypes.contains(primitiveTypeBinding.getPrimitive())){
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PROPERTY_INVALID_TYPE_FOR_NUM_ELEMENTS,
						new String[] {
	            			"\"" + structureItemBinding.getCaseSensitiveName() + "\"",
	            			canonicalRecordName,
							primitiveTypeBinding.getPrimitive().getName()
						});
				}
				else {
					if(primitiveTypeBinding.getLength() > 9){
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.NUM_ELEMENTS_ITEM_VALUE_TOO_LONG,
							new String[] {
		            			"\"" + structureItemBinding.getCaseSensitiveName() + "\"",
		            			canonicalRecordName,
								primitiveTypeBinding.getName()
							});
					}
					
					if(primitiveTypeBinding.getDecimals() > 0){
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.NUM_ELEMENTS_ITEM_VALUE_HAS_DECIMALS,
							new String[] {
	        					"\"" + structureItemBinding.getCaseSensitiveName() + "\"",
								canonicalRecordName
							});
					}
				}
			}
			else{
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.PROPERTY_INVALID_TYPE_FOR_NUM_ELEMENTS,
					new String[] {
            			"\"" + structureItemBinding.getCaseSensitiveName() + "\"",
            			canonicalRecordName,
						IEGLConstants.KEYWORD_CHAR
					});
			}
			
			validateNumElementsItem(errorNode, target, canonicalRecordName, structureItemBinding, problemRequestor);
		}
	}
	
	private void validateNumElementsItem(Node errorNode, Node target, String canonicalRecordName, StructureItemBinding numElementsItem, IProblemRequestor problemRequestor){
		// record must contain an array as the last element
		ITypeBinding typeBinding = (ITypeBinding) TypeNameUtility.getType(target);
		
		if(typeBinding != null && ITypeBinding.FIXED_RECORD_BINDING == typeBinding.getKind()) {
			FixedRecordBinding recordBinding = (FixedRecordBinding) typeBinding;
			
			StructureItemBinding structureItem = (StructureItemBinding)recordBinding.getStructureItems().get(recordBinding.getStructureItems().size()-1);
				
			// Last item cannot be the numElementsItem
			if(structureItem == numElementsItem){
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.NUM_ELEMENTS_ITEM_CANNOT_HAVE_SAME_NAME_AS_LAST_ITEM, 
					new String[] {
						canonicalRecordName,
						"\"" + numElementsItem.getCaseSensitiveName() + "\""
					});
			}else{
				// Last item must have occurs
				if(!structureItem.definedWithOccurs()) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.NUM_ELEMENTS_HAS_AN_INVALID_NUMBER_OF_OCCURS, 
						new String[] {
							canonicalRecordName,
							"\"" + numElementsItem.getCaseSensitiveName() + "\"",
							Integer.toString(0),
							structureItem.getName()
						});
				}
			}
	
			// numElementsItem cannot be nested under last item 
			StructureItemBinding parentItem = numElementsItem.getParentItem();
			
			while(parentItem != null){
				if(parentItem == structureItem){
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.ITEM_REFERENCED_BY_NUM_ELEMENTS_MUST_BE_IN_RECORD, 
						new String[] {
							"\"" + numElementsItem.getCaseSensitiveName() + "\"",
							canonicalRecordName
						});
					break;
				}else{
					parentItem = parentItem.getParentItem();
				}
			}
		}
	}
}
