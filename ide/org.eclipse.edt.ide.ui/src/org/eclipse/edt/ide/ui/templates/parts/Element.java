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
package org.eclipse.edt.ide.ui.templates.parts;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    private List<Annotation> annotations = new ArrayList<Annotation>();
	
	public void addAnnotation(Annotation ann) {
		annotations.add(ann);
	}
	
	protected void setAnnotations( Annotation[] annotations) {
		this.annotations = new ArrayList<Annotation>();
		for (int i=0; i < annotations.length; i++) {
			addAnnotation(annotations[i]);
		}
	}
	
	public Annotation[] getAnnotations() {
		return annotations.toArray(new Annotation[annotations.size()]);
	}

	public String getAnnotationString() {
		if (annotations.isEmpty()) {
			return "";
		}
		
		StringBuffer buff = new StringBuffer();
		buff.append(" {");
		boolean first = true;
		
		for(Annotation anno : annotations) {
			if (first) {
				first = false;
			} else {
				buff.append(", ");
			}
			buff.append(anno.toString());
		}
		buff.append("}");
		
		return buff.toString();
	}
}
