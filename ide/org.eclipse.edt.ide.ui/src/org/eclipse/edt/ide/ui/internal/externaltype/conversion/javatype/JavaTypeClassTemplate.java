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
package org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.externaltype.util.ReflectionUtil;
import org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype.JavaType;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class JavaTypeClassTemplate extends AbstractTemplate {
	public void genClass(java.lang.Class<?> clazz, EglSourceContext ctx, TabbedWriter out){
		if(!ReflectionUtil.isBuiltinEglType(clazz)) {
			JavaType toBeGenerated = (JavaType)ctx.get(JavaTypeConstants.TO_BE_GENERATED_TYPE);
			
			Class<?> declaringClass = clazz;
			while(declaringClass.isArray()){
				declaringClass = declaringClass.getComponentType();
			}
			String packageName = declaringClass.getPackage().getName();
			String eglName = declaringClass.getSimpleName();
			boolean isEGLKeyWord = EGLNameValidator.isKeyword(eglName);
			if(isEGLKeyWord) {//Alias EGL keywords
				eglName = JavaTypeConstants.UNDERSTORE_PREFIX + eglName;
			}
			out.print("externalType " + eglName);
			
			//All referenced types will not generate super type
			if(JavaType.ReferencedType != toBeGenerated.getSource()) {
				String superTypes = getDirectSuperTypes(clazz);
				if(superTypes != null && !superTypes.isEmpty()) {
					out.print(" extends " + superTypes);
				}
			}
			
			out.println(" type JavaObject ");
			out.print("{externalName = \"" + clazz.getSimpleName() +"\"");
			out.println(", PackageName = \"" + packageName +"\"}");
			
			for(Field javaField : toBeGenerated.getFields()) {
				ctx.invoke(JavaTypeConstants.genField, javaField, ctx, out);	
			}
			
			for(Constructor<?> javaCon : toBeGenerated.getConstructors()) {
				ctx.invoke(JavaTypeConstants.genConstructor, javaCon, ctx, out);	
			}
			
			for(Method javaMethod : toBeGenerated.getMethods()) {
				ctx.invoke(JavaTypeConstants.genMethod, javaMethod, ctx, out);	
			}
			
			out.println("end");
		}
		
		out.println("");
	}
	
	public void genObject(java.lang.Class<?> clazz, EglSourceContext ctx, TabbedWriter out){
			genClass(clazz, ctx, out);
	}
	
	private String getDirectSuperTypes(java.lang.Class<?> clazz) {
		StringBuilder buffer = new StringBuilder(25);
		
		Class<?>[] interfaces = clazz.getInterfaces();
		for(Class<?> interfaceClass : interfaces) {
			buffer.append(SQLConstants.COMMA);
			buffer.append(interfaceClass.getSimpleName());
		}
		
		Class<?> superclass = clazz.getSuperclass();
		if(superclass != null && !superclass.equals(Object.class)) {
			buffer.insert(0, superclass.getSimpleName());
		} else if(buffer.length() > 0){
			buffer.deleteCharAt(0);
		}
	
		return buffer.toString();
	}
}
