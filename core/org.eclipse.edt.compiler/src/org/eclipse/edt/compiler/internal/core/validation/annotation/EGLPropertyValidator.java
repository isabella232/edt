/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.utils.NameUtile;

public class EGLPropertyValidator implements IAnnotationValidationRule {
	
	@Override
	public void validate(Node errorNode, Node target, Element element, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Annotation ann = (Annotation)allAnnotationsAndFields.get(NameUtile.getAsName(IEGLConstants.PROPERTY_EGLPROPERTY));
		
		//validate the getter and setter methods if they were not explicitly named
		if (allAnnotationsAndFields.get(NameUtile.getAsName(IEGLConstants.PROPERTY_GETMETHOD)) == null && allAnnotationsAndFields.get(NameUtile.getAsName(IEGLConstants.PROPERTY_SETMETHOD)) == null) {
			//neither was specified...must check for the default names
			Part declaringPart = BindingUtil.getDeclaringPart(target);
			if (ann == null || !(declaringPart instanceof StructPart) || !(target instanceof ClassDataDeclaration)) {
				return;
			}
			
			ClassDataDeclaration decl = (ClassDataDeclaration) target;
			
			for (Name name : decl.getNames()) {
				String getterName = "get" + name.getCanonicalName();
				String setterName = "set" + name.getCanonicalName();
				
				Function getter = ((StructPart)declaringPart).getFunction(NameUtile.getAsName(getterName));
				Function setter = ((StructPart)declaringPart).getFunction(NameUtile.getAsName(setterName));
				
				if (getter != null) {
					//validate the attributes of the function!
					new GetMethodAnnotationValueValidator().validateGetMethod(errorNode, target, getter, declaringPart, problemRequestor, compilerOptions);
				}
				else {
					//output error that function was not found!
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, IMarker.SEVERITY_ERROR, new String[] {getterName});
				}

				if (setter != null) {
					//validate the attributes of the function!
					new SetMethodAnnotationValueValidator().validateSetMethod(errorNode, target, setter, declaringPart, problemRequestor, compilerOptions);
				}
				else {
					//output error that function was not found!
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, IMarker.SEVERITY_ERROR, new String[] {setterName});
				}
			}
		}
	}
}
