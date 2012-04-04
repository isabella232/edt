/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.gen.generator.eglsource;

import java.util.Hashtable;
import java.io.StringWriter;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;

@SuppressWarnings("serial")
public class EglSourceContext extends EglContext {

	private Hashtable<String, String> sourceFileContentTable = new Hashtable<String, String>();
	private Hashtable<String, String> variables = new Hashtable<String, String>();
	private TabbedWriter out;
	
	public EglSourceContext(AbstractGeneratorCommand processor) {
		super(processor);
		makeTabbedWriter();
	}

	@Override
	public void handleValidationError(Element ex) {

	}

	@Override
	public void handleValidationError(Annotation ex) {

	}

	@Override
	public void handleValidationError(Type ex) {

	}

	public Hashtable<String, String> getSourceFileContentTable() {
		return sourceFileContentTable;
	}

	public Hashtable<String, String> getVariables() {
		return variables;
	}
	
	public void appendVariableValue(String key, String newValue, String prefix){
		String value = variables.get(key);
		if(value != null){
			variables.put(key, value + prefix + newValue);
		}else{
			variables.put(key, newValue);
		}
	}

	public TabbedWriter getTabbedWriter() {
		return out;
	}
	
	public String getTabbedWriterContent(){
		return out.getWriter().toString();
	}

	public void makeTabbedWriter() {
		out = new TabbedWriter(new StringWriter());
		out.setAutoIndent(false);
	}

	
}
