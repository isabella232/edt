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
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class NumElementsItemForPageItemFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_NUMELEMENTSITEM));
		if(annotationBinding != null && IDataBinding.STRUCTURE_ITEM_BINDING == containerBinding.getKind()) {
			StructureItemBinding numElementsItemContainer = (StructureItemBinding) containerBinding;
			StructureItemBinding parentItem = numElementsItemContainer.getParentItem();
			boolean parentIsMultiplyOccuring = parentItem == null ? false : parentItem.isMultiplyOccuring();
			if(!numElementsItemContainer.hasOccurs() || parentIsMultiplyOccuring) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.NUMELEMENTSITEM_ITEM_MUST_BE_ARRAY);
			}
		}
	}
}
