package org.eclipse.edt.gen.egl.templates.annotations.xml;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.gen.egl.templates.CommonUtilities;
import org.eclipse.edt.gen.egl.templates.Constants;
import org.eclipse.edt.gen.egl.templates.EglTemplate;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class XmlAttributeTemplate  extends EglTemplate{
	public void genAnnotation(XmlAttribute annotation, Context ctx, Member member) throws MofObjectNotFoundException, DeserializationException {
		org.eclipse.edt.mof.egl.Annotation eAnnotation = CommonUtilities.getAnnotation(ctx, Constants.XMLAttribute);
		if(annotation != null){
			member.addAnnotation(eAnnotation);
			if(annotation.name() != null && !"##default".equals(annotation.name())){
				eAnnotation.setValue(IEGLConstants.PROPERTY_NAME, annotation.name());
			}
			if(annotation.namespace() != null && !"##default".equals(annotation.namespace())){
				eAnnotation.setValue(IEGLConstants.PROPERTY_NAMESPACE, annotation.namespace());
			}
		}
	}
}
