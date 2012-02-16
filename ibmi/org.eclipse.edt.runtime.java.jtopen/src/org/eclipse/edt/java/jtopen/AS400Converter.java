/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.java.jtopen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.java.jtopen.access.AS400Array;
import org.eclipse.edt.java.jtopen.access.AS400Timestamp;
import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.runtime.java.eglx.lang.EBigint;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import org.eclipse.edt.runtime.java.eglx.lang.EFloat;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallfloat;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallint;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;

import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Structure;

import eglx.lang.AnyException;
import eglx.lang.AnyValue;
import eglx.lang.InvalidArgumentException;

public class AS400Converter {
	public AS400Converter xx;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object convertFromAS400(final Object eglObject, final Object as400ConvertedData)throws AnyException
	{
	    if(eglObject instanceof AnyBoxedObject<?>){
	    	((AnyBoxedObject)eglObject).ezeCopy(convertFromAS400(((AnyBoxedObject<?>)eglObject).ezeUnbox(), as400ConvertedData));
	        return eglObject;
	    }
	    if(eglObject instanceof List<?>){
	    	for(int idx = 0; idx < ((List<?>)eglObject).size(); idx++){
	    		((List)eglObject).set(idx, convertFromAS400(((List<?>)eglObject).get(idx), ((Object[])as400ConvertedData)[idx]));
	    	}
	    	return eglObject;
	    }
	    else if(eglObject instanceof AnyValue){
	    	//record
	    	return convertFromAS400Structure(eglObject, as400ConvertedData);
	    }
	    else if(eglObject instanceof ExecutableBase){
	    	//handler
	    	return convertFromAS400Structure(eglObject, as400ConvertedData);
	    }
	    else{
	    	return as400ConvertedData;
	    }
	}
	private static Object convertFromAS400Structure(final Object object, final Object as400ConvertedData)throws AnyException
	{
		int idx = 0;
		for(Field field : getFields(object.getClass())){
			String name = field.getName();
			try {
				StringBuilder setterName = new StringBuilder(field.getName().substring(0,1).toUpperCase());
				setterName.append(field.getName().substring(1));
				Method setMethod = null;
				try{
					setMethod = object.getClass().getMethod(new StringBuilder("set").append(setterName).toString(), field.getType());
				}
				catch(NoSuchMethodException nsme){
				}
				Method getMethod = null;
				try{
					getMethod = object.getClass().getMethod(new StringBuilder("get").append(setterName).toString(), (Class<?>[])null);
				}
				catch(NoSuchMethodException nsme){
				}
				if(getMethod == null){
					try{
						getMethod = object.getClass().getMethod(new StringBuilder("is").append(setterName).toString(), (Class<?>[])null);
					}
					catch(NoSuchMethodException nsme){
					}
				}
				Object existingFieldObject = null;
				if(getMethod != null && Modifier.isPublic(getMethod.getModifiers())){
					existingFieldObject = getMethod.invoke(object, (Object[])null);
				}
				else if(Modifier.isPublic(field.getModifiers())){
					existingFieldObject = field.get(object);
				}
				if((getMethod != null && Modifier.isPublic(getMethod.getModifiers()) ||
						Modifier.isPublic(field.getModifiers()))){
					Object newEglValue = convertFromAS400(existingFieldObject, ((Object[])as400ConvertedData)[idx++]);
					if(setMethod != null && Modifier.isPublic(setMethod.getModifiers())){
						setMethod.invoke(object, newEglValue);
					}
					else if(Modifier.isPublic(field.getModifiers())){
						field.set(object, newEglValue);
					}
				}
			} catch (Throwable t) {
			    InvalidArgumentException ex = new InvalidArgumentException();
				ex.initCause(t);
				ex.setMessageID("convertFromAS400Structure");
				ex.setMessage("Error convertFromAS400Structure field:" + name);
				throw ex;
			}
		}
	    return object;
	}

