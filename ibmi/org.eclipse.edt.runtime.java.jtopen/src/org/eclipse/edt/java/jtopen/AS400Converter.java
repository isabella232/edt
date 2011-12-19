package org.eclipse.edt.java.jtopen;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.eclipse.edt.java.jtopen.access.AS400Date;
import org.eclipse.edt.java.jtopen.access.AS400Text;
import org.eclipse.edt.java.jtopen.access.AS400Time;
import org.eclipse.edt.java.jtopen.access.AS400Timestamp;
import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.json.Json;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.util.TimestampIntervalMask;
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

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Array;
import com.ibm.as400.access.AS400Bin2;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400Bin8;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400DecFloat;
import com.ibm.as400.access.AS400Float4;
import com.ibm.as400.access.AS400Float8;
import com.ibm.as400.access.AS400PackedDecimal;
import com.ibm.as400.access.AS400Structure;
import com.ibm.as400.access.AS400ZonedDecimal;

import eglx.jtopen.annotations.AS400DecimalFloat;
import eglx.jtopen.annotations.AS400DecimalPacked;
import eglx.jtopen.annotations.AS400DecimalZoned;
import eglx.jtopen.annotations.AS400UnsignedBin2;
import eglx.jtopen.annotations.AS400UnsignedBin4;
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

	public static Object convertToObjects(Object object){
	    if(object instanceof AnyBoxedObject<?>)
	        return convertToObjects(((AnyBoxedObject<?>)object).ezeUnbox());
	    else if(object instanceof List<?>){
	    	List<Object> array = new ArrayList<Object>();
	    	for(Object element : (List<?>)object){
	    		array.add(convertToObjects(element));
	    	}
	    	return array.toArray(new Object[array.size()]);
	    }
	    else if(object instanceof AnyValue){
	    	//record
	    	return convertStructureToObjects(object);
	    }
	    else if(object instanceof ExecutableBase){
	    	//handler
	    	return convertStructureToObjects(object);
	    }
	    else{
	    	return object;
	    }
	}
	private static Object convertStructureToObjects(final Object object)throws AnyException
	{
		List<Object> convertedFields = new ArrayList<Object>();
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
					convertedFields.add(convertToObjects(method.invoke(object, (Object[])null)));
				}
				else if(Modifier.isPublic(field.getModifiers())){
					convertedFields.add(convertToObjects(field));
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
	public static AS400DataType createAS400DataType(final Object object, final List<Annotation>annotations,final AS400 connection)throws AnyException
	{
		Annotation annotation = null;
	    if(object instanceof AnyBoxedObject<?>)
	        return createAS400DataType(((AnyBoxedObject<?>)object).ezeUnbox(), annotations, connection);
	    if(object instanceof BigDecimal){
	    	if((annotation = getAnnotation(AS400DecimalFloat.class, annotations)) != null){
	    		return new AS400DecFloat(((AS400DecimalFloat)annotation).length());
	    	}
	    	else if((annotation = getAnnotation(AS400DecimalPacked.class, annotations)) != null){
	    		return new AS400PackedDecimal(((AS400DecimalPacked)annotation).length(), ((AS400DecimalPacked)annotation).decimals());
	    	}
	    	else if((annotation = getAnnotation(AS400DecimalZoned.class, annotations)) != null){
	    		return new AS400ZonedDecimal(((AS400DecimalZoned)annotation).length(), ((AS400DecimalZoned)annotation).decimals());
	    	}
	    }
	    if(object instanceof BigInteger){
	    	if((annotation = getAnnotation(AS400UnsignedBin4.class, annotations)) != null){
	    		return new com.ibm.as400.access.AS400UnsignedBin4();
	    	}
	    	else{
	    		return new AS400Bin8();
	    	}
	    }
	    if(object instanceof Long){
	    	if((annotation = getAnnotation(AS400UnsignedBin4.class, annotations)) != null){
	    		return new com.ibm.as400.access.AS400UnsignedBin4();
	    	}
	    	else{
	    		return new AS400Bin8();
	    	}
	    }
	    if(object instanceof Byte){
    		return new AS400Bin2();
	    }
	    if(object instanceof Short){
	    	return new AS400Bin2();
	    }
	    if(object instanceof Integer){
	    	if((annotation = getAnnotation(AS400UnsignedBin2.class, annotations)) != null){
	    		return new com.ibm.as400.access.AS400UnsignedBin2();
	    	}
	    	else{
	    		return new AS400Bin4();
	    	}
	    }
	    if(object instanceof Double){
	    	return new AS400Float8();
	    }
	    if(object instanceof Float){
	    	return new AS400Float4();
	    }
	    if(object instanceof String){
	    	annotation = getAnnotation(eglx.jtopen.annotations.AS400Text.class, annotations);
	    	if(annotation != null){
	    		if(((eglx.jtopen.annotations.AS400Text)annotation).encoding() != null && !((eglx.jtopen.annotations.AS400Text)annotation).encoding().isEmpty()){
	    			return new AS400Text(((eglx.jtopen.annotations.AS400Text)annotation).length(), ((eglx.jtopen.annotations.AS400Text)annotation).encoding(), ((eglx.jtopen.annotations.AS400Text)annotation).preserveTrailingSpaces());
	    		}
	    		else{
	    			return new AS400Text(((eglx.jtopen.annotations.AS400Text)annotation).length(), connection, ((eglx.jtopen.annotations.AS400Text)annotation).preserveTrailingSpaces());
	    		}
	    	}
	    }
	    if(object instanceof Calendar){
	    	annotation = getAnnotation(eglx.jtopen.annotations.AS400Date.class, annotations);
	    	if(annotation != null){
	    		if(((eglx.jtopen.annotations.AS400Date)annotation).ibmiSeperator().isEmpty()){
	    			return new AS400Date(((eglx.jtopen.annotations.AS400Date)annotation).ibmiFormat());
	    		}
	    		else{
	    			return new AS400Date(((eglx.jtopen.annotations.AS400Date)annotation).ibmiFormat(), "null".equalsIgnoreCase(((eglx.jtopen.annotations.AS400Date)annotation).ibmiSeperator())? null : ((eglx.jtopen.annotations.AS400Date)annotation).ibmiSeperator().charAt(0));
	    		}
	    	}
	    	annotation = getAnnotation(eglx.jtopen.annotations.AS400Timestamp.class, annotations);
	    	if(annotation != null){
	    		TimestampIntervalMask tsMask = new TimestampIntervalMask(((eglx.jtopen.annotations.AS400Timestamp)annotation).eglPattern());
    			return new AS400Timestamp(((eglx.jtopen.annotations.AS400Timestamp)annotation).ibmiFormat(), tsMask.getStartCode(), tsMask.getEndCode());
	    	}
	    	annotation = getAnnotation(eglx.jtopen.annotations.AS400Time.class, annotations);
	    	if(annotation != null){
	    		if(((eglx.jtopen.annotations.AS400Time)annotation).ibmiSeperator().isEmpty()){
	    			return new AS400Time(((eglx.jtopen.annotations.AS400Time)annotation).ibmiFormat());
	    		}
	    		else{
	    			return new AS400Time(((eglx.jtopen.annotations.AS400Time)annotation).ibmiFormat(), "null".equalsIgnoreCase(((eglx.jtopen.annotations.AS400Time)annotation).ibmiSeperator())? null : ((eglx.jtopen.annotations.AS400Time)annotation).ibmiSeperator().charAt(0));
	    		}
	    	}
	    }
	    if(object instanceof List<?>){
	    	if((( List<?>)object).size() == 0){
	    		resizeList(( List<?>)object, annotations);
	    	}
	    	if((( List<?>)object).size() > 0){
	    		AS400DataType arrayType = createAS400DataType((( List<?>)object).get(0), annotations, connection);
		    	return new AS400Array(arrayType, ((List<?>)object).size());
	    	}
	    }
	    if(object instanceof AnyValue){
	    	//record
	    	return createToAS400Structure(object, connection, annotations);
	    }
	    if(object instanceof ExecutableBase){
	    	//handler
	    	return createToAS400Structure(object, connection, annotations);
	    }
	    
	    InvalidArgumentException ex = new InvalidArgumentException();
		ex.setMessageID("createAS400DataType");
		ex.setMessage("Can not determine the type for:" + object.getClass().getName());
		throw ex;
	}
	private static AS400Structure createToAS400Structure(final Object object,final AS400 connection, List<Annotation>annotations)throws AnyException
	{
    	List<AS400DataType> structuredFields = new ArrayList<AS400DataType>();
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
					structuredFields.add(createAS400DataType(method.invoke(object, (Object[])null), new ArrayList<Annotation>(annotationList(method)), connection));
				}
				else if(Modifier.isPublic(field.getModifiers())){
					structuredFields.add(createAS400DataType(field, new ArrayList<Annotation>(annotationList(field)), connection));
				}
			} catch (Throwable t) {
			    InvalidArgumentException ex = new InvalidArgumentException();
				ex.initCause(t);
				ex.setMessageID("CreateStructure");
				ex.setMessage("Error creating AS400Structure field:" + name);
				throw ex;
			}
		}
	    return new AS400Structure(structuredFields.toArray(new AS400DataType[structuredFields.size()]));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void resizeList(List list, List<Annotation> annotations){
		Annotation annotation = getAnnotation(eglx.jtopen.annotations.AS400Array.class, annotations);
    	if(annotation != null){
			int size = ((eglx.jtopen.annotations.AS400Array)annotation).elementCount();
			annotation = getAnnotation(Json.class, annotations);
			if(annotation != null && ((Json)annotation).clazz() != null){
				if(EBigint.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.BigintFactory);
				}
				else if(EDate.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.DateFactory);
				}
				else if(EDecimal.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.DecimalFactory);
				}
				else if(EFloat.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.FloatFactory);
				}
				else if(EInt.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.IntFactory);
				}
				else if(ESmallfloat.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.SmallfloatFactory);
				}
				else if(ESmallint.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.SmallintFactory);
				}
				else if(EString.class.equals( ((Json)annotation).clazz())){
					EList.resize(list, size, EList.StringFactory);
				}
				else if(ETimestamp.class.equals( ((Json)annotation).clazz())){
			    	annotation = getAnnotation(eglx.jtopen.annotations.AS400Timestamp.class, annotations);
			    	if(annotation != null){
			    		TimestampIntervalMask tsMask = new TimestampIntervalMask(((eglx.jtopen.annotations.AS400Timestamp)annotation).eglPattern());
			    		EList.resize(list, size, new EList.TimestampFactory(tsMask.getStartCode(), tsMask.getEndCode()));
			    	}
				}
				else if(AnyValue.class.isAssignableFrom(((Json)annotation).clazz())){
		    		EList.resize(list, size, new EList.ElementFactory(((Json)annotation).clazz()));
				}
				else if(ExecutableBase.class.isAssignableFrom(((Json)annotation).clazz())){
		    		EList.resize(list, size, new EList.ElementFactory(((Json)annotation).clazz()));
				}
			}
    	}
	}

	private static List<Annotation> annotationList(AccessibleObject obj){
		Annotation[] annotations = obj.getAnnotations();
		List<Annotation> annotationList = new ArrayList<Annotation>();
		if(annotations != null){
			annotationList.addAll(Arrays.asList(annotations));
		}
		return annotationList;
	}
	private static Annotation getAnnotation(final Class<?> annotationClass, final List<Annotation> annotations){
		if(annotations != null){
			for(Annotation annotation : annotations){
				if(annotationClass.equals(annotation.annotationType())){
					return annotation;
				}
			}
		}
		return null;
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
