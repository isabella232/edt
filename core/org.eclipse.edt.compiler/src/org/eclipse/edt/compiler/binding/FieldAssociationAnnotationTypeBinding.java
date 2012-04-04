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
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class FieldAssociationAnnotationTypeBinding extends AnnotationValidationAnnotationTypeBinding {

	private String ifHasAnnotationName;
	private String requiresAnnotationName;
	
	public FieldAssociationAnnotationTypeBinding(String ifHasAnnotationName, String requiresAnnotationName) {
		super(InternUtil.internCaseSensitive("Association"));
		
		this.ifHasAnnotationName = ifHasAnnotationName;
		this.requiresAnnotationName = requiresAnnotationName;
	}

	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		boolean hasIfHasAnnotation = allAnnotations.containsKey(InternUtil.intern(ifHasAnnotationName));
		boolean hasRequiresAnnotation = allAnnotations.containsKey(InternUtil.intern(requiresAnnotationName));
		
		if(hasIfHasAnnotation && !hasRequiresAnnotation) { 
			problemRequestor.acceptProblem(
		            	errorNode,
						IProblemRequestor.BOTH_PROPERTIES_REQUIRED_IF_ONE_SPECIFIED,
						new String[] {ifHasAnnotationName, requiresAnnotationName});	
		}
	}
}
