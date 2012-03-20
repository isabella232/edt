/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ThrowsValidator implements IAnnotationValidationRule {
	
	
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

		//validate that this is only specified in a JavaObject externalType
		if (Binding.isValidBinding(targetTypeBinding) && targetTypeBinding.getKind() == ITypeBinding.FUNCTION_BINDING && Binding.isValidBinding(((FunctionBinding)targetTypeBinding).getDeclarer())) {
			IPartBinding decl = ((FunctionBinding)targetTypeBinding).getDeclarer();
			if (ITypeBinding.EXTERNALTYPE_BINDING == decl.getKind()) {
				if (decl.getAnnotation(new String[]{"eglx", "java"}, "JavaObject") != null) {
					return;
				}
			}
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.THROWS_NOT_VALID_HERE);
		}
		
	}
}
