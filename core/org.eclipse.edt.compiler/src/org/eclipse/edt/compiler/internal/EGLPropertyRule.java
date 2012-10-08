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
package org.eclipse.edt.compiler.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TypeUtils;



/**
 * @author dollar
 *
 * This class represents an EGL property.
 * It is not usually instantiated.  Instead on of it's subclasses is.
 */
public class EGLPropertyRule implements Comparable{

	protected String name;
	protected String description="";
	protected int [] types;
	protected int [] elementTypes;
	protected EGLPropertyRule[] elementAnnotationTypes;
	private String [] specificValues;
	
	private String packageName;
	
	//Distinguish those properties that are only allowed on an array, not an array element
	private boolean arrayOnly; 
	
	private boolean isComplex = false;
	private boolean isValueless = false;
	
	private boolean isAnnotationField = false;
	
	private EGLEnumeration enumeration;
	
	private int specialRules = 0;
	
	private AnnotationType elementAnnotationType;
	
	public static int DONT_VALIDATE_IN_OPENUI_STATEMENT = 1 << 1;
	public static int IS_DLI_NAME = 1 << 2;

	public EGLPropertyRule(String name, String description, int [] types, String [] specificValues, boolean arrayOnly) {
		super();
		this.name = name;
		this.description = description;
		this.types = types;
		this.specificValues = specificValues;
		this.arrayOnly = arrayOnly;
	}
	
	public EGLPropertyRule(String name, int [] types, String [] specificValues, boolean arrayOnly) {
	    this(name, "", types, specificValues, arrayOnly);
	}

	public EGLPropertyRule(String name, String description, int [] types, String [] specificValues) {
	    this(name, description, types, specificValues, false);
	}
	
	public EGLPropertyRule(String name, int [] types, String [] specificValues) {
	    this(name, types, specificValues, false);
	}

	public EGLPropertyRule(String name, String description, int [] types) {
		this(name, types);
		this.description = description;		
	}
	
	public EGLPropertyRule(String name, int [] types) {
	    this(name, types, null, false);
	}
	
	public EGLPropertyRule(String name, int [] types, int [] elementTypes, EGLPropertyRule[] elementAnnotationTypes) {
		this(name, types, null, true);
		this.elementTypes = elementTypes;
		this.elementAnnotationTypes = elementAnnotationTypes;
	}
	
	public EGLPropertyRule(String name, EGLPropertyRule[] elementAnnotationTypes ) {
		this.name = name;
		this.elementAnnotationTypes = elementAnnotationTypes;
		this.isComplex = true;
	}

	public EGLPropertyRule(AnnotationType annType) {
		this.name = annType.getCaseSensitiveName();
		this.packageName = annType.getCaseSensitivePackageName();
		if (annType.getEClass() == null || annType.getEFields().isEmpty()) {
			this.isComplex = true;
			return;
		}
		
		if ( annType.getEFields().size() == 1) {
			setFieldsFromEType(annType.getEFields().get(0).getEType());
			return;
		}
		
		this.types = new int[] {EGLNewPropertiesHandler.nestedValue};
		this.isComplex = true;
    	List elementRules = new ArrayList();
			for(EField field : annType.getEFields()) {
			    elementRules.add(new EGLPropertyRule(annType, field.getName()));
			}
			this.elementAnnotationTypes = (EGLPropertyRule[]) elementRules.toArray(new EGLPropertyRule[0]);			
	
		
	}
	
	private EField getEField(AnnotationType annType, String name) {
		for (EField f : annType.getEFields()) {
			if (f.getName().equalsIgnoreCase(name))
				return f;
		}
		return null;
	}


	public EGLPropertyRule(AnnotationType annType, String fieldName) {
		this.name = fieldName;
		this.packageName = annType.getCaseSensitivePackageName();
		EField field = getEField(annType, fieldName);
		if (field != null) {
			setFieldsFromEType(field.getEType());
		}
	}

	public EGLPropertyRule(Field field) {
		this.name = field.getCaseSensitiveName();
		
		if (field.getContainer() instanceof EClassifier) {
			this.packageName = ((EClassifier)field.getContainer()).getPackageName();
		}
		setFieldsFromType(field.getType());
	}

