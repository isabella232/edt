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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.AnnotationBinding;
import org.eclipse.edt.compiler.binding.AnnotationBindingForElement;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartFoundButNotAnnotationRecordAnnotationBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.binding.annotationType.EGLSystemConstantAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * @author Harmon
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AnnotationLeftHandScope extends Scope {

    Scope scopeToUseWhenResolving;

    public IBinding getBindingBeingAnnotated() {
        return bindingBeingAnnotated;
    }

    public IBinding getBindingToHoldAnnotation() {
        return bindingToHoldAnnotation;
    }

    public int getIndex() {
        return index;
    }

    IBinding bindingBeingAnnotated;

    IBinding bindingToHoldAnnotation;

    IAnnotationBinding notApplicableBinding;

    ITypeBinding typeOfBindingBeingAnnotated;

    IDataBinding[] path;

    int index;
    
    boolean annotationFoundUsingThisScope;
    
    IPartBinding partBinding;

	boolean withinAnnotationExpression;
	
	boolean withinNewExpression;
	
	boolean resolveToAnnotations = true;

    /**
     * @param parentScope
     */
    public AnnotationLeftHandScope(Scope parentScope, IBinding bindingBeingAnnotated, ITypeBinding typeOfBindingBeingAnnotated,
            IBinding bindingToHoldAnnotation, int index, IPartBinding partBinding) {
        super(parentScope);
        this.scopeToUseWhenResolving = parentScope;
        this.bindingBeingAnnotated = bindingBeingAnnotated;
        this.bindingToHoldAnnotation = bindingToHoldAnnotation;
        this.index = index;
        this.typeOfBindingBeingAnnotated = typeOfBindingBeingAnnotated;
        this.partBinding = partBinding;
    }

    public IDataBinding findData(String simpleName) {
    	if(!withinNewExpression) {
	    	IDataBinding fieldWithinStereotype = getFieldWithinStereotype(simpleName);
	        if(fieldWithinStereotype != null) {
	        	return fieldWithinStereotype;
	        }
    	}
        
    	if(!withinAnnotationExpression && typeOfBindingBeingAnnotated != null) {
	    	IDataBinding fieldWithinTarget = typeOfBindingBeingAnnotated.getBaseType().findData(simpleName);
	    	if(fieldWithinTarget != IBinding.NOT_FOUND_BINDING) {
	    		annotationFoundUsingThisScope = fieldWithinTarget.isAnnotationBinding();
	    		return fieldWithinTarget;
	    	}
    	}
    	
        annotationFoundUsingThisScope = false;
        notApplicableBinding = null;
        
        if(resolveToAnnotations) {
	        IAnnotationTypeBinding typeBinding = AnnotationTypeManager.getAnnotationType(simpleName);
	        if (typeBinding != null) {
	            if (isApplicableFor(typeBinding, bindingBeingAnnotated, index)) {
	                annotationFoundUsingThisScope = true;
	                AnnotationBinding annotationBinding;
	                if (index < 0) {
	                    annotationBinding = new AnnotationBinding(typeBinding.getCaseSensitiveName(), partBinding, typeBinding);
	                } else {
	                    annotationBinding = new AnnotationBindingForElement(typeBinding.getCaseSensitiveName(), partBinding, typeBinding, index);
	                }
	                return annotationBinding;
	            } else {
	                IDataBinding result = getScopeToUseWhenResolving().findData(simpleName);
	
	                if (result == IBinding.NOT_FOUND_BINDING || result == IAnnotationBinding.NOT_APPLICABLE_ANNOTATION_BINDING) {
	                    notApplicableBinding = new AnnotationBindingForElement(typeBinding.getCaseSensitiveName(), null, typeBinding, index);
	                    return IAnnotationBinding.NOT_APPLICABLE_ANNOTATION_BINDING;
	                }
	                return result;
	            }
	        }
        }
        
        boolean foundPartButNotAnnotationRecord = false;
        ITypeBinding type = null;
        
        if(resolveToAnnotations) {
	        type = getScopeToUseWhenResolving().findType(simpleName);
	        if(IBinding.NOT_FOUND_BINDING != type && type.isPartBinding() && !((IPartBinding) type).isValid()) {
	        	type = ((IPartBinding) type).realize();
	        }
	        if(IBinding.NOT_FOUND_BINDING != type) {
	        	if(type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING  && ((IPartBinding) type).getSubType() == AnnotationAnnotationTypeBinding.getInstance()) {
		        	AnnotationTypeBindingImpl annotationTypeBindingImpl = new AnnotationTypeBindingImpl((FlexibleRecordBinding) type, partBinding);
		        	if (isApplicableFor(annotationTypeBindingImpl, bindingBeingAnnotated, index)) {
			        	annotationFoundUsingThisScope = true;
			        	AnnotationBinding annotationBinding;	        	
						if (index < 0) {
			                annotationBinding = new AnnotationBinding(type.getCaseSensitiveName(), partBinding, annotationTypeBindingImpl);
			            } else {
			                annotationBinding = new AnnotationBindingForElement(type.getCaseSensitiveName(), partBinding, annotationTypeBindingImpl, index);
			            }
			            return annotationBinding;
		        	}
		        	else {
		        		IDataBinding result = getScopeToUseWhenResolving().findData(simpleName);
		        		
		        		if(IBinding.NOT_FOUND_BINDING != result && result != null &&
		        			(IDataBinding.ENUMERATION_BINDING == result.getKind() ||
		        			 IDataBinding.NESTED_FUNCTION_BINDING == result.getKind())) {
		        			result = IBinding.NOT_FOUND_BINDING;
		        		}
		        		
		        		if (result == IBinding.NOT_FOUND_BINDING || result == IAnnotationBinding.NOT_APPLICABLE_ANNOTATION_BINDING) {
		        			result = getFieldWithinStereotype(simpleName);
		        		}
		
		                if (result == null) {
		                    notApplicableBinding = new AnnotationBindingForElement(type.getCaseSensitiveName(), partBinding, annotationTypeBindingImpl, index);
		                    return IAnnotationBinding.NOT_APPLICABLE_ANNOTATION_BINDING;
		                }
		                return result;
		        	}
	        	}
	        	else {
	        		foundPartButNotAnnotationRecord = true;
	        	}
	        }
        }
        
        IDataBinding result = getScopeToUseWhenResolving().findData(simpleName);
        
        if(result == IBinding.NOT_FOUND_BINDING && foundPartButNotAnnotationRecord) {
        	return new PartFoundButNotAnnotationRecordAnnotationBinding(simpleName, null, type);
        }
        
        return result; 
    }

	private IDataBinding getFieldWithinStereotype(String simpleName) {
		if(!withinAnnotationExpression && typeOfBindingBeingAnnotated != null) {
	        IDataBinding fieldWithinStereotype = findInStereotypes(getStereotypes(typeOfBindingBeingAnnotated), simpleName);
	    	if(fieldWithinStereotype != IBinding.NOT_FOUND_BINDING) {
	    		annotationFoundUsingThisScope = true;
	    		return fieldWithinStereotype;
	    	}
	    	ITypeBinding baseType = typeOfBindingBeingAnnotated.getBaseType();
	    	if (typeOfBindingBeingAnnotated != baseType && baseType != null ) {
		        fieldWithinStereotype = findInStereotypes(getStereotypes(baseType), simpleName);
		    	if(fieldWithinStereotype != IBinding.NOT_FOUND_BINDING) {
		    		annotationFoundUsingThisScope = true;
		    		return fieldWithinStereotype;
		    	}
	    	}
        }
		return null;
	}

    private IDataBinding findInStereotypes(List stereotypes, String fieldName) {
		for(Iterator iter = stereotypes.iterator(); iter.hasNext();) {
			IDataBinding result = ((ITypeBinding) iter.next()).findData(fieldName);
			if(IBinding.NOT_FOUND_BINDING != result) {
				return result;
			}
		}
		return IBinding.NOT_FOUND_BINDING;
	}

	private List getStereotypes(ITypeBinding type) {
		List result = new ArrayList();
		for(Iterator iter = type.getAnnotations().iterator(); iter.hasNext();) {
			IBinding next = (IBinding) iter.next();
			if(next.isAnnotationBinding()) {
				IAnnotationBinding aBinding = (IAnnotationBinding) next;
				IAnnotationTypeBinding aType = (IAnnotationTypeBinding) aBinding.getType();
				if(aType != null && aType.isPartSubType()) {
					result.add(aBinding.getType());
				}
			}
		}
		return result;
	}

	private boolean isApplicableFor(IAnnotationTypeBinding annotationType, IBinding binding) {
		if(!withinAnnotationExpression) {
			//For simple names on the left side of an assignment, resolve to ordering or caseSensitive
			//only when the target is a dictionary type. When the target is an arrayDictionary type,
			//do not resolve to any annotations.
			if(binding != null && IBinding.NOT_FOUND_BINDING != binding) {
				ITypeBinding type = null;
				if(binding.isTypeBinding()) {
					type = (ITypeBinding) binding; 
				}
				else if(binding.isDataBinding()) {
					 type = ((IDataBinding) binding).getType();
				}
				
				if(type != null) {
					switch(type.getKind()) {
						case ITypeBinding.DICTIONARY_BINDING:
							return annotationType == EGLSystemConstantAnnotationTypeBinding.getInstance() ||
								   AbstractBinder.annotationIs(annotationType, new String[] {"eglx", "lang"}, IEGLConstants.PROPERTY_ORDERING) ||
								   AbstractBinder.annotationIs(annotationType, new String[] {"eglx", "lang"}, IEGLConstants.PROPERTY_CASESENSITIVE);
						case ITypeBinding.ARRAYDICTIONARY_BINDING:
							return false;
					}
				}
			}
		}
        return (annotationType.isApplicableFor(binding));
    }

    public boolean isApplicableFor(IAnnotationTypeBinding annotationType, IBinding binding, int index) {
        boolean result = isApplicableFor(annotationType, binding);
        if (index < 0) {
            return result;
        }
        return result && annotationType.supportsElementOverride();
    }

    public IFunctionBinding findFunction(String simpleName) {
        return getScopeToUseWhenResolving().findFunction(simpleName);
    }

    public IPackageBinding findPackage(String simpleName) {
        return getScopeToUseWhenResolving().findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
    	/*
    	 * There should not be a reason to locate a part when binding the left hand side
    	 * of an annotation assignment. Returning NOT_FOUND_BINDING, so in strange cases
    	 * like:
    	 * Library lib ... end
    	 * Record rec {lib = 10} ... end
    	 * we issue error "lib cannot be resolved" instead of "types lib and int are not
    	 * assignment compatible"
    	 */
    	return IBinding.NOT_FOUND_BINDING;
    }

    public AnnotationLeftHandScope getTopLevelAnnotationLeftHandScope() {

        if (getParentScope().isAnnotationLeftHandScope()) {
            return ((AnnotationLeftHandScope) getParentScope()).getTopLevelAnnotationLeftHandScope();
        }
        return this;

    }

    public boolean isAnnotationLeftHandScope() {
        return true;
    }

    public IAnnotationBinding getNotApplicableBinding() {
        return notApplicableBinding;
    }
    
    public void resetNotApplicableBinding() {
    	notApplicableBinding = null;
    }
    

    public Scope getScopeToUseWhenResolving() {
        return scopeToUseWhenResolving;
    }

    public void setScopeToUseWhenResolving(Scope scopeToUseWhenResolving) {
        this.scopeToUseWhenResolving = scopeToUseWhenResolving;
    }

    public ITypeBinding getTypeOfBindingBeingAnnotated() {
        return typeOfBindingBeingAnnotated;
    }

    public IDataBinding[] getPath() {
        return path;
    }

    public void setPath(IDataBinding[] path) {
        this.path = path;
    }
    
    public void setAnnotationFoundUsingThisScope(boolean b) {
        annotationFoundUsingThisScope = b;
    }
    
    public boolean isAnnotationFoundUsingThisScope() {
        return annotationFoundUsingThisScope;
    }
    
    public void setWithinAnnotationExpression(boolean b) {
    	withinAnnotationExpression = b;
    }
    
    public void setWithinNewExpression(boolean b) {
    	withinNewExpression = b;
    }

	public void setNotApplicableBinding(IAnnotationBinding annotationBinding) {
		notApplicableBinding = annotationBinding;
	}
	
	public void setResolveToAnnotations(boolean b) {
		resolveToAnnotations = b; 
	}

	public boolean isWithinNewExpression() {
		return withinNewExpression;
	}
}
