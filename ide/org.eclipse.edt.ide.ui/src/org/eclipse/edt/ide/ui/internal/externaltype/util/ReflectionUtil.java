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
package org.eclipse.edt.ide.ui.internal.externaltype.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaTypeConstants;
import org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype.JavaType;

public class ReflectionUtil {
	
	public static Class<?> getClass(ClassLoader urlLoader,String fullyQualifiedName) {
		try {
			return Class.forName(fullyQualifiedName,true,urlLoader);
		} catch (Throwable ee) {
			ee.printStackTrace();
		} 
		
		return null;
	}
	
	/**
	 * Get inner classes and its super types and referenced types
	 */
	public static void getInnerTypes(Class<?> clazz, Map<Class<?>,JavaType> toBeGenerated) {
		JavaType javaType;
		
		Class<?>[] innerClasses = clazz.getDeclaredClasses();
		if(innerClasses != null) {
			for(Class<?> inner : innerClasses) {
				if (Modifier.isPublic(inner.getModifiers())) {
					javaType = new JavaType();
					javaType.setSource(JavaType.SelectedType);
					
					List<Field> selectedFields = new ArrayList<Field>();
					for(Field field : inner.getDeclaredFields()) {
						 if (Modifier.isPublic(field.getModifiers())) {
							 selectedFields.add(field);
						 }
					}
					javaType.setFields(selectedFields);
					
					List<Constructor<?>> selectedCons = new ArrayList<Constructor<?>>();
					for(Constructor<?> constr : inner.getDeclaredConstructors() ) {
						 if (Modifier.isPublic(constr.getModifiers())) {
							 selectedCons.add(constr);
						 }
					}
					javaType.setConstructors(selectedCons);
					
					List<Method> selectedMethods = new ArrayList<Method>();
					for(Method method : inner.getDeclaredMethods()) {
						if (Modifier.isPublic(method.getModifiers())) {
							selectedMethods.add(method);
						 }
					}
					javaType.setMethods(selectedMethods);
					
					toBeGenerated.put(inner, javaType);
				}
			} //for
		}
	}
	
	public static Set<Class<?>> getAllSuperTypes(Class<?> clazz) {
		Set<Class<?>> superTypes = new HashSet<Class<?>>();
		
		Class<?> declaringClass = clazz;
		while(declaringClass!=null && !declaringClass.equals(Object.class)) {
			Class<?> superClass = declaringClass.getSuperclass();
			if(superClass!=null && !superClass.equals(Object.class)) {
				superTypes.add(superClass);
			}
			
			//try interfaces
			Class<?>[] interfaces = declaringClass.getInterfaces();
			for(Class<?> interfaceClass : interfaces) {
				superTypes.addAll(getImplementedInterfaces(interfaceClass));
			}
			
			declaringClass = declaringClass.getSuperclass();
		}
		
		return superTypes;
	}
	
	public static Set<Class<?>> getImplementedInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        
        if (clazz.isInterface())
            interfaces.add(clazz);

        while (clazz != null) {
            for (Class<?> iface : clazz.getInterfaces())
                  interfaces.addAll(getImplementedInterfaces(iface));
            clazz = clazz.getSuperclass();
        } 