	@SuppressWarnings("unchecked")
	public static Object convertToObjects(Object object, AS400DataType as400Datatype){
	    if(object instanceof AnyBoxedObject<?>)
	        return convertToObjects(((AnyBoxedObject<?>)object).ezeUnbox(), as400Datatype);
	    else if(object instanceof List<?> && as400Datatype instanceof AS400Array){
	    	List<Object> array = new ArrayList<Object>();
	    	AS400DataType arrayElementsAS400Type = ((AS400Array)as400Datatype).getType();
	    	if(array.size() != ((AS400Array)as400Datatype).getNumberOfElements()){
	    		resizeList((List<Object>)object, (AS400Array)as400Datatype);
	    	}
	    	for(Object element : (List<?>)object){
	    		array.add(convertToObjects(element, arrayElementsAS400Type));
	    	}
	    	return array.toArray(new Object[array.size()]);
	    }
	    else if(object instanceof AnyValue && as400Datatype instanceof AS400Structure){
	    	//record
	    	return convertStructureToObjects(object, (AS400Structure)as400Datatype);
	    }
	    else if(object instanceof ExecutableBase && as400Datatype instanceof AS400Structure){
	    	//handler
	    	return convertStructureToObjects(object, (AS400Structure)as400Datatype);
	    }
	    else{
	    	return object;
	    }
	}
	private static Object convertStructureToObjects(final Object object, AS400Structure as400Datatype)throws AnyException
	{
		List<Object> convertedFields = new ArrayList<Object>();
		AS400DataType[] as400Fields = as400Datatype.getMembers(); 
		int as400Idx = 0;
		for(Field field : getFields(object.getClass())){
			String name = field.getName();
			try {
				StringBuilder getterName = new StringBuilder(field.getName().substring(0,1).toUpperCase());
				getterName.append(field.getName().substring(1));
				Method method = null;
				try{
					method = object.getClass().getMethod(new StringBuilder("get").append(getterName).toString(), (Class<?>[])null);
				}
				catch(NoSuchMethodException nsme){
				}
				if(method == null){
					try{
						method = object.getClass().getMethod(new StringBuilder("is").append(getterName).toString(), (Class<?>[])null);
					}
					catch(NoSuchMethodException nsme){
					}
				}
				if(method != null && Modifier.isPublic(method.getModifiers())){
					convertedFields.add(convertToObjects(method.invoke(object, (Object[])null), as400Fields[as400Idx++]));
				}
				else if(Modifier.isPublic(field.getModifiers())){
					convertedFields.add(convertToObjects(field.get(object), as400Fields[as400Idx++]));
				}
			} catch (Throwable t) {
			    InvalidArgumentException ex = new InvalidArgumentException();
				ex.initCause(t);
				ex.setMessageID("convertStructureToObjects");
				ex.setMessage("Error convert AS400 objects to a structure field:" + name);
				throw ex;
			}
		}
		return convertedFields.toArray(new Object[convertedFields.size()]);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void resizeList(List list, AS400Array as400Array){
		if(EBigint.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.BigintFactory);
		}
		else if(EDate.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.DateFactory);
		}
		else if(EDecimal.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.DecimalFactory);
		}
		else if(EFloat.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.FloatFactory);
		}
		else if(EInt.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.IntFactory);
		}
		else if(ESmallfloat.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.SmallfloatFactory);
		}
		else if(ESmallint.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.SmallintFactory);
		}
		else if(EString.class.equals(as400Array.getEGLElementType())){
			EList.resize(list, as400Array.getNumberOfElements(), EList.StringFactory);
		}
		else if(ETimestamp.class.equals(as400Array.getEGLElementType())){
			AS400DataType elementType = as400Array.getType();
			while(elementType instanceof AS400Array){
				elementType = ((AS400Array)elementType).getType();
			}
	    	if(elementType instanceof AS400Timestamp){
	    		EList.resize(list, as400Array.getNumberOfElements(), new EList.TimestampFactory(((AS400Timestamp)elementType).getStartCode(), ((AS400Timestamp)elementType).getEndCode()));
	    	}
		}
		else if(AnyValue.class.isAssignableFrom(as400Array.getEGLElementType())){
    		EList.resize(list, as400Array.getNumberOfElements(), new EList.ElementFactory(as400Array.getEGLElementType()));
		}
		else if(ExecutableBase.class.isAssignableFrom(as400Array.getEGLElementType())){
    		EList.resize(list, as400Array.getNumberOfElements(), new EList.ElementFactory(as400Array.getEGLElementType()));
		}
	}

    private static List<Field> getFields(Class<?> clazz){
    	List<Field> fields = new ArrayList<Field>();
    	do{
    		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    		clazz = clazz.getSuperclass();
    	}while(eglx.lang.EAny.class.isAssignableFrom(clazz) || Executable.class.isAssignableFrom(clazz));
    	return fields;
    }

}
