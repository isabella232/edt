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

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


/**
 * @author winghong
 */
public class AmbiguousTypeBinding implements IPartBinding {

    public String[] getPackageName() {
        return null;
    }

    public String getName() {
        return null;
    }
    
    public String getCaseSensitiveName() {
    	return null;
    }
    
    public boolean isDataBinding() {
        return false;
    }

    public boolean isFunctionBinding() {
        return false;
    }

    public boolean isPackageBinding() {
        return false;
    }

    public boolean isTypeBinding() {
        return true;
    }
    
	public boolean isReference() {
		return false;
	}
	
	public boolean isDynamic() {
		return false;
	}
	
	public boolean isDynamicallyAccessible() {
		return false;
	}
    
    public boolean isAnnotationBinding() {
        return false;
    }

	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
		return null;
	}
    
    public List getAnnotations() {
        return null;
    }

    public IDataBinding findData(String simpleName) {
        return null;
    }
    
    public IDataBinding findPublicData(String simpleName) {
        return null;
    }
    
    public Map getSimpleNamesToDataBindingsMap() {
        return null;
    }
    
	public IFunctionBinding findFunction(String simpleName) {
		return null;
	}
	
	public IFunctionBinding findPublicFunction(String simpleName) {
		return null;
	}

    public boolean isValid() {
        return false;
    }

    public IPartBinding realize() {
        return this;
    }

    public InputStream getSerializedInputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public byte[] getSerializedBytes() throws IOException{
    	return new byte[0]; 
    }
    
    public int getKind() {
        return ITypeBinding.NULL_BINDING;
    }

    public void clear() {
    }

    public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
        throw new RuntimeException("Shouldn't call this method");
    }
    
	public ITypeBinding copyTypeBinding() {
		return new AmbiguousTypeBinding();
	}
	
	public boolean isPartBinding(){
		return false;
	}

    public void addAnnotation(IAnnotationBinding annotation) {
    }
    
	public void addAnnotations(Collection annotations) {
	}

    public boolean isUsedTypeBinding() {
         return false;
    }

    public ITypeBinding getBaseType() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IBinding#getAnnotation(org.eclipse.edt.compiler.binding.IAnnotationTypeBinding, int)
     */
    public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
        return null;
    }
    public boolean isOpenUIStatementBinding() {
        return false;
    }

	public IEnvironment getEnvironment() {
		return null;
	}

	public void setEnvironment(IEnvironment environment) {
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		return false;
	}

	public void setValid(boolean isValid) {
	}

	public IPartSubTypeAnnotationTypeBinding getSubType() {
		return null;
	}
	
	public IAnnotationBinding getSubTypeAnnotationBinding() {
		return null;
	}

	public boolean isDeclarablePart() {
		return false;
	}
	
	public boolean isPrivate() {
		return false;
	}
	
	public ITypeBinding getNullableInstance() {
		return null;
	}
	
	public boolean isNullable() {
		return false;
	}

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
		return null;
	}

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, int index) {
		return null;
	}

	public String getPackageQualifiedName() {
		return null;
	}
	
	public boolean isSystemPart() {
		return false;
	}
	
	public byte[] getMD5HashKey() {
		return null;
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

	public boolean isValidBinding() {
		return false;
	}

	public StaticPartDataBinding getStaticPartDataBinding() {
		return null;
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
