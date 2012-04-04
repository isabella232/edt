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

import java.util.List;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author winghong
 */
public interface IAnnotationBinding extends IDataBinding {
    
    public static IAnnotationBinding NOT_APPLICABLE_ANNOTATION_BINDING = NotApplicableAnnotationBinding.getInstance();
    
    Object getValue();
    void setValue(Object value, IProblemRequestor problemRequestor, Expression expression, ICompilerOptions compilerOptions, boolean performValidation);
    void setValue(Object value, IProblemRequestor problemRequestor, Expression expression, ICompilerOptions compilerOptions);
    IAnnotationTypeBinding getAnnotationType();
    
    boolean isForElement();
    
    boolean isAnnotationField();
    IAnnotationTypeBinding getEnclosingAnnotationType();
    List getAnnotationFields();
    void addFields(List list);
    void addField(IAnnotationBinding newField);
    boolean isCopiedAnnotationBinding();
    
    /**
     * Returns true if this annotation exists because the compiler calculated a value for it, and not
     * because there is a property setting in the EGL source. Examples of calculated properties are
     * 'fieldLen' for form fields and 'position' for form field array elements.
     */
    boolean isCalculatedValue();
}
