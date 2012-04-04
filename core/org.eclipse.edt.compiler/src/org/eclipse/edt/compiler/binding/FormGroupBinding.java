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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


/**
 * @author Dave Murray
 */
public class FormGroupBinding extends PartBinding {

	private transient FormGroupDataBinding staticFormGroupDataBinding;
    List nestedForms = Collections.EMPTY_LIST;
    List usedForms = Collections.EMPTY_LIST;
    
	public FormGroupBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    /**
     * @return A list of FormBinding objects for the forms declared inside of
     *         and USEd by this formGroup.
     */
    public List getForms() {
		ArrayList result = new ArrayList();
		result.addAll(realizeUsedForms());
		result.addAll(nestedForms);
		
		return result;
    }
    
    private List realizeUsedForms() {
    	List list = new ArrayList();
    	Iterator i = usedForms.iterator();
    	while (i.hasNext()) {
    		FormBinding form = (FormBinding) i.next();
    		list.add(realizeTypeBinding(form, getEnvironment()));
    	}
    	return list;
    }
    
    /**
     * Adds a form binding to this form group binding. To avoid an unnecceasary
     * cast, this function accepts an ITypeBinding instead of a FormBinding. It
     * is up to the caller to verify that formBinding.getKind() == ITypeBinding.FORM_BINDING
     */
    public void addUsedForm(ITypeBinding formBinding) {
    	if(usedForms == Collections.EMPTY_LIST) {
    		usedForms = new ArrayList();
    	}
    	usedForms.add(formBinding);
    }
    
    /**
     * Adds a form binding to this form group binding. To avoid an unnecceasary
     * cast, this function accepts an ITypeBinding instead of a FormBinding. It
     * is up to the caller to verify that formBinding.getKind() == ITypeBinding.FORM_BINDING
     */
    public void addNestedForm(ITypeBinding formBinding) {
    	if(nestedForms == Collections.EMPTY_LIST) {
    		nestedForms = new ArrayList();
    	}
    	nestedForms.add(formBinding);
    }
    
	public int getKind() {
		return FORMGROUP_BINDING;
	}

	public void clear() {
		super.clear();
		nestedForms = Collections.EMPTY_LIST;
		usedForms = Collections.EMPTY_LIST;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected IDataBinding primFindData(String simpleName) {
		for(Iterator iter = getForms().iterator(); iter.hasNext();) {
			FormBinding nextForm = (FormBinding) iter.next();
			if(nextForm.getName() == simpleName) {
				return nextForm.getStaticFormDataBinding();
			}
		}
		
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public ITypeBinding findForm(String simpleName) {
		for(Iterator iter = getForms().iterator(); iter.hasNext();) {
			FormBinding nextForm = (FormBinding) iter.next();
			if(nextForm.getName() == simpleName) {
				return nextForm;
			}
		}
		
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public boolean isDeclarablePart() {
		return false;
	}
	
	public void setEnvironment(IEnvironment environment) {
        super.setEnvironment(environment);
        
        for (Iterator iter = nestedForms.iterator(); iter.hasNext();) {
            FormBinding form = (FormBinding) iter.next();
            form.setEnvironment(environment);
        }
    }
	
	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticFormGroupDataBinding();
	}
	
	public IDataBinding getStaticFormGroupDataBinding() {
	    if(staticFormGroupDataBinding == null) {
			staticFormGroupDataBinding = new FormGroupDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticFormGroupDataBinding;
	}
	
	public List getNestedForms() {
		return nestedForms;
	}

	public List getUsedForms() {
		return usedForms;
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}

}
