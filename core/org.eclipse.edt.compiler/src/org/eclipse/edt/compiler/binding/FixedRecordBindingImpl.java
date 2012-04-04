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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.egl.utils.InternUtil;


public class FixedRecordBindingImpl extends FixedRecordBinding {

    public FixedRecordBindingImpl(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private FixedRecordBindingImpl(FixedRecordBindingImpl old) {
        super(old);
    }

	private String getResourceAssociationName() {
        return InternUtil.intern("resourceAssociation");
    }
    
    public int getKind() {
		return FIXED_RECORD_BINDING;
	}

	public void clear() {
		super.clear();
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public ITypeBinding copyTypeBinding() {
		FixedRecordBinding newRecord = new FixedRecordBindingImpl(packageName, caseSensitiveInternedName);
		if(structureItems == Collections.EMPTY_LIST) {
			newRecord.structureItems = Collections.EMPTY_LIST;
		}
		else {
			newRecord.structureItems = new ArrayList();
			HashMap itemMapping = new HashMap();
			for( Iterator iter = structureItems.iterator(); iter.hasNext(); ) {
				DataBinding newItem = (DataBinding) ((StructureItemBinding) iter.next()).copyDataBinding(itemMapping);
				newItem.setDeclarer(this);
				newRecord.structureItems.add(newItem);
			}
			

			Iterator i = newRecord.getStructureItems().iterator();
			while (i.hasNext()) {
				copyResolvedAnnotationValues((StructureItemBinding) i.next(), itemMapping);
			}
		}
		
		newRecord.setNullable(isNullable());
		return newRecord;
	}
	
	private void copyResolvedAnnotationValues(StructureItemBinding siBinding, HashMap itemMappings) {
		//For annotation values that point to other structureItemBindings, we must update the anntoation
		//values to point to the copied version of the structure items
		switchResolovedAnnotationValues(siBinding, itemMappings);
		List list = siBinding.getChildren();
		Iterator i = list.iterator();
		while (i.hasNext()) {
			StructureItemBinding item = (StructureItemBinding)i.next();
			copyResolvedAnnotationValues(item, itemMappings);
		}
	}
	
	private void switchResolovedAnnotationValues(StructureItemBinding siBinding, HashMap itemMappings) {
		List anns = siBinding.getAnnotations();
		Iterator i = anns.iterator();
		HashMap oldAnnToCopyAnnMap = new HashMap();
		while (i.hasNext()) {
			
			AnnotationBinding origAnn = (AnnotationBinding)i.next();
			//must make a copy of the ann
			AnnotationBinding copyAnn = origAnn.createCopy();
			
			List fields = copyAnn.getAnnotationFields();
			Iterator j = fields.iterator();
			while (j.hasNext()) {
				AnnotationFieldBinding field = (AnnotationFieldBinding) j.next();
				if (itemMappings.containsKey(field.getValue())) {
					field.setValue(itemMappings.get(field.getValue()), null, null, null, false);
					oldAnnToCopyAnnMap.put(origAnn, copyAnn);
				}
			}
		}
		
		//now copy any changed annotations back into the item
		i = oldAnnToCopyAnnMap.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			int index = anns.indexOf(key);
			if (index >=0) {
				anns.remove(key);
				anns.add(index, oldAnnToCopyAnnMap.get(key));
			}
		}
	}
	
	protected IDataBinding primFindData(String simpleName) {
		IDataBinding result = super.primFindData(simpleName);
		
		if(result == IBinding.NOT_FOUND_BINDING) {
			if(simpleName == getResourceAssociationName()) {
				if(BindingUtilities.hasResourceAssociation(this)) {
					return RESOURCEASSOCIATION;
				}
			}
		}
		
		return result;
	}
	
	public boolean isDeclarablePart() {
		return true;
	}

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IRecordBinding#getFields()
     */
    public IDataBinding[] getFields() {
        return (IDataBinding[])getStructureItems().toArray(new IDataBinding[getStructureItems().size()]);
    }   
    
	@Override
	public ITypeBinding primGetNullableInstance() {
		FixedRecordBindingImpl nullable = new FixedRecordBindingImpl(this);
		nullable.setNullable(true);
		return nullable;
	}

}
