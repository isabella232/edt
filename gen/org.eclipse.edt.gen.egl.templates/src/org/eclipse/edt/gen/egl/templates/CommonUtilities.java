/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egl.templates;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.StringTokenizer;

import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class CommonUtilities {
	public static String getValidEGLName(String str){
		StringTokenizer toks = new StringTokenizer(str, ".");
		StringBuilder retPkg = new StringBuilder();
		while(toks.hasMoreTokens()){
			if(retPkg.length() > 0){
				retPkg.append('.');
			}
			retPkg.append(validateEGLName(toks.nextToken()));
		}
		return retPkg.toString();
	}
	private static String validateEGLName(String javaName){
		//check for a valid EGL name
/*FIXME		if(EGLNameValidator.isKeyword(javaName)){
			return "_" + javaName;
		}*/
		return javaName;
	}
	static Method getSetter(String fieldName, Type fieldType, Class<?> parent){
		String methodName = buildMethodName("set", fieldName);
		for(Method method : parent.getMethods() ){
			if(method.getName().equalsIgnoreCase(methodName) &&
					method.getParameterTypes() != null && 
					method.getParameterTypes().length == 1 &&
					void.class.equals(method.getGenericReturnType()) &&
					compare(method.getParameterTypes()[0], fieldType)){
				return method;
			}
		}
		return null;
	}
	
	static Method getGetter(String fieldName, Class<?> fieldType, Class<?> parent){
		String methodName = buildMethodName(boolean.class.equals(fieldType) ? "is" : "get", fieldName);
		for(Method method : parent.getMethods() ){
			if(method.getName().equalsIgnoreCase(methodName)  &&
					(method.getParameterTypes() == null || 
					method.getParameterTypes().length == 0) &&
					!void.class.equals(method.getGenericReturnType()) && 
					compare(method.getGenericReturnType(), fieldType)){
				return method;
			}
		}
		return null;
	}
	private static String buildMethodName( String prefix, String fieldName ){
		StringBuilder methodName = new StringBuilder(prefix);
		methodName.append(fieldName);
		if(fieldName.length() > 0 && Character.isLetter(fieldName.charAt(0)))
		{
			methodName.setCharAt(prefix.length(),Character.toUpperCase(methodName.charAt(prefix.length())));
		}
		return methodName.toString();
	}
	private static boolean compare(Type methodType, Type fieldType){
		return methodType.equals(fieldType);
	}
	
	public static boolean isNullable(Type type){
		
		if(boolean.class.equals(type)){
			return false;
		}
		else if(byte.class.equals(type)){
			return false;
		}
		else if(char.class.equals(type)){
			return false;
		}
		else if(double.class.equals(type)){
			return false;
		}
		else if(float.class.equals(type)){
			return false;
		}
		else if(int.class.equals(type)){
			return false;
		}
		else if(long.class.equals(type)){
			return false;
		}
		else if(short.class.equals(type)){
			return false;
		}
		return true;
	}

	public static Annotation getAnnotation(Context ctx, String key) throws MofObjectNotFoundException, DeserializationException{
		EObject eObject = Environment.getCurrentEnv().find(key);
		if(eObject instanceof StereotypeType && 
				(eObject = ((StereotypeType)eObject).newInstance()) instanceof Annotation){
			return (Annotation)eObject;
		}
		else if(eObject instanceof AnnotationType &&
				(eObject = ((AnnotationType)eObject).newInstance()) instanceof Annotation){
			return (Annotation)eObject;
		}
		return null;
	}
	
	static org.eclipse.edt.mof.egl.Type findType(Context ctx , String className) throws MofObjectNotFoundException, DeserializationException{
		org.eclipse.edt.mof.egl.Type eType = null;
		String nativeType = ctx.getNativeMapping(className);
		if(nativeType != null){
			eType = TypeUtils.getType(org.eclipse.edt.mof.egl.Type.EGL_KeyScheme + org.eclipse.edt.mof.egl.Type.KeySchemeDelimiter + nativeType);
		}
		else{
			EObject eObject = Environment.getCurrentEnv().find(org.eclipse.edt.mof.egl.Type.EGL_KeyScheme + org.eclipse.edt.mof.egl.Type.KeySchemeDelimiter + CommonUtilities.getValidEGLName(className), true);
			if(eObject instanceof org.eclipse.edt.mof.egl.Type){
				eType = (org.eclipse.edt.mof.egl.Type)eObject;
			}
		}
		return eType;
	}
}
