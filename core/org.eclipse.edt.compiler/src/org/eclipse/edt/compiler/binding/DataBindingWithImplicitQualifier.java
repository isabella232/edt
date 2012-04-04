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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author Dave Murray
 */
public class DataBindingWithImplicitQualifier implements IDataBinding {
	
	IDataBinding wrappedDataBinding;
	IDataBinding implicitQualifierBinding;
	
	public DataBindingWithImplicitQualifier(IDataBinding wrappedDataBinding, IDataBinding implicitQualifierBinding) {
		this.wrappedDataBinding = wrappedDataBinding;
		this.implicitQualifierBinding = implicitQualifierBinding;
	}

	public int getKind() {
		return wrappedDataBinding.getKind();
	}

	public ITypeBinding getType() {
		throw new UnsupportedOperationException();
	}

	public IDataBinding copyDataBinding(HashMap itemMapping) {
		throw new UnsupportedOperationException();
	}

	public boolean isDataBindingWithImplicitQualifier() {
		return true;
	}

	public IDataBinding getImplicitQualifier() {
		return implicitQualifierBinding;
	}

	public IDataBinding getWrappedDataBinding() {
		return wrappedDataBinding.isDataBindingWithImplicitQualifier() ?
			wrappedDataBinding.getWrappedDataBinding() : wrappedDataBinding;
	}

	public String getName() {
		return wrappedDataBinding.getName();
	}
	
	public String getCaseSensitiveName() {
		return wrappedDataBinding.getCaseSensitiveName();
	}
	
	public IPartBinding getDeclaringPart() {
		return wrappedDataBinding.getDeclaringPart();
	}

	public boolean isPackageBinding() {
		return false;
	}

	public boolean isFunctionBinding() {
		return false;
	}

	public boolean isTypeBinding() {
		return false;
	}

	public boolean isDataBinding() {
		return true;
	}

	public boolean isAnnotationBinding() {
		return false;
	}

	public List getAnnotations() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
		throw new UnsupportedOperationException();
	}

	public void addAnnotation(IAnnotationBinding annotation) {
		throw new UnsupportedOperationException();
	}
	
	public void addAnnotations(Collection annotations) {
		throw new UnsupportedOperationException();
	}

	public InputStream getSerializedInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	public byte[] getSerializedBytes() throws IOException {
		throw new UnsupportedOperationException();
	}

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IBinding#isUsedTypeBinding()
     */
    public boolean isUsedTypeBinding() {
        return false;
    }
    
	public List getAnnotationsFor(IDataBinding[] path) {
		throw new UnsupportedOperationException();
	}

    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path) {
		throw new UnsupportedOperationException();
    }

    public IDataBinding findData(String simpleName) {
		throw new UnsupportedOperationException();
    }

    public void addAnnotation(IAnnotationBinding annotation, IDataBinding[] path) {
		throw new UnsupportedOperationException();
        
    }

    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path, int index) {
		throw new UnsupportedOperationException();
    }
    
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

    public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
		throw new UnsupportedOperationException();
    }
    
    public boolean isOpenUIStatementBinding() {
        return false;
    }
    
    public boolean isCallStatementBinding() {
        return false;
    }
    
    public boolean isTransferStatementBinding() {
        return false;
    }
    
    public boolean isShowStatementBinding() {
        return false;
    }

    public boolean isExitStatementBinding() {
        return false;
    }
    
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotationFor(String[] packageName, String annotationName, IDataBinding[] path) {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotationFor(String[] packageName, String annotationName, IDataBinding[] path, int index) {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, int index) {
		throw new UnsupportedOperationException();
	}

	public byte[] getMD5HashKey() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isStaticPartDataBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isValidBinding() {
		return false;
	}

}
