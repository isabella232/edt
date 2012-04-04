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

import java.util.HashMap;
import java.util.List;

/**
 * @author winghong
 */
public interface IDataBinding extends IBinding {
	
	int FUNCTION_PARAMETER_BINDING = 0;
	int PROGRAM_PARAMETER_BINDING = 1;
	int LOCAL_VARIABLE_BINDING = 2;
	int CLASS_FIELD_BINDING = 3;
	int SYSTEM_VARIABLE_BINDING = 4;
	int STRUCTURE_ITEM_BINDING = 5;
	int DYNAMIC_BINDING = 6;
	int FORM_FIELD = 7;
	int FORM_BINDING = 8;
	int FORMGROUP_BINDING = 9;
	int DATATABLE_BINDING = 10;
	int LIBRARY_BINDING = 11;
	int AMBIGUOUS_BINDING = 12;
	int ANNOTATION_BINDING = 13;
	int FLEXIBLE_RECORD_FIELD = 14;
	int ENUMERATION_BINDING = 15;
	int SYSTEM_FUNCTION_ARGUMENT_MNEMONIC = 16;
	int IS_NOT_STATE = 17;
	int ENUMERATIONTYPE_BINDING = 18;
	int SERVICE_REFERENCE_BINDING = 19;
	int NESTED_FUNCTION_BINDING = 20;
	int TOP_LEVEL_FUNCTION_BINDING = 21;
	int CONSTRUCTOR_BINDING = 22;
	int OVERLOADED_FUNCTION_SET_BINDING = 23;
	int AMBIGUOUSSYSTEMLIBRARYFIELD_BINDING = 24;
	int FOREIGN_LANGUAGE_TYPE = 25;
	int EXTERNALTYPE_BINDING = 26;
	int PROGRAM_BINDING = 27;
	
	int getKind();

    ITypeBinding getType();
    
    IDataBinding copyDataBinding(HashMap itemMapping);
    
    IPartBinding getDeclaringPart();
    
    boolean isReadOnly();
    
    /**
     * This method is to set the attribute IMPLICIT_QUALIFIER_DATA_BINDING in
     * the class Name. Outside of the binder classes, it should always return
     * false and need never be called.
     */
    boolean isDataBindingWithImplicitQualifier();
    
    /**
     * This method is to set the attribute IMPLICIT_QUALIFIER_DATA_BINDING in
     * the class Name. Outside of the binder classes, it should always return
     * null and need never be called.
     */
    IDataBinding getImplicitQualifier();
    
    /**
     * This method is to set the attribute IMPLICIT_QUALIFIER_DATA_BINDING in
     * the class Name. Outside of the binder classes, it should always return
     * null and need never be called.
     */
    IDataBinding getWrappedDataBinding();    
    
    /**
     * This method returns an AnnotationBinding for the specified child item (field or structureItem)
     * based on the given path. This is the method that should be used when attempting to retrieve
     * the annotation for a data binding (will return the correct annotation if overrides are used).
     * When retrieving an annotation for a structureItemBinding, the path should contain a single
     * element (the structureItem that is of interest). Path should only contain multiple entries
     * when looking for annotations in a flexible record chain
     */
    IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path );
    IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path, int index );
    IAnnotationBinding getAnnotationFor(String[] packageName, String annotationName, IDataBinding[] path );
    IAnnotationBinding getAnnotationFor(String[] packageName, String annotationName, IDataBinding[] path, int index );
    void addAnnotation(IAnnotationBinding annotation, IDataBinding[] path);
    List getAnnotationsFor(IDataBinding[] path);
    
    IDataBinding findData(String simpleName);
    
    boolean isStaticPartDataBinding();
}
