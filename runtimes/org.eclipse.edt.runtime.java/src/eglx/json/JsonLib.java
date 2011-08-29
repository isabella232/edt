/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eglx.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.BooleanNode;
import org.eclipse.edt.javart.json.DecimalNode;
import org.eclipse.edt.javart.json.FloatingPointNode;
import org.eclipse.edt.javart.json.IntegerNode;
import org.eclipse.edt.javart.json.Json;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.JsonUtilities;
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.NullNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.edt.javart.json.ValueNode;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartUtil;
import org.eclipse.edt.runtime.java.egl.lang.AnyValue;
import org.eclipse.edt.runtime.java.egl.lang.EBigint;
import org.eclipse.edt.runtime.java.egl.lang.EBoolean;
import org.eclipse.edt.runtime.java.egl.lang.EDate;
import org.eclipse.edt.runtime.java.egl.lang.EDecimal;
import org.eclipse.edt.runtime.java.egl.lang.EDictionary;
import org.eclipse.edt.runtime.java.egl.lang.EFloat;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import org.eclipse.edt.runtime.java.egl.lang.ESmallfloat;
import org.eclipse.edt.runtime.java.egl.lang.ESmallint;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import org.eclipse.edt.runtime.java.egl.lang.ETimestamp;
import org.eclipse.edt.runtime.java.egl.lang.EglAny;
import org.eclipse.edt.runtime.java.egl.lang.EglList;
import org.eclipse.edt.runtime.java.egl.lang.NullType;

import com.ibm.icu.text.SimpleDateFormat;

import egl.lang.AnyException;
import eglx.http.HttpResponse;
import eglx.lang.StringLib;

public class JsonLib {

	public static String convertToJSON(Object obj){
		return process(obj).toJson(); 
	}

	public static String convertToJSON(HttpResponse response){
		EDictionary httpResponse = new EDictionary();
		httpResponse.put("headers", response.getHeaders());
		httpResponse.put("body", response.getBody());
		httpResponse.put("status", response.getStatus());
		httpResponse.put("statusMessage", response.getStatusMessage());
		return process(httpResponse).toJson(); 
	}

	public static ValueNode convertToJsonNode(Object obj){
		return process(obj); 
	}

	private static ValueNode process(Object object)throws AnyException
	{
	    if(object == null)
	        return new NullNode();
	    if(object instanceof AnyBoxedObject<?>)
	        return process(((AnyBoxedObject<?>)object).ezeUnbox());
	    if(object instanceof BigDecimal)
	        return new DecimalNode((BigDecimal)object);
	    if(object instanceof BigInteger)
	        return new IntegerNode((BigInteger)object);
	    if(object instanceof Byte)
	        return new IntegerNode((Byte)object);
	    if(object instanceof Short)
	        return new IntegerNode((Short)object);
	    if(object instanceof Integer)
	        return new IntegerNode((Integer)object);
	    if(object instanceof Long)
	        return new IntegerNode((Long)object);
	    if(object instanceof Boolean)
	        return new BooleanNode((Boolean)object);
	    if(object instanceof Double)
	        return new FloatingPointNode((Double)object);
	    if(object instanceof Float)
	        return new FloatingPointNode((Float)object);
	    if(object instanceof String)
	        return new StringNode((String)object, false);
	    if(object instanceof Enum<?>)
	        return new IntegerNode(((Enum<?>)object).ordinal() + 1);
	    if(object instanceof Calendar)
	        return process((Calendar)object);
	    if(object instanceof EglList<?>)
	        return process((EglList<?>)object);
	    if(object instanceof EDictionary)
	    	return process((EDictionary)object);
	    if(object instanceof AnyValue)
	    	return processObject(object);
	    if(object instanceof ExecutableBase)
	    	return processObject(object);
	    if(object instanceof AnyException)
	    	return processObject(object);
		throw new AnyException(Message.SOA_E_JSON_TYPE_EXCEPTION,
				JavartUtil.errorMessage( Runtime.getRunUnit(), Message.SOA_E_JSON_TYPE_EXCEPTION, 
						new Object[] { object.getClass().getName() } ));
	}
	
