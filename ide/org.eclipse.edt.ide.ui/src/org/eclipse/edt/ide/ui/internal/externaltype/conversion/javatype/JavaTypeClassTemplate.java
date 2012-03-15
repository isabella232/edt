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
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Set;

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
			if(declaringClass.getEnclosingClass() != null) {
				eglName = declaringClass.getEnclosingClass().getSimpleName() 
						  + JavaTypeConstants.UNDERSTORE_PREFIX + eglName;
			}
			boolean isEGLKeyWord = EGLNameValidator.isKeyword(eglName);
			boolean isStartWithEze = eglName.toLowerCase().startsWith(JavaTypeConstants.EZE_PREFIX);
			
			if(isEGLKeyWord || isStartWithEze) {//Alias EGL keywords
				eglName = JavaTypeConstants.UNDERSTORE_PREFIX + eglName;
			}
			out.print("externalType " + eglName);
			
			Set<Class<?>> allClassMeta = (Set<Class<?>>)ctx.get(JavaTypeConstants.ALL_CLASS_META);
			String superTypes = getDirectSuperTypes(clazz,toBeGenerated.getSource(),allClassMeta);
			if(superTypes != null && !superTypes.isEmpty()) {
				out.print(" extends " + superTypes);
			}
			
			boolean notNeedPackage = false;
			if(packageName != null && packageName.equals((String)ctx.get(JavaTypeConstants.CONTAINING_EGL_PACKAGE))) {
				notNeedPackage = true;
			}
			out.println(" type JavaObject ");
			if(isEGLKeyWord || isStartWithEze) {
				out.print("{externalName = " + SQLConstants.DOUBLE_QUOTE);
				if(declaringClass.getEnclosingClass() == null) {
					out.print(declaringClass.getSimpleName());
				} else {
					out.print(declaringClass.getEnclosingClass().getSimpleName() + SQLConstants.QUALIFICATION_DELIMITER);
					out.print(declaringClass.getSimpleName());
				}
				out.print(SQLConstants.DOUBLE_QUOTE);
				
				if(notNeedPackage) {
					out.println("}");
				} else {
					out.println(", PackageName = \"" + packageName +"\"}");
				}
			} else {
				if(declaringClass.getEnclosingClass() != null) {
					out.print("{externalName = " + SQLConstants.DOUBLE_QUOTE);
					out.print(declaringClass.getEnclosingClass().getSimpleName() + SQLConstants.QUALIFICATION_DELIMITER);
					out.print(declaringClass.getSimpleName());
					out.print(SQLConstants.DOUBLE_QUOTE);
					
					if(notNeedPackage) {
						out.println("}");
					} else {
						out.println(SQLConstants.COMMA + " PackageName = " + SQLConstants.DOUBLE_QUOTE +
								packageName + SQLConstants.DOUBLE_QUOTE + "}");
					}
					
				} else if(!notNeedPackage) {
					out.println("{ PackageName = " + SQLConstants.DOUBLE_QUOTE +
							packageName + SQLConstants.DOUBLE_QUOTE + "}");
				}
			}
			
			
			for(Field javaField : toBeGenerated.getFields()) {
				ctx.invoke(JavaTypeConstants.genField, javaField, ctx, out);	
			}
			
			boolean isAbstractClas = Modifier.isAbstract(clazz.getModifiers());
			if(clazz.isInterface() || isAbstractClas) {
				out.println("  private constructor();");
			} else {
				for(Constructor<?> javaCon : toBeGenerated.getConstructors()) {
					ctx.invoke(JavaTypeConstants.genConstructor, javaCon, ctx, out);	
				}
			}		
			
			for(Method javaMethod : toBeGenerated.getMethods()) {
				ctx.invoke(JavaTypeConstants.genMethod, javaMethod, ctx, out);	
			}
			
			out.println("end");
		}
		
		out.println("");
	}
	
	public void genObject(java.lang.Class<?> clazz, EglSourceContext ctx){
			genClass(clazz, ctx, ctx.getTabbedWriter());
	}
	
	private String getDirectSuperTypes(java.lang.Class<?> clazz,int source,Set<Class<?>> allClassMeta) {
		StringBuilder buffer = new StringBuilder(25);
		
		Class<?>[] interfaces = clazz.getInterfaces();
		for(Class<?> interfaceClass : interfaces) {
			if(JavaType.SelectedType == source
				 || allClassMeta.contains(interfaceClass)) {
				buffer.append(SQLConstants.COMMA);
				boolean isEGLPart =JavaTypeConstants.EglPartNames.contains(interfaceClass.getSimpleName().toLowerCase(Locale.ENGLISH));
				if(isEGLPart) {
					buffer.append(JavaTypeConstants.UNDERSTORE_PREFIX );
				}
				buffer.append(interfaceClass.getSimpleName());
			}
		}
		
		Class<?> superclass = clazz.getSuperclass();
		if(superclass != null && !superclass.equals(Object.class)) {
			if(JavaType.SelectedType == source
					 || allClassMeta.contains(superclass)) {
				String simpleName = superclass.getSimpleName();
				boolean isEGLPart =JavaTypeConstants.EglPartNames.contains(simpleName.toLowerCase(Locale.ENGLISH));
				if(isEGLPart) {
					simpleName = JavaTypeConstants.UNDERSTORE_PREFIX + simpleName;
				}
				buffer.insert(0, simpleName);
			}
		} 
		
		if(buffer.length() > 0 && SQLConstants.COMMA.equals(buffer.substring(0, 1))){
			buffer.deleteCharAt(0);
		}
	
		return buffer.toString();
	}
}