        return interfaces;
    }
	
	public static Set<Class<?>> getFieldReferencedTypes(List<Field> fields) {
		Set<Class<?>> referenced = new HashSet<Class<?>>();
		for(Field field : fields) {
			if(!isBuiltinEglType(field.getType())) {
				referenced.add(field.getType());
			}
		}
		return referenced;
	}
	
	public static Set<Class<?>> getConReferencedTypes(List<Constructor<?>> constructors) {
		Set<Class<?>> referenced = new HashSet<Class<?>>();
		
		for(Constructor<?> constructor : constructors) {
			Class<?>[] paraTypes = constructor.getParameterTypes();
			for(Class<?> paraType : paraTypes) {
				paraType = getComponentClass(paraType);
				if(!isBuiltinEglType(paraType)) {
					referenced.add(paraType);
				}
			}
		}
		
		return referenced;
	}
	
	public static Set<Class<?>> getMethodReferencedTypes(List<Method> methods) {
		Set<Class<?>> referenced = new HashSet<Class<?>>();
		
		for(Method method : methods) {
			Class<?>[] paraTypes = method.getParameterTypes();
			for(Class<?> paraType : paraTypes) {
				paraType = getComponentClass(paraType);
				if(!isBuiltinEglType(paraType) && !Object.class.equals(paraType)) {
					referenced.add(paraType);
				}
			}
			
			Class<?> returnType = getComponentClass(method.getReturnType());
			if(!Void.TYPE.equals(returnType) && !Object.class.equals(returnType)
					&& !isBuiltinEglType(returnType)) {
				referenced.add(returnType);
			}
		}
		
		return referenced;
	}
	
	public static String getMethodLabel(java.lang.reflect.Method method) {
		StringBuilder buffer = new StringBuilder(60);
		buffer.append(method.getName());
		buffer.append('(');
		
		Class<?>[] paraTypes = method.getParameterTypes();
		for(int i=0; i<paraTypes.length; i++) {
			buffer.append(paraTypes[i].getSimpleName());
			if(i < paraTypes.length-1){
				buffer.append(',');
			}
		}
		buffer.append(')');
		return buffer.toString();
	}
	
	public static String getConstructorLabel(java.lang.reflect.Constructor<?> con) {
		StringBuilder buffer = new StringBuilder(60);
		buffer.append(con.getDeclaringClass().getSimpleName());
		buffer.append('(');
		
		Class<?>[] paraTypes = con.getParameterTypes();
		for(int i=0; i<paraTypes.length; i++) {
			buffer.append(paraTypes[i].getSimpleName());
			if(i < paraTypes.length-1){
				buffer.append(',');
			}
		}
		buffer.append(')');
		
		return buffer.toString();
	}
	
	public static String getJavaFieldLabel(java.lang.reflect.Field field) {
		StringBuilder buffer = new StringBuilder(60);
		if(Modifier.isStatic(field.getModifiers())) {
			buffer.append("static ");
		}
		
		if (Modifier.isFinal(field.getModifiers())) {
			buffer.append("final ");
		}
		
		buffer.append(field.getName() + " " + getTypeName(field.getType()));
		
		return buffer.toString();
	}
	
	public static String getFieldLabel(java.lang.reflect.Field field) {
		StringBuilder buffer = new StringBuilder(60);
		if(Modifier.isStatic(field.getModifiers())) {
			buffer.append("static ");
		}
		
		if (Modifier.isFinal(field.getModifiers())) {
			buffer.append("const ");
		}
		
		boolean isEGLKeyWord = EGLNameValidator.isKeyword(field.getName());
		boolean isStartWithEze = field.getName().toLowerCase().startsWith(JavaTypeConstants.EZE_PREFIX);
		if(isEGLKeyWord || isStartWithEze) {
			buffer.append(JavaTypeConstants.UNDERSTORE_PREFIX);
		} 
		
		buffer.append(field.getName() + " " + getEGLTypeName(field.getType()));
		if(isStartWithEze) {
			buffer.append(' ');
			buffer.append("{@externalName{\"" + field.getName() + "\"}}");
		}
		
		return buffer.toString();
	}
	
	public static String getEGLTypeName(Class<?> paraType) {
		String typeName;
		int dim = 0;
		while(paraType.isArray()) {
			dim++;
			paraType = paraType.getComponentType();
		}
		
		typeName = JavaTypeConstants.JavaToEglMapping.get(paraType.getName());
		if(typeName == null) {
			typeName = paraType.getSimpleName();
			if(!paraType.isPrimitive()) {
				typeName = typeName + "?";
			}
			
			if(paraType.getEnclosingClass() != null) {
				typeName = paraType.getEnclosingClass().getSimpleName() 
						  + JavaTypeConstants.UNDERSTORE_PREFIX + typeName;
			}
		}
			
		
		boolean isEGLPart = JavaTypeConstants.EglPartNames.contains(paraType.getSimpleName().toLowerCase(Locale.ENGLISH));
		boolean isStartWithEze = typeName.toLowerCase().startsWith(JavaTypeConstants.EZE_PREFIX);
		if(isEGLPart || isStartWithEze) {
			typeName = JavaTypeConstants.UNDERSTORE_PREFIX + typeName;
		}
		while(dim > 0) {
			typeName = typeName + "[]";
			dim--;
		}
		
		return typeName;
	}
	
	public static String getTypeName(Class<?> paraType) {
		String typeName;
		int dim = 0;
		while(paraType.isArray()) {
			dim++;
			paraType = paraType.getComponentType();
		}
		
		typeName = paraType.getSimpleName();
		
		while(dim > 0) {
			typeName = typeName + "[]";
			dim--;
		}
		
		return typeName;
	}
	
	public static boolean isBuiltinEglType(Class<?> paraType) {
		String typeName;
		paraType = getComponentClass(paraType);
		typeName = JavaTypeConstants.JavaToEglMapping.get(paraType.getName());
		
		return (typeName != null);
	}
	
	public static Class<?> getComponentClass(Class<?> paraType) {
		Class<?> clazz = paraType;
		
		while(clazz.isArray())
			clazz = clazz.getComponentType();
		return clazz;
	}
	
	public static boolean isSkippedType(Class<?> paraType) {
		boolean skipped = false;
		
		if(paraType.isArray()) {
			return true;
		}
		
		if(Byte.TYPE.equals(paraType)
				  || Byte.class.equals(paraType)
				  || Character.TYPE.equals(paraType)
				  || Character.class.equals(paraType)
				  ) {
			skipped = true;
		}
		
		return skipped;
	}
	
}
