package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.Util;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EModelElement;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;

public class AnnotationTemplate extends EGLDocTemplate {


	
	public void genAnnotation(Annotation annotation, Context ctx, TabbedWriter out) {
				
		String annotationName = annotation.getEClass().getETypeSignature();

		// gives display value, element type, and list type (if appropriate)
		ArrayList<String>stringDetails = (ArrayList<String>) Util.getEGLSimpleType(annotationName);
		if (stringDetails.get(0).equals("EGL_Location")) {
			return;
		}
		
		out.println("<dl>");
		
		ctx.invoke(genDeclaration, annotation, ctx, out, stringDetails);
        
		out.println("</dl>");
		
		
		// "eglx.lang.ExternalName EGL_Location":
		// out.println(iterator.next().getEClass().getETypeSignature());
		// annotationName =
		// iterator.next().getEClass().getETypeSignature();

		// display, element, and list (if appropriate); but assume no
		// list
		// stringDetails = (ArrayList<String>)
		// Util.getEGLSimpleType(annotationName);
		// out.println("<a href=\"" + stringDetails.get(1) + "\">");
		// out.println(stringDetails.get(0) + "</a>");

		// "hello 41":
		// out.println(iterator.next().getValue().toString());		
	}

		
	public void genDeclaration(Annotation annotation, Context ctx, TabbedWriter out, ArrayList<String> stringList) {

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
		
	
		
		
		/* does not provide "setUpper" --- only the first value.
		Object theAnno = annotation.getValue();
		if (theAnno != null){
			out.println(theAnno.toString());
			
		}
		*/
		
	
		
		
	
		
		
		
		
		List<EField> fields  = annotation.getEClass().getEFields();
		
		Iterator<EField> fieldsIterator = fields.iterator();
		
		while (fieldsIterator.hasNext()) {
			// EField theEField = (EField)annotation.eGet(fieldsIterator.next());
			EField theEField = fieldsIterator.next();
			String theSignature = theEField.getTypeSignature();
			String theName = theEField.getName();	
			Object theDetail = annotation.eGet(theEField);
			
			// display, element, and list (if appropriate)
			ArrayList<String> stringDetails;
			stringDetails = (ArrayList<String>) Util.getEGLSimpleType(theSignature);

			if (fields.size() > 1) {
			   out.println ("<p><span style=\"padding-left:20px\">");
			}
			out.println (theName);
			out.println("<a href=\"" + stringDetails.get(1) + "\">");
			out.println(stringDetails.get(0) + "</a>");

			if (stringDetails.get(2) != null) {
				out.println("<a href=\"" + stringDetails.get(2) + "\">");
				out.println(" [ ]</a>");
			}
			
			out.println (" = ");
		
			if (theDetail != null){
				
		        String theDetailString = theDetail.toString();
		        
		        if (theDetailString.startsWith("Instance of")){
		        	
		        	
		        	
		        	
		        }
		        
		        
		        
			}
			
			
			
			// List<EMetadataObject> metadata = annotation.getMetadataList();
			
			
			
			
		//	List<EMetadataObject> metadata = ((EModelElement) theDetail).getMetadataList();
			
		//	Iterator<EMetadataObject> metadataIterator = metadata.iterator();
		/*	
			while (metadataIterator.hasNext()) {
				EMetadataObject theMetadataObject = metadataIterator.next();
				String theMetadata = theMetadataObject.toString();
				
				out.println(theMetadata);
			}
		*/	 
			
			

			
			/* gives nothing
			Object theValue = theEField.getInitialValue();
			
			
			   out.println(theName + " " + theSignature);	
			
			   if (theValue != null) { 
				   out.println(" " + theValue.toString());
			   }
			*/
		
//			out.println(theName + " " + theSignature);
			
			
		}
		
		
				
		
		
	//	Iterator<EMetadataObject> metadataIterator = metadata.iterator();
		
	//	while (metadataIterator.hasNext()) {
	//		EMetadataObject theMetadataObject = metadataIterator.next();
			
			// String theMetadata = theMetadataObject.toString();			
			// out.println(theMetadata);
	//	}
		
		/*
		 * for(Iterator<EField> fieldsIterator : fields.iterator() ){
		 
		   annotation.eGet(filedsIterator.next());
		}
		*/
		
		out.println("}");
		if (fields.size() > 1) {
			out.println("</p>");
		}
		
		out.println("</dd>");
	}
}
