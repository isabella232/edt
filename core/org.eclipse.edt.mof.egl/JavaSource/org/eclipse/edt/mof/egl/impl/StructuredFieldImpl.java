/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.impl;

import java.util.List;

import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ElementAnnotations;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.StructuredContainer;
import org.eclipse.edt.mof.egl.StructuredField;


public class StructuredFieldImpl extends FieldImpl implements StructuredField {
	private static int Slot_elementAnnotations=0;
	private static int Slot_children=1;
	private static int Slot_parent=2;
	private static int Slot_occurs=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + FieldImpl.totalSlots();
	}
	
	static {
		int offset = FieldImpl.totalSlots();
		Slot_elementAnnotations += offset;
		Slot_children += offset;
		Slot_parent += offset;
		Slot_occurs += offset;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ElementAnnotations> getElementAnnotations() {
		return (List<ElementAnnotations>)slotGet(Slot_elementAnnotations);
	}
	
	@Override
	public List<Annotation> getElementAnnotations(int index) {
		if (index > getActualOccurs())
			return null;
		else {
			List<Annotation> anns = null;
			for (ElementAnnotations elemAnns : getElementAnnotations()) {
				if (elemAnns.getIndex() == index) {
					anns = elemAnns.getAnnotations();
					break;
				}
			}
			if (anns == null) {
				ElementAnnotations elemAnns = IrFactory.INSTANCE.createElementAnnotations();
				elemAnns.setIndex(index);
				anns = elemAnns.getAnnotations();
				getElementAnnotations().add(elemAnns);
			}
			return anns;
		}
	}
	
	@Override
	public Annotation getElementAnnotation(String typeName, int index) {
		Annotation ann = null;
		if (index <= getOccurs()) {
			ElementAnnotations anns = null;
			for (ElementAnnotations elemAnns : getElementAnnotations()) {
				if (elemAnns.getIndex() == index) {
					anns = elemAnns;
					break;
				}
			}
			if (anns != null) {
				ann = anns.getAnnotation(typeName);
			}
		}
		return ann;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<StructuredField> getChildren() {
		return (List<StructuredField>)slotGet(Slot_children);
	}
	
	@Override
	public StructuredField getParent() {
		return (StructuredField)slotGet(Slot_parent);
	}
	
	@Override
	public void setParent(StructuredField value) {
		slotSet(Slot_parent, value);
	}
	
	@Override
	public Integer getOccurs() {
		return (Integer)slotGet(Slot_occurs);
	}
	
	@Override
	public void setOccurs(Integer value) {
		slotSet(Slot_occurs, value);
	}
	
	
	@Override
	public Integer getActualOccurs() {
		if (getParent() == null) {
			return getOccurs();
		}
		else {
			return getParent().getActualOccurs();
		}
	}
	
	@Override
	public void addChild(StructuredField child) {
		getChildren().add(child);
		child.setParent(this);
	}

	@Override
	public int getSizeInBytes() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void collectAllLeafItems(List<StructuredField> list) {
		if (getChildren().isEmpty() && !(getType() instanceof StructuredContainer)) {
			list.add(this);
		}
		else {
			if (getType() instanceof StructuredContainer) {
				((StructuredContainer)getType()).collectAllLeafItems(list);
			}
			else {
				for (StructuredField field : getChildren()) {
					field.collectAllLeafItems(list);
				}
			}
		}
	}
		
}
