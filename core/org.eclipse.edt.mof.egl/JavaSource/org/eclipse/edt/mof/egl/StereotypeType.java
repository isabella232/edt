/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.mof.EClass;


public interface StereotypeType extends AnnotationType {
	List<AnnotationType> getMemberAnnotations();
	
	List<Annotation> getImplicitFields();
	
	List<Annotation> getImplicitFunctions();
	
	String getValidationProxy();
	void setValidationProxy(String className);
	
	boolean isReferenceType();
	void setIsReferenceType(boolean value);
	
	EClass getPartType();
	void setPartType(EClass eClass);

	EClass getDefaultSuperType();
	void setDefaultSuperType(EClass eClass);

}
