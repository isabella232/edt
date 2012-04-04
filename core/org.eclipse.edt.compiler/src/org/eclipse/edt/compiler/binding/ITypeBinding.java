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
package org.eclipse.edt.compiler.binding;

import java.util.Map;

/**
 * @author winghong
 */
public interface ITypeBinding extends IBinding {
    
    IPartBinding AMBIGUOUS_TYPE = new AmbiguousTypeBinding();
    
    int NULL_BINDING = 0;
    int NOT_FOUND_BINDING = 1;
    int ARRAY_TYPE_BINDING = 2;
    int PRIMITIVE_TYPE_BINDING = 3;
    int DYNAMIC_BINDING = 4;
    int DATATABLE_BINDING = 5;
    int FIXED_RECORD_BINDING = 6;
    int FLEXIBLE_RECORD_BINDING = 7;
    int FORM_BINDING = 8;
    int FORMGROUP_BINDING = 9;
    int HANDLER_BINDING = 10;
    int LIBRARY_BINDING = 11;
    int PROGRAM_BINDING = 13;
    int SERVICE_BINDING = 14;
    int INTERFACE_BINDING = 15;
    int FILE_BINDING = 16;
    int DATAITEM_BINDING = 17;
    int ANNOTATION_BINDING = 18;
    int ENUMERATION_BINDING = 19;
    int FUNCTION_BINDING = 20;
    int DICTIONARY_BINDING = 21;
    int ARRAYDICTIONARY_BINDING = 22;
    int SPECIALSYSTEMFUNCTIONPARAMETER_BINDING = 23;
    int NIL_BINDING = 25;
    int MULTIPLY_OCCURING_ITEM = 26;
    int DELEGATE_BINDING = 27;
    int EXTERNALTYPE_BINDING = 28;
    int FOREIGNLANGUAGETYPE_BINDING = 29;
    
    int getKind();

    boolean isValid();
    
    String[] getPackageName();
    
    IDataBinding findData(String simpleName);
    IDataBinding findPublicData(String simpleName);
    
    /**
     * Returns a Map whose keys are Strings (all of which are unqualified names)
     * mapped to IDataBinding objects within this type. The data bindings come
     * from any level of the structure within this type that can be accessed in
     * statements when the "allowUnqualifiedItemReferences" flag is set.
     */
    Map getSimpleNamesToDataBindingsMap();
    IFunctionBinding findFunction(String simpleName);
    IFunctionBinding findPublicFunction(String simpleName);
    
    boolean isReference();
    
    /**
     * Returns true if the type of this type binding cannot be determined until
     * runtime. True for types of fields from dictionaries and the primitive
     * type 'any'
     */
    boolean isDynamic();
    /**
     * Returns true if an item of this type can be accessed with the dot notation
     * to obtain a dynamic type. True for all dynamic types, as well as dictionaries
     * and arrayDictionaries.
     */
    boolean isDynamicallyAccessible();
    
    boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding);
    
    boolean isPartBinding();   
   
    ITypeBinding copyTypeBinding();  
    
    /**
     * Returns the base type of this typeBinding
     * For most typeBindings, it will just return "this", but for arrays
     * and reference types, it will return the element type binding
     */
    ITypeBinding getBaseType();
    
    boolean isNullable();
    ITypeBinding getNullableInstance();
    ITypeBinding getNonNullableInstance();
    
    String getPackageQualifiedName();
    
    boolean isInstantiable();
}
