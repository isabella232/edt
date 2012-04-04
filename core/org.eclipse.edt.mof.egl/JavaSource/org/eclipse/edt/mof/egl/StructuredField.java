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
package org.eclipse.edt.mof.egl;

import java.util.List;

public interface StructuredField extends Field {
	List<ElementAnnotations> getElementAnnotations();

	List<Annotation> getElementAnnotations(int index);
	public Annotation getElementAnnotation(String typeName, int index);
	
	List<StructuredField> getChildren();
	
	StructuredField getParent();
	
	void setParent(StructuredField value);
	
	Integer getOccurs();
	
	void setOccurs(Integer value);
	
	
	Integer getActualOccurs();
	
	void addChild(StructuredField child);
	
	int getSizeInBytes();
	
	void collectAllLeafItems(List<StructuredField> list);
}
