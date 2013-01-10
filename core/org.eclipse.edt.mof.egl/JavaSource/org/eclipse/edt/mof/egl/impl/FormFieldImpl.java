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
import org.eclipse.edt.mof.egl.FormField;
import org.eclipse.edt.mof.egl.IrFactory;


public class FormFieldImpl extends FormFieldImplBase implements FormField {
	
	@Override
	public List<Annotation> getElementAnnotations(int index) {
		if (index > getOccurs())
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
	
}
