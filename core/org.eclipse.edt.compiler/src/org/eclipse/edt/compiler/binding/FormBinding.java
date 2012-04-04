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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;


/**
 * @author Dave Murray
 */
public class FormBinding extends PartBinding {
	
	private transient List formFields = Collections.EMPTY_LIST;
	private transient FormDataBinding staticFormDataBinding;
	private transient Map unqualifiedNamesToDataBindings;;
	private transient FormGroupBinding enclosingFormGroup;

	public FormBinding(String[] packageName, String simpleName) {
		this(packageName, simpleName, null);
	}
	
	public FormBinding(String[] packageName, String caseSensitiveInternedName, FormGroupBinding enclosingFormGroup) {
        super(packageName, caseSensitiveInternedName);
        this.enclosingFormGroup = enclosingFormGroup;
    }
    
    /**
     * @return A list of FormFieldBinding objects.
     */
    public List getFields() {
		return formFields;
    }
    
    public void addFormField(FormFieldBinding field) {
    	if(formFields == Collections.EMPTY_LIST) {
    		formFields = new ArrayList();
    	}
    	formFields.add(field);
    }
    
	public int getKind() {
		return FORM_BINDING;
	}

	public void clear() {
		super.clear();
		formFields = Collections.EMPTY_LIST;
		staticFormDataBinding = null;
		unqualifiedNamesToDataBindings = null;
	}
	
	protected IDataBinding primFindData(String simpleName) {
		for( Iterator iter = formFields.iterator(); iter.hasNext();) {
			FormFieldBinding nextField = (FormFieldBinding) iter.next();
			if(nextField.getName() == simpleName) {
				return nextField;
			}
		}
		return IBinding.NOT_FOUND_BINDING;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public IDataBinding getStaticFormDataBinding() {
	    if(staticFormDataBinding == null) {
			staticFormDataBinding = new FormDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticFormDataBinding;
	}
	
	public boolean isNestedForm() {
		return enclosingFormGroup != null;
	}
	
	public FormGroupBinding getEnclosingFormGroup() {
		return enclosingFormGroup;
	}
	
	public boolean isValid() {
		if(isNestedForm()) return true;
		return super.isValid();
	}

	public Map getSimpleNamesToDataBindingsMap() {
    	if(unqualifiedNamesToDataBindings == null) {
    		if(formFields == Collections.EMPTY_LIST) {
    			unqualifiedNamesToDataBindings = Collections.EMPTY_MAP;
    		}
    		else {
    			unqualifiedNamesToDataBindings = new HashMap();
    			for(Iterator iter = formFields.iterator(); iter.hasNext();) {
    				FormFieldBinding nextField = (FormFieldBinding) iter.next();
    				BindingUtilities.addToUnqualifiedBindingNameMap(unqualifiedNamesToDataBindings, null, nextField);
    			}
    		}
    	}    
    	return unqualifiedNamesToDataBindings;
	}
	
	public boolean isPrintForm() {
	    return AbstractBinder.annotationIs(getSubType(), new String[] {"egl", "ui", "text"}, "PrintForm");
	}

	public boolean isTextForm() {
	    return !isPrintForm();
	}
	
	public boolean isDeclarablePart() {
		return false;
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getConstantFields());
        out.writeObject(getVariableFields());
        out.writeBoolean(enclosingFormGroup != null);
        if(enclosingFormGroup != null) {
        	writeTypeBindingReference(out, enclosingFormGroup);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        getFields().addAll((List) in.readObject());
        getFields().addAll((List) in.readObject());
        boolean hasEnclosingFormGroup = in.readBoolean();
        if(hasEnclosingFormGroup) {
        	enclosingFormGroup = (FormGroupBinding) readTypeBindingReference(in);
        }
    }
	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticFormDataBinding();
	}
	
	private List getConstantFields() {
		List list = new ArrayList();
		Iterator i = getFields().iterator();
		while (i.hasNext()) {
			FormFieldBinding field = (FormFieldBinding) i.next();
			if (field.isConstant()) {
				list.add(field);
			}
		}
		return list;
	}
	
	private List getVariableFields() {
		List list = new ArrayList();
		Iterator i = getFields().iterator();
		while (i.hasNext()) {
			FormFieldBinding field = (FormFieldBinding) i.next();
			if (field.isVariable()) {
				list.add(field);
			}
		}
		return list;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}
	
}
