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


import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class Bootstrap {
	
	public static void initialize(IEnvironment env) {
		Bootstrap bootstrap = new Bootstrap(env);
		bootstrap.doIt();
	}
	
	public static void initialize() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.doIt();
	}
	
	IEnvironment env;
	static boolean loaded;
	static EClass EObjectClass;
	static EClass MetadataObjectClass;
	static EClass ModelElementClass;
	static EClass MofSerializableClass;
	static EClass NamedElementClass;
	static EClass TypeClass;
	static EClass ClassifierClass;
	static EClass TypeParameterClass;
	static EClass GenericTypeClass;
	static EClass EClassClass;
	static EClass EEnumClass;
	static EClass EEnumLiteralClass;
	static EClass EDataTypeClass;
	static EClass MemberClass;
	static EClass MemberContainerClass;
	static EClass FieldClass;
	static EClass FunctionClass;
	static EClass ParameterClass;
	static EClass MetadataTypeClass;
	static EDataType JavaObject;
	static EDataType EString;
	static EDataType EBoolean;
	static EDataType EInt32;
	static EDataType EFloat;
	static EDataType EDecimal;
	static EDataType EList;
	
	Bootstrap() {
		env = Environment.getCurrentEnv();
	}
	
	Bootstrap(IEnvironment env) {
		this.env = env;
	}
	
	
	public void doIt() {
		initializeClasses();
	}
	
	synchronized void initializeClasses() {
		if (!loaded) {
			System.out.println("Bootstrap initialize...");
			getEClassClass();
			getFieldClass();
			getMetadataObjectClass();
			getMetadataTypeClass();
			getEEnumClass();
			getFunctionClass();
			getEList();
			getJavaObject();
			getEDecimal();
			getEFloat();
			getEString();
			loaded = true;
		}
		env.save(EObjectClass, false); 
		env.save(ModelElementClass, false);
		env.save(NamedElementClass, false);
		env.save(TypeClass, false);
		env.save(ClassifierClass, false);
		env.save(MemberClass, false);
		env.save(MemberContainerClass, false);
		env.save(MofSerializableClass, false);
		env.save(FieldClass, false);
		env.save(FunctionClass, false);
		env.save(TypeParameterClass, false);
		env.save(GenericTypeClass, false);
		env.save(MetadataObjectClass, false);
		env.save(EEnumLiteralClass, false);
		env.save(EDataTypeClass, false);
		env.save(EString, false);
		env.save(EBoolean, false);
		env.save(EInt32, false);
		env.save(EFloat, false);
		env.save(EDecimal, false);
		env.save(EList, false);
		env.save(ParameterClass, false);
		env.save(EClassClass, false);
		env.save(EEnumClass, false);
		env.save(MetadataTypeClass, false);
		env.save(JavaObject, false);

	}
	
	EClass getEClassClass() {
		if (EClassClass == null) {
			EClassClass = createEClassClass();
		}
		return EClassClass;
	}
	
	EClass getEObjectClass() {
		if (EObjectClass == null) {
			EObjectClass = newEClass();
			createEObjectClass();
		}
		return EObjectClass;
	}
	EClass getModelElementClass() {
		if (ModelElementClass == null) {
			ModelElementClass = newEClass();
			createModelElementClass();
		}
		return ModelElementClass;
	}
	EClass getMetadataObjectClass() {
		if (MetadataObjectClass == null) {
			MetadataObjectClass = newEClass();
			createMetadataObjectClass();
		}
		return MetadataObjectClass;
	}
	
	EClass getMetadataTypeClass() {
		if (MetadataTypeClass == null) {
			MetadataTypeClass = newEClass();
			createMetadataTypeClass();
		}
		return MetadataTypeClass;
	}
	
	EClass getNamedElementClass() {
		if (NamedElementClass == null) {
			NamedElementClass = newEClass();
			createNamedElementClass();
		}
		return NamedElementClass;
	}
	EClass getTypeClass() {
		if (TypeClass == null) {
			TypeClass = newEClass();
			createTypeClass();
		}
		return TypeClass;
	}
	
	EClass getMofSerializableClass() {
		if (MofSerializableClass == null) {
			MofSerializableClass = newEClass();
			createMofSerializableClass();
		}
		return MofSerializableClass;
	}
	
	EClass getClassifierClass() {
		if (ClassifierClass == null) {
			ClassifierClass = newEClass();
			createClassifierClass();
		}
		return ClassifierClass;
	}
	EClass getTypeParameterClass() {
		if (TypeParameterClass == null) {
			TypeParameterClass = newEClass();
			createTypeParameterClass();
		}
		return TypeParameterClass;
	}

	EClass getGenericTypeClass() {
		if (GenericTypeClass == null) {
			GenericTypeClass = newEClass();
			createGenericTypeClass();
		}
		return GenericTypeClass;
	}
	
	EClass getEDataTypeClass() {
		if (EDataTypeClass == null) {
			EDataTypeClass = newEClass();
			createEDataTypeClass();
		}
		return EDataTypeClass;
	}
	
	EClass getEEnumClass() {
		if (EEnumClass == null) {
			EEnumClass = newEClass();
			createEEnumClass();
		}
		return EEnumClass;
	}

	EClass getEEnumerationEntryClass() {
		if (EEnumLiteralClass == null) {
			EEnumLiteralClass = newEClass();
			createEEnumLiteralClass();
		}
		return EEnumLiteralClass;
	}

	EClass getMemberClass() {
		if (MemberClass == null) {
			MemberClass = newEClass();
			createMemberClass();
		}
		return MemberClass;
	}
	EClass getMemberContainerClass() {
		if (MemberContainerClass == null) {
			MemberContainerClass = newEClass();
			createMemberContainerClass();
		}
		return MemberContainerClass;
	}
	EClass getFieldClass() {
		if (FieldClass == null) {
			FieldClass = newEClass();
			createFieldClass();
		}
		return FieldClass;
	}
	
	EClass getFunctionClass() {
		if (FunctionClass == null) {
			FunctionClass = newEClass();
			createFunctionClass();
		}
		return FunctionClass;
	}
	EClass getParameterClass() {
		if (ParameterClass == null) {
			ParameterClass = newEClass();
			createParameterClass();
		}
		return ParameterClass;
	}
	
	EDataType getJavaObject() {
		if (JavaObject == null) {
			JavaObject = newEDataType();
			createJavaObject();
		}
		return JavaObject;
	}		
	
	EDataType getEString() {
		if (EString == null) {
			EString = newEDataType();
			createEString();
		}
		return EString;
	}

	EDataType getEBoolean() {
		if (EBoolean == null) {
			EBoolean = newEDataType();
			createEBoolean();
		}
		return EBoolean;
	}
	EDataType getEInt32() {
		if (EInt32 == null) {
			EInt32 = newEDataType();
			createEInt32();
		}
		return EInt32;
	}
	EDataType getEFloat() {
		if (EFloat == null) {
			EFloat = newEDataType();
			createEFloat();
		}
		return EFloat;
	}
	EDataType getEDecimal() {
		if (EDecimal == null) {
			EDecimal = newEDataType();
			createEDecimal();
		}
		return EDecimal;
	}
	EDataType getEList() {
		if (EList == null) {
			EList = newEDataType();
			createEList();
		}
		return EList;
	}
	
	
	EClass createEClassClass() {
		EClassImpl classInstance = new EClassImpl();
		classInstance.setSlots(new Slot[EClassImpl.totalSlots()]);
		classInstance.setEClass(classInstance);
		EClassClass = classInstance;
		classInstance.setName("EClass");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getClassifierClass());
		classInstance.getSuperTypes().add(getMemberContainerClass());
		newField(classInstance, "superTypes", getEList(), getEClassClass());
		newField(classInstance, "isAbstract", getEBoolean());
		newField(classInstance, "isInterface", getEBoolean());
		newField(classInstance, "eFields", getEList(), getFieldClass(), true);
		newField(classInstance, "eFunctions", getEList(), getFunctionClass(), true);  
		return classInstance;
	}
	
	EClass createTypeClass() {
		EClass classInstance = TypeClass;
		classInstance.setName("EType");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.setIsAbstract(true);
		classInstance.getSuperTypes().add(getModelElementClass());
		classInstance.getSuperTypes().add(getMofSerializableClass());
		return classInstance;
	} 

	EClass createClassifierClass() {
		EClass classInstance = ClassifierClass;
		classInstance.setName("EClassifier");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.setIsAbstract(true);
		classInstance.getSuperTypes().add(getNamedElementClass());
		classInstance.getSuperTypes().add(getTypeClass());
		newField(classInstance, "packageName", getEString());
		newField(classInstance, "eTypeParameters", getEList(), getTypeParameterClass(), true);
//		env.saveType(classInstance);
		return classInstance;
	} 
	
	EClass createTypeParameterClass() {
		EClass classInstance = TypeParameterClass;
		classInstance.setName("ETypeParameter");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.setIsAbstract(false);
		classInstance.getSuperTypes().add(getNamedElementClass());
		newField(classInstance, "bounds", getEList(), getGenericTypeClass());
//		env.saveType(classInstance);
		return classInstance;
	}
	

	EClass createGenericTypeClass() {
		EClass classInstance = GenericTypeClass;
		classInstance.setName("EGenericType");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getTypeClass());
		newField(classInstance, "eClassifier", getClassifierClass());
		newField(classInstance, "eTypeArguments", getEList(), getTypeClass());
		return classInstance;

	}

	EClass createMofSerializableClass() {
		EClass classInstance = MofSerializableClass;
		classInstance.setName("MofSerializable");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.setIsAbstract(true);
		classInstance.setIsInterface(true);
		classInstance.getSuperTypes().add(getEObjectClass());
		return classInstance;
	}
	
	EClass createEDataTypeClass() {
		EClass classInstance = EDataTypeClass;
		classInstance.setName("EDataType");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getClassifierClass());
		newField(classInstance, "defaultValueString", getEString());
		newField(classInstance, "javaClassName", getEString());
