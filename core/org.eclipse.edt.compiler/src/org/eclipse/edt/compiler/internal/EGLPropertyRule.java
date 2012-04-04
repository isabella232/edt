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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.EGLSpecificValuesAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration;
import org.eclipse.edt.mof.egl.utils.InternUtil;



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
	
	private String[] packageName;
	
	//Distinguish those properties that are only allowed on an array, not an array element
	private boolean arrayOnly; 
	
	private boolean isComplex = false;
	private boolean isValueless = false;
	
	private boolean isAnnotationField = false;
	
	private EGLEnumeration enumeration;
	
	private int specialRules = 0;
	
	private IAnnotationTypeBinding elementAnnotationType;
	
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
	
	/**
	 * For properties whose value is an array of annotations, like linkParms in programLinkData.
	 */
	public EGLPropertyRule(String name, int [] types, int [] elementTypes, EGLPropertyRule[] elementAnnotationTypes) {
		this(name, types, null, true);
		this.elementTypes = elementTypes;
		this.elementAnnotationTypes = elementAnnotationTypes;
	}
	
	/**
	 * For 6.0.1 "annotation" properties, like programLinkData and linkParameter.
	 */
	public EGLPropertyRule(String name, EGLPropertyRule[] elementAnnotationTypes ) {
		this.name = name;
		this.elementAnnotationTypes = elementAnnotationTypes;
		this.isComplex = true;
	}

	public EGLPropertyRule(IAnnotationTypeBinding typeBindingImpl) {
		this.name = typeBindingImpl.getCaseSensitiveName();
		this.packageName = typeBindingImpl.getPackageName();
		List fieldNames = typeBindingImpl.getFieldNames();
		if(typeBindingImpl.hasSingleValue() && (fieldNames.isEmpty() || "value".equalsIgnoreCase((String)fieldNames.iterator().next()))) {
			setFieldsFromType(typeBindingImpl.getSingleValueType(), typeBindingImpl);
		}
		else {
			
			if (typeBindingImpl.isValueless()) {
				this.isValueless = true;
			}
			else {
				this.types = new int[] {EGLNewPropertiesHandler.nestedValue};
				if(typeBindingImpl.isComplex()){
					this.isComplex = true;
				}
	    	    List elementRules = new ArrayList();
				for(Iterator iter = typeBindingImpl.getFieldNames().iterator(); iter.hasNext();) {
				    elementRules.add(new EGLPropertyRule((IAnnotationTypeBinding) typeBindingImpl.findData((String) iter.next()).getType()));
				}
				this.elementAnnotationTypes = (EGLPropertyRule[]) elementRules.toArray(new EGLPropertyRule[0]);			
			}
		}
	}

	public EGLPropertyRule(IDataBinding dBinding) {
		this.name = dBinding.getCaseSensitiveName();
		this.packageName = dBinding.getDeclaringPart().getPackageName();
		setFieldsFromType(dBinding.getType(), null);
	}

	public EGLPropertyRule(ClassFieldBinding cfBinding) {
		this.name = cfBinding.getCaseSensitiveName();
		this.packageName = cfBinding.getDeclaringPart().getPackageName();
		setFieldsFromType(cfBinding.getType(), null);
	}

	private void setFieldsFromType(ITypeBinding type, IAnnotationTypeBinding typeBindingImpl) {
		switch(type.getKind()) {
			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
				switch(((PrimitiveTypeBinding) type).getPrimitive().getType()) {
					case Primitive.STRING_PRIMITIVE :
						IAnnotationBinding aBinding = typeBindingImpl == null ? null : typeBindingImpl.getAnnotation(EGLSpecificValuesAnnotationTypeBinding.getInstance());
						if(aBinding == null) {
							this.types = typeBindingImpl != null && typeBindingImpl.isBIDIEnabled()?
								new int[] {EGLNewPropertiesHandler.quotedValue,EGLNewPropertiesHandler.bidiEnabled} :
								new int[] {EGLNewPropertiesHandler.quotedValue};
						}
						else {
							this.types = new int[] {EGLNewPropertiesHandler.quotedValue, EGLNewPropertiesHandler.specificValue};
							Object[] specificValues = (Object[]) aBinding.getValue();
							this.specificValues = new String[specificValues.length];
							for (int i = 0; i < specificValues.length; i++) {
								this.specificValues[i] = (String) specificValues[i];
							}
						}
						break;
						
					case Primitive.BOOLEAN_PRIMITIVE:
						this.types = new int[] {EGLNewPropertiesHandler.specificValue};
						this.specificValues = new String[] {"yes", "no"};
						break;
						
					case Primitive.INT_PRIMITIVE:
						this.types = new int[] {EGLNewPropertiesHandler.integerValue};
						break;
						
					case Primitive.ANY_PRIMITIVE:
						this.types = (typeBindingImpl != null && typeBindingImpl.isBIDIEnabled())? new int[] {EGLNewPropertiesHandler.literalValue,EGLNewPropertiesHandler.bidiEnabled}: new int[] {EGLNewPropertiesHandler.literalValue};
						break;
				}
				break;
				
			case ITypeBinding.ENUMERATION_BINDING:
				List fieldNames = new ArrayList();
				for(Iterator iter = ((EnumerationTypeBinding) type).getEnumerations().iterator(); iter.hasNext();) {
					fieldNames.add(((IDataBinding) iter.next()).getCaseSensitiveName());
				}
				this.types = new int[] {EGLNewPropertiesHandler.specificValue};
				this.specificValues = (String[]) fieldNames.toArray(new String[0]);
				break;
				
			case ITypeBinding.INTERFACE_BINDING:
				if(SystemPartManager.SQLSTRING_BINDING == type) {
					this.types = new int[] {EGLNewPropertiesHandler.sqlValue};
				}
				else if(InternUtil.intern(new String[] {"egl", "core", "reflect"}) == type.getPackageName()) {					
					this.types = new int[] {EGLNewPropertiesHandler.nameValue};
				}				
				break;
				
			case ITypeBinding.ARRAY_TYPE_BINDING:
				this.types = new int[] {EGLNewPropertiesHandler.listValue};
				ITypeBinding elementType = ((ArrayTypeBinding) type).getElementType(); 
				switch(elementType.getKind()) {
					case ITypeBinding.ENUMERATION_BINDING:
						fieldNames = new ArrayList();
						for(Iterator iter = ((EnumerationTypeBinding) elementType).getEnumerations().iterator(); iter.hasNext();) {
							fieldNames.add(firstLower(((IDataBinding) iter.next()).getName()));
						}
						this.specificValues = (String[]) fieldNames.toArray(new String[0]);
						break;
						
					case ITypeBinding.ARRAY_TYPE_BINDING:
						this.types = new int[] {EGLNewPropertiesHandler.arrayOfArrays};
						break;
						
					case ITypeBinding.EXTERNALTYPE_BINDING:
						this.types = new int[] {EGLNewPropertiesHandler.listValue};
						break;
						
					default:
						if(elementType.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()) != null) {
							this.types = new int[] {EGLNewPropertiesHandler.arrayOf};
							this.elementAnnotationType = new AnnotationTypeBindingImpl((FlexibleRecordBinding) elementType, (IPartBinding) elementType);
						}						
				}
				break;
		}
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
	
	public IAnnotationTypeBinding getElementAnnotationType() {
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

	public String[] getPackageName() {
		return packageName;
	}

	public boolean isAnnotationField() {
		return isAnnotationField;
	}

	public void setAnnotationField(boolean isAnnotationField) {
		this.isAnnotationField = isAnnotationField;
	}
}
