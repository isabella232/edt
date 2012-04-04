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
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class IsCompatibleWithValidator implements IAnnotationValidationRule {
	
	
	private boolean supportsProperty(IPartBinding part) {
		if (part == null || part.getSubType() == null) {
			return false;
		}
				
		String[] ruiPkg = InternUtil.intern(new String[] {"eglx", "ui", "rui"});
				
		if (InternUtil.intern(IEGLConstants.PROPERTY_RUIWIDGET) == part.getName() && part.getPackageName() == ruiPkg) {
			return true;
		}

		String subName = part.getSubType().getName();
		String[] subPkg = part.getSubType().getPackageName();

		if (ruiPkg != subPkg) {
			return false;
		}

		if (InternUtil.intern(IEGLConstants.PROPERTY_RUIWIDGET) == subName) {
			return true;
		}

		if (InternUtil.intern(IEGLConstants.PROPERTY_RUIHANDLER) == subName) {
			return true;
		}
			
		return false;

	}
	
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (targetTypeBinding.getKind() != ITypeBinding.ANNOTATION_BINDING)
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.ANNOTATION_NOT_APPLICABLE, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_EGLPROPERTY});				
		}
}
