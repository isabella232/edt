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
package org.eclipse.edt.gen.java.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class CommonUtilities {

	public static Annotation getAnnotation(Context ctx, String key) throws MofObjectNotFoundException, DeserializationException {
		EObject eObject = Environment.getCurrentEnv().find(key);
		if (eObject instanceof StereotypeType && (eObject = ((StereotypeType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		} else if (eObject instanceof AnnotationType && (eObject = ((AnnotationType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		}
		return null;
	}
}
