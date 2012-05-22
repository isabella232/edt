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
package org.eclipse.edt.mof;


public interface MofFactory extends EFactory {
	MofFactory INSTANCE = org.eclipse.edt.mof.impl.MofFactoryImpl.init();
	String PackageName = "org.eclipse.edt.mof";
	
	EClass createEClass(boolean initialize);
	EDataType createEDataType(boolean initialize);
	EObject createEObject(boolean initialize);
	EMetadataObject createEMetadataObject(boolean initialize);
	EField createEField(boolean initialize);
	EFunction createEFunction(boolean initialize);
	EParameter createEParameter(boolean initialize);
	EGenericType createEGenericType(boolean initialize);
	ETypeParameter createETypeParameter(boolean initialize);
	EMetadataType createEMetadataType(boolean initialize);
	
	EClass getEClassClass();
	EClass getEDataTypeClass();
	EClass getEEnumClass();
	EClass getEEnumLiteralClass();
	EClass getEMetadataObjectClass();
	EClass getEObjectClass();
	EClass getEModelElementClass();
	EClass getENamedElementClass();
	EClass getETypeClass();
	EClass getEClassifierClass();
	EClass getEMemberClass();
	EClass getEFieldClass();
	EClass getEFunctionClass();
	EClass getEParameterClass();
	EClass getETypeParameterClass();
	EClass getEGenericTypeClass();
	EClass getEMetadataTypeClass();
	EClass getMofReferenceTypeClass();
	EDataType getEListEDataType();
	EDataType getEStringEDataType();
	EDataType getEIntEDataType();
	EDataType getEBooleanEDataType();
	EDataType getEFloatEDataType();
	EDataType getEDecimalEDataType();
	EDataType getJavaObjectEDataType();
}
