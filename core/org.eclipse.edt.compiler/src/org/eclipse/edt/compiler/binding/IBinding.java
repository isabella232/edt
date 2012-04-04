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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author winghong
 */
public interface IBinding extends Serializable {
    
    NotFoundBinding NOT_FOUND_BINDING = NotFoundBinding.getInstance();
    
    /**
     * Get an interned version of the Binding's name
     * @return the interned name
     */
    String getName();
    
    /**
     * Get a case sensitive version of the Binding's name
     * @return the case sensitive name
     */
    String getCaseSensitiveName();
    
    boolean isPackageBinding();
    boolean isFunctionBinding();
    boolean isTypeBinding();
    boolean isDataBinding();
    boolean isAnnotationBinding();
    boolean isUsedTypeBinding();
    boolean isOpenUIStatementBinding();
    boolean isCallStatementBinding();
    boolean isTransferStatementBinding();
    boolean isShowStatementBinding();
    boolean isExitStatementBinding();
    
    List getAnnotations();
    IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType);
    IAnnotationBinding getAnnotation(String[] packageName, String annotationName);
    IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index);
    IAnnotationBinding getAnnotation(String[] packageName, String annotationName, int index);
    void addAnnotation(IAnnotationBinding annotation);
    void addAnnotations(Collection annotations);
    
    InputStream getSerializedInputStream() throws IOException;
    byte[]      getSerializedBytes() throws IOException;
    
    byte[] getMD5HashKey();
    
    boolean isValidBinding();
}
