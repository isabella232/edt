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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public abstract class Binding implements IBinding {
    
    
    List annotations = Collections.EMPTY_LIST;
    protected String caseSensitiveInternedName;
    private transient String caseInsensitiveInternedName;
    
    public Binding(String caseSensitiveInternedName) {
    	this.caseSensitiveInternedName = caseSensitiveInternedName;
    }
    
    protected Binding(Binding old) {
    	if (old.annotations == Collections.EMPTY_LIST) {
    		old.annotations = new ArrayList();
    	}
        annotations = old.annotations;
        caseSensitiveInternedName = old.caseSensitiveInternedName;
        caseInsensitiveInternedName = old.caseInsensitiveInternedName;
   }

    /**
     * Get an interned version of the Binding's name
     * @return the interned name
     */
    public String getName() {
    	if (caseSensitiveInternedName == null) {
    		return null;
    	}
    	
    	if(caseInsensitiveInternedName == null) {
    		caseInsensitiveInternedName = InternUtil.intern(caseSensitiveInternedName);
    	}
        return caseInsensitiveInternedName;
    }
    
    /**
     * Get a case sensitive version of the Binding's name
     * @return the case sensitive name
     */
    public String getCaseSensitiveName() {
    	return caseSensitiveInternedName;
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
        return false;
    }
    
    public boolean isAnnotationBinding() {
        return false;
    }
    
    public List getAnnotations() {
    	return annotations;
    }
    
    public void addAnnotation(IAnnotationBinding annotation) {
        if (annotations == Collections.EMPTY_LIST) {
            annotations = new ArrayList();
        }
        annotations.add(annotation);
    }
    
    public void addAnnotations(Collection annotations) {
    	if (this.annotations == Collections.EMPTY_LIST) {
            this.annotations = new ArrayList();
        }
        this.annotations.addAll(annotations);
	}
    
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType) {
	    return getAnnotation(annotationType, annotations);
     }

	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, int index) {
	    return getAnnotation(annotationType, annotations, index);
     }
	
	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, List list) {
	    IAnnotationBinding lastFound = null;
        for(Iterator iter = list.iterator(); iter.hasNext();) {
            IAnnotationBinding binding = (IAnnotationBinding) iter.next();
            if(!binding.isAnnotationField()) {
	            if(binding.getAnnotationType() == annotationType && !binding.isForElement()) {
	                lastFound = binding;
	            }
            }
        }
        return lastFound;
     }

	public IAnnotationBinding getAnnotation(IAnnotationTypeBinding annotationType, List list, int index) {
	    IAnnotationBinding lastFound = null;
        for(Iterator iter = list.iterator(); iter.hasNext();) {
            IAnnotationBinding binding = (IAnnotationBinding) iter.next();
            if(binding.getAnnotationType() == annotationType && binding.isForElement() && ((AnnotationBindingForElement)binding).getIndex() == index) {
                lastFound = binding;
            }
        }
        if (lastFound == null) {
            return getAnnotation(annotationType, list);
        }
        return lastFound;
     }
	
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName) {
	    return getAnnotation(packageName, annotationName, annotations);
     }

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, int index) {
	    return getAnnotation(packageName, annotationName, annotations, index);
     }
	
	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, List list) {
	    IAnnotationBinding lastFound = null;
	    if(list.size() > 0){
	    	String[] internedPackageName = InternUtil.intern(packageName);
	    
		    String internedAnnotationName = InternUtil.intern(annotationName);
	        for(Iterator iter = list.iterator(); iter.hasNext();) {
	            IAnnotationBinding binding = (IAnnotationBinding) iter.next();
	            
	            ITypeBinding annotationType = binding.getType();
				if(annotationType != null &&
				   annotationType.getPackageName() == internedPackageName &&
				   annotationType.getName() == internedAnnotationName &&
				   !binding.isForElement()) {
	                lastFound = binding;
	            }
	        }
	    }
        return lastFound;
     }

	public IAnnotationBinding getAnnotation(String[] packageName, String annotationName, List list, int index) {
	    IAnnotationBinding lastFound = null;
	    String[] internedPackageName = InternUtil.intern(packageName);
	    String internedAnnotationName = InternUtil.intern(annotationName);
        for(Iterator iter = list.iterator(); iter.hasNext();) {
            IAnnotationBinding binding = (IAnnotationBinding) iter.next();
            IAnnotationTypeBinding annotationType = binding.getAnnotationType();
            if(annotationType != null &&
               annotationType.getPackageName() == internedPackageName &&
               annotationType.getName() == internedAnnotationName &&
               binding.isForElement() &&
               ((AnnotationBindingForElement)binding).getIndex() == index) {
                lastFound = binding;
            }
        }
        if (lastFound == null) {
            return getAnnotation(packageName, annotationName, list);
        }
        return lastFound;
     }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        caseSensitiveInternedName = InternUtil.internCaseSensitive(caseSensitiveInternedName);
    }
        
    public byte[] getSerializedBytes() throws IOException
	{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
        return byteStream.toByteArray();
	}
    
    public InputStream getSerializedInputStream() throws IOException {
        return new ByteArrayInputStream(getSerializedBytes());
    }
    
    protected void writeTypeBindingReference(ObjectOutputStream out, ITypeBinding typeBinding) throws IOException {
        if(typeBinding != null && typeBinding != IBinding.NOT_FOUND_BINDING && typeBinding.isPartBinding()) {
            out.writeBoolean(true);
            out.writeInt(typeBinding.getKind());
            out.writeUnshared(typeBinding.getPackageName());
            out.writeUTF(typeBinding.getCaseSensitiveName());
        }
        else {
            out.writeBoolean(false);
            out.writeObject(typeBinding);
        }
    }
    
    protected ITypeBinding readTypeBindingReference(ObjectInputStream in) throws IOException, ClassNotFoundException {
        boolean isPart = in.readBoolean();
        if(isPart) {
            int partKind = in.readInt();
            String[] packageName = (String[]) in.readUnshared();
            if(packageName.length != 0) {packageName = InternUtil.intern(packageName);}
            String partName = InternUtil.internCaseSensitive(in.readUTF());
            return PartBinding.newPartBinding(partKind, packageName, partName);
        }
        else {
            return (ITypeBinding) in.readObject();
        }
    }
    
    public boolean isUsedTypeBinding() {
        return false;
    }
    
    protected ITypeBinding realizeTypeBinding(ITypeBinding typeBinding, IEnvironment environment) {
		if (Binding.isValidBinding(typeBinding)) {

			if (typeBinding.getKind() == ITypeBinding.FORM_BINDING) {
				FormBinding form = (FormBinding) typeBinding;
				if (form.getEnclosingFormGroup() != null) {
					FormGroupBinding fg = (FormGroupBinding) realizeTypeBinding(form.getEnclosingFormGroup(), environment);
					if (isValidBinding(fg)) {
						return fg.findForm(typeBinding.getName());
					}
				}
			}

			if (!typeBinding.isValid()) {
				if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
					typeBinding = realizeTypeBinding(((ArrayTypeBinding) typeBinding).getElementType(), environment);
					typeBinding = ArrayTypeBinding.getInstance(typeBinding);
				} else {

					IPartBinding partBinding = (IPartBinding) typeBinding;
					if (partBinding.getEnvironment() != null) {
						return partBinding.realize();
					} else {
						if (environment != null) {
							IPartBinding result = environment.getPartBinding(partBinding.getPackageName(), partBinding.getName());
							if (result != null && result != IBinding.NOT_FOUND_BINDING && typeBinding.isNullable()) {
								return result.getNullableInstance();
							} else {
								return result;
							}
						}
					}
				}
			}
		}
		return typeBinding;
	}
    
    protected ITypeBinding getTypeBinding(ITypeBinding typeBinding, IDataBinding dataBinding) {
        if (typeBinding == null || typeBinding == IBinding.NOT_FOUND_BINDING) {
            return null;
        }
        
        if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
            ArrayTypeBinding array = (ArrayTypeBinding) typeBinding;
            boolean isNullable = array.isNullable();
            ITypeBinding result = ArrayTypeBinding.getInstance(getTypeBinding(array.getElementType(), dataBinding));
            if(isNullable) {
            	result = result.getNullableInstance();
            }
            return result;
        }
        
        //TODO in the future, must handle ReferenceTypeBinding
        
        if (typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING) {
            DataItemBinding dataItem = (DataItemBinding) typeBinding;
            if (!dataItem.isValid) {
               dataItem = (DataItemBinding)dataItem.realize();
            }
            setDataItemReference(dataItem);
            copyAnnotations(dataItem, dataBinding);
            if (dataItem.getPrimitiveTypeBinding() != null) {
                return dataItem.getPrimitiveTypeBinding();
            }
            else {
                return PrimitiveTypeBinding.getInstance(Primitive.INT);
            }
        }
        
        return typeBinding;
    }
    
    public void setDataItemReference(DataItemBinding item) {
    	//default is to do nothing
    }
    
    
    private void copyAnnotations(DataItemBinding dataItem, IDataBinding dataBinding) {
        if (dataBinding != null) {
	        Iterator i = dataItem.getAnnotations().iterator();
	        while (i.hasNext()) {
	            IAnnotationBinding annotation = (IAnnotationBinding)i.next();
	            if (annotation.getAnnotationType().isApplicableFor(dataBinding)) {
	            	
	            	DataItemCopiedAnntotationBinding annotationBinding = new DataItemCopiedAnntotationBinding(annotation.getCaseSensitiveName(), dataBinding.getDeclaringPart(), annotation.getType());
	            	annotationBinding.addAnnotations(annotation.getAnnotations());
	            	annotationBinding.addFields(annotation.getAnnotationFields());
	            	
	                dataBinding.addAnnotation(annotation);
	            }
	        }
        }
    }
    
    public boolean isOpenUIStatementBinding() {
        return false;
    }
    
    public boolean isCallStatementBinding() {
        return false;
    }
    
	public boolean isExitStatementBinding() {
		return false;
	}

   public boolean isTransferStatementBinding() {
        return false;
    }
    
    public boolean isShowStatementBinding() {
        return false;
    }

	public byte[] getMD5HashKey() {
		try {
			byte[] bytes = getStructurallySignificantBytes();		
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected byte[] getStructurallySignificantBytes() throws IOException{
		return getSerializedBytes();
	}
	
	
	public static boolean isValidBinding(IBinding binding) {
		if (binding == null) {
			return false;
		}
		return binding.isValidBinding();
	}
	
	public boolean isValidBinding() {
		return true;
	}
	
	public void resetAnnotations() {
		annotations = Collections.EMPTY_LIST;
	}

	public void clear() {
		annotations = Collections.EMPTY_LIST;
	}
}
