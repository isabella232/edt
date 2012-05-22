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
package org.eclipse.edt.mof.serialization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EMemberContainer;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.EVisitor;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.impl.EObjectImpl;
import org.eclipse.edt.mof.impl.Slot;


@SuppressWarnings("unchecked")
public class ProxyEObject implements EObject, EField, EFunction {
	class ProxyReference {
		Slot[] slots;
		List list;
		int index;
		
		ProxyReference(Slot[] slots, int index) {
			this.slots = slots;
			this.index = index;
		}
		ProxyReference(List list, int index) {
			this.list = list;
			this.index = index;
		}
	}
	
	public Slot[] slots;
	public List<ProxyReference> references = new ArrayList<ProxyReference>();
	

	public void registerReference(EObject container, EField field) {
		Slot[] slots = ((EObjectImpl)container).getSlots();
		int i = ((EObjectImpl)container).calculateIndex(field);
		registerReference(slots, i);
	}
	
	public void registerReference(Slot[] slots, int index) {
		references.add(new ProxyReference(slots, index));
	}
	
	public void registerReference(List list, int index) {
		references.add(new ProxyReference(list, index));
	}
	
	public void updateReferences() {
		EClass type = (EClass)slots[0];
		EObject obj = type.newInstance(false);
		updateReferences(obj);
	}
	
	public void updateReferences(Object obj) {
		for (ProxyReference ref : references) {
			if (ref.slots != null) {
				ref.slots[ref.index].set(obj);
			} else {
				ref.list.set(ref.index, obj);
			}
		}
	}

	@Override
	public Object eGet(EField field) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eGet(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eSet(EField field, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eSet(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EClass getEClass() {
		return MofFactory.INSTANCE.getEObjectClass();
	}

	@Override
	public void accept(EVisitor visitor) {
		visitor.primVisit(this);
		visitor.endVisit(this);
	}

	@Override
	public boolean isNull(EField field) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEClass(EClass type) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean getContainment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTransient() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContainment(boolean value) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void setIsTransient(boolean value) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public EClass getDeclarer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public EMetadataObject getMetadata(String typeName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EMetadataObject getMetadata(EClass annType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<EMetadataObject> getMetadataList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EType getEType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeSignature() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNullable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIsNullable(boolean value) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void setEType(EType type) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public List<EParameter> getEParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EType getReturnType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSignature() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDeclarer(EMemberContainer container) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Object eGet(Integer index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eSet(Integer index, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object clone() {
		return this;
	}

	@Override
	public void addMember(EMember mbr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInitialValue(Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toStringHeader() {
		return "Instance of: ProxyEObject";
	}

	@Override
	public void visitChildren(EVisitor visitor) {
		throw new UnsupportedOperationException();		
	}


}
