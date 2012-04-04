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
package org.eclipse.edt.compiler.binding;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author svihovec
 *
 */
class MutuallyExclusiveFieldAnnotationTypeBinding extends AnnotationValidationAnnotationTypeBinding {

	private String annotationName;
	private String[] mutuallyExclusiveWith;
	
	public MutuallyExclusiveFieldAnnotationTypeBinding(String caseSensitiveInternedName, String annotationName, String[] mutuallyExclusiveWith) {
		super(caseSensitiveInternedName);
		
		this.annotationName = annotationName;
		this.mutuallyExclusiveWith = mutuallyExclusiveWith;
	}

	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		if(allAnnotations.containsKey(annotationName)){
			for (int i = 0; i < mutuallyExclusiveWith.length; i++) {
				if(allAnnotations.containsKey(mutuallyExclusiveWith[i])){
					// Not an error if the value of the annotation is Boolean.NO or Enumeration.NONE
	//				AnnotationExpression annotation = (AnnotationExpression)allAnnotations.get(mutuallyExclusiveWith[i]);
	//				
	//				if(((AnnotationTypeBinding)annotation.resolveTypeBinding()).getBaseType().getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
	//					if(((PrimitiveTypeBinding)((AnnotationTypeBinding)annotation.resolveTypeBinding()).getBaseType()).getPrimitive() == Primitive.BOOLEAN){
	//						expression.accept(new DefaultASTVisitor(){
	//							
	//							public boolean visit(Assignment assignment) {
	//								IAnnotationBinding annotationBinding = assignment.resolveBinding();
	//								
	//								SystemEnumerationDataBinding value = (SystemEnumerationDataBinding)annotationBinding.getValue();
	//								
	//								if(value == Boolean.YES){
	//									problemRequestor.acceptProblem(
	//							            	expression,
	//											IProblemRequestor.PROPERTY_VALUE_MUST_BE_A_STRING_ARRAY_ARRAY,
	//											new String[] {getName()});	
	//								}
	//								return false;
	//							}
	//						});
	//					}
	//				}else if(((AnnotationTypeBinding)annotation.resolveTypeBinding()).getBaseType().getKind() == ITypeBinding.ENUMERATION_BINDING){
	//					expression.accept(new DefaultASTVisitor(){
	//							
	//						public boolean visit(Assignment assignment) {
	//							IAnnotationBinding annotationBinding = assignment.resolveBinding();
	//							
	//							Enumeration value = (Enumeration)annotationBinding.getValue();
	//							
	//							if(value.booleanValue() == true){
	//								problemRequestor.acceptProblem(
	//						            	expression,
	//										IProblemRequestor.PROPERTY_VALUE_MUST_BE_A_STRING_ARRAY_ARRAY,
	//										new String[] {getName()});	
	//							}
	//							return false;
	//						}
	//					});
	//				}else{
						
						problemRequestor.acceptProblem(
				            	errorNode,
								IProblemRequestor.PROPERTY_VALUE_MUST_BE_A_STRING_ARRAY_ARRAY,
								new String[] {getCaseSensitiveName()});	
	//				}
					
				}
			}
		}
	}
}
