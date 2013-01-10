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
package org.eclipse.edt.mof.egl;

import java.util.List;

import org.eclipse.edt.mof.EObject;


/**
 * Root for entire EGL model.  Any EGL model element can have associated with it
 * a list of annotation values.
 *
 */
public interface Element extends EObject, ElementReference {
	List<Annotation> getAnnotations();
	
	
	public Annotation getAnnotation(String name);
	public Annotation getAnnotation(AnnotationType type);
	
	public void addAnnotation(Annotation ann);
	public void removeAnnotation(Annotation ann);
}
