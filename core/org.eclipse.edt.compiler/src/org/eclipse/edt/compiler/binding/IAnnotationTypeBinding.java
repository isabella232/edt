/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.List;

/**
 * @author winghong
 */
public interface IAnnotationTypeBinding extends ITypeBinding{
    
    boolean isApplicableFor(IBinding binding);
    boolean isPartSubType();
    boolean isComplex();
    EnumerationTypeBinding getEnumerationType();
    
    boolean supportsElementOverride();
    
    boolean takesExpressionInOpenUIStatement();
    ITypeBinding requiredTypeForOpenUIStatement();
    
    public List getValueAnnotations();
    public List getAnnotations();
    
    public List getFieldAnnotations(String field);
    
    public List getPartSubTypeAnnotations();
    
    public List getPartTypeAnnotations();
    
    public List getFieldAccessAnnotations();
    
    public List getInstantiationValidators();
    public List getInvocationValidators();
    
    public Object getDefaultValue();
    public Object getDefaultValueForField(String fieldName);
    
    boolean hasSingleValue();
    boolean isValueless();
    
    ITypeBinding getSingleValueType();
    
    public List getFieldNames();
    public List getCaseSensitiveFieldNames();
    
    IAnnotationTypeBinding getValidationProxy();
    
    public boolean isSystemAnnotation();
    public boolean isBIDIEnabled();
}