//		env.saveType(classInstance);
		return classInstance;
	}
	
	EClass createEEnumClass() {
		EClass classInstance = EEnumClass;
		classInstance.setName("EEnum");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getEDataTypeClass());
		classInstance.getSuperTypes().add(getMemberContainerClass());
		newField(classInstance, "literals", getEList(), getEEnumerationEntryClass(), true);
		return classInstance;
	}
	
	EClass createEEnumLiteralClass() {
		EClass classInstance = EEnumLiteralClass;
		classInstance.setName("EEnumLiteral");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getMemberClass());
		newField(classInstance, "value", getEInt32());
		return classInstance;
	}


	EClass createNamedElementClass() {
		EClass classInstance = NamedElementClass;
		classInstance.setName("ENamedElement");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getModelElementClass());
		classInstance.setIsAbstract(true);
		newField(classInstance, "name", getEString());
//		env.saveType(classInstance);
		return classInstance;

	}
	
	EClass createModelElementClass() {
		EClass classInstance = ModelElementClass;
		classInstance.setName("EModelElement");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getEObjectClass());
		classInstance.setIsAbstract(true);
		newField(classInstance, "metadata", getEList(), getMetadataObjectClass(), true);
//		env.saveType(classInstance);
		return classInstance;

	}
	
	EClass createMetadataObjectClass() {
		EClass type = MetadataObjectClass;
		type.setPackageName(MofFactory.PackageName);
		type.setName("EMetadataObject");
		type.getSuperTypes().add(getModelElementClass());
//		env.saveType(type);
		return type;
	}
	
	EClass createEObjectClass() {
		EClass classInstance = EObjectClass;
		classInstance.setName("EObject");
		classInstance.setPackageName(MofFactory.PackageName);
		newField(classInstance, "eClass", getEClassClass());
//		env.saveType(classInstance);
		return classInstance;

	}
	
	EClass createMemberClass() {
		EClass classInstance = MemberClass;
		classInstance.setName("EMember");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.setIsAbstract(true);
		classInstance.getSuperTypes().add(getNamedElementClass());
		newField(classInstance, "declarer", getMemberContainerClass());
		newField(classInstance, "eType", getTypeClass());
		newField(classInstance, "nullable", getEBoolean());
//		env.saveType(classInstance);
		return classInstance;

	}
	EClass createMemberContainerClass() {
		EClass classInstance = MemberContainerClass;
		classInstance.setName("EMemberContainer");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.setIsAbstract(true);
		classInstance.setIsInterface(true);
		classInstance.getSuperTypes().add(getEObjectClass());
		return classInstance;

	}
		
	EClass createFunctionClass() {
		EClass classInstance = FunctionClass;
		classInstance.setEClass(getEClassClass());
		classInstance.setName("EFunction");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getMemberClass());
		classInstance.getSuperTypes().add(getMemberContainerClass());
		newField(classInstance, "eParameters", getEList(), getParameterClass(), true);
