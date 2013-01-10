/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.math.BigDecimal;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.ETypeParameter;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class MofFactoryImpl extends EFactoryImpl implements MofFactory {

	EClass eObjectClass;
	EClass modelElementClass;
	EClass namedElementClass;
	EClass mofSerializableClass;
	EClass typeClass;
	EClass classifierClass;
	EClass eClassClass;
	EClass eDataTypeClass;
	EClass enumClass;
	EClass enumLiteralClass;
	EClass memberClass;
	EClass fieldClass;
	EDataType javaObjectEDataType;
	EDataType eListEDataType;
	EDataType eStringEDataType;
	EDataType eInt32EDataType;
	EDataType eFloatEDataType;
	EDataType eDecimalEDataType;
	EDataType eBooleanEDataType;
	EClass functionClass;
	EClass metadataObjectClass;
	EClass parameterClass;
	EClass typeArgumentClass;
	EClass typeParameterClass;
	EClass valueParameterClass;
	EClass metadataTypeClass;
	
	IEnvironment env = Environment.getCurrentEnv();

	
	public static MofFactory init() {
		MofFactoryImpl core = new MofFactoryImpl();
		core.setPackageName(PackageName);
		EFactory.Registry.INSTANCE.put(PackageName, core);
		Bootstrap.initialize();
		core.initializeClasses();
		return core;
	}
	
	public  EClass getEObjectClass() {
		return eObjectClass;
	}

	public  EClass getEModelElementClass() {
		return modelElementClass;
	}
	public  EClass getENamedElementClass() {
		return namedElementClass;
	}
	public  EClass getETypeClass() {
		return typeClass;
	}
	public  EClass getEClassifierClass() {
		return classifierClass;
	}
	
	public  EClass getMofReferenceTypeClass() {
		return mofSerializableClass;
	}
	
	public  EClass getEClassClass() {
		return eClassClass;
	}
	public  EClass getEDataTypeClass() {
		return eDataTypeClass;
	}
	public  EClass getEEnumClass() {
		return enumClass;
	}
	public  EClass getEEnumLiteralClass() {
		return enumLiteralClass;
	}
	public  EClass getEMemberClass() {
		return memberClass;
	}
	public  EClass getEFieldClass() {
		return fieldClass;
	}

	public EDataType getJavaObjectEDataType() {
		return javaObjectEDataType;
	}
	
	public  EDataType getEListEDataType() {
		return eListEDataType;
	}

	public  EDataType getEStringEDataType() {
		return eStringEDataType;
	}

	public  EDataType getEIntEDataType() {
		return eInt32EDataType;
	}

	public  EDataType getEFloatEDataType() {
		return eFloatEDataType;
	}

	public  EDataType getEDecimalEDataType() {
		return eDecimalEDataType;
	}

	public  EDataType getEBooleanEDataType() {
		return eBooleanEDataType;
	}

	@Override
	public EClass getEFunctionClass() {
		return functionClass;
	}

	@Override
	public EClass getEMetadataObjectClass() {
		return metadataObjectClass;
	}

	@Override
	public EClass getEParameterClass() {
		return parameterClass;
	}
	@Override
	public EClass getEGenericTypeClass() {
		return typeArgumentClass;
	}

	@Override
	public EClass getETypeParameterClass() {
		return typeParameterClass;
	}
	
	@Override
	public EClass getEMetadataTypeClass() {
		return metadataTypeClass;
	}
	
	@Override
	public EObject create(EClass type) {
		return create(type, true);
	}

	public EObject create(EClass type, boolean initialize) {
		if (!type.getCaseSensitivePackageName().equalsIgnoreCase(PackageName) || type.isAbstract()) {
			throw new IllegalArgumentException("Type is not valid: " + type.getETypeSignature());
		}
		if (type == getEObjectClass())
			return createEObject(initialize);
		else if (type == getEClassClass())
			return createEClass(initialize);
		else if (type == getEFieldClass())
			return createEField(initialize);
		else if (type == getEDataTypeClass())
			return createEDataType(initialize);
		else if (type == getEMetadataObjectClass()) 
			return createEMetadataObject(initialize);
		else if (type == getEGenericTypeClass()) 
			return createEGenericType(initialize);
		else if (type == getETypeParameterClass()) 
			return createETypeParameter(initialize);
		else if (type == getEFunctionClass()) 
			return createEFunction(initialize);
		else if (type == getEEnumClass()) 
			return createEEnum(initialize);
		else if (type == getEEnumLiteralClass()) 
			return createEEnumLiteral(initialize);
		else if (type == getEParameterClass()) 
			return createEParameter(initialize);
		else if (type == getEMetadataTypeClass()) 
			return createEMetadataType(initialize);
		else {
			return super.create(type, initialize);
		}
	}
	
	public Object createFromString(EDataType type, String value) {
		if (!type.getCaseSensitivePackageName().equalsIgnoreCase(PackageName)) {
			throw new IllegalArgumentException("Type is not valid: " + type.getETypeSignature());
		}
		if (type == getEIntEDataType())
			return Integer.parseInt(value);
		else if (type == getEFloatEDataType())
			return Float.parseFloat(value);
		else if (type == getEDecimalEDataType()) {
			int scale = 0;
			long lint = 0;
			int pos = value.indexOf('.');
			if (pos == -1) {
				lint = Long.parseLong(value);
			}
			else {
				scale = value.length()-pos-1;
				StringBuffer buffer = new StringBuffer();
				for (int i=0; i<value.length(); i++) {
					if (value.charAt(i) != '.') 
						buffer.append(value.charAt(i));
				}
				lint = Long.parseLong(buffer.toString());
			}
			return BigDecimal.valueOf(lint, scale);
		}
		else if (type == getEStringEDataType())
			return value;
		else if (type == getEBooleanEDataType())
			return Boolean.parseBoolean(value);
		else 
			return value;
	}

	public String convertToString(EDataType type, Object value) {
		if (!type.getCaseSensitivePackageName().equalsIgnoreCase(PackageName)) {
			throw new IllegalArgumentException("Type is not valid: " + type.getETypeSignature());
		}
		// TODO: Handle real conversions
		return value.toString();
	}


	public EClass createEClass(boolean initialize) {
		EClassImpl eClass = new EClassImpl();
		if (initialize) {
			((EClassImpl)getEClassClass()).initialize(eClass);
			eClass.setEClass(getEClassClass());
		}
		return eClass;
	}

	public EObject createEObject(boolean initialize) {
		EObjectImpl eObject = new EObjectImpl();
		if (initialize) {
			((EClassImpl)getEObjectClass()).initialize(eObject);
			eObject.setEClass(getEObjectClass());
		}
		return eObject;
	}

	public EMetadataObject createEMetadataObject(boolean initialize) {
		EMetadataObjectImpl eObject = new EMetadataObjectImpl();
		if (initialize) {
			((EClassImpl)getEMetadataObjectClass()).initialize(eObject);
			eObject.setEClass(getEMetadataObjectClass());
		}
		return eObject;
	}
	
	public EField createEField(boolean initialize) {
		EFieldImpl field = new EFieldImpl();
		if (initialize) {
			((EClassImpl)getEFieldClass()).initialize(field);
			field.setEClass(getEFieldClass());
		}
		return field;
	}


	public EEnum createEEnum(boolean initialize) {
		EEnumImpl dataType = new EEnumImpl();
		if (initialize) {
			((EClassImpl)getEEnumClass()).initialize(dataType);
			dataType.setEClass(getEEnumClass());
		}
		return dataType;
	}
	
	public EEnum createEEnumLiteral(boolean initialize) {
		EEnumImpl dataType = new EEnumImpl();
		if (initialize) {
			((EClassImpl)getEEnumClass()).initialize(dataType);
			dataType.setEClass(getEEnumClass());
		}
		return dataType;
	}

	public EDataType createEDataType(boolean initialize) {
		EDataTypeImpl dataType = new EDataTypeImpl();
		if (initialize) {
			((EClassImpl)getEDataTypeClass()).initialize(dataType);
			dataType.setEClass(getEDataTypeClass());
		}
		return dataType;
	}

	public EGenericType createEGenericType(boolean initialize) {
		EGenericTypeImpl dataType = new EGenericTypeImpl();
		if (initialize) {
			((EClassImpl)getEGenericTypeClass()).initialize(dataType);
			dataType.setEClass(getEGenericTypeClass());
		}
		return dataType;
	}
	public ETypeParameter createETypeParameter(boolean initialize) {
		ETypeParameterImpl dataType = new ETypeParameterImpl();
		if (initialize) {
			((EClassImpl)getETypeParameterClass()).initialize(dataType);
			dataType.setEClass(getETypeParameterClass());
		}
		return dataType;
	}
	

	@Override
	public EMetadataType createEMetadataType(boolean initialize) {
		EMetadataType ann = new EMetadataTypeImpl();
		if (initialize) {
			((EClassImpl)getEMetadataTypeClass()).initialize(ann);
			ann.setEClass(getEMetadataTypeClass());
			ann.getSuperTypes().add(getEMetadataTypeClass());
		}
		return ann;
	}
	
	

	public EFunction createEFunction(boolean initialize) {
		EFunctionImpl obj = new EFunctionImpl();
		if (initialize) {
			((EClassImpl)getEFunctionClass()).initialize(obj);
			obj.setEClass(getEFunctionClass());
		}
		return obj;
	}

	public EParameter createEParameter(boolean initialize) {
		EParameterImpl obj = new EParameterImpl();
		if (initialize) {
			((EClassImpl)getEParameterClass()).initialize(obj);
			obj.setEClass(getEParameterClass());
		}
		return obj;
	}
	
	private void initializeClasses() {
		eObjectClass = (EClass)getTypeNamed(PackageName+".EObject");
		modelElementClass = (EClass)getTypeNamed(PackageName+".EModelElement");
		namedElementClass = (EClass)getTypeNamed(PackageName+".ENamedElement");
		typeClass = (EClass)getTypeNamed(PackageName+".EType");
		classifierClass = (EClass)getTypeNamed(PackageName+".EClassifier");
		mofSerializableClass = (EClass)getTypeNamed(PackageName+".MofSerializable");
		eClassClass = (EClass)getTypeNamed(PackageName+".EClass");
		eDataTypeClass = (EClass)getTypeNamed(PackageName+".EDataType");
		memberClass = (EClass)getTypeNamed(PackageName+".EMember");
		fieldClass = (EClass)getTypeNamed(PackageName+".EField");
		functionClass = (EClass)getTypeNamed(PackageName+".EFunction");
		metadataObjectClass = (EClass)getTypeNamed(PackageName+".EMetadataObject");
		parameterClass = (EClass)getTypeNamed(PackageName+".EParameter");
		metadataTypeClass = (EClass)getTypeNamed(PackageName+".EMetadataType");
		javaObjectEDataType = (EDataType)getTypeNamed(PackageName+".JavaObject");
		eListEDataType = (EDataType)getTypeNamed(PackageName+".EList");
		eStringEDataType = (EDataType)getTypeNamed(PackageName+".EString");
		eInt32EDataType = (EDataType)getTypeNamed(PackageName+".EInt32");
		eFloatEDataType = (EDataType)getTypeNamed(PackageName+".EFloat");
		eDecimalEDataType = (EDataType)getTypeNamed(PackageName+".EDecimal");
		eBooleanEDataType = (EDataType)getTypeNamed(PackageName+".EBoolean");
		typeArgumentClass = (EClass)getTypeNamed(PackageName+".EGenericType");
		typeParameterClass = (EClass)getTypeNamed(PackageName+".ETypeParameter");
		enumClass = (EClass)getTypeNamed(PackageName+".EEnum");
		enumLiteralClass = (EClass)getTypeNamed(PackageName+".EEnumLiteral");
	}

}
