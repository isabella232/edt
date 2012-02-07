package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;

public class CommonUtilities {

	public static void addAnntation(Member member, Annotation annot, Context ctx){
		if(annot != null){
			@SuppressWarnings("unchecked")
			Map<String, Annotation> annotations = (Map<String, Annotation>)ctx.getAttribute(member, Constants.subKey_ibmiAnnotations);
			if(annotations == null){
				annotations = new HashMap<String, Annotation>();
				ctx.putAttribute(member, Constants.subKey_ibmiAnnotations, annotations);
			}
			annotations.put(annot.getEClass().getETypeSignature(), annot);
		}
	}
	public static Annotation getAnnotation(Member member, String annotationSignature, Context ctx){
		//check the context first because array will add the subtype to the field on the context
		Annotation annotation = null;
		@SuppressWarnings("unchecked")
		Map<String, Annotation> annotations = ((Map<String, Annotation>)ctx.getAttribute(member, Constants.subKey_ibmiAnnotations));
		if(annotations != null){
			annotation = annotations.get(annotationSignature);
		}
		if(annotation == null){
			annotation = member.getAnnotation(annotationSignature);
		}
		return annotation;
	}
}
