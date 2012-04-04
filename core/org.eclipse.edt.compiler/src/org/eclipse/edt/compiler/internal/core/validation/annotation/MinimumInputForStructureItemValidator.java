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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class MinimumInputForStructureItemValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUT))){
			
			int minimumInputLength = ((Integer)((IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUT))).getValue()).intValue();
				
			int definedLength = getFieldLength(container);
			if (definedLength < 0) {
				return;
			}
			
			int maxLengthAllowed = definedLength;
			
			//decimal point
			if(hasDecimals(container)){
				maxLengthAllowed += 1;
			}
			
			// sign
			if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_SIGN))){
				// if yes
				maxLengthAllowed += 1;
			}

			//currency
			if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_CURRENCY))){
				// if yes
				maxLengthAllowed += 1;
			}
			
			// TODO CurrencyValue?
	
    		// numeric separator
			if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_NUMERICSEPARATOR))){
				// if yes
				maxLengthAllowed += 1;
			}

			if(minimumInputLength > maxLengthAllowed) {
				problemRequestor.acceptProblem(errorNode, 
												IProblemRequestor.PROPERTY_MINIMUM_INPUT_MUST_BE_LESS_THAN_PRIMITIVE_LENGTH,
												new String[]{String.valueOf(minimumInputLength), String.valueOf(maxLengthAllowed), ""});
										
			}
		}
		
	}

	private boolean hasDecimals(Node expression) {
		final Boolean[] value = new Boolean[1];
		
		expression.accept(new DefaultASTVisitor(){
			public boolean visit(StructureItem structureItem) {
				Type type = structureItem.getType();
				
				type.accept(new DefaultASTVisitor(){
				
					public boolean visit(NameType nameType) {
						ITypeBinding binding = nameType.resolveTypeBinding();
						
						value[0] = new Boolean(((DataItemBinding)binding).getPrimitiveTypeBinding().getDecimals() > 0);
						return false;
					}
					
					public boolean visit(PrimitiveType primitiveType) {
						value[0] = new Boolean(primitiveType.hasPrimDecimals());
						return false;
					}
				});
				
				return false;
			}
		});
		
		return value[0].booleanValue();
	}

	private int getPrimLengthForMinInput(ITypeBinding type) {
		
		//Return -1 for the types that have no length. That way we wont put out an error for them
		
		if (Binding.isValidBinding(type)) {
			return -1;
		}
		
		if (type.getKind() == ITypeBinding.DATAITEM_BINDING) {
			return getPrimLengthForMinInput(((DataItemBinding)type).getPrimitiveTypeBinding());
		}
		
		if (type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			
			PrimitiveTypeBinding prim = (PrimitiveTypeBinding) type;
			if (prim.getPrimitive() != null && prim.getPrimitive() == Primitive.STRING) {
				return -1;
			}
			
			if (prim.getPrimitive() != null && prim.getPrimitive() == Primitive.ANY) {
				return -1;
			}
			
			return prim.getLength();
		}
		return -1;
		
	}
	private int getFieldLength(Node expression) {
		final Integer[] length = new Integer[1];
		
		expression.accept(new DefaultASTVisitor(){
			public boolean visit(StructureItem structureItem) {
				Type type = structureItem.getType();
				
				type.accept(new DefaultASTVisitor(){
				
					public boolean visit(org.eclipse.edt.compiler.core.ast.ArrayType arrayType) {
						return true;
					}
					
					public boolean visit(NameType nameType) {
						ITypeBinding binding = nameType.resolveTypeBinding();
						
						
						length[0] = new Integer(getPrimLengthForMinInput(binding));
						return false;
					}
					
					public boolean visit(PrimitiveType primitiveType) {
						if (primitiveType.hasPrimLength()) {
							length[0] = new Integer(primitiveType.getPrimLength());
						}
						else {
							if (Binding.isValidBinding(primitiveType.resolveTypeBinding())) {
								length[0] = new Integer(getPrimLengthForMinInput((PrimitiveTypeBinding) primitiveType.resolveTypeBinding()));
							}
						}
						return false;
					}
				});
				
				return false;
			}
		});
		
		return length[0].intValue();
	}
}
