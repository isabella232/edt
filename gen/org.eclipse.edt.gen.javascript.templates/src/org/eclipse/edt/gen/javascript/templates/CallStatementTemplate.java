/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.compiler.binding.annotationType.DeleteRestAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.GetRestAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.PostRestAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.PutRestAnnotationTypeBinding;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.Expression;

public class CallStatementTemplate extends JavaScriptTemplate {

	public void genStatementBody(CallStatement stmt, Context ctx, TabbedWriter out) {
		Expression serviceInterface = stmt.getInvocationTarget();
		Annotation rest;
		String method = "POST";
		rest = serviceInterface.getAnnotation(PostRestAnnotationTypeBinding.name);
		if(rest == null){
			rest = serviceInterface.getAnnotation(PutRestAnnotationTypeBinding.name);
			method = "PUT";
			if(rest == null){
				rest = serviceInterface.getAnnotation(DeleteRestAnnotationTypeBinding.name);
				method = "DELETE";
				if(rest == null){
					rest = serviceInterface.getAnnotation(GetRestAnnotationTypeBinding.name);
					method = "GET";
				}
			}
		}
		if(rest != null){
			buildHttpRestBind(rest, method, out);
		}
		else{
			buildHttpSoapBind();
		}
	}
	private void buildHttpRestBind(Annotation rest, String method, TabbedWriter out){
		out.print(", " + quoted(method));
		printQuotedString((String)rest.getValue("uriTemplate"), out);
		printInteger((Integer)rest.getValue("requestFormat"), out);
		printQuotedString((String)rest.getValue("requestCharset"), out);;
		printQuotedString((String)rest.getValue("requestContentType"), out);;
		printInteger((Integer)rest.getValue("responseFormat"), out);
		printQuotedString((String)rest.getValue("responseCharset"), out);;
		printQuotedString((String)rest.getValue("responseContentType"), out);;
	}
	
	private void printQuotedString(String val, TabbedWriter out){
		out.print(", ");
		out.print(val == null ? "null" : quoted(val));
	}
	
	private void printInteger(Integer val, TabbedWriter out){
		out.print(", ");
		out.print(val == null ? "null" : val.toString());
	}
	
	private void buildHttpSoapBind(){
		
	}
}
