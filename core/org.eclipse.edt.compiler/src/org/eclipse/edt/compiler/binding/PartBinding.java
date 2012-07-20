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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.EGLIsSystemPartAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public abstract class PartBinding extends TypeBinding implements IPartBinding {

    protected String[] packageName;

    protected transient IEnvironment environment;
    protected boolean isValid; 
    
    protected boolean isPrivate;
      
    public PartBinding(String[] packageName, String caseSensitiveInternedName) {
        super(caseSensitiveInternedName);
        this.packageName = packageName;
    }
    
    protected PartBinding(PartBinding old) {
    	super(old);
        packageName = old.packageName;
        environment = old.environment;
        isValid = old.isValid;        
        isPrivate = old.isPrivate;
   	
    }
    
    public boolean isPartBinding(){
    	return true;
    }
    
    public IEnvironment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(IEnvironment environment) {
        this.environment = environment;
    }
    
    public IPartBinding realize() {
    	
    	if (isNullable()) {
    		return (IPartBinding)environment.getPartBinding(packageName, getName()).getNullableInstance();
    	}
        return environment.getPartBinding(packageName, getName());
    }
    
    public boolean isValid() {
        return isValid;
    }
    
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
    
    public static IPartBinding newPartBinding(int kind, String[] packageName, String caseSensitivePartName) {
        switch(kind) {
        	case ITypeBinding.DELEGATE_BINDING:
        		return new DelegateBinding(packageName, caseSensitivePartName);
            case ITypeBinding.DATATABLE_BINDING:
                return new DataTableBinding(packageName, caseSensitivePartName);
            case ITypeBinding.EXTERNALTYPE_BINDING:
                return new ExternalTypeBinding(packageName, caseSensitivePartName);
            case ITypeBinding.FIXED_RECORD_BINDING:
                return new FixedRecordBindingImpl(packageName, caseSensitivePartName);
            case ITypeBinding.FLEXIBLE_RECORD_BINDING:
                return new FlexibleRecordBindingImpl(packageName, caseSensitivePartName);
            case ITypeBinding.FORM_BINDING:
                return new FormBinding(packageName, caseSensitivePartName);
            case ITypeBinding.FORMGROUP_BINDING:
                return new FormGroupBinding(packageName, caseSensitivePartName);
            case ITypeBinding.HANDLER_BINDING:
                return new HandlerBinding(packageName, caseSensitivePartName);
            case ITypeBinding.INTERFACE_BINDING:
                return new InterfaceBinding(packageName, caseSensitivePartName);
            case ITypeBinding.LIBRARY_BINDING:
                return new LibraryBinding(packageName, caseSensitivePartName);
            case ITypeBinding.PROGRAM_BINDING:
                return new ProgramBinding(packageName, caseSensitivePartName);
            case ITypeBinding.CLASS_BINDING:
                return new EGLClassBinding(packageName, caseSensitivePartName);
            case ITypeBinding.SERVICE_BINDING:
                return new ServiceBinding(packageName, caseSensitivePartName);
            case ITypeBinding.DATAITEM_BINDING:
                return new DataItemBinding(packageName, caseSensitivePartName);
            case ITypeBinding.FILE_BINDING:
            	return new FileBinding(packageName, caseSensitivePartName);
            case ITypeBinding.FUNCTION_BINDING:
            	return new TopLevelFunctionBinding(packageName, caseSensitivePartName);
            // TODO Is this what we want to do here?
            case ITypeBinding.ARRAYDICTIONARY_BINDING:
            	return ArrayDictionaryBinding.INSTANCE;
            case ITypeBinding.DICTIONARY_BINDING:
            	return DictionaryBinding.INSTANCE;
            case ITypeBinding.ENUMERATION_BINDING:
//            	return (EnumerationTypeBinding)EnumerationManager.getInstance().getEnumTypes().get(partName);
            	return new EnumerationTypeBinding(packageName, caseSensitivePartName);
            
            default:
                throw new RuntimeException("Unsupported kind: " + kind);
        }
    }
    
    public String[] getPackageName() {
        return packageName;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if(packageName.length != 0) packageName = InternUtil.intern(packageName);
    }

    public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
        // TODO check package name
        if(!(anotherTypeBinding instanceof PartBinding)) return false;
        
        PartBinding anotherPartBinding = (PartBinding) anotherTypeBinding;
        if(!packageName.equals(anotherPartBinding.packageName)) return false;
        if(!getName().equals(anotherPartBinding.getName())) return false;
        
        return true;
    }
    
    public ITypeBinding getBaseType() {
        return this;
    }
    
    public IPartSubTypeAnnotationTypeBinding getSubType() {
        IAnnotationBinding annotation = getSubTypeAnnotationBinding();
        if (annotation != null) {
            return (IPartSubTypeAnnotationTypeBinding) annotation.getAnnotationType();
        }
        return null;
    }

    public IAnnotationBinding getSubTypeAnnotationBinding() {
        Iterator i = getAnnotations().iterator();
        while (i.hasNext()) {
            IAnnotationBinding annotation = (IAnnotationBinding)i.next();
            IAnnotationTypeBinding annotationType = annotation.getAnnotationType();
			if (annotationType != null && annotationType.isPartSubType()) {
                return annotation;
            }
        }
        return null;
    }

    public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
        IAnnotationBinding result = super.getAnnotation(annotationType);
        if (result == null) {
            IAnnotationBinding subType = getSubTypeAnnotationBinding();
            if (subType != null) {
                return subType.getAnnotation(annotationType);
            }
        }
        return result;
    }
    
    public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
        IAnnotationBinding result = super.getAnnotation(packageName, annotationName);
        if (result == null) {
            IAnnotationBinding subType = getSubTypeAnnotationBinding();
            if (subType != null) {
                return subType.getAnnotation(packageName, annotationName);
            }
        }
        return result;
    }
    	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	public boolean isReference() {
		IPartSubTypeAnnotationTypeBinding subType = getSubType();
		if(subType != null) {
			IAnnotationBinding aBinding = getAnnotation(subType).getAnnotation(StereotypeAnnotationTypeBinding.getInstance());
			if(aBinding != null) {
				aBinding = (IAnnotationBinding) aBinding.findData("referenceType");
				if(IBinding.NOT_FOUND_BINDING != aBinding) {
					return ((Boolean) aBinding.getValue()).booleanValue();
				}
			}
		}
		return super.isReference();
	}
	
	public IPartBinding getDefaultSuperType() {
		IPartSubTypeAnnotationTypeBinding subType = getSubType();
		if(subType != null) {
			IAnnotationBinding aBinding = getAnnotation(subType).getAnnotation(StereotypeAnnotationTypeBinding.getInstance());
			if(aBinding != null) {
				aBinding = (IAnnotationBinding) aBinding.findData("DefaultSuperType");
				if(IBinding.NOT_FOUND_BINDING != aBinding) {
					if (aBinding.getValue() instanceof IPartBinding) {
						return (IPartBinding) aBinding.getValue();
					}
				}
			}
		}
		return null;
	}
	
	public boolean isSystemPart() {
		IAnnotationBinding aBinding = super.getAnnotation(EGLIsSystemPartAnnotationTypeBinding.getInstance());
		return aBinding != null && Boolean.YES == aBinding.getValue();
	}
	
	
	public StaticPartDataBinding getStaticPartDataBinding() {
		return null;
	}
	
    public List getDeclaredFunctions() {
		return null;
	}
    
    private boolean isNestedFunctionOrOverloadedFunctionSet(IDataBinding binding) {
    	if (!Binding.isValidBinding(binding)) {
    		return false;
    	}
    	
    	int kind = binding.getKind();
    	
    	return 
    		kind == IDataBinding.NESTED_FUNCTION_BINDING ||
    		kind == IDataBinding.OVERLOADED_FUNCTION_SET_BINDING;
    }
    
    private void addAllFunctionsToSet(OverloadedFunctionSet set, IDataBinding binding) {
    	if (!Binding.isValidBinding(binding)) {
    		return;
    	}
    	
    	if (binding.getKind() == IDataBinding.OVERLOADED_FUNCTION_SET_BINDING) {
    		OverloadedFunctionSet oldSet = (OverloadedFunctionSet)binding;
    		Iterator i = oldSet.getNestedFunctionBindings().iterator();
    		while (i.hasNext()) {
    			IDataBinding next = (IDataBinding)i.next();
    			set.addNestedFunctionBinding(next);
    		}
    	}
    	else {
        	if (binding.getKind() == IDataBinding.NESTED_FUNCTION_BINDING) {
        		set.addNestedFunctionBinding(binding);
        	}
    	}
    }
    
    
	public IDataBinding findData(String simpleName) {
		IDataBinding result = primFindData(simpleName);
		
		//if we found the data as a non-function (i.e. a field) just return it
		if (Binding.isValidBinding(result) && !isNestedFunctionOrOverloadedFunctionSet(result)) {
			return result;
		}
		
		if (getDefaultSuperType() != null) {
			IDataBinding superResult = getDefaultSuperType().findData(simpleName);

			if (Binding.isValidBinding(superResult)) {
				//We found data in our defaultSuperType! If it is not a function or function set, just return it
				if (result == IBinding.NOT_FOUND_BINDING || !isNestedFunctionOrOverloadedFunctionSet(superResult)) {
					return superResult;
				}
				
				//at this point, we found functions in this part and in our defaultSuperType. Merge them all together into a single
				//overloadedFunctionSet
		        OverloadedFunctionSet functionSet = new OverloadedFunctionSet();
		        functionSet.setName(result.getCaseSensitiveName());
				addAllFunctionsToSet(functionSet, result);
				addAllFunctionsToSet(functionSet, superResult);
				return functionSet;				
			}			
		}
		
		
		if (result == IBinding.NOT_FOUND_BINDING) {
			return super.findData(simpleName);
		}

		return result;
	}

	protected IDataBinding primFindData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}

	public IFunctionBinding findFunction(String simpleName) {
		IFunctionBinding result = primFindFunction(simpleName);
		if (result == IBinding.NOT_FOUND_BINDING && getDefaultSuperType() != null) {
			result = getDefaultSuperType().findFunction(simpleName);
		}
		
		if (result == IBinding.NOT_FOUND_BINDING) {
			return super.findFunction(simpleName);
		}

		return result;
	}

	protected IFunctionBinding primFindFunction(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}

}
