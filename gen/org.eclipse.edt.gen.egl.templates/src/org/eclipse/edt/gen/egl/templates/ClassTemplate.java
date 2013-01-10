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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class ClassTemplate extends EglTemplate{

	public void genPart(Class<?> clazz, Context ctx){
		if(clazz.isInterface()){
			ctx.invoke(genInterface, (Object)clazz, ctx);
		}
		else{
			ctx.invoke(genExternalType, (Object)clazz, ctx);
		}
	}
	public void genInterface(Class<?> clazz, Context ctx){
		Interface iFace = ctx.getFactory().createInterface();
		ctx.setResult(iFace);
		basicConfig(clazz, iFace);
		ctx.invoke(genExtends, (Object)clazz, ctx, iFace);
		ctx.invoke(genFunctions, (Object)clazz, ctx, iFace);
		ctx.invoke(genAnnotations, (Object)clazz, ctx, iFace);
	}
	public void genExternalType(Class<?> clazz, Context ctx){
		ExternalType eType = ctx.getFactory().createExternalType();
		ctx.setResult(eType);
		basicConfig(clazz, eType);
		if(Modifier.isAbstract(clazz.getModifiers())){
			eType.setIsAbstract(Boolean.valueOf(Modifier.isAbstract(clazz.getModifiers())));
		}
		ctx.invoke(genExtends, (Object)clazz, ctx, eType);
		ctx.invoke(genSubType, (Object)clazz, ctx, eType);
		ctx.invoke(genConstructors, (Object)clazz, ctx, eType);
		ctx.invoke(genFields, (Object)clazz, ctx, eType);
		ctx.invoke(genFunctions, (Object)clazz, ctx, eType);
		ctx.invoke(genAnnotations, (Object)clazz, ctx, eType);
	}
	private void basicConfig(Class<?> clazz, LogicAndDataPart part){
		part.setPackageName(CommonUtilities.getValidEGLName(clazz.getPackage().getName()));
		part.setName(CommonUtilities.getValidEGLName(clazz.getSimpleName()));
	}
	public void genSubType(Class<?> clazz, Context ctx, LogicAndDataPart eType) throws MofObjectNotFoundException, DeserializationException {
		org.eclipse.edt.mof.egl.Annotation javaObject = CommonUtilities.getAnnotation(ctx, Constants.Property);
		if(javaObject != null){
			eType.addAnnotation(javaObject);
			if(!clazz.getSimpleName().equals(eType.getCaseSensitiveName())){
				javaObject.setValue(IEGLConstants.PROPERTY_JAVANAME, clazz.getSimpleName());
			}
			if(!clazz.getPackage().getName().equals(eType.getCaseSensitivePackageName())){
				javaObject.setValue(IEGLConstants.PROPERTY_PACKAGENAME, clazz.getPackage().getName());
			}
		}
	}
	public void genFunctions(Class<?> clazz, Context ctx, LogicAndDataPart part) {
		for(Method method : clazz.getDeclaredMethods()){
			ctx.invoke(genFunction, (Object)method, ctx, clazz, part);
		}
	}
	public void genConstructors(Class<?> clazz, Context ctx, LogicAndDataPart part) {
		for(Constructor<?> constructor : clazz.getDeclaredConstructors()){
			ctx.invoke(genConstructor, (Object)constructor, ctx, clazz, part);
		}
	}
	public void genFields(Class<?> clazz, Context ctx, LogicAndDataPart part) {
		for(Field field : clazz.getDeclaredFields()){
			ctx.invoke(genField, (Object)field, ctx, clazz, part);
		}
	}

	public void genAnnotations(Class<?> clazz, Context ctx, LogicAndDataPart part){
		for(Annotation annotation : clazz.getDeclaredAnnotations()){
			ctx.invoke(genAnnotation, (Object)annotation, ctx, part);
		}
	}
	
	public void genExtends(Class<?> clazz, Context ctx, LogicAndDataPart part) throws MofObjectNotFoundException, DeserializationException{
		Class<?> extend = clazz.getSuperclass();
		if( extend != null && !Object.class.equals(extend)){
			Type type = CommonUtilities.findType(ctx, extend.getName());
			if(type instanceof StructPart){
				part.getSuperTypes().add((StructPart)type);
			}
		}
	}	
}
