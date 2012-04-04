/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egl.templates.annotations.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.gen.egl.templates.CommonUtilities;
import org.eclipse.edt.gen.egl.templates.Constants;
import org.eclipse.edt.gen.egl.templates.EglTemplate;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class XmlRootElementTemplate  extends EglTemplate{
	public void genAnnotation(XmlRootElement annotation, Context ctx, LogicAndDataPart part) throws MofObjectNotFoundException, DeserializationException {
		org.eclipse.edt.mof.egl.Annotation eAnnotation = CommonUtilities.getAnnotation(ctx, Constants.XMLRootElement);
		if(annotation != null){
			part.addAnnotation(eAnnotation);
			if(annotation.name() != null && !"##default".equals(annotation.name())){
				eAnnotation.setValue(IEGLConstants.PROPERTY_NAME, annotation.name());
			}
			if(annotation.namespace() != null && !"##default".equals(annotation.namespace())){
				eAnnotation.setValue(IEGLConstants.PROPERTY_NAMESPACE, annotation.namespace());
			}
		}
	}
}