//		env.saveType(classInstance);
		return classInstance;

	}

	EClass createFieldClass() {
		EClass classInstance = FieldClass;
		classInstance.setEClass(getEClassClass());
		classInstance.setName("EField");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getMemberClass());
		newField(classInstance, "isTransient", getEBoolean());
		newField(classInstance, "containment", getEBoolean());
		newField(classInstance, "initialValue", getJavaObject(), true);
//		env.saveType(classInstance);
		return classInstance;

	}
	EClass createParameterClass() {
		EClass classInstance = ParameterClass;
		classInstance.setEClass(getEClassClass());
		classInstance.setName("EParameter");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getFieldClass());
		newField(classInstance, "eFunction", getFunctionClass());
//		env.saveType(classInstance);
		return classInstance;

	}
	
	EClass createMetadataTypeClass() {
		EClass classInstance = MetadataTypeClass;
		classInstance.setEClass(getEClassClass());
		classInstance.setName("EMetadataType");
		classInstance.setPackageName(MofFactory.PackageName);
		classInstance.getSuperTypes().add(getEClassClass());
		newField(classInstance, "targets", getEList(), getEClassClass()); 
		return classInstance;

	}
	EDataType createJavaObject() {
		EDataType type = JavaObject;
		type.setName("JavaObject");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_JavaObject);
