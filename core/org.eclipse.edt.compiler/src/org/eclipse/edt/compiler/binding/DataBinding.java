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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public abstract class DataBinding extends Binding implements IDataBinding {
    Map propertyOverrides = Collections.EMPTY_MAP;

    protected transient ITypeBinding typeBinding;

    private transient IPartBinding declarer;

    public DataBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName);
        this.declarer = declarer;
        //Temporarily set to typeBinding so this.typeBinding is not null in getTypeBinding()
        this.typeBinding = typeBinding;
        this.typeBinding = getTypeBinding(typeBinding, this);
    }

    public ITypeBinding getType() {
        return realizeTypeBinding(typeBinding, declarer != null ? declarer.getEnvironment() : null);
    }

    public void setType(ITypeBinding partBinding) {
        this.typeBinding = partBinding;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        if (shouldSerializeTypeBinding()) {
         	writeTypeBindingReference(out, typeBinding);
        }
    }

    protected boolean shouldSerializeTypeBinding() {
    	return true;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (shouldSerializeTypeBinding()) {
        	typeBinding = readTypeBindingReference(in);
        }
    }

    public boolean isDataBinding() {
        return true;
    }

    public IDataBinding copyDataBinding(HashMap itemMapping) {
        throw new UnsupportedOperationException("copy() not overriden for " + getClass().getName());
    }

    public IPartBinding getDeclaringPart() {
        return declarer;
    }

    public void setDeclarer(IPartBinding declarer) {
        this.declarer = declarer;
    }

    public boolean isDataBindingWithImplicitQualifier() {
        return false;
    }

    public IDataBinding getWrappedDataBinding() {
        return null;
    }

    public IDataBinding getImplicitQualifier() {
        return null;
    }

    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path) {
        if (path == null || path.length == 0) {
            return getAnnotation(annotationType);
        }
        IAnnotationBinding result = null;
        if (propertyOverrides.size() > 0) {
			String key = getPathString(path);
			List list = (List) propertyOverrides.get(key);
			if (list != null) {
				result = getAnnotation(annotationType, list);
			}
			if (result != null) {
				return result;
			}
		}
        
        if (path.length > 1) {
            IDataBinding binding = path[0];
            IDataBinding[] newPath = new IDataBinding[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, newPath.length);
            return binding.getAnnotationFor(annotationType, newPath);
        }
        else {
           return path[0].getAnnotation(annotationType);
        }
    }
    
    private String getPathString(IDataBinding[] path) {
    	StringBuffer buffer = new StringBuffer();
    	boolean first = true;
    	for (int i = 0; i < path.length; i++) {
			if (path[i] != null && path[i] != IBinding.NOT_FOUND_BINDING) {
				if (first) {
					first = false;
				}
				else {
					buffer.append(".");
				}
				if (path[i].getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
					buffer.append(((StructureItemBinding) path[i]).getParentQualifiedName());
				}
				else {
					buffer.append(path[i].getName());
				}
			}
		}
    	return InternUtil.intern(buffer.toString());
    }
    
    public IAnnotationBinding getAnnotationFor(String[] packageName, String annotationName, IDataBinding[] path) {
        if (path == null || path.length == 0) {
            return getAnnotation(packageName, annotationName);
        }
        
        if (propertyOverrides.size() > 0) {
            IAnnotationBinding result = null;
			String key = getPathString(path);
			List list = (List) propertyOverrides.get(key);
			if (list != null) {
				result = getAnnotation(packageName, annotationName, list);
			}
			if (result != null) {
				return result;
			}
		}
        
        if (path.length > 1) {
            IDataBinding binding = path[0];
            IDataBinding[] newPath = new IDataBinding[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, newPath.length);
            return binding.getAnnotationFor(packageName, annotationName, newPath);
        }
        else {
           return path[0].getAnnotation(packageName, annotationName);
        }
    }
    
    public List getAnnotationsFor(IDataBinding[] path) {
        if (path == null || path.length == 0) {
            return getAnnotations();
        }
        
		List result = new ArrayList();
        if (propertyOverrides.size() > 0) {
			String key = getPathString(path);
			List list = (List) propertyOverrides.get(key);
			if (list != null) {
				result = list;
			}
		}
        
        if (path.length > 1) {
            IDataBinding binding = path[0];
            IDataBinding[] newPath = new IDataBinding[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, newPath.length);
            addNonExisting(binding.getAnnotationsFor(newPath), result);
            return result;
        }
        else {
        	addNonExisting(path[0].getAnnotations(), result);
           return result;
        }
    }
    
    private void addNonExisting(List sourceAnnotations, List targetAnnotations) {
    	Set annotationTypesInTargetList = new HashSet();
    	for(Iterator iter = targetAnnotations.iterator(); iter.hasNext();) {
    		annotationTypesInTargetList.add(((IAnnotationBinding) iter.next()).getType());
    	}
    	for(Iterator iter = sourceAnnotations.iterator(); iter.hasNext();) {
    		IAnnotationBinding next = (IAnnotationBinding) iter.next();
    		if(!contains(annotationTypesInTargetList, next.getType())) {
    			targetAnnotations.add(next);
    		}
    	}
    }
    
    private boolean contains(Collection listOfTypes, ITypeBinding type) {
		for(Iterator iter = listOfTypes.iterator(); iter.hasNext();) {
			ITypeBinding nextType = (ITypeBinding) iter.next();
			if(nextType.getPackageName() == type.getPackageName() &&
			   nextType.getName() == type.getName()) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IBinding#getAnnotation(org.eclipse.edt.compiler.binding.IAnnotationTypeBinding)
     */
    public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
        IAnnotationBinding result = super.getAnnotation(annotationType);
        if (result == null && getType() != null && getType() != IBinding.NOT_FOUND_BINDING && getType().getBaseType() != null && getType().getBaseType() != IBinding.NOT_FOUND_BINDING ) {
            return getType().getBaseType().getAnnotation(annotationType);
        }
        return result;
    }
    
    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path, int index) {
        if (path == null || path.length == 0) {
            return getAnnotation(annotationType, index);
        }
        
		IAnnotationBinding result = null;
        if (propertyOverrides.size() > 0) {
			String key = getPathString(path);
			List list = (List) propertyOverrides.get(key);
			if (list != null) {
				result = getAnnotation(annotationType, list, index);
				if (result == null) {
					result = getAnnotation(annotationType, list);
				}
			}
			if (result != null) {
				return result;
			}
		}
        
        if (path.length > 1) {
            IDataBinding binding = path[0];
            IDataBinding[] newPath = new IDataBinding[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, newPath.length);
            result = binding.getAnnotationFor(annotationType, newPath, index);
            if (result == null) {
                return binding.getAnnotationFor(annotationType, newPath);
            }
            return result;
        }
        else {
           result =  path[0].getAnnotation(annotationType, index);
           if (result == null) {
               return  path[0].getAnnotation(annotationType);
           }
           return result;
        }
    }
    
    public IAnnotationBinding getAnnotationFor(String[] packageName, String annotationName, IDataBinding[] path, int index) {
        if (path == null || path.length == 0) {
            return getAnnotation(packageName, annotationName, index);
        }
        IAnnotationBinding result = null;
        
        if (propertyOverrides.size() > 0) {
			String key = getPathString(path);
			List list = (List) propertyOverrides.get(key);
			if (list != null) {
				result = getAnnotation(packageName, annotationName, list, index);
				if (result == null) {
					result = getAnnotation(packageName, annotationName, list);
				}
			}
			if (result != null) {
				return result;
			}
		}
        
        if (path.length > 1) {
            IDataBinding binding = path[0];
            IDataBinding[] newPath = new IDataBinding[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, newPath.length);
            result = binding.getAnnotationFor(packageName, annotationName, newPath, index);
            if (result == null) {
                return binding.getAnnotationFor(packageName, annotationName, newPath);
            }
            return result;
        }
        else {
           result =  path[0].getAnnotation(packageName, annotationName, index);
           if (result == null) {
               return  path[0].getAnnotation(packageName, annotationName);
           }
           return result;
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IBinding#getAnnotation(org.eclipse.edt.compiler.binding.IAnnotationTypeBinding)
     */
    public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
        IAnnotationBinding result = super.getAnnotation(packageName, annotationName);
        if (result == null && getType() != null && getType() != IBinding.NOT_FOUND_BINDING && getType().getBaseType() != null && getType().getBaseType() != IBinding.NOT_FOUND_BINDING ) {
            return getType().getBaseType().getAnnotation(packageName, annotationName);
        }
        return result;
    }
    
    public void addAnnotation(IAnnotationBinding annotation) {
    	if(annotation.isAnnotationField()) {
    		/*
    		 * A field in one of my type's stereotypes is being overriden. Need to add a
    		 * new stereotype annotation that has the same fields as the old one, plus
    		 * the overriden field.
    		 */
    		IAnnotationBinding myStereotype = getAnnotation(annotation.getEnclosingAnnotationType());
    		IAnnotationBinding newStereotype = new AnnotationBinding(myStereotype.getCaseSensitiveName(), declarer, myStereotype.getType());
    		for(Iterator iter = myStereotype.getAnnotationFields().iterator(); iter.hasNext();) {
    			newStereotype.addField((IAnnotationBinding) iter.next());
    		}
    		newStereotype.addField(annotation);
    		super.addAnnotation(newStereotype);
    	}
    	else {
    		super.addAnnotation(annotation);
    	}
    }

    public void addAnnotation(IAnnotationBinding annotation, IDataBinding[] path) {
        if (path == null || path.length == 0) {
            addAnnotation(annotation);
            return;
        }
        
        addAnnotation(annotation, getPathString(path));
    }
 
    public void addAnnotation(IAnnotationBinding annotation, String path) {
        if (path == null) {
            addAnnotation(annotation);
            return;
        }
        if (propertyOverrides == Collections.EMPTY_MAP) {
            propertyOverrides = new HashMap();
        }
        List list = (List)propertyOverrides.get(path);
        if (list == null) {
            list = new ArrayList();
            propertyOverrides.put(path, list);
        }
        list.add(annotation);
    }

    public IDataBinding findData(String simpleName) {
        if (getType() == null || getType() == IBinding.NOT_FOUND_BINDING) {
            return IBinding.NOT_FOUND_BINDING;
        }
        if (getType().isDynamicallyAccessible()) {
            return new DynamicDataBinding(InternUtil.internCaseSensitive(simpleName), getDeclaringPart());
        }
        else {
            IDataBinding result = getType().getBaseType().findData(simpleName);
            if (result != IBinding.NOT_FOUND_BINDING) {
                return result;
            }
            if (isFixedRecordType()) {
                FixedRecordBinding recordBinding =  (FixedRecordBinding) getType();
                result = (IDataBinding)recordBinding.getSimpleNamesToDataBindingsMap().get(simpleName);
                if(result != null) return result;
            }
            return IBinding.NOT_FOUND_BINDING;
        }
    }
    
    private boolean isFixedRecordType() {
        ITypeBinding type = getType();
        if (type == null || getType() == IBinding.NOT_FOUND_BINDING) {
            return false;
        }
        if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
            return true;
        }
        return false;
    }

	public boolean isReadOnly() {
		return false;
	}
	
	public boolean isVariable() {
	    return false;
	}

	public Map getPropertyOverrides() {
		return propertyOverrides;
	}
	
	public boolean isStaticPartDataBinding() {
		return false;
	}
}
