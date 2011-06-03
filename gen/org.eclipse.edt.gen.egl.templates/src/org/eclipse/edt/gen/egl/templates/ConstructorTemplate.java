package org.eclipse.edt.gen.egl.templates;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.LogicAndDataPart;

public class ConstructorTemplate extends EglTemplate{
	
	public void genConstructor(Constructor<?> constructor, Context ctx, Class<?> clazz, LogicAndDataPart eType) {
		if(Modifier.isPublic(constructor.getModifiers())){
			org.eclipse.edt.mof.egl.Constructor eConstructor = ctx.getFactory().createConstructor();
			eType.getConstructors().add(eConstructor);
			ctx.invoke(genFunctionParameters, (Object)constructor, ctx, eConstructor);
		}
	}
	public void genFunctionParameters(Constructor<?> constructor, Context ctx, FunctionMember functionMember) {
		int idx = 0;
		for(Type type : constructor.getGenericParameterTypes()){
			ctx.invoke(genFunctionParameter, (Object)constructor, ctx, functionMember, Integer.valueOf(idx), type);
			idx++;
		}
	}
	
	public Annotation getAnnotation(Constructor<?> constructor, Context ctx, Integer argIdx, Class<?> annotationClass){
		if(constructor.getParameterAnnotations() != null && constructor.getParameterAnnotations()[argIdx.intValue()] != null){
			for(Annotation annotation : constructor.getParameterAnnotations()[argIdx]){
				if(annotationClass.equals(annotation.getClass())){
					return annotation;
				}
			}
		}
		return null;
	}
}
