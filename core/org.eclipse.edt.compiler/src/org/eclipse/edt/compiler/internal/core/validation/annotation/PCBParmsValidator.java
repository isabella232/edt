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

import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class PCBParmsValidator implements IAnnotationValidationRule {
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding pcbParmsAnnotation = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PCBPARMS));
		if(pcbParmsAnnotation != null) {
			int pcbParmsCount = ((Object[]) pcbParmsAnnotation.getValue()).length;
			
			IAnnotationBinding psbAnnotation = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PSB));
			if(psbAnnotation != null && IBinding.NOT_FOUND_BINDING != psbAnnotation.getValue()) {
				ITypeBinding psbType = ((IDataBinding) psbAnnotation.getValue()).getType();
				if(psbType != null && ITypeBinding.FLEXIBLE_RECORD_BINDING == psbType.getKind()) {
					int pcbCount = 0;
					for(Iterator iter = ((FlexibleRecordBinding) psbType).getDeclaredFields().iterator(); iter.hasNext();) {
						IDataBinding next = (IDataBinding) iter.next();
						if(next.getAnnotation(new String[] {"egl", "core"}, "Redefines") == null) {
							pcbCount += 1;
						}						
					}
					
					if(pcbParmsCount > pcbCount) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.DLI_PCB_PARM_LIST_IS_TOO_LARGE,
							new String[] {
								psbType.getName(),
								Integer.toString(pcbCount),
								Integer.toString(pcbParmsCount)
							});
					}
				}
			}
		}
	}
}
