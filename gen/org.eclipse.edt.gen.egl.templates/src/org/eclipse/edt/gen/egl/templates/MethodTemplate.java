/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class MethodTemplate extends EglTemplate{
	public void genName(Method method, Context ctx, Function eFunction) {
		eFunction.setName(CommonUtilities.getValidEGLName((method.getName())));
	}
	public void genFunctionParameters(Method method, Context ctx, FunctionMember functionMember) {
		int idx = 0;
		for(Type type : method.getGenericParameterTypes()){
			ctx.invoke(genFunctionParameter, (Object)method, ctx, functionMember, Integer.valueOf(idx), type);
			idx++;
		}
	}
	public void genFunction(Method method, Context ctx, Class<?> clazz, LogicAndDataPart part) throws MofObjectNotFoundException, DeserializationException {
		if(Modifier.isPublic(method.getModifiers()) && !isJavaProperty(method, clazz)){
			Function eFunction = ctx.getFactory().createFunction();
			eFunction.setContainer(part);
			eFunction.setName(CommonUtilities.getValidEGLName(method.getName()));
			part.getFunctions().add(eFunction);
			eFunction.setIsStatic(Modifier.isStatic(method.getModifiers()));
			ctx.invoke(genName, (Object)method, ctx, eFunction);
			ctx.invoke(genFunctionParameters, (Object)method, ctx, eFunction);
			if(!void.class.equals(method.getGenericReturnType())){
				ctx.invoke(genType, (Object)method.getGenericReturnType(), ctx, eFunction);
			}

			if(!eFunction.getName().equals(method.getName())){
				org.eclipse.edt.mof.egl.Annotation annotation = CommonUtilities.getAnnotation(ctx, Constants.ExternalName);
				if(annotation != null){
					annotation.setValue(method.getName());
					eFunction.addAnnotation(annotation);
				}
			}
			ctx.invoke(genAnnotations, (Object)method, ctx, eFunction);
		}
	}
	public void genAnnotations(Method method, Context ctx, Member eFunction){
		for(Annotation annotation : method.getDeclaredAnnotations()){
			ctx.invoke(genAnnotation, (Object)annotation, ctx, eFunction);
		}
	}
	
	public Annotation getAnnotation(Method method, Context ctx, Integer argIdx, Class<?> annotationClass){
		if(method.getParameterAnnotations() != null && method.getParameterAnnotations()[argIdx.intValue()] != null){
			for(Annotation annotation : method.getParameterAnnotations()[argIdx]){
				if(annotationClass.equals(annotation.getClass())){
					return annotation;
				}
			}
		}
		return null;
	}
	public static boolean isJavaProperty(Method method, Class<?> parent){
		if(method.getName() != null && 
				method.getName().startsWith("is") &&
				(method.getParameterTypes() == null || 
						method.getParameterTypes().length == 0)){
			String fieldName = method.getName().substring(2);
			return CommonUtilities.getSetter(fieldName, method.getGenericReturnType(), parent) != null;
		}
		else if(method.getName() != null && 
				method.getName().startsWith("get") &&
				(method.getParameterTypes() == null || 
						method.getParameterTypes().length == 0)){

			String fieldName = method.getName().substring(3);
			return CommonUtilities.getSetter(fieldName, method.getGenericReturnType(), parent) != null;
		}
		else if(method.getName() != null && 
				method.getName().startsWith("set") &&
				method.getParameterTypes() != null && 
						method.getParameterTypes().length == 1 &&
						void.class.equals(method.getGenericReturnType())){
			String fieldName = method.getName().substring(3);
			return CommonUtilities.getGetter(fieldName, method.getParameterTypes()[0], parent) != null;
		}
		return false;
	}
}