	private void setFieldsFromType(Type type) {
		
		if (type == null || type.getClassifier() == null) {
			return;
		}
		
		if (type instanceof Enumeration) {
			List fieldNames = new ArrayList();
			for(EEnumLiteral entry : ((Enumeration)type).getEntries()) {
				fieldNames.add(entry.getCaseSensitiveName());
			}
			this.types = new int[] {EGLNewPropertiesHandler.specificValue};
			this.specificValues = (String[]) fieldNames.toArray(new String[0]);
			return;
		}
		
		if (type instanceof ArrayType) {
			this.types = new int[] {EGLNewPropertiesHandler.listValue};
			ArrayType arrType = (ArrayType) type;
			if (arrType.getElementType() instanceof Enumeration) {
				List fieldNames = new ArrayList();
				for(EEnumLiteral entry : ((Enumeration)arrType.getElementType()).getEntries()) {
					fieldNames.add(entry.getCaseSensitiveName());
				}
				this.types = new int[] {EGLNewPropertiesHandler.specificValue};
				this.specificValues = (String[]) fieldNames.toArray(new String[0]);
				return;
			}
			
			if (arrType.getElementType() instanceof ArrayType) {
				this.types = new int[] {EGLNewPropertiesHandler.arrayOfArrays};
				return;
			}
			
			if (arrType.getElementType() instanceof ExternalType) {
				this.types = new int[] {EGLNewPropertiesHandler.listValue};
				return;
			}
			
			if (arrType.getElementType() instanceof AnnotationType) {
				this.types = new int[] {EGLNewPropertiesHandler.arrayOf};
				this.elementAnnotationType = (AnnotationType) arrType.getElementType();
			}
			return;
		}
					
		Classifier classifier = type.getClassifier();
		
		if (classifier.equals(TypeUtils.Type_STRING)) {
			this.types = new int[] {EGLNewPropertiesHandler.quotedValue};
			return;
		}
		
		if (classifier.equals(TypeUtils.Type_INT)) {
			this.types = new int[] {EGLNewPropertiesHandler.integerValue};
			return;
		}
		
		if (classifier.equals(TypeUtils.Type_BOOLEAN)) {
			this.types = new int[] {EGLNewPropertiesHandler.specificValue};
			this.specificValues = new String[] {"yes", "no"};
			return;
		}
		
		if (classifier.equals(TypeUtils.Type_ANY)) {
			this.types = new int[] {EGLNewPropertiesHandler.literalValue};
			return;
		}
	}


	private void setFieldsFromEType(EType type) {
		
		if (type == null) {
			return;
		}
		
		if (type instanceof EEnum) {
			List fieldNames = new ArrayList();
			for(EEnumLiteral entry : ((EEnum)type).getLiterals()) {
				fieldNames.add(entry.getCaseSensitiveName());
			}
			this.types = new int[] {EGLNewPropertiesHandler.specificValue};
			this.specificValues = (String[]) fieldNames.toArray(new String[0]);
			return;
		}
		
    	if (type instanceof EGenericType) {
			this.types = new int[] {EGLNewPropertiesHandler.listValue};
    		EGenericType genType = (EGenericType) type;
			
    		EType elemType = ((EGenericType)type).getETypeArguments().get(0);

			if (elemType instanceof EEnum) {
				List fieldNames = new ArrayList();
				for(EEnumLiteral entry : ((EEnum)elemType).getLiterals()) {
					fieldNames.add(entry.getCaseSensitiveName());
				}
				this.types = new int[] {EGLNewPropertiesHandler.specificValue};
				this.specificValues = (String[]) fieldNames.toArray(new String[0]);
				return;
			}
			
			if (elemType instanceof EGenericType) {
				this.types = new int[] {EGLNewPropertiesHandler.arrayOfArrays};
				return;
			}
			
			if (elemType instanceof ExternalType) {
				this.types = new int[] {EGLNewPropertiesHandler.listValue};
				return;
			}
			
			if (elemType instanceof AnnotationType) {
				this.types = new int[] {EGLNewPropertiesHandler.arrayOf};
				this.elementAnnotationType = (AnnotationType) elemType;
			}
			return;
		}
					
		if (type instanceof EDataType) {
			String className = ((EDataType)type).getJavaClassName();
			if (className.equals(EDataType.EDataType_JavaObject)) {
				this.types = new int[] {EGLNewPropertiesHandler.literalValue};
				return;
			}

			if (className.equals(EDataType.EDataType_String)) {
				this.types = new int[] {EGLNewPropertiesHandler.quotedValue};
				return;
			}

			if (className.equals(EDataType.EDataType_Boolean)) {
				this.types = new int[] {EGLNewPropertiesHandler.specificValue};
				this.specificValues = new String[] {"yes", "no"};
				return;
			}

			if (className.equals(EDataType.EDataType_Int32)) {
				this.types = new int[] {EGLNewPropertiesHandler.integerValue};
				return;
			}
		}

		//handle reflect types:
		this.types = new int[] {EGLNewPropertiesHandler.nameValue};

	}
	
