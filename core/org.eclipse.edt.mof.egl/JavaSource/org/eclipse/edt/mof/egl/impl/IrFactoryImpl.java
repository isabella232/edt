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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.InvalidName;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class IrFactoryImpl extends IrFactoryBase implements IrFactory {
	
	public Annotation createDynamicAnnotation(String typeSignature){
		return createAnnotation(IEnvironment.DynamicScheme + ":" + getAnnotationEClass().getETypeSignature() + ":" + typeSignature);
		
	}
	public Annotation createAnnotation(String typeSignature) {
		EClassifier annType;
		
		try {
			annType = getTypeNamed(typeSignature);
		} catch (Exception e) {
			// In case we missed a place that is invoking createAnnotation and should be invoking createDynamicAnnotation
			typeSignature = IEnvironment.DynamicScheme + ":" + getAnnotationEClass().getETypeSignature() + ":" + typeSignature;
			annType = getTypeNamed(typeSignature);
		}
		if (!(annType instanceof EClass)) {
			throw new IllegalArgumentException(typeSignature + " is not an AnnotationType");
		}

		return (Annotation)((EClass)annType).newInstance();
	}
	
	public InvalidName createInvalidName(String id) {
		InvalidName name = createInvalidName();
		name.setId(id);
		return name;
	}
	
	@Override
	public AnnotationType createAnnotationType() {
		AnnotationType type = (AnnotationType)getAnnotationTypeEClass().newInstance();
		// Default super type is EMetadataObject so replace it.
		type.getSuperTypes().set(0, getAnnotationEClass());
		return type;
	}

	@Override
	public StereotypeType createStereotypeType() {
		StereotypeType type = (StereotypeType)getStereotypeTypeEClass().newInstance();
		// Default super type is EMetadataObject so replace it.
		type.getSuperTypes().add(getStereotypeEClass());
		return type;
	}
}