//		env.saveType(type);
		return type;
	}

	EDataType createEString() {
		EDataType type = EString;
		type.setName("EString");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_String);
//		env.saveType(type);
		return type;
	}
	EDataType createEBoolean() {
		EDataType type = EBoolean;
		type.setName("EBoolean");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_Boolean);
//		env.saveType(type);
		return type;
	}
	
	EDataType createEInt32() {
		EDataType type = EInt32;
		type.setName("EInt32");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_Int32);
		return type;
	}

	EDataType createEFloat() {
		EDataType type = EFloat;
		type.setName("EFloat");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_Float);
		return type;
	}
	
	EDataType createEDecimal() {
		EDataType type = EDecimal;
		type.setName("EDecimal");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_Decimal);
		return type;
	}

	EDataType createEList() {
		EDataType type = EList;
		type.setName("EList");
		type.setPackageName(MofFactory.PackageName);
		type.setJavaClassName(EDataType.EDataType_List);
		ETypeParameterImpl parm = new ETypeParameterImpl();
		parm.setSlots(new Slot[ETypeParameterImpl.totalSlots()]);
		parm.setEClass(getTypeParameterClass());
		parm.setName("E");
		type.getETypeParameters().add(parm);
//		env.saveType(type);
		return type;
	}

	EField newField() {
		EFieldImpl field = new EFieldImpl();
		field.setSlots(new Slot[EFieldImpl.totalSlots()]);
		field.setIsTransient(false);
		field.setIsNullable(false);
		field.setEClass(getFieldClass());
		return field;
	}
	EField newField(EClass container, String name, EType type, boolean containment) {
		EField field = newField();
		field.setName(name);
		field.setEType(type);
		field.setDeclarer(container);
		field.setContainment(containment);
		container.getEFields().add(field);
		return field;
	}
	EField newField(EClass container, String name, EClassifier type) {
		return newField(container, name, type, false);
	}
	
	EField newField(EClass container, String name, EClassifier type, EClassifier typeArgument) {
		return newField(container, name, type, typeArgument, false);
	}

	EField newField(EClass container, String name, EClassifier type, EClassifier typeArgument, boolean containment ) {
		EGenericTypeImpl generic = new EGenericTypeImpl();
		generic.setSlots(new Slot[EGenericTypeImpl.totalSlots()]);
		generic.setEClass(getGenericTypeClass());
		generic.setEClassifier(type);
		generic.getETypeArguments().add(typeArgument);
		EField field = newField(container, name, generic, containment);
		return field;
	}
		
	
	EClass newEClass() {
		EClassImpl eClass = new EClassImpl();
		eClass.setSlots(new Slot[EClassImpl.totalSlots()]);
		getEClassClass().initialize(eClass);
		eClass.setEClass(getEClassClass());
		return eClass;
	}
	
	EDataType newEDataType() {
		EDataTypeImpl type = new EDataTypeImpl();
		type.setSlots(new Slot[EDataTypeImpl.totalSlots()]);
		type.setEClass(getEDataTypeClass());
		return type;
	}

}
