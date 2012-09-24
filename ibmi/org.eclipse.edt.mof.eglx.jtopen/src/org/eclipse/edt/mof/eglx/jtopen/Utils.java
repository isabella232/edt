/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.jtopen;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class Utils {
	
	private static final String IBMiConnectionMofKey = MofConversion.EGL_KeyScheme + "eglx.jtopen.IBMiConnection";
	
	public static boolean isIBMiConnection(Type type) {
		return TypeUtils.isTypeOrSubtypeOf(type, IBMiConnectionMofKey);
	}
	
	public static boolean isFunctionServiceQualified(Expression exp, Function function) {
		return !(exp instanceof FieldAccess && ((FieldAccess)exp).getPrimary() instanceof ThisExpression) &&
				function.getContainer() instanceof Service;
	}
	public static boolean requiresAS400TypeAnnotation(Type type) {
		if (type == null) {
			return false; //avoid excess error messages
		}
		
		if(isSupportedPrimitiveTypes(type)){
			return TypeUtils.isReferenceType(type);
		}
		
		if(type instanceof ArrayType) {
			if (((ArrayType)type).getElementType() == null) {
				return false; //avoid excess error messages
			}
			if (((ArrayType)type).getElementType() instanceof ArrayType) {
				return false;
			}
			return requiresAS400TypeAnnotation(((ArrayType)type).getElementType());
			
		}
		return false;
	}
	public static boolean isValidAS400Type(Type type) {
		if (type == null) {
			return true; //avoid excess error messages
		}
		
		if(isSupportedPrimitiveTypes(type)){
			return true;
		}
		if (type instanceof Handler) {
			return true;
		}
		
		if (type instanceof Record) {
			return true;
		}
		
		if(type instanceof ArrayType) {
			if (((ArrayType)type).getElementType() ==null) {
				return true; //avoid excess error messages
			}
			if (((ArrayType)type).getElementType() instanceof ArrayType) {
				return false;
			}
			return isValidAS400Type(((ArrayType)type).getElementType());
			
		}
		return false;
	}
	
	private static boolean isSupportedPrimitiveTypes(Type type){
			return TypeUtils.isNumericType(type) ||
					TypeUtils.isTextType(type) ||
					(type != null && type.getClassifier() instanceof EGLClass &&
							(type.getClassifier().equals(TypeUtils.Type_DATE) ||
							type.getClassifier().equals(TypeUtils.Type_TIME) ||
							type.getClassifier().equals(TypeUtils.Type_TIMESTAMP) ||
							type.getClassifier().equals(TypeUtils.Type_BYTES)));
	}
}
