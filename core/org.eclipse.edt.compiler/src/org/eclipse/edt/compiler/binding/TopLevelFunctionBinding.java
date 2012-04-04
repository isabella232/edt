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
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class TopLevelFunctionBinding extends FunctionBinding implements IPartBinding {
	
	transient private TopLevelFunctionDataBinding staticFunctionBinding;
	
	protected String[] packageName;
	protected boolean isValid;
    protected transient IEnvironment environment;
	
	public TopLevelFunctionBinding(String[] packageName, String caseSensitiveInternedName) {
        super(caseSensitiveInternedName, null);
        this.packageName = packageName;
    }

	public IEnvironment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(IEnvironment environment) {
        this.environment = environment;
    }

	public IPartBinding realize() {
		return environment.getPartBinding(packageName, getName());
	}

	public void clear() {
		parameters = Collections.EMPTY_LIST;
		returnType = null;
		returnTypeIsSqlNullable = false;
		isStatic = false;
		isAbstract = false;
		isPrivate = false;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public int getKind() {
		return FUNCTION_BINDING;
	}

	public boolean isValid() {
		return isValid;
	}

	public String[] getPackageName() {
		return packageName;
	}

	public IDataBinding findData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public IDataBinding findPublicData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}

	public Map getSimpleNamesToDataBindingsMap() {
		return null;
	}

	public IFunctionBinding findFunction(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public IFunctionBinding findPublicFunction(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
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

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPartBinding() {
		return true;
	}

	public ITypeBinding copyTypeBinding() {
		throw new UnsupportedOperationException( "copyTypeBinding() not overriden for " + getClass().getName() );
	}
	
    public IPartBinding getDeclarer() {
        return this;
    }

    public ITypeBinding getBaseType() {
        return this;
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
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if(packageName.length != 0) packageName = InternUtil.intern(packageName);
    }
	
	public IDataBinding getStaticTopLevelFunctionDataBinding() {
		if(staticFunctionBinding == null) {
			staticFunctionBinding = new TopLevelFunctionDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticFunctionBinding;
	}
	
	public boolean isSystemPart() {
		return false;
	}

	public StaticPartDataBinding getStaticPartDataBinding() {
		return null;
	}
	
	public boolean isTopLevelFunction() {
		return true;
	}

}
