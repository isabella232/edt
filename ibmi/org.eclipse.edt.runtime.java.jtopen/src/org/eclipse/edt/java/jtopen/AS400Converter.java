package org.eclipse.edt.java.jtopen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.resources.ExecutableBase;

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
	
/*	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	}*/
    private static List<Field> getFields(Class<?> clazz){
    	List<Field> fields = new ArrayList<Field>();
    	do{
    		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    		clazz = clazz.getSuperclass();
    	}while(eglx.lang.EAny.class.isAssignableFrom(clazz) || Executable.class.isAssignableFrom(clazz));
    	return fields;
    }

}