	private static ValueNode process(EglList<?> array)throws AnyException
	{
	    ArrayNode node = new ArrayNode();
	    for(Object object : array)
	    {
	        node.addValue(process(object));
	    }
	
	    return node;
	}
	private static ValueNode process(Calendar calendar)throws AnyException
	{
	    String format = null;
	    if(calendar.isSet(Calendar.HOUR_OF_DAY) || 
	    		calendar.isSet(Calendar.MINUTE) || 
	    		calendar.isSet(Calendar.SECOND) ||
	    		calendar.isSet(Calendar.MILLISECOND)){
	    	//timestamp
	    	format = "yyyy-MM-dd HH:mm:ss";
	    }
	    else{
	    	//date
	    	format = "yyyy-MM-dd";
	    }
	    return new StringNode(StringLib.format(calendar, format), false);
	}

	private static ValueNode process(EDictionary dictionary)throws AnyException
	{
		ObjectNode objectNode = new ObjectNode();
	    String[] contents = dictionary.getKeyArray();
	    for(int i = 0; i < contents.length; i++){
	    	objectNode.addPair(new NameValuePairNode(new StringNode(contents[i], false), process(dictionary.get(contents[i]))));
	    }
	
	    return objectNode;
	}
	private static ValueNode processObject(Object object)throws AnyException
	{
		//get fields
		//	if there is a json name use the name and get the field value
		//	else if is there a get or is method
		//		use the json name 
		//	else if method is public
		ObjectNode objectNode = new ObjectNode();
		String name;
		for(Field field : getFields(object.getClass())){
			name = field.getName();
			try {
				Annotation annot = field.getAnnotation(Json.class);
				if(annot != null){
					name = ((Json)annot).name();
				}
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
					annot = method.getAnnotation(Json.class);
					if(annot != null){
						name = ((Json)annot).name();
					}
					objectNode.addPair(new NameValuePairNode(new StringNode(name, false), process(method.invoke(object, (Object[])null))));
				}
				else if(Modifier.isPublic(field.getModifiers())){
					objectNode.addPair(new NameValuePairNode(new StringNode(name, false), process(field.get(object))));
				}
			} catch (Throwable t) {
				AnyException exc = new AnyException(Message.SOA_E_JSON_TYPE_EXCEPTION,
						JavartUtil.errorMessage( Runtime.getRunUnit(), Message.SOA_E_JSON_TYPE_EXCEPTION, 
								new Object[] { name, object.getClass().getName() } ));
				exc.initCause(t);
				throw exc;
			}
		}
	    return objectNode;
	}
	
	
	
	public static void convertFromJSON(String jsonString, Object obj)throws AnyException{
		try {
			convertToEgl(obj, JsonParser.parseValue(jsonString));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			throw new AnyException(e);
		}
	}
	public static Object convertToEgl(ValueNode jsonValue) throws AnyException{
		return convertToEgl(null, jsonValue);
	}
	public static Object convertToEgl(Object object, ValueNode jsonValue) throws AnyException{
		if(object instanceof AnyBoxedObject)
		{
			convertToEgl(((AnyBoxedObject<?>)object).ezeUnbox(), jsonValue);
		}
		else if(object instanceof ExecutableBase && jsonValue instanceof ObjectNode){
			convertObject(object, (ObjectNode)jsonValue);
		}
		else if(object instanceof AnyValue && jsonValue instanceof ObjectNode){
			convertObject(object, (ObjectNode)jsonValue);
		}
		else if(object instanceof EDictionary && jsonValue instanceof ObjectNode){
			((EDictionary)object).removeAll();
			convertObject((EDictionary)object, (ObjectNode)jsonValue);
		}
		else if(object == null && jsonValue instanceof ObjectNode){
			convertObject(new EDictionary(), (ObjectNode)jsonValue);
		}
		else if(jsonValue instanceof NullNode){
			object = null;
		}
		return object;
	}
	static Object convertToEgl(final Class<?> fieldType, final String[] fieldTypeOptions, final Object field, ValueNode jsonValue) throws AnyException{
		try{
	        if(jsonValue instanceof NullNode){
	        	return null;
	        }
	        if(jsonValue instanceof ArrayNode){
	        	EglList<Object> list = new EglList<Object>();
	        	for(Object node : ((ArrayNode)jsonValue).getValues()){
	        		list.add(convertToEgl(fieldType, fieldTypeOptions, null, (ValueNode)node));
	        	}
	        	return list;
	        }
	        if(fieldType.equals(EBigint.class)){
	        	return EBigint.asBigint(jsonValue.toJava());
	        }
	        if(fieldType.equals(EBoolean.class) && jsonValue instanceof BooleanNode){
	       		return ((BooleanNode)jsonValue).getValue();
	        }
	        if(fieldType.equals(EDate.class)){
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		Calendar cal = DateTimeUtil.getBaseCalendar();
	    		cal.setTimeInMillis(sdf.parse(jsonValue.toJava()).getTime());
	    		cal.get(Calendar.YEAR);
	       		return EDate.asDate(cal);
	        }
	        if(fieldType.equals(EDecimal.class)){
	        	///get options for the as statement
	        	if(fieldTypeOptions != null && fieldTypeOptions.length > 1){
		        	int length = Integer.parseInt(fieldTypeOptions[0]);
		        	int decimal = Integer.parseInt(fieldTypeOptions[1]);
		        	return EDecimal.asDecimal(jsonValue.toJava(), length, decimal);
	        	}
	        	else{
		        	return EDecimal.asDecimal(jsonValue.toJava());
	        	}
	        }
	        if(fieldType.equals(EFloat.class)){
	        	return EFloat.asFloat(jsonValue.toJava());
	        }
	        if(fieldType.equals(EInt.class)){
	        	return EInt.asInt(jsonValue.toJava());
	        }
	        if(fieldType.equals(ESmallfloat.class)){
	        	return ESmallfloat.asSmallfloat(jsonValue.toJava());
	        }
	        if(fieldType.equals(ESmallint.class)){
	        	return ESmallint.asSmallint(jsonValue.toJava());
	        }
	        if(fieldType.equals(EString.class)){
	        	return jsonValue.toJava();
	        }
	        if(fieldType.equals(ETimestamp.class)){
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		Calendar cal = DateTimeUtil.getBaseCalendar();
	    		cal.setTimeInMillis(sdf.parse(jsonValue.toJava()).getTime());
	    		cal.get(Calendar.YEAR);
        		int start = ETimestamp.YEAR_CODE;
        		int end = ETimestamp.SECOND_CODE;
	        	if(fieldTypeOptions != null && fieldTypeOptions.length > 1){
	        		start = getETimestampStaticField(fieldTypeOptions[0]);
	        		end = getETimestampStaticField(fieldTypeOptions[1]);
	        	}
        		return ETimestamp.asTimestamp(cal, start, end);
	        }
	        if(AnyValue.class.isAssignableFrom(fieldType) && jsonValue instanceof ObjectNode){
	        	AnyValue anyValue;
	        	if(field instanceof AnyValue){
	        		anyValue = (AnyValue)field;
	        	}
	        	else {
	        		anyValue =  (AnyValue)fieldType.newInstance();
	        	}
	        	return convertObject(anyValue,(ObjectNode)jsonValue);
	        }
	        if(ExecutableBase.class.isAssignableFrom(fieldType) && jsonValue instanceof ObjectNode){
	        	ExecutableBase base;
	        	if(field instanceof ExecutableBase){
	        		base = (ExecutableBase)field;
	        	}
	        	else {
	        		base =  (ExecutableBase)fieldType.newInstance();
	        	}
	        	return convertObject(base,(ObjectNode)jsonValue);
	        }
	        if(fieldType.isEnum() && jsonValue instanceof IntegerNode){
	        	for(Object enumConstant : fieldType.getEnumConstants()){
	        		if(enumConstant instanceof Enum){
	        			int value;
	        			try{
	        				Method method = fieldType.getMethod("getValue", (Class[])null);
	        				value = ((Integer)method.invoke(enumConstant, (Object[])null));
	        			}
	        			catch(NoSuchMethodException nsme){
	        				value = ((Enum<?>)enumConstant).ordinal() + 1;
	        			}
	        			if(((IntegerNode)jsonValue).getBigIntegerValue().intValue() == value){
	        				return enumConstant;
	        			}
	        		}
	        		else{
	        			//FIXME
	        		}
	        	}
	        }
	        if(jsonValue instanceof ObjectNode){
	        	EDictionary dict;
	        	if(field instanceof EDictionary){
	        		dict = (EDictionary)field;
	        	}
	        	else {
	        		dict = new EDictionary();
	        	}
	        	convertObject(dict, (ObjectNode)jsonValue);
	        	return dict;
	        }
		} catch (Throwable t) {
			AnyException e = new AnyException();
			e.initCause(t);
			throw e;
		}
        throw new AnyException();
	}

	private static int getETimestampStaticField(String fieldName){
		return 0;
	}
    private static EDictionary convertObject(EDictionary dictionary, ObjectNode objectNode)
	    throws AnyException
	{
		for(Object pair : objectNode.getPairs()){
			dictionary.put(((NameValuePairNode)pair).getName().getJavaValue(), convertJsonNode(((NameValuePairNode)pair).getValue()));
		}
		return dictionary;
	}
    private static Object convertJsonNode(ValueNode jsonValue){
    	Object retVal = null;
        if(jsonValue instanceof ArrayNode){
        	retVal = new EglList<Object>();
        	for(Object node : ((ArrayNode)jsonValue).getValues()){
        		((EglList<Object>)retVal).add(convertJsonNode((ValueNode)node));
        	}
        }
    	else if(jsonValue instanceof BooleanNode){
    		retVal = convertToEgl(EBoolean.class, null, null, jsonValue);
    	}
    	else if(jsonValue instanceof DecimalNode){
    		retVal = convertToEgl(EDecimal.class, null, null, jsonValue);
    	}
    	else if(jsonValue instanceof FloatingPointNode){
    		retVal = convertToEgl(EFloat.class, null, null, jsonValue);
    	}
    	else if(jsonValue instanceof IntegerNode){
    		retVal = convertToEgl(EBigint.class, null, null, jsonValue);
    	}
    	else if(jsonValue instanceof NullNode){
        	return NullType.ezeWrap(null);
    	}
    	else if(jsonValue instanceof ObjectNode){
    		retVal = convertToEgl(EDictionary.class, null, null, jsonValue);
    	}
    	else if(jsonValue instanceof StringNode){
    		retVal = convertToEgl(EString.class, null, null, jsonValue);
    	}
    	if(!(retVal instanceof egl.lang.EglAny)){
    		retVal = EglAny.ezeBox(retVal);
    	}
    	return retVal;
    }
    private static Object convertObject(Object object, ObjectNode objectNode)
	    throws AnyException
	{
		//get fields
		//	if there is a json name use the name and get the field value
		//	else if is there a get or is method
		//		use the json name 
		//	else if method is public
		String name;
		for(Field field : getFields(object.getClass())){
			name = field.getName();
			try {
				Json jsonAnnotation = field.getAnnotation(Json.class);
				if(jsonAnnotation != null){
					name = ((Json)jsonAnnotation).name();
				}
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
					if(getMethod.getAnnotation(Json.class) != null){
						jsonAnnotation = getMethod.getAnnotation(Json.class);
						name = ((Json)jsonAnnotation).name();
					}
					existingFieldObject = getMethod.invoke(object, (Object[])null);
				}
				else if(Modifier.isPublic(field.getModifiers())){
					existingFieldObject = field.get(object);
				}
				if(jsonAnnotation != null && (getMethod != null && Modifier.isPublic(getMethod.getModifiers()) ||
						Modifier.isPublic(field.getModifiers()))){
					Object newEglValue = convertToEgl(jsonAnnotation.clazz(), jsonAnnotation.asOptions(), existingFieldObject, JsonUtilities.getValueNode(objectNode, name));
					if(setMethod != null && Modifier.isPublic(setMethod.getModifiers())){
						setMethod.invoke(object, newEglValue);
					}
					else if(Modifier.isPublic(field.getModifiers())){
						field.set(object, newEglValue);
					}
				}
			} catch (Throwable t) {
				AnyException exc = new AnyException(Message.SOA_E_JSON_TYPE_EXCEPTION,
						JavartUtil.errorMessage( Runtime.getRunUnit(), Message.SOA_E_JSON_TYPE_EXCEPTION, 
								new Object[] { name, object.getClass().getName() } ));
				exc.initCause(t);
				throw exc;
			}
		}
	    return object;
	}
    
    private static List<Field> getFields(Class<?> clazz){
    	List<Field> fields = new ArrayList<Field>();
    	do{
    		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    		clazz = clazz.getSuperclass();
    	}while(egl.lang.EglAny.class.isAssignableFrom(clazz) || Executable.class.isAssignableFrom(clazz));
    	return fields;
    }
}
