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

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class PositionRequiredForConsoleFieldUnlessSegmentsDefinedValidator extends DefaultFieldContentAnnotationValidationRule {
	
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(AbstractBinder.typeIs(containerBinding.getType(), new String[] {"egl", "ui", "console"}, "ConsoleField")) {
			if(!allAnnotations.keySet().contains(InternUtil.intern(IEGLConstants.PROPERTY_SEGMENTS)) &&
			   !allAnnotations.keySet().contains(InternUtil.intern(IEGLConstants.PROPERTY_POSITION))) {				
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.SEGMENTS_OR_POSITION_REQUIRED_FOR_CONSOLE_FIELDS);
			}
		}
	}	
}
