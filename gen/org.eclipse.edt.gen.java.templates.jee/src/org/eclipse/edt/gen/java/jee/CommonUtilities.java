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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;

public class CommonUtilities {
	private static CommonUtilities instance = new CommonUtilities();

	public static Annotation getLocalAnnotation(Context ctx, String key){
		if(generationAnnotations.containsKey(key)){
			return org.eclipse.edt.gen.CommonUtilities.newInstance(generationAnnotations.get(key).getAnnotationType(ctx));
		}
		return null;
	}

	
	private static Map<String, BaseAnnotationCreator> generationAnnotations = new HashMap<String, BaseAnnotationCreator>();
	static {
		XmlJavaTypeAdapter xmlJavaTypeAdapter = instance.new XmlJavaTypeAdapter("eglx.xml.javax.xml.bind.annotation.adapters", "XmlJavaTypeAdapter");
		generationAnnotations.put(xmlJavaTypeAdapter.getQualifiedName(), xmlJavaTypeAdapter);
	}

	
	private abstract class BaseAnnotationCreator{
		protected abstract AnnotationType getAnnotationType(Context ctx);
	}	
	private class XmlJavaTypeAdapter extends BaseAnnotationCreator{
		private String pkg;
		private String name;
		private AnnotationType annotationType;
		public XmlJavaTypeAdapter(String pkg, String name) {
			this.name = name;
			this.pkg = pkg;
		}
		public String getQualifiedName() {
			return pkg + '.' + name;
		}
		protected AnnotationType getAnnotationType(Context ctx){
			if(annotationType == null){
				annotationType = ctx.getFactory().createAnnotationType();
				annotationType.setPackageName(pkg);
				annotationType.setName(name);
				EField eField = MofFactory.INSTANCE.createEField(true);
				eField.setName("value");
				eField.setEType(MofFactory.INSTANCE.getEStringEDataType());
				eField.setContainment(true);
				annotationType.addMember(eField);
		
				eField = MofFactory.INSTANCE.createEField(true);
				eField.setName("type");
				eField.setEType(MofFactory.INSTANCE.getEStringEDataType());
				eField.setContainment(true);
				annotationType.addMember(eField);
			}
			return annotationType;
		}
	}
}
