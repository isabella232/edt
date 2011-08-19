/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.TypeConstraints;
import org.eclipse.edt.javart.util.JavartUtil;

import egl.lang.AnyException;
import egl.lang.DynamicAccessException;
import egl.lang.TypeCastException;

public abstract class EglAny implements egl.lang.EglAny {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public static egl.lang.EglAny box(egl.lang.EglAny value) throws AnyException {
		return ezeBox(value);
	}
		
	@SuppressWarnings("unchecked")
	public static <R extends Object> AnyBoxedObject<R> ezeBox(R object) throws AnyException {
		if (object instanceof AnyValue) {
			AnyValue newValue = ((AnyValue)object).ezeNewValue();
			newValue.ezeCopy((AnyValue)object);
			return new AnyBoxedObject<R>((R)newValue);
		}
		return new AnyBoxedObject<R>(object);
	}
	
	public static <R extends Object> AnyBoxedObject<R> ezeWrap(R object) {
		return new AnyBoxedObject<R>(object);
	}

	public static <T extends Object> T ezeCast(Object value, Class<T> clazz) throws AnyException {
		try {
			Object unboxed = value instanceof egl.lang.EglAny ? ((egl.lang.EglAny)value).ezeUnbox() : value;
			return clazz.cast(unboxed);
		}
		catch(ClassCastException ex) {
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = clazz.getName();
			Object unboxed = value instanceof egl.lang.EglAny ? ((egl.lang.EglAny)value).ezeUnbox() : value;
			tcx.actualTypeName = unboxed.getClass().getName();
			throw tcx;
		}
	}
	
	/**
	 * Dynamic implementation of data type conversion.  Used when casting values
	 * of unknown type to the receiver type.  This method looks up the appropriate
	 * conversion operation first in the receiver class and if not found then
	 * lookup will continue into the class of the <code>value</code> parameter.
	 *
	 * @param value
	 * @param conversionMethod
	 * @param clazz
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws AnyException
	 */
	@SuppressWarnings("unchecked")
	public static Object ezeCast(Object value, String conversionMethod, Class clazz, Class[] parameterTypes, Object[] args) throws AnyException {
		try {
			// Conversion operation needs to be invoked
			Method method = null;
			Object unboxed = value instanceof egl.lang.EglAny ? ((egl.lang.EglAny)value).ezeUnbox() : value;
			Class[] parmTypes = new Class[parameterTypes == null ? 1 : parameterTypes.length + 1];
			parmTypes[0] = unboxed.getClass();
			if (parameterTypes != null)
				java.lang.System.arraycopy(parameterTypes, 0, parmTypes, 1, parameterTypes.length);
			try {
				method = clazz.getMethod(conversionMethod, parmTypes);
			}
			catch(NoSuchMethodException ex) {
				try {
					method = value.getClass().getMethod(conversionMethod, parmTypes);
				} 
				catch (NoSuchMethodException ex1) {
					TypeCastException tcx = new TypeCastException();
					tcx.castToName = clazz.getName();
					tcx.actualTypeName = unboxed.getClass().getName();
					throw tcx;
				}
			}
			if (args == null) 
				return method.invoke(null, unboxed);
			else 
				return method.invoke(null, unboxed, args);
		}
		catch(InvocationTargetException ex) {
			if (ex.getTargetException() instanceof AnyException)
				throw (AnyException)ex.getTargetException();
			else
				throw JavartUtil.makeEglException(ex.getTargetException());
		}
		catch (Exception ex1) {
			// Should not ever get here
			throw JavartUtil.makeEglException(ex1);
		}
	}

	
	public static <T extends Object> boolean ezeIsa(Object object, Class<T> clazz) {
		return clazz.isInstance(object);
	}
	
	public EglAny() { super(); }
	
	public java.lang.String ezeTypeSignature() {
		return getClass().getName();
	}
	
	public String ezeName() {
		return getClass().getSimpleName();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	public egl.lang.EglAny ezeGet(String name) throws AnyException {
		try {
			Field field = this.getClass().getField(name);
			if (ezeTypeConstraints(name) != null) {
				TypeConstraints constraints = ezeTypeConstraints(name);
				Object value = field.get(this);
				return constraints.box(value);
			}
			Object value = field.get(this);
			return value instanceof egl.lang.EglAny ? (egl.lang.EglAny) value : ezeBox(value);
		} catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			throw dax;
		} 
	}

	@SuppressWarnings("unchecked")
	public void ezeSet(String name, Object value) throws AnyException {
		try {
			Field field = this.getClass().getField(name);
			if (ezeTypeConstraints(name) != null){
				TypeConstraints constraints = ezeTypeConstraints(name);
				value = constraints.constrainValue(value);
			}
			else if (value instanceof AnyBoxedObject) {
				value = ((AnyBoxedObject<Object>)value).ezeUnbox();
			}
			field.set(this, value);
		} catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			throw dax;
		} 
	}

	@SuppressWarnings("unchecked")
	@Override
	public egl.lang.EglAny ezeUnbox() {
		return this;
	}

	/**
	 * Subclasses of this method that contain fields with types that can be parameterized with constraints such as length, precision
	 * decimals, pattern, etc. should override this method to return the particular constraints set each field.  These 
	 * constraints are used to convert values to conform to the given constraints.  An example would be a string of length 10 being
	 * dynamically assigned to a String field that was defined as char(6).  The actual java field type will be java.lang.String.  However
	 * the TypeConstraints for that field will have a reference to Class egl.lang.Char.class as well as the length of 6.  This information
	 * will be used to then truncated the string of length 10 to one of length 6.
	 * @param fieldName
	 * @return
	 */
	public TypeConstraints ezeTypeConstraints(String fieldName) {
		return null;
	}
	
	/**
	 * Default implementation does nothing
	 */
	public void ezeInitialize() throws AnyException {
		
	}
	
//	private void throwDynamicAccessException(String name) throws AnyException {
//		JavartUtil.throwRuntimeException( 
//				Message.DYNAMIC_ACCESS_FAILED, 
//				JavartUtil.errorMessage( 
//						ezeProgram, 
//						Message.DYNAMIC_ACCESS_FAILED,
//						new Object[] { name, this.ezeTypeSignature() } ), 
//						ezeProgram );
//
//	}
}
