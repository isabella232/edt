package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;

public class MofSerializableTemplate extends EGLDocTemplate {

	public void genMofSerializable(MofSerializable mofSerializable, Context ctx, TabbedWriter out) {
		/* gives this:  org.eclipse.edt.mof.egl.ExternalType
		 * out.println(mofSerializable.getEClass().getETypeSignature()); if (1
		 * == 1) return; else {
		 */

		String mofSerializableName = mofSerializable.getMofSerializationKey();
				// getEClass().getETypeSignature();

		
		// gives display value, element type, and list type (if appropriate)
		ArrayList<String> stringDetails = (ArrayList<String>) Util.getEGLSimpleType(mofSerializableName);
		if (stringDetails.get(0).equals("EGL_Location")) {
			return;
		}

		out.println("<dl>");

		ctx.invokeSuper(this, genDeclaration, mofSerializable, ctx, out, stringDetails);

		out.println("</dl>");
	}

	public void genDeclaration(MofSerializable mofSerializable, Context ctx, TabbedWriter out,
			ArrayList<String> stringList) {

		out.println("<dt></dt><dd>");
		out.println("<a href=\"" + stringList.get(1) + "\">");
		out.println(stringList.get(0) + "</a>");

		if (stringList.get(2) != null) {
			out.println("<a href=\"" + stringList.get(2) + "\">");
			out.println(" [ ]</a>");
		}

		out.println("{");

		// java.lang.UnsupportedOperationException
		// List<EMetadataObject> metadata = annotation.getMetadataList();

		/*
		 * does not provide "setUpper" --- only the first value. Object theAnno
		 * = annotation.getValue(); if (theAnno != null){
		 * out.println(theAnno.toString());
		 * 
		 * }
		 */

		List<EField> fields = mofSerializable.getEClass().getEFields();
		Iterator<EField> fieldsIterator = fields.iterator();
		int numberOfAnnoFields = fields.size();
		int counter = 0;

		while (fieldsIterator.hasNext()) {
			EField theEField = fieldsIterator.next();
			counter = counter + 1;
			String theSignature = theEField.getTypeSignature();
			String theName = theEField.getName();

			if (numberOfAnnoFields > 1) {
				out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			out.println(theName);

			/*
			 * START... if you want to show the field type.
			 * 
			 * the display name, element name, and (if appropriate) the List
			 * type name
			 * 
			 * ArrayList<String> stringDetails; stringDetails =
			 * (ArrayList<String>) Util.getEGLSimpleType(theSignature);
			 * out.println("<a href=\"" + stringDetails.get(1) + "\">");
			 * out.println(stringDetails.get(0) + "</a>");
			 * 
			 * if (stringDetails.get(2) != null) { out.println("<a href=\"" +
			 * stringDetails.get(2) + "\">"); out.println(" [ ]</a>"); } END...
			 * if you want to show the field type.
			 */

			out.println(" = ");

			Object theDetail = mofSerializable.eGet(theEField);

			if (theDetail instanceof Element) {
				ctx.invoke(genDeclaration, theDetail, ctx, out);
			} else {
				if (theSignature.endsWith("String")) {
					out.println("\"" + theDetail.toString() + "\"");
				} else {
					out.println(theDetail.toString());
				}
			}

			if (counter < numberOfAnnoFields) {
				out.println(", ");
			}
		}

		out.println("}");
		if (numberOfAnnoFields > 1) {
			out.println("</p>");
		}

		out.println("</dd>");
	}
}
