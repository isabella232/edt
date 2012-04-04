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
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class TypeAheadAnnotationValidator extends DefaultFieldContentAnnotationValidationRule {
	
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
 		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_TYPEAHEAD));
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			org.eclipse.edt.compiler.core.Boolean value = (org.eclipse.edt.compiler.core.Boolean)annotationBinding.getValue();
			if (value.booleanValue() ){
				if (allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUES)) == null && 
					allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORDATATABLE)) == null	){
					problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.TYPEAHEAD_REQUIRES_PROPERTIES);
				}
			}
			
			if (containerBinding != null && containerBinding != IBinding.NOT_FOUND_BINDING){
				if (containerBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
					if (((StructureItemBinding)containerBinding).getChildren().size()> 0 && ((StructureItem)container).hasLevel()){
						problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.TYPEAHEAD_MUST_BE_ON_LEAF_ITEM,
								new String[] {IEGLConstants.PROPERTY_TYPEAHEAD});
					}
				}
				ITypeBinding typeBinding = containerBinding.getType();
				if (typeBinding != null && typeBinding != IBinding.NOT_FOUND_BINDING ){
					if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
						typeBinding = typeBinding.getBaseType();
					}
					
					if (typeBinding.getKind() != typeBinding.PRIMITIVE_TYPE_BINDING){
						problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.TYPE_INVALID_FOR_TYPEAHEAD,
								new String[] {IEGLConstants.PROPERTY_TYPEAHEAD,typeBinding.getCaseSensitiveName()});
					}
				}
			}
		}
		
		
	}
	

}
