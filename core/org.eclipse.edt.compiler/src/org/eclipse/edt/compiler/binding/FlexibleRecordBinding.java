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

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;

/**
 * @author Dave Murray
 */
public abstract class FlexibleRecordBinding extends PartBinding implements IRecordBinding {
	
	public abstract List getDeclaredFields();
	public abstract List getDeclaredFields(boolean includeDefaultSuperType);
	public abstract boolean containsReferenceTo(FlexibleRecordBinding record);
	public abstract void addField(FlexibleRecordFieldBinding fieldBinding);
	public abstract void addField(FlexibleRecordFieldBinding fieldBinding, int index);
	public abstract void addReference(FlexibleRecordFieldBinding fieldBinding);
	abstract void addReferencedRecord(FlexibleRecordBinding record);
	
	public FlexibleRecordBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
	
	protected FlexibleRecordBinding(FlexibleRecordBinding old) {
		super(old);
	}
	
	protected abstract List getReferencedRecords();
	
	private boolean primContainsReferenceTo(FlexibleRecordBinding record, List processedRecords, FlexibleRecordBinding currentRecord) {
        if (currentRecord == record) {
            return true;
        }
        if (!processedRecords.contains(currentRecord)) {
            processedRecords.add(currentRecord);
            if (((FlexibleRecordBinding) currentRecord).containsReferenceTo(record, processedRecords)) {
                return true;
            }
        }
        return false;
    }
	
    protected boolean containsReferenceTo(FlexibleRecordBinding record, List processedRecords) {
        Iterator i = getReferencedRecords().iterator();
        while (i.hasNext()) {
            FlexibleRecordBinding currentRecord = (FlexibleRecordBinding) i.next();
            if (primContainsReferenceTo(record, processedRecords, currentRecord)) {
                return true;
            }
            
            FlexibleRecordBinding resolvedCurrentRecord = (FlexibleRecordBinding) realizeTypeBinding(currentRecord, getEnvironment());
            if (resolvedCurrentRecord != currentRecord) {
                if (primContainsReferenceTo(record, processedRecords, resolvedCurrentRecord)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isAnnotationRecord() {
    	return (getSubType() == AnnotationAnnotationTypeBinding.getInstance() || getSubType() == StereotypeAnnotationTypeBinding.getInstance());

    }
}
