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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class OverloadedFunctionSet implements IDataBinding {
	
	private List nestedFunctionBindings;
	private String caseSensitiveInternedFunctionName;
	
	public OverloadedFunctionSet() {
		nestedFunctionBindings = new ArrayList();		
	}
	
	public void setName(String caseSensitiveInternedFunctionName) {
		this.caseSensitiveInternedFunctionName = caseSensitiveInternedFunctionName;
	}
	
	public List getNestedFunctionBindings() {
		return nestedFunctionBindings;
	}
	
	public void addNestedFunctionBinding(IDataBinding newBinding) {
		nestedFunctionBindings.add(newBinding);
	}

	public int getKind() {
		return OVERLOADED_FUNCTION_SET_BINDING;
	}
	
	public String getName() {
		return InternUtil.intern(caseSensitiveInternedFunctionName);
	}
	
	public String getCaseSensitiveName() {
		return caseSensitiveInternedFunctionName;
	}

	public ITypeBinding getType() {
		return null;
	}

	public IDataBinding copyDataBinding(HashMap itemMapping) {
		throw new UnsupportedOperationException();
	}

	public boolean isDataBindingWithImplicitQualifier() {
		return false;
	}

	public IPartBinding getDeclaringPart() {
		return null;
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

	public IDataBinding getImplicitQualifier() {
		throw new UnsupportedOperationException();
	}

	public IDataBinding getWrappedDataBinding() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
		throw new UnsupportedOperationException();
	}
	
	public byte[] getMD5HashKey() {
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

	public OverloadedFunctionSet trimFunctionsWithIdenticalSignatures(ICompilerOptions compilerOptions) {
		OverloadedFunctionSet result = new OverloadedFunctionSet();
		result.setName(caseSensitiveInternedFunctionName);
		for(Iterator iter = nestedFunctionBindings.iterator(); iter.hasNext();) {
			IDataBinding next = (IDataBinding) iter.next();
			if(next.getKind() == IDataBinding.CONSTRUCTOR_BINDING) {
				result.addNestedFunctionBinding(next);
			}
			else {
				NestedFunctionBinding fBinding = (NestedFunctionBinding) next;
				boolean foundMatchingSignature = false;
				for(Iterator iter2 = result.getNestedFunctionBindings().iterator(); iter2.hasNext() && !foundMatchingSignature;) {
					if(TypeCompatibilityUtil.functionSignituresAreIdentical((IFunctionBinding) fBinding.getType(), (IFunctionBinding) ((IDataBinding) iter2.next()).getType(), compilerOptions)) {
						foundMatchingSignature = true;
					}
				}
				if(!foundMatchingSignature) {
					result.addNestedFunctionBinding(fBinding);
				}
			}
		}
		return result;
	}

	public boolean isCallStatementBinding() {
		return false;
	}

	public boolean isShowStatementBinding() {
		return false;
	}

	public boolean isTransferStatementBinding() {
		return false;
	}
	
	public boolean isExitStatementBinding() {
		return false;
	}

	public boolean isStaticPartDataBinding() {
		return false;
	}

	public boolean isValidBinding() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getActualBindingName() {
		return null;
	}

	@Override
	public String[] getActualBindingPackage() {
		return null;
	}
	
}
