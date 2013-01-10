/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egl.templates.annotations;

import java.lang.annotation.Annotation;

import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.gen.egl.templates.EglTemplate;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class AnnotationTemplate  extends EglTemplate{
	public void genAnnotation(Annotation annotation, Context ctx, Member member) {
		
	}
	public void genAnnotation(Annotation annotation, Context ctx, LogicAndDataPart part) throws MofObjectNotFoundException, DeserializationException {
	}
}
