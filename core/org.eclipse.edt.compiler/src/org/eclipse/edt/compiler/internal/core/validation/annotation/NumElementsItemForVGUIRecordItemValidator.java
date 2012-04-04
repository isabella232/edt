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

import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class NumElementsItemForVGUIRecordItemValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding aBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_NUMELEMENTSITEM));
		if(aBinding != null) {
			IDataBinding numElementsItemDBinding = dataBindingFor(aBinding.getValue());
			if(numElementsItemDBinding != null && IBinding.NOT_FOUND_BINDING != numElementsItemDBinding) {
				boolean conflictWithAnotherName = false;
				
				FixedStructureBinding enclosingStructureBinding = ((StructureItemBinding) containerBinding).getEnclosingStructureBinding();
				IAnnotationBinding commandValueItemForRecord = (IAnnotationBinding) enclosingStructureBinding.getAnnotation(enclosingStructureBinding.getSubType()).findData(InternUtil.intern(IEGLConstants.PROPERTY_COMMANDVALUEITEM));
				if(commandValueItemForRecord != IBinding.NOT_FOUND_BINDING) {
					if(dataBindingFor(commandValueItemForRecord.getValue()) == numElementsItemDBinding) {
						conflictWithAnotherName = true;
					}
				}
				
				if(!conflictWithAnotherName) {
					conflictWithAnotherName = numElementsItemDBinding.getName() == containerBinding.getName();
				}
				
				if(!conflictWithAnotherName) {
					IAnnotationBinding selectedIndexItemABinding = containerBinding.getAnnotation(new String[] {"egl", "ui"}, "SelectedIndexItem");
					if(selectedIndexItemABinding != null) {
						if(dataBindingFor(selectedIndexItemABinding.getValue()) ==
						   numElementsItemDBinding) {
							conflictWithAnotherName = true;
						}
					} 
				}
				
				if(conflictWithAnotherName) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.CONFLICT_WITH_NUMELEMENTSITEM_COMMANDVALUEITEM_SELECTEDINDEXITEM_ITEMNAME,
						new String[] {
							IEGLConstants.PROPERTY_NUMELEMENTSITEM,
							IEGLConstants.PROPERTY_COMMANDVALUEITEM,
							IEGLConstants.PROPERTY_SELECTEDINDEXITEM
						});
				}
				
				if(IDataBinding.STRUCTURE_ITEM_BINDING == numElementsItemDBinding.getKind()) {
					int numElementsItemOccurs = getFirstOccurs((StructureItemBinding) numElementsItemDBinding);
					if(numElementsItemOccurs != 0) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.PROPERTY_REFERENCE_CANNOT_BE_MUTLIPLY_OCCURING,
							new String[] {
								IEGLConstants.PROPERTY_NUMELEMENTSITEM,
								numElementsItemDBinding.getName()	
							});
					}
				}
				
				if(!((StructureItemBinding) containerBinding).isMultiplyOccuring()) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PROPERTY_ONLY_APPLICABLE_TO_MULTIPLY_OCCURING_ITEMS,
						new String[] {
							containerBinding.getName(),
							IEGLConstants.PROPERTY_NUMELEMENTSITEM,
						});
				}
			}
		}		
	}
	
	private IDataBinding dataBindingFor(Object annotationValue) {
		if(annotationValue instanceof IDataBinding) {
			return (IDataBinding) annotationValue;
		}
		if(annotationValue instanceof Name) {
			return ((Name) annotationValue).resolveDataBinding();
		}
		return null;
	}
	
	private int getFirstOccurs(StructureItemBinding sItemBinding) {
		if(sItemBinding.isMultiplyOccuring()) {
			if(sItemBinding.hasOccurs()) {
				return sItemBinding.getOccurs();
			}
			return getFirstOccurs(sItemBinding.getParentItem());
		}
		return 0;
	}
}
