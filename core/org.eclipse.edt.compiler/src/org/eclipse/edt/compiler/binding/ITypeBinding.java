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


/**
 * @author winghong
 */
public interface ITypeBinding extends IBinding {
    
    int NULL_BINDING = 0;
    int NOT_FOUND_BINDING = 1;
    int ARRAY_TYPE_BINDING = 2;
    int PRIMITIVE_TYPE_BINDING = 3;
    int DYNAMIC_BINDING = 4;
    int FIXED_RECORD_BINDING = 6;
    int FLEXIBLE_RECORD_BINDING = 7;
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
    int NIL_BINDING = 25;
    int MULTIPLY_OCCURING_ITEM = 26;
    int DELEGATE_BINDING = 27;
    int EXTERNALTYPE_BINDING = 28;
    int FOREIGNLANGUAGETYPE_BINDING = 29;
    int CLASS_BINDING = 30;
    int STEREOTYPE_BINDING = 31;
    
    int getKind();

    boolean isValid();
    
    String getPackageName();
    String getCaseSenstivePackageName();
    
    boolean isPartBinding();       
    
    String getPackageQualifiedName();
    
}
