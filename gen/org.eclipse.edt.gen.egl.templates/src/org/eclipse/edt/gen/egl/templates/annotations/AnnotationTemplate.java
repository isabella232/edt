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
