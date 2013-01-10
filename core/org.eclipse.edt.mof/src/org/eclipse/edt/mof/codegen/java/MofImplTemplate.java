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
package org.eclipse.edt.mof.codegen.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.ETypedElement;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TemplateContext;



public class MofImplTemplate extends AbstractTemplate implements MofImplConstants {
		
	public EClassifier[] getReferencedTypes(EClass eClass) {
		Set<EClassifier> set = getReferencedTypesSet(eClass);
		return set.toArray(new EClassifier[set.size()]);
	}

	public Set<EClassifier> getReferencedTypesSet(EClass eClass) {
		Set<EClassifier> set = new HashSet<EClassifier>();
		set.addAll(eClass.getSuperTypes());
		for (EField field : eClass.getEFields() ) {
			collectReferencedTypes(field, set);
		}
		for (EFunction func : eClass.getEFunctions() ) {
			collectReferencedTypes(func, set);
			for (EParameter parm : func.getEParameters()) {
				collectReferencedTypes(parm, set);
			}
		}
		return set;
	}
	
	private void collectReferencedTypes(ETypedElement element, Set<EClassifier> set) {
		if (element.getEType() != null)
			set.add(element.getEType().getEClassifier());
		if (element.getEType() instanceof EGenericType) {
			for (EType arg : ((EGenericType)element.getEType()).getETypeArguments()) {
				set.add(arg.getEClassifier());
			}
		}
	}
	
	public  List<EClassifier> getImportTypes(EClass eClass, boolean forImpl) {
		List<EClassifier> list = new ArrayList<EClassifier>();
		if (forImpl) list.add(eClass);
		for (EClassifier type : getReferencedTypesSet(eClass)) {
			if (needsImport(type)) {
				if (type.getPackageName().equals(eClass.getPackageName())) {
					if (forImpl && (type != eClass)) {
						list.add(type);	
					} else if (type instanceof EDataType) {
						list.add(type);
					}
				}
				else {
					list.add(type);
				}
			}
		}
		return list;
	}

	public boolean needsImport(EClassifier type) {
		if (type instanceof EDataType) return needsImport((EDataType)type);
		else return needsImport((EClass)type);
	}
	
	public boolean needsImport(EClassifier importType, EClass referencingClass) {
		EClass superType = getSuperType(referencingClass);
		return needsImport(importType) 
			&& superType == null 
				? true
				:!superType.getPackageName().equalsIgnoreCase(importType.getPackageName());
	}

	public boolean needsImport(EDataType type) {
		return !type.getJavaClassName().startsWith("java.lang");
	}

	public boolean needsImport(EClass eClass) {
		return eClass != null;
	}

	public EClass getSuperType(EClass eClass) {
		if (eClass.getSuperTypes().isEmpty())
			return null;
		else
			return eClass.getSuperTypes().get(0);
	}

	public String getterName(EField field) {
		if (field.getEType() == factory.getEBooleanEDataType()) {
			return field.getName();
		} else {
			StringBuffer buffer = new StringBuffer();
			buffer.append("get");
			buffer.append(field.getName().substring(0,1).toUpperCase());
			buffer.append(field.getName().substring(1));
			return buffer.toString();
		}
	}

	public String setterName(EField field) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("set");
		buffer.append(field.getName().substring(0,1).toUpperCase());
		buffer.append(field.getName().substring(1));
		return buffer.toString();
	}

	public String getETypeSignature(EMember mbr) {
		return getETypeSignature(mbr.getEType());
	}


	public String getETypeSignature(EType type) {
		if (type == null) return "void";
		EClassifier classifier = type.getEClassifier();
		String typeSignature = getETypeSignature(classifier);
		if (type instanceof EGenericType) {
			typeSignature += "<";
			for (EType arg : ((EGenericType)type).getETypeArguments()) {
				typeSignature += getETypeSignature(arg);
			}
			typeSignature += ">";
		}
		return typeSignature;
	}

	public String getETypeSignature(EClassifier type, TemplateContext ctx) {
		if (type == factory.getEStringEDataType() )
			return EDataType.EDataType_String;
		if (type == factory.getEBooleanEDataType() )
			return EDataType.EDataType_Boolean;
		if ( type == factory.getEIntEDataType() )
			return EDataType.EDataType_Int32;
		if ( type == factory.getEListEDataType() )
			return EDataType.EDataType_List;
		return getPackageName(type, ctx)+"."+getETypeName(type);
	}

	public String getETypeName(EMember mbr) {
		return getETypeName(mbr.getEType());
	}

	public String getETypeName(EType type) {
		if (type == null) return "void";
		EClassifier classifier = type.getEClassifier();
		String typeSignature = getETypeName(classifier);
		if (type instanceof EGenericType) {
			typeSignature += "<";
			for (EType arg : ((EGenericType)type).getETypeArguments()) {
				typeSignature += getETypeName(arg);
			}
			typeSignature += ">";
		}
		return typeSignature;
	}

	public String getETypeName(EClassifier type) {
		if (type == factory.getJavaObjectEDataType() )
			return "Object";
		if (type == factory.getEStringEDataType() )
			return "String";
		if (type == factory.getEBooleanEDataType() )
			return "Boolean";
		if ( type == factory.getEIntEDataType() )
			return "Integer";
		if ( type == factory.getEListEDataType() )
			return "List";
		return type.getName();
	}
	public String getPackageName(EClassifier classifier, TemplateContext ctx) {
		String packageName = (String)ctx.get(classifier.getPackageName());
		if (packageName == null) {
			return classifier.getPackageName();
		}
		else {
			return packageName;
		}
	}
}
