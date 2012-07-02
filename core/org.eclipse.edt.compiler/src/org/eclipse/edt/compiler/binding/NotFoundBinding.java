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
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


/**
 * @author winghong
 */
public class NotFoundBinding implements IDataBinding, IFunctionBinding, IPackageBinding, IPartBinding, IAnnotationBinding {
    
    private static final NotFoundBinding INSTANCE = new NotFoundBinding();
    
    private NotFoundBinding() {
        super();
    }
    
    protected static NotFoundBinding getInstance() {
        return INSTANCE;
    }
    
    private Object readResolve() {
        return INSTANCE;
    }

    
    public ITypeBinding copyTypeBinding() {
        throw new UnsupportedOperationException();
    }

    public IDataBinding copyDataBinding(HashMap itemMapping) {
        throw new UnsupportedOperationException();
    }

    public int getKind() {
        throw new UnsupportedOperationException();
    }

    public ITypeBinding getType() {
        throw new UnsupportedOperationException();
    }

    public List getParameters() {
        throw new UnsupportedOperationException();
    }

    public IDataBinding getQualifier() {
        throw new UnsupportedOperationException();
    }

    public ITypeBinding getReturnType() {
        throw new UnsupportedOperationException();
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

    public boolean isAbstract() {
        throw new UnsupportedOperationException();
    }
    
    public IPartBinding getDeclaringPart() {
		throw new UnsupportedOperationException();
	}

    public boolean isPrivate() {
        throw new UnsupportedOperationException();
    }

    public boolean isStatic() {
        throw new UnsupportedOperationException();
    }

    public boolean isSystemFunction() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isTopLevelFunction() {
        throw new UnsupportedOperationException();
    }

    public boolean returnTypeIsSqlNullable() {
        throw new UnsupportedOperationException();
    }

    public String[] getPackageName() {
        throw new UnsupportedOperationException();
    }

    public IPackageBinding resolvePackage(String simpleName) {
        throw new UnsupportedOperationException();
    }

    public ITypeBinding resolveType(String simpleName) {
        throw new UnsupportedOperationException();
    }

    public IDataBinding findData(String simpleName) {
        throw new UnsupportedOperationException();
    }
    
    public IDataBinding findPublicData(String simpleName) {
        throw new UnsupportedOperationException();
    }

    public IFunctionBinding findFunction(String simpleName) {
        throw new UnsupportedOperationException();
    }
    
    public IFunctionBinding findPublicFunction(String simpleName) {
        throw new UnsupportedOperationException();
    }

    public Map getSimpleNamesToDataBindingsMap() {
        throw new UnsupportedOperationException();
    }

    public boolean isPartBinding() {
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

    public boolean isValid() {
        throw new UnsupportedOperationException();
    }

    public void addAnnotation(IAnnotationBinding annotation) {
        throw new UnsupportedOperationException();
    }
    
	public void addAnnotations(Collection annotations) {
        throw new UnsupportedOperationException();
    }

	public void addFields(List list) {
        throw new UnsupportedOperationException();
    }

    public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
        throw new UnsupportedOperationException();
    }
    
	public List getAnnotationsFor(IDataBinding[] path) {
		throw new UnsupportedOperationException();
	}

    public List getAnnotations() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }
    
    public String getCaseSensitiveName() {
    	throw new UnsupportedOperationException();
    }

    public byte[] getSerializedBytes() throws IOException {
        throw new UnsupportedOperationException();
    }

    public InputStream getSerializedInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public boolean isAnnotationBinding() {
        throw new UnsupportedOperationException();
    }

    public boolean isDataBinding() {
        throw new UnsupportedOperationException();
    }

    public boolean isFunctionBinding() {
        throw new UnsupportedOperationException();
    }

    public boolean isPackageBinding() {
        throw new UnsupportedOperationException();
    }

    public boolean isTypeBinding() {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public IEnvironment getEnvironment() {
        throw new UnsupportedOperationException();
    }

    public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
        throw new UnsupportedOperationException();
    }

    public IPartBinding realize() {
        throw new UnsupportedOperationException();
    }

    public void setEnvironment(IEnvironment environment) {
        throw new UnsupportedOperationException();
    }

    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IBinding#isUsedTypeBinding()
     */
    public boolean isUsedTypeBinding() {
        return false;
    }

	public boolean isDataBindingWithImplicitQualifier() {
		throw new UnsupportedOperationException();
	}

	public IDataBinding getImplicitQualifier() {
		throw new UnsupportedOperationException();
	}

	public IDataBinding getWrappedDataBinding() {
		throw new UnsupportedOperationException();
	}
	
    public ITypeBinding getBaseType() {
        return this;
    }
	
	public boolean isTopLevel() {
		throw new UnsupportedOperationException();
	}
	
    public IPartSubTypeAnnotationTypeBinding getSubType() {
		throw new UnsupportedOperationException();
    }

	public IAnnotationBinding getSubTypeAnnotationBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isFunctionBindingWithImplicitQualifier() {
		throw new UnsupportedOperationException();
	}

	public IFunctionBinding getWrappedFunctionBinding() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path) {
		throw new UnsupportedOperationException();
	}
	
	public IPartBinding getDeclarer() {
		throw new UnsupportedOperationException();
	}
	
	public void addAnnotation(IAnnotationBinding annotation, IDataBinding[] path) {
		throw new UnsupportedOperationException();
    }

    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path, int index) {
		throw new UnsupportedOperationException();
    }

    public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
		throw new UnsupportedOperationException();
    }
    public boolean isOpenUIStatementBinding() {
        throw new UnsupportedOperationException();
    }
	public boolean isDeclarablePart() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}
	
	public LibraryBinding getSystemLibrary() {
		throw new UnsupportedOperationException();
	}

	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	public void setValue(Object value, IProblemRequestor problemRequestor, Expression expression, ICompilerOptions compilerOptions, boolean performValidation) {
		throw new UnsupportedOperationException();
		
	}

	public void setValue(Object value, IProblemRequestor problemRequestor, Expression expression, ICompilerOptions compilerOptions) {
		throw new UnsupportedOperationException();
	}

	public IAnnotationTypeBinding getAnnotationType() {
		throw new UnsupportedOperationException();
	}

	public boolean isForElement() {
		throw new UnsupportedOperationException();
	}

	public boolean isAnnotationField() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationTypeBinding getEnclosingAnnotationType() {
		throw new UnsupportedOperationException();
	}
	
	public List getAnnotationFields() {
		throw new UnsupportedOperationException();
	}
	
	public void addField(IAnnotationBinding newField) {
		throw new UnsupportedOperationException();		
	}
	
	public boolean isNullable() {
		throw new UnsupportedOperationException();
	}
	
	public ITypeBinding getNullableInstance() {
		throw new UnsupportedOperationException();
	}

	public boolean isCopiedAnnotationBinding() {
		throw new UnsupportedOperationException();
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

	public String getPackageQualifiedName() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hasConverse() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isSystemPart() {
		throw new UnsupportedOperationException();
	}

	public byte[] getMD5HashKey() {
		throw new UnsupportedOperationException();
	}

	public boolean isCallStatementBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isShowStatementBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isTransferStatementBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isExitStatementBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isStaticPartDataBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isValidBinding() {
		return false;
	}

	public StaticPartDataBinding getStaticPartDataBinding() {
		throw new UnsupportedOperationException();
	}

	public boolean isCalculatedValue() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isImplicit() {
		throw new UnsupportedOperationException();
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
