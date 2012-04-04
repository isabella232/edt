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
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author demurray
 */
public class KeyItemForDLISegmentValidator implements IValueValidationRule{
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		if(annotationBinding.getValue() instanceof StructureItemBinding) {
			StructureItemBinding structureItemBinding = (StructureItemBinding)annotationBinding.getValue();
			if(!MustBeDLINameAnnotationValidator.isValidDLIName(structureItemBinding.getName())) {
				if(structureItemBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLIFieldName") == null) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.KEYITEM_NOT_VALID_DLINAME_SO_MUST_DEFINE_DLIFIELDNAME,
						new String[] {
							structureItemBinding.getCaseSensitiveName(),
							structureItemBinding.getCaseSensitiveName()
						});
				}
			}
		}
	}
}
