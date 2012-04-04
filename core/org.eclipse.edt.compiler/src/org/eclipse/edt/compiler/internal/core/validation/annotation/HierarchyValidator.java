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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class HierarchyValidator implements IValueValidationRule{

//		@ValidationRule{//Releationships cannot be duplicated in the hierarchy},
//		@ValidationRule{// no loops around in relationships},
//		@ValidationRule{// no more than 15 levels in hierarchy},
//		@ValidationRule{// a parent record must exist in the hierarchy}
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Object[] annotationValue = (Object[]) annotationBinding.getValue();
		IAnnotationBinding[] relationships = (annotationValue.length == 0 || !(annotationBinding.getValue() instanceof IAnnotationBinding[])) ?
			new IAnnotationBinding[0] : (IAnnotationBinding[])annotationBinding.getValue();
		
		if(relationships.length != 0) {
			validateFirstRelationshipDoesntSpecifyParentRecord(errorNode, problemRequestor, relationships[0]);
		}
	}

	private void validateFirstRelationshipDoesntSpecifyParentRecord(Node errorNode, IProblemRequestor problemRequestor, IAnnotationBinding firstRelationship) {
		if(firstRelationship.findData(InternUtil.intern(IEGLConstants.PROPERTY_PARENTRECORD)) != IBinding.NOT_FOUND_BINDING) {
			problemRequestor.acceptProblem(errorNode,
											IProblemRequestor.FIRST_PCB_HIERARCHY_ENTRY_HAS_PARENTRECORD);
		}
	}

}
