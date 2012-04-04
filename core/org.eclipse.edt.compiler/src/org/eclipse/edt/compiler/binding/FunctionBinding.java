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
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.annotationType.EGLIsSystemPartAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLSystemConstantAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLValidNumberOfArgumentsAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;



/**
 * @author Harmon
 */
public class FunctionBinding extends Binding implements IFunctionBinding {
	
	protected List parameters = Collections.EMPTY_LIST;
	protected transient ITypeBinding returnType;
	protected boolean returnTypeIsSqlNullable;
	protected boolean isStatic;
	protected boolean isAbstract;
	protected boolean isPrivate;
	private IPartBinding declarer;
	private boolean hasConverse;
	private boolean implicit;
    
    /**
     * @param simpleName
     */
    public FunctionBinding(String caseSensitiveInternedName, IPartBinding declarer) {
        super(caseSensitiveInternedName);
        this.declarer = declarer;
    }
    
    public IPartBinding getDeclarer() {
        return declarer;
    }
    
    public boolean isFunctionBinding() {
		return true;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#getParameters()
     */
    public List getParameters() {
        return parameters;
    }
    
    public void addParameter(FunctionParameterBinding parameter) {
    	if(parameters == Collections.EMPTY_LIST) {
    		parameters = new ArrayList();
    	}
    	parameters.add(parameter);
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#getReturnType()
     */
    public ITypeBinding getReturnType() {
        return realizeTypeBinding(returnType, getDeclarer() != null ? getDeclarer().getEnvironment() : null);
    }
    
    public void setReturnType(ITypeBinding type) {
    	returnType = getTypeBinding(type, null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#returnTypeIsNullable()
     */
    public boolean returnTypeIsSqlNullable() {
        return returnTypeIsSqlNullable;
    }
    
    public void setReturnTypeIsSqlNullable(boolean returnIsNullable) {
    	this.returnTypeIsSqlNullable = returnIsNullable;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#isSystemFunction()
     */
    public boolean isSystemFunction() {
    	if(declarer != null) {
    		IAnnotationBinding aBinding = declarer.getAnnotation(EGLIsSystemPartAnnotationTypeBinding.getInstance());
    		if(aBinding != null && Boolean.YES == aBinding.getValue()) {
    			return true;
    		}
    	}
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#getSystemFunctionType()
     */
    public int getSystemFunctionType() {
    	IAnnotationBinding ann = getAnnotation(EGLSystemConstantAnnotationTypeBinding.getInstance());
    	if (ann != null) {
    		return ((Integer)ann.getValue()).intValue();
    	}
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#getValidNumbersOfArguments()
     */
    public int[] getValidNumbersOfArguments() {
    	IAnnotationBinding aBinding = getAnnotation(EGLValidNumberOfArgumentsAnnotationTypeBinding.getInstance());
    	if(aBinding != null) {
    		Object[] value = (Object[]) aBinding.getValue();
    		int[] result = new int[value.length];
    		for(int i = 0; i < value.length; i++) {
    			result[i] = ((Integer)value[i]).intValue();
    		}
    		return result;
    	}
        return new int[] {parameters.size()};
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#getQualifier()
     */
    public IDataBinding getQualifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IFunctionBinding#isStatic()
     */
    public boolean isStatic() {
        return isStatic;
    }
    
    public void setStatic(boolean isStatic) {
    	this.isStatic = isStatic;
    }
    
	public boolean isPrivate() {
		return isPrivate;
	}
	
    public void setPrivate(boolean isPrivate) {
    	this.isPrivate = isPrivate;
    }
    
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
    	this.isAbstract = isAbstract;
    }
	
	public boolean hasConverse() {
		return hasConverse;
	}
	
	public void setHasConverse(boolean hasConverse) {
		this.hasConverse = hasConverse;
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
        writeTypeBindingReference(out, returnType);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	returnType = readTypeBindingReference(in);
    }


	public boolean isFunctionBindingWithImplicitQualifier() {
		return false;
	}


	public IDataBinding getImplicitQualifier() {
		return null;
	}


	public IFunctionBinding getWrappedFunctionBinding() {
		return null;
	}
	
	public LibraryBinding getSystemLibrary() {
		return null;
	}
	
	public int getKind() {
		return FUNCTION_BINDING;
	}

	public boolean isValid() {
		return true;
	}

	public String[] getPackageName() {
		return null;
	}

	public IDataBinding findData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}

	public IDataBinding findPublicData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}

	public Map getSimpleNamesToDataBindingsMap() {
		return Collections.EMPTY_MAP;
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
		return false;
	}

	public boolean isPartBinding() {
		return false;
	}

	public ITypeBinding copyTypeBinding() {
		return null;
	}

	public ITypeBinding getBaseType() {
		return this;
	}
	
	public boolean isNullable() {
		return false;
	}
	
	public ITypeBinding getNullableInstance() {
		return null;
	}
	
	public String getPackageQualifiedName() {
		StringBuffer result = new StringBuffer();
    	String[] packageName = getPackageName();
    	if(packageName != null) {
    		for(int i = 0; i < packageName.length; i++) {
    			result.append(packageName[i]);
    			result.append('.');
    		}    		
    	}
    	result.append(getCaseSensitiveName());
    	return result.toString();
	}
	
	public boolean isTopLevelFunction() {
		return false;
	}

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}

	@Override
	public ITypeBinding getNonNullableInstance() {
		return this;
	}

	@Override
	public boolean isInstantiable() {
		return false;
	}
}
