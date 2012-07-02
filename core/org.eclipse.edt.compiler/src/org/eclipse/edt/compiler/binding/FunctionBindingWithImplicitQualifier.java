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
import java.util.List;
import java.util.Map;

/**
 * @author Dave Murray
 */
public class FunctionBindingWithImplicitQualifier implements IFunctionBinding {

	IFunctionBinding wrappedFunctionBinding;
	IDataBinding implicitQualifierBinding;
	
	public FunctionBindingWithImplicitQualifier(IFunctionBinding wrappedFunctionBinding, IDataBinding implicitQualifierBinding) {
		this.wrappedFunctionBinding = wrappedFunctionBinding;
		this.implicitQualifierBinding = implicitQualifierBinding;
	}
	
	public boolean isFunctionBindingWithImplicitQualifier() {
		return true;
	}

	public IDataBinding getImplicitQualifier() {
		return implicitQualifierBinding;
	}

	public IFunctionBinding getWrappedFunctionBinding() {
		return wrappedFunctionBinding;
	}

	public List getParameters() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getReturnType() {
		throw new UnsupportedOperationException();
	}

	public boolean returnTypeIsSqlNullable() {
		throw new UnsupportedOperationException();
	}

	public boolean isSystemFunction() {
		return wrappedFunctionBinding.isSystemFunction();
	}
	
	public boolean isTopLevelFunction() {
		return wrappedFunctionBinding.isTopLevelFunction();
	}


	public int getSystemFunctionType() {
		throw new UnsupportedOperationException();
	}

	public int[] getValidNumbersOfArguments() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hasMnemonicArguments() {
		throw new UnsupportedOperationException();
	}

	public IDataBinding getQualifier() {
		throw new UnsupportedOperationException();
	}

	public boolean isStatic() {
		throw new UnsupportedOperationException();
	}

	public boolean isAbstract() {
		throw new UnsupportedOperationException();
	}

	public boolean isPrivate() {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}
	
	public String getCaseSensitiveName() {
		throw new UnsupportedOperationException();
	}

	public boolean isPackageBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isFunctionBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isTypeBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isDataBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isAnnotationBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isUsedTypeBinding() {
		throw new UnsupportedOperationException();
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
	
	public IPartBinding getDeclarer() {
		throw new UnsupportedOperationException();
	}
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
		throw new UnsupportedOperationException();
    }
    public boolean isOpenUIStatementBinding() {
        return false;
    }
    
	public LibraryBinding getSystemLibrary() {
		return null;
	}

	public int getKind() {
		throw new UnsupportedOperationException();
	}

	public boolean isValid() {
		throw new UnsupportedOperationException();
	}

	public String[] getPackageName() {
		throw new UnsupportedOperationException();
	}

	public IDataBinding findData(String simpleName) {
		throw new UnsupportedOperationException();
	}

	public IDataBinding findPublicData(String simpleName) {
		throw new UnsupportedOperationException();
	}

	public Map getSimpleNamesToDataBindingsMap() {
		throw new UnsupportedOperationException();
	}

	public IFunctionBinding findFunction(String simpleName) {
		throw new UnsupportedOperationException();
	}

	public IFunctionBinding findPublicFunction(String simpleName) {
		throw new UnsupportedOperationException();
	}

	public boolean isReference() {
		throw new UnsupportedOperationException();
	}

	public boolean isDynamic() {
		throw new UnsupportedOperationException();
	}

	public boolean isDynamicallyAccessible() {
		throw new UnsupportedOperationException();
	}

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		throw new UnsupportedOperationException();
	}

	public boolean isPartBinding() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding copyTypeBinding() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getBaseType() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isNullable() {
		throw new UnsupportedOperationException();
	}
	
	public ITypeBinding getNullableInstance() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isImplicit() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, int index) {
		throw new UnsupportedOperationException();
	}

	public String getPackageQualifiedName() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hasConverse() {
		throw new UnsupportedOperationException();
	}
	
	public byte[] getMD5HashKey() {
		throw new UnsupportedOperationException();
	}

	public boolean isCallStatementBinding() {
		return false;
	}

    public boolean isExitStatementBinding() {
        return false;
    }
    
	public boolean isShowStatementBinding() {
		return false;
	}

	public boolean isTransferStatementBinding() {
		return false;
	}

	public boolean isValidBinding() {
		return false;
	}

	@Override
	public ITypeBinding getNonNullableInstance() {
		return this;
	}

	@Override
	public boolean isInstantiable() {
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
