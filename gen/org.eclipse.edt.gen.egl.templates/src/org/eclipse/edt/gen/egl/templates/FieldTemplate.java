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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class FieldTemplate extends EglTemplate{

	public void genName(Field field, Context ctx, Member member) {
		member.setName(CommonUtilities.getValidEGLName((field.getName())));
	}
	public void genField(Field field, Context ctx, Class<?> clazz, LogicAndDataPart part) throws MofObjectNotFoundException, DeserializationException {
		boolean isJavaProperty = isJavaProperty(field, clazz);
		if( isJavaProperty ||
				Modifier.isPublic(field.getModifiers())){
			org.eclipse.edt.mof.egl.Field eField = ctx.getFactory().createField();
			eField.setContainer(part);
			part.getFields().add(eField);
			ctx.invoke(genName, (Object)field, ctx, eField);
			if(Modifier.isStatic((field.getModifiers()))){
				eField.setIsStatic(Modifier.isStatic((field.getModifiers())));
			}
			ctx.invoke(genType, (Object)field.getGenericType(), ctx, eField);
			if(isJavaProperty){
				org.eclipse.edt.mof.egl.Annotation annotation = CommonUtilities.getAnnotation(ctx, Constants.Property);
				if(annotation != null){
					eField.addAnnotation(annotation);
				}
			}
			if(!eField.getCaseSensitiveName().equals(field.getName())){
				org.eclipse.edt.mof.egl.Annotation annotation = CommonUtilities.getAnnotation(ctx, Constants.ExternalName);
				if(annotation != null){
					annotation.setValue(field.getName());
					eField.addAnnotation(annotation);
				}
			}
			ctx.invoke(genAnnotations, (Object)field, ctx, eField);
		}
	}
	public void genAnnotations(Field field, Context ctx, Member member){
		for(Annotation annotation : field.getDeclaredAnnotations()){
			ctx.invoke(genAnnotation, (Object)annotation, ctx, member);
		}
	}
	public static boolean isJavaProperty(Field field, Class<?> parent){
		return CommonUtilities.getSetter(field.getName(), field.getType(), parent) != null && CommonUtilities.getGetter(field.getName(), field.getType(), parent) != null;
		
	}

}
