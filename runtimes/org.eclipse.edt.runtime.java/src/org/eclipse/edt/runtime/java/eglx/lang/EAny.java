/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.eglx.lang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.TypeConstraints;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

import eglx.lang.AnyException;
import eglx.lang.DynamicAccessException;
import eglx.lang.NullValueException;
import eglx.lang.TypeCastException;

public abstract class EAny implements eglx.lang.EAny {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public static eglx.lang.EAny box(eglx.lang.EAny value) throws AnyException {
		return ezeBox(value);
	}

	@SuppressWarnings("unchecked")
	public static <R extends Object> AnyBoxedObject<R> ezeBox(R object) throws AnyException {
		if (object instanceof AnyValue) {
			AnyValue newValue = ((AnyValue) object).ezeNewValue();
			newValue.ezeCopy((AnyValue) object);
			return new AnyBoxedObject<R>((R) newValue);
		}
		return new AnyBoxedObject<R>(object);
	}

	public static <R extends Object> AnyBoxedObject<R> ezeWrap(R object) {
		return new AnyBoxedObject<R>(object);
	}

	public static <T extends Object> T ezeCast(Object value, Class<T> clazz) throws AnyException {
		try {
			Object unboxed = value instanceof eglx.lang.EAny ? ((eglx.lang.EAny) value).ezeUnbox() : value;
			return clazz.cast(unboxed);
		}
		catch (ClassCastException ex) {
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = clazz.getName();
			Object unboxed = value instanceof eglx.lang.EAny ? ((eglx.lang.EAny) value).ezeUnbox() : value;
			tcx.actualTypeName = unboxed.getClass().getName();
			tcx.initCause(ex);
			throw tcx.fillInMessage(Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName);
		}
	}

