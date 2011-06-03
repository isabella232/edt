package org.eclipse.edt.gen.egl.templates.annotations.xml;

import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.gen.egl.templates.EglTemplate;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;

public class XmlSchemaTypeTemplate  extends EglTemplate{
	public void genAnnotation(XmlSchemaType annotation, Context ctx, Member member) {
		//FIXME add this type
		Annotation eAnnotation = ctx.getFactory().createAnnotation("XmlSchemaType");
		member.addAnnotation(eAnnotation);
		if(annotation.name() != null && annotation.name().length() > 0){
			eAnnotation.setValue(IEGLConstants.PROPERTY_NAME, annotation.name());
		}
		if(annotation.namespace() != null && !"http://www.w3.org/2001/XMLSchema".equals(annotation.namespace())){
			eAnnotation.setValue(IEGLConstants.PROPERTY_NAMESPACE, annotation.namespace());
		}
		if(annotation.type() != null &&
				annotation.type().getName() != null &&
				annotation.type().getName().length() > 0 &&
				!"javax.xml.bind.annotation.XmlSchemaType$DEFAULT".equals(annotation.type().getName())){
			eAnnotation.setValue("_type", annotation.type().getName());
		}
	}
}
