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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class FlexibleRecordBindingImpl extends FlexibleRecordBinding {

	private List fields = Collections.EMPTY_LIST;

    private transient List referencedRecords = Collections.EMPTY_LIST;

    public FlexibleRecordBindingImpl(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private FlexibleRecordBindingImpl(FlexibleRecordBindingImpl old) {
        super(old);

    	if (old.fields == Collections.EMPTY_LIST) {
    		old.fields = new ArrayList();
    	}

        if (old.referencedRecords == Collections.EMPTY_LIST) {
        	old.referencedRecords = new ArrayList();
        }

        fields = old.fields;
       referencedRecords = old.referencedRecords;

    }

    /**
     * @return A list of ClassFieldBinding objects representing the fields
     *         declared inside of this flexible record.
     */
    public List getDeclaredFields() {
        return fields;
    }
    
    /**
     * @return A list of classFieldBinding Objects representing the fields
     * 		   declared inside of this flexible record and super record 
     */
    public List getDeclaredFields(boolean includeDefaultSuperType){
    	if(!includeDefaultSuperType){
    		return(getDeclaredFields());
    	}

    	List retList = new LinkedList();
    	retList.addAll(fields);
    	
		//get supertype's declared data
		IPartBinding spBinding = this.getDefaultSuperType();
		if(spBinding instanceof FunctionContainerBinding){
			retList.addAll(((FunctionContainerBinding)spBinding).getDeclaredData(includeDefaultSuperType));
		}else if(spBinding instanceof ExternalTypeBinding){
			retList.addAll(((ExternalTypeBinding)spBinding).getDeclaredAndInheritedData());
		}
		
    	return retList;
    }

    public int getKind() {
        return FLEXIBLE_RECORD_BINDING;
    }

    public void addField(FlexibleRecordFieldBinding fieldBinding) {
        if (fields == Collections.EMPTY_LIST) {
            fields = new ArrayList();
        }
        fields.add(fieldBinding);
    }

    public void addField(FlexibleRecordFieldBinding fieldBinding, int index) {
        if (fields == Collections.EMPTY_LIST) {
            fields = new ArrayList();
        }
        fields.add(index, fieldBinding);
    }

    public void addReference(FlexibleRecordFieldBinding fieldBinding) {
        if (fieldBinding.typeBinding != null && fieldBinding.typeBinding != IBinding.NOT_FOUND_BINDING && fieldBinding.typeBinding.getBaseType().getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
            addReferencedRecord((FlexibleRecordBinding) fieldBinding.typeBinding.getBaseType());
        }
    }

    void addReferencedRecord(FlexibleRecordBinding record) {
        if (referencedRecords == Collections.EMPTY_LIST) {
            referencedRecords = new ArrayList();
        }
        if (!referencedRecords.contains(record)) {
            referencedRecords.add(record);
        }
    }

    public void clear() {
        super.clear();
        fields = Collections.EMPTY_LIST;
        referencedRecords = Collections.EMPTY_LIST;
    }

    public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
        // TODO Auto-generated method stub
        return false;
    }

	private String getResourceAssociationName() {
        return InternUtil.intern("resourceAssociation");
    }
	
	protected IDataBinding primFindData(String simpleName) {
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            IDataBinding binding = (IDataBinding) iter.next();
            if (binding.getName() == simpleName) {
                return binding;
            }
        }
        
		if(simpleName == getResourceAssociationName()) {
			if(BindingUtilities.hasResourceAssociation(this)) {
				return FixedRecordBinding.RESOURCEASSOCIATION;
			}
		}
		
		
        return IBinding.NOT_FOUND_BINDING;
    }

    public boolean containsReferenceTo(FlexibleRecordBinding record) {
        List processedRecords = new ArrayList();
        return containsReferenceTo(record, processedRecords);
    }

    protected List getReferencedRecords() {
        return referencedRecords;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(getReferencedRecords().size());
        Iterator i = getReferencedRecords().iterator();
        while (i.hasNext()) {
            FlexibleRecordBinding currentRecord = (FlexibleRecordBinding) i.next();
            writeTypeBindingReference(out, currentRecord);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        if (size == 0) {
            referencedRecords = Collections.EMPTY_LIST;
        } else {
            referencedRecords = new ArrayList();
            for (int i = 0; i < size; i++) {
                FlexibleRecordBinding currentRecord = (FlexibleRecordBinding) readTypeBindingReference(in);
                referencedRecords.add(currentRecord);
            }
        }
    }
    
    public boolean isDeclarablePart() {
    	
    	return !isAnnotationRecord();
	}

    public IDataBinding[] getFields() {
        return (IDataBinding[])getDeclaredFields().toArray(new IDataBinding[getDeclaredFields().size()]);
    }

	@Override
	public ITypeBinding primGetNullableInstance() {
		FlexibleRecordBindingImpl nullable = new FlexibleRecordBindingImpl(this);
		nullable.setNullable(true);
		return nullable;
	}
}
