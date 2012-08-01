package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

// Brian, consider growing a mustache.

public class EFieldTemplate extends EGLDocTemplate {

	public void genDeclaration(EField field, Context ctx, TabbedWriter out) {

		out.println("<dt class=\"dt pt dlterm\"><span class=\"ph synph\"><span class=\"keyword kwd\">"
				+ field.getName() + "</span>");
		
		// display, element, and list (if appropriate)
		ArrayList<String> stringDetails = (ArrayList<String>) Util.getEGLSimpleType(field.getTypeSignature());			
	
		out.println("<a href=\"" + stringDetails.get(1) + "\">");
		out.println(stringDetails.get(0) + "</a>");
		
		if (stringDetails.get(2) != null) {
             out.println("<a href=\"" + stringDetails.get(2) + "\">");
             out.println(" [ ]</a>");
		}     
		
		
		/* shows nothing
		List<EMetadataObject> theList= field.getMetadataList();
		
		Iterator<EMetadataObject> iterator = theList.iterator();
		while (iterator.hasNext()){
			out.println(iterator.next().toString());
		}
		*/
		
		/* fails
		java.lang.annotation.Annotation[] theAnnoList = field.getClass().getAnnotations();
		int numberOfAnnos = theAnnoList.length; 
		for (int i = 0; i < numberOfAnnos; i++) {
			out.println(theAnnoList[i].toString());
		}
		*/
		
		
		
		out.println("</span></dt>");
		out.println("<dd class=\"dd pd\">" + "<p></p>" + "This is the purpose!"
				+ "</dd>");
	}
}
