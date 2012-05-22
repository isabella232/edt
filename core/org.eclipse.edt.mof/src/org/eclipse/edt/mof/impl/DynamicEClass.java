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
package org.eclipse.edt.mof.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;



public class DynamicEClass extends EClassImpl implements Dynamic {
	
	public DynamicEClass() {
		MofFactory.INSTANCE.getEClassClass().initialize(this);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * This method works on the convention that the implementation package
	 * is named by extending the interface package with ".impl" and that the
	 * class names are referenced by extending the Interface class name with
	 * "DynamicImpl".  Failure to follow this convention results in the method always
	 * returning null.  Furthermore explicit DynamicEClass implementations can inherit from each other
	 * but only to one level, i.e. the inheritance depth can only be 1.
	 */
	protected Class<EObject> getClazz() {
		if (noClazz) return null;
		if (clazz == null) {
			try {
				String typeSignature = getImplTypeSignature();
				clazz = (Class<EObject>)Class.forName(typeSignature);
			} catch (ClassNotFoundException e) {
				if (!getSuperTypes().isEmpty()) {
					EClass eClass = getSuperTypes().get(0);
					String typeSignature = eClass.getPackageName() + ".impl." + eClass.getName()+ "DynamicImpl";
					try {
						clazz = (Class<EObject>)Class.forName(typeSignature);						
					} catch (ClassNotFoundException e1) {
						noClazz = true;
					}
				}
			}
		}
		return clazz;
	}

	@Override
	public String getImplTypeSignature() {
		return getPackageName() + ".impl." + getName() + "DynamicImpl";
	}
	
	@Override
	public DynamicEObject newEObject() {
		return new DynamicEObject();
	}

	@Override
	/**
	 * Dynamic EClasses have a special serialization key syntax that is used by 
	 * MofLookupDelegate to understand that the key is referring to a DynamicEClass
	 * instead of one that is to be found in an ObjectStore
	 */
	public String getMofSerializationKey() {
		String typeName = getSuperTypes().isEmpty() 
			? MofFactory.INSTANCE.getEObjectClass().getETypeSignature() 
			: getSuperTypes().get(0).getETypeSignature();
		return IEnvironment.DynamicScheme + ":" + typeName + ":" + getETypeSignature();
	}
	
	public String xmlEncodeValue(Object value) {
		if (value instanceof Integer) {
			return IntTypePrefix + value.toString();
		}
		if (value instanceof Float) {
			return FloatTypePrefix + value.toString();
		}
		if (value instanceof Boolean) {
			return BooleanTypePrefix + value.toString();
		}
		if (value instanceof MofSerializable) {
			return MofTypePrefix + ((MofSerializable)value).getMofSerializationKey();
		}
		return value.toString();
	}
	
	public Object getEncodedValue(String value) {
		int i = value.indexOf(":");
		if (i != -1) {
			String prefix = value.substring(0,i+1);
			if (prefix.equals(IntTypePrefix)) {
				return Integer.parseInt(value.substring(i+1));
			}
			if (prefix.equals(BooleanTypePrefix)) {
				return Boolean.parseBoolean(value.substring(i+1));
			}
			if (prefix.equals(FloatTypePrefix)) {
				return Float.parseFloat(value.substring(i+1));
			}
			if (prefix.equals(MofTypePrefix)) {
				try {
					return Environment.INSTANCE.find(value.substring(i+1));
				} catch (Exception e) {
					return null;
				}
			}
		}
		return value;
	}
	
	public EType getEncodedType(String encodedValue) {
		int i = encodedValue.indexOf(":");
		if (i != -1) {
			String prefix = encodedValue.substring(0,i+1);
			if (prefix.equals(IntTypePrefix)) {
				return MofFactory.INSTANCE.getEIntEDataType();
			}
			if (prefix.equals(BooleanTypePrefix)) {
				return MofFactory.INSTANCE.getEBooleanEDataType();
			}
			if (prefix.equals(FloatTypePrefix)) {
				return MofFactory.INSTANCE.getEFloatEDataType();
			}
			if (prefix.equals(DecimalTypePrefix)) {
				return MofFactory.INSTANCE.getEDecimalEDataType();
			}
			if (prefix.equals(MofTypePrefix)) {
				try {
					return (EClass)Environment.INSTANCE.find(encodedValue.substring(i+1));
				} catch (Exception e) {
					return null;
				}
			}
		}
		return MofFactory.INSTANCE.getEStringEDataType();
	}
	
}