	private String firstLower(String str) {
		StringBuffer result = new StringBuffer();
		result.append(str.substring(0, 1).toLowerCase());
		result.append(str.substring(1));
		return result.toString();
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

	public String[] getSpecificValues() {
		return specificValues;
	}

	public int [] getTypes() {
		return types;
	}
	
	public boolean hasType(int type) {
		if (getTypes() != null)
			for (int i = 0; i < getTypes().length; i++)
				if (getTypes()[i] == type)
					return true;
		return false; 
	}
	
	public int [] getElementTypes() {
		return elementTypes;
	}
	
	public boolean hasElementType(int type) {
		if (getElementTypes() != null)
			for (int i = 0; i < getElementTypes().length; i++)
				if (getElementTypes()[i] == type)
					return true;
		return false; 
	}
	
	public EGLPropertyRule [] getElementAnnotationTypes() {
		return elementAnnotationTypes;
	}
	
	public AnnotationType getElementAnnotationType() {
		return elementAnnotationType;
	}
	
	public boolean isSQLItemPropertyRule() {
		return false; 
	}

	public boolean isPageItemPropertyRule() {
		return false; 
	}

	public boolean isFormattingPropertyRule() {
		return false; 
	}
	
	public boolean isValidationPropertyRule() {
		return false; 
	}	
	
	public boolean isFieldPresentationPropertyRule() {
		return false; 
	}

	public boolean isArrayOnly() {
		return arrayOnly;
	}
	
	public boolean isComplex() {
		return isComplex;
	}
	
	public boolean isValueless() {
		return isValueless;
	}
	
	public boolean isCombo() {
		//Jon - need to add this to property handler
		return
			//name.equals(IEGLConstants.PROPERTY_NUMELEMENTSITEM) ||
			name.equals(IEGLConstants.PROPERTY_CURRENCY) ||
			name.equals(IEGLConstants.PROPERTY_FILLCHARACTER) ||
			name.equals(IEGLConstants.PROPERTY_KEYITEM) ||
			name.equals(IEGLConstants.PROPERTY_LENGTHITEM);
	}

	public boolean isRecordPropertyRule() {
		return false;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return this.getName().equals(((EGLPropertyRule) arg0).getName());
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		return this.getName().toLowerCase().compareTo(((EGLPropertyRule) arg0).getName().toLowerCase());
	}
	
	public void setEnumeration( EGLEnumeration enumer ) {
		enumeration = enumer;		
	}
	
	public EGLEnumeration getEnumeration() {
		return enumeration;
	}
	
	public boolean isEnumerationProperty() {
		return enumeration != null;
	}
	
	public void setSpecialRules( int newRules ) {
		specialRules = newRules;
	}
	
	public boolean shouldValidateInOpenUIStatement() {
		return (specialRules & DONT_VALIDATE_IN_OPENUI_STATEMENT) == 0;
	}
	
	public boolean isDLIName() {
		return (specialRules & IS_DLI_NAME) != 0;
	}
    /**
     * @return Returns the description.
     */
    public String getDescription() {
    	if (description == null)
    		description = "";
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

	public String getPackageName() {
		return packageName;
	}

	public boolean isAnnotationField() {
		return isAnnotationField;
	}

	public void setAnnotationField(boolean isAnnotationField) {
		this.isAnnotationField = isAnnotationField;
	}
}
