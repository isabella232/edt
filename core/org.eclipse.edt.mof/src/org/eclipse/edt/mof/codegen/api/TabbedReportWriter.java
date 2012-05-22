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
package org.eclipse.edt.mof.codegen.api;

import java.io.StringWriter;
import java.io.Writer;

//import org.eclipse.edt.mof.egl.Annotation;

/**
 * TabbedReportWriter implements automatic creation of a generation report providing detailed information about the templates
 * involved in producing a given fragment of generated code. It essentially forks the usual TabbedWriter to capture what's
 * being output at a particular point as well as the call stack which is filtered to capture only template classes.
 */
public class TabbedReportWriter extends TabbedWriter {

	// public Annotation location = null; /* TODO sbg Revisit support for line number references in the EGL source */

	/*
	 * TODO sbg It's inefficient to have the rpt be its own TabbedWriter; it'd be more efficient to instead have a
	 * stringbuffer (or maybe a stringwriter) and then wrap all of the parent TabbedWriter's methods. When these are invoked,
	 * mark the length of the parent TabbedWriter's buffer, call the parent, and then get the length of the buffer
	 * afterwards. If the delta is non-zero, then something was added and we can simply copy the delta into our rpt's string,
	 * interjecting any additional content such as the stack trace, etc.
	 */
	public TabbedWriter rpt = new TabbedWriter(new StringWriter());

	private String qualifier = null; // TODO sbg New design should obviate this

	/**
	 * TabbedReportWriter
	 * @param templateQualifier this is a string used to identify template classes
	 * @param writer
	 */
	public TabbedReportWriter(String templateQualifier, Writer writer) {
		super(writer);

		qualifier = templateQualifier;

		/*
		 * TODO sbg Although we want the HTML report to be completely standalone and self-contained, the HTML header & CSS
		 * should be maintained in a separate file.
		 */
		rpt.println("<html>" + "<head>" + "<style>" + "a.info{" + "    position:relative; /*this is the key*/" + "    z-index:24; /* background-color:#ccc; */"
			+ "    color:#111;" + "    text-decoration:none}" +

			"a.info:hover{z-index:25; background-color:#ff0}" +

			"a.info > span{display: none}" +

			"a.info:hover > span{ /*the span will display just on :hover state*/" + "    display:block;" + "    position:absolute;"
			+ "    top:2em; left:2em; /* width:15em; */" + "    margin:0px;" + "    padding:3px;"
			+ "    border:1px solid rgb(119,119,0);"
			+ // #0cf;"+
			"    border-radius: 10px;  -moz-border-radius: 10px;" + "    -moz-box-shadow: 10px 10px 5px rgba(50,50,75,0.8);"
			+ "    -webkit-box-shadow: 10px 10px 5px rgba(50,50,75,0.8);" + "    box-shadow: 10px 10px 5px rgba(50,50,75,0.8);"
			+ "    background-color:rgb(255,255,185);" + // #cff;
			"    color:#000;" + "    color:#333;" + "    text-align: left}" +

			"a > span > span:first-child{" + "font-weight:bold;" + "}" +

			"a > span > span:nth-child(n+2){" + "padding-left:2em;" + "}" +

			"</style>" + "<title>Gen Report</title>" + "</head>" +

			"<body style=\"background-color:#ddd;\">" +

			"<pre>");
	}

	public String[] getStack() {
		String[] result = new String[] { null, null }; // first slot holds the top-most stack element, the second slot holds
														// the full stack
		try {
			StringBuffer templateStack = new StringBuffer();
			{
				StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();

				if (stackTrace.length > 0) {
					templateStack.append("<span>");
				}
				boolean first = true;
				for (StackTraceElement stackTraceElement : stackTrace) {
					if (stackTraceElement.getClassName().startsWith(qualifier)) {
						StackTraceElement element = stackTraceElement;
						String cname = element.getClassName();

						if (first) {
							result[0] = cname.substring(cname.lastIndexOf('.') + 1) + "." + element.getMethodName() + ":" + element.getLineNumber();
							first = false;
						}

						templateStack.append("<span>" + cname.substring(cname.lastIndexOf('.') + 1) + "." + element.getMethodName() + ":"
							+ element.getLineNumber() + "</span><br>");
						// TODO sbg revisit support for line numbers in EGL source....
						// if ((first) && (location != null)){
						// result.append("line = "+location.getValue("line")+"<br>");
						// }
					}
				}
				if (stackTrace.length > 0) {
					templateStack.append("</span>");
				}
			}
			result[1] = templateStack.length() == 0 ? null : templateStack.toString();
		}
		catch (Exception e) {
			/*
			 * Firewall any problems in the collection of data for the generation report to ensure that normal src generation
			 * always occurs as-expected....
			 */
			result = new String[] { null, null };
		}
		return result;
	}

	public void print(char c) {
		super.print(c);

		String[] stack = getStack();
		String stackLoc = stack[0];
		if (stackLoc != null) {
			rpt.print("<a  class=info href=\"" + stackLoc + "\">");
		}
		rpt.print(escHTML(new char[] { c }));
		if (stackLoc != null) {
			rpt.print(stack[1]);
		}
	}

	public void print(char[] chars) {
		super.print(chars);

		String[] stack = getStack();
		String stackLoc = stack[0];
		if (stackLoc != null) {
			rpt.print("<a  class=info href=\"" + stackLoc + "\">");
		}
		rpt.print(escHTML(chars));
		if (stackLoc != null) {
			rpt.print(stack[1]);
		}
	}

	// TODO sbg Replace this trivial, incomplete implementation with something more robust
	private static String escHTML(char[] chars) {
		StringBuilder b = new StringBuilder();
		for (char c : chars) {
			switch (c) {
				case '<':
					b.append("&lt;");
					break;
				case '>':
					b.append("&gt;");
					break;
				case '&':
					b.append("&amp;");
					break;
				case '\"':
					b.append("&quot;");
					break;
				default:
					b.append(c);
			}
		}
		return b.toString();
	}

	public void close() {
		super.close();
		rpt.close();
	}

	public void flush() {
		super.flush();
		rpt.flush();
	}
}