	/**
	 * Dynamic implementation of data type conversion. Used when casting values of unknown type to the receiver type. This
	 * method looks up the appropriate conversion operation first in the receiver class and if not found then lookup will
	 * continue into the class of the <code>value</code> parameter.
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
			// first try finding the method if it is boxed
			if (value instanceof eglx.lang.EAny) {
				Method method = null;
				Class[] parmTypes = new Class[parameterTypes == null ? 1 : parameterTypes.length + 1];
				parmTypes[0] = value.getClass();
				if (parameterTypes != null)
					java.lang.System.arraycopy(parameterTypes, 0, parmTypes, 1, parameterTypes.length);
				try {
					method = clazz.getMethod(conversionMethod, parmTypes);
				}
				catch (NoSuchMethodException ex) {
					try {
						method = value.getClass().getMethod(conversionMethod, parmTypes);
					}
					catch (Exception ex1) {
					}
				}
				if (method != null) {
					if (args == null)
						return method.invoke(null, value);
					else
						return method.invoke(null, value, args);
				}
			}
			// Conversion operation needs to be invoked
			Object unboxed = value instanceof eglx.lang.EAny ? ((eglx.lang.EAny) value).ezeUnbox() : value;
			if (unboxed == null) {
				return null;
			}
			Method method;
			Class[] parmTypes = new Class[parameterTypes == null ? 1 : parameterTypes.length + 1];
			parmTypes[0] = unboxed.getClass();
			if (parameterTypes != null)
				java.lang.System.arraycopy(parameterTypes, 0, parmTypes, 1, parameterTypes.length);
			try {
				method = clazz.getMethod(conversionMethod, parmTypes);
			}
			catch (NoSuchMethodException ex) {
				try {
					method = value.getClass().getMethod(conversionMethod, parmTypes);
				}
				catch (NoSuchMethodException ex1) {
					TypeCastException tcx = new TypeCastException();
					tcx.castToName = clazz.getName();
					tcx.actualTypeName = unboxed.getClass().getName();
					throw tcx.fillInMessage(Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName);
				}
			}
			if (args == null)
				return method.invoke(null, unboxed);
			else
				return method.invoke(null, unboxed, args);
		}
		catch (InvocationTargetException ex) {
			throw JavartUtil.makeEglException(ex.getTargetException());
		}
		catch (Exception ex1) {
			// Should not ever get here
			throw JavartUtil.makeEglException(ex1);
		}
	}

	public static <T extends Object> boolean ezeIsa(Object object, Class<T> clazz) {
		Object unboxed = object instanceof eglx.lang.EAny ? ((eglx.lang.EAny) object).ezeUnbox() : object;
		return clazz.isInstance(unboxed);
	}

	public static Object ezeDeepCopy(Object object) {
		if (object instanceof EAny) {
			EAny any = (EAny) object;
			// TODO Using clone is not the correct way to make a deep copy of some kinds of objects.
			try {
				return any.clone();
			}
			catch (CloneNotSupportedException ex) {
				AnyException anyEx = new AnyException();
				anyEx.setMessage(ex.toString());
				throw anyEx;
			}
		}

		return object;
	}

	public EAny() {
		super();
	}

	public java.lang.String ezeTypeSignature() {
		return getClass().getName();
	}

	public String ezeName() {
		return getClass().getSimpleName();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static Object ezeGet(Object obj, String name) throws AnyException {
		if (obj == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		if (obj instanceof eglx.lang.EAny) 
			return ((eglx.lang.EAny)obj).ezeGet(name);
		DynamicAccessException dax = new DynamicAccessException();
		dax.key = name;
		throw dax.fillInMessage(Message.DYNAMIC_ACCESS_FAILED, name, obj);
	}
	
	public static Object ezeGet(Object obj, int index) throws AnyException {
		if (obj == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		if (obj instanceof eglx.lang.EAny) 
			return ((eglx.lang.EAny)obj).ezeGet(index);
		DynamicAccessException dax = new DynamicAccessException();
		dax.key = "" + index;
		throw dax.fillInMessage(Message.DYNAMIC_ACCESS_FAILED, "" + index, obj);
	}

	public static void ezeSet(Object obj, String name, Object value) throws AnyException {
		if (obj == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		if (obj instanceof eglx.lang.EAny) { 
			((eglx.lang.EAny)obj).ezeSet(name, value);
			return;
		}
		DynamicAccessException dax = new DynamicAccessException();
		dax.key = name;
		throw dax.fillInMessage(Message.DYNAMIC_ACCESS_FAILED, name, obj);
	}
	
	public Object ezeGet(String name) throws AnyException {
		try {
			Field field = this.getClass().getField(name);
			if (ezeTypeConstraints(name) != null) {
				TypeConstraints constraints = ezeTypeConstraints(name);
				Object value = field.get(this);
				return constraints.box(value);
			}
			Object value = field.get(this);
			return value;
		}
		catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			dax.initCause(e);
			throw dax.fillInMessage(Message.DYNAMIC_ACCESS_FAILED, name, this);
		}
	}
	
	public Object ezeGet(int index) throws AnyException {
		Object unboxed = ezeUnbox();
		if (unboxed instanceof List)
			return EAny.asAny(((List<?>) unboxed).get(index));
		else {
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = "list";
			tcx.actualTypeName = unboxed.getClass().getName();
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, unboxed, tcx.actualTypeName,
					tcx.castToName );
		}
	}

	@SuppressWarnings("unchecked")
	public void ezeSet(String name, Object value) throws AnyException {
		try {
			Field field = this.getClass().getField(name);
			if (ezeTypeConstraints(name) != null) {
				TypeConstraints constraints = ezeTypeConstraints(name);
				value = constraints.constrainValue(value);
			} else if (value instanceof AnyBoxedObject) {
				value = field.getType().getName().equalsIgnoreCase("eglx.lang.EAny") ? value : ((AnyBoxedObject<Object>) value).ezeUnbox();
			}
			field.set(this, value);
		}
		catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			dax.initCause(e);
			throw dax.fillInMessage(Message.DYNAMIC_ACCESS_FAILED, name, this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public eglx.lang.EAny ezeUnbox() {
		return this;
	}

	public static Object asAny(Object value) {
		return value;
	}

	public static boolean equals(Object object1, Object object2) {
		Object unboxedOp1 = object1 instanceof eglx.lang.EAny ? ((eglx.lang.EAny) object1).ezeUnbox() : object1;
		Object unboxedOp2 = object2 instanceof eglx.lang.EAny ? ((eglx.lang.EAny) object2).ezeUnbox() : object2;
		if (unboxedOp1 == unboxedOp2)
			return true;
		if (unboxedOp1 == null || unboxedOp2 == null)
			return false;
		if (object1 instanceof eglx.lang.ENumber && object2 instanceof eglx.lang.ENumber)
			return ENumber.equals(object1, object2);
		if (unboxedOp1 instanceof Number && unboxedOp2 instanceof Number)
			return ENumber.equals((Number) unboxedOp1, (Number) unboxedOp2);
		return unboxedOp1.equals(unboxedOp2);
	}

	public static boolean notEquals(Object object1, Object object2) {
		return !equals(object1, object2);
	}

	/**
	 * Subclasses of this method that contain fields with types that can be parameterized with constraints such as length,
	 * precision decimals, pattern, etc. should override this method to return the particular constraints set each field.
	 * These constraints are used to convert values to conform to the given constraints. An example would be a string of
	 * length 10 being dynamically assigned to a String field that was defined as char(6). The actual java field type will be
	 * java.lang.String. However the TypeConstraints for that field will have a reference to Class egl.lang.Char.class as
	 * well as the length of 6. This information will be used to then truncated the string of length 10 to one of length 6.
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
}
