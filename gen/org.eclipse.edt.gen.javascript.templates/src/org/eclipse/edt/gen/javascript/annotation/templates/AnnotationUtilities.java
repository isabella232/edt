package org.eclipse.edt.gen.javascript.annotation.templates;

import org.eclipse.edt.mof.egl.Annotation;

public class AnnotationUtilities {
	static String getName(Annotation annot) {
		String name = null;
		if (annot != null) {
			if (annot.getValue("name") != null && ((String) annot.getValue("name")).length() > 0)

			{
				name = (String) annot.getValue("name");
			}
		}

		return name;
	}

	static String getNamespace(Annotation annot) {
		String namespace = null;
		if (annot != null) {
			if (annot.getValue("namespace") != null && ((String) annot.getValue("namespace")).length() > 0)

			{
				namespace = (String) annot.getValue("namespace");
			}
		}
		return namespace;
	}

}
