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


/**
 * @author demurray
 */
public class EGLPropertyValidator implements IAnnotationValidationRule {
	
	
	private boolean supportsEglProperty(IPartBinding part) {
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

		//validate that this is only specified in a RUIWidget or RUIHandler!
		IAnnotationBinding ann = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_EGLPROPERTY));
		if (ann != null && ann.getDeclaringPart() != null) {
			
//			if (!supportsEglProperty(ann.getDeclaringPart())) {
//				problemRequestor.acceptProblem(errorNode, IProblemRequestor.ANNOTATION_NOT_APPLICABLE, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_EGLPROPERTY});				
//				return;
//			}
			
		}
		
		//validate the getter and setter methods if they were not explicitly named
		if ((allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_GETMETHOD)) == null) && (allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SETMETHOD)) == null)) {
			//neither was specified...must check for the default names
			if (ann == null || ann.getDeclaringPart() == null || !(target instanceof ClassDataDeclaration)) {
				return;
			}
			
			ClassDataDeclaration decl = (ClassDataDeclaration) target;
			
			Iterator i = decl.getNames().iterator();
			while (i.hasNext()) {
				Name name = (Name) i.next();
				String getterName = "get" + name.getCanonicalName();
				String setterName = "set" + name.getCanonicalName();
				
				IFunctionBinding getter = ann.getDeclaringPart().findFunction(InternUtil.intern(getterName));
				IFunctionBinding setter = ann.getDeclaringPart().findFunction(InternUtil.intern(setterName));
				
				if (Binding.isValidBinding(getter)) {
					//validate the attributes of the function!
					new GetMethodAnnotationValueValidator().validateGetMethod(errorNode, target, getter, ann.getDeclaringPart(), problemRequestor, compilerOptions);
				}
				else{
					//output error that function was not found!
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, IMarker.SEVERITY_ERROR, new String[] {getterName});
				}

				if (Binding.isValidBinding(setter)) {
					//validate the attributes of the function!
					new SetMethodAnnotationValueValidator().validateSetMethod(errorNode, target, setter, ann.getDeclaringPart(), problemRequestor, compilerOptions);
				}
				else{
					//output error that function was not found!
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, IMarker.SEVERITY_ERROR, new String[] {setterName});
				}
			}

		}
		
	}
}
