/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Type;

public class Context  extends EglContext{

	private static final long serialVersionUID = 6429116299734843162L;
	private String generationTime;
	private LogicAndDataPart part;
	
	public Context(AbstractGeneratorCommand processor) {
		super(processor);
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		generationTime = df.format(dt);
	}
	public String getGenerationTime() {
		return generationTime;
	}
	@Override
	public void handleValidationError(Element ex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleValidationError(Annotation ex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleValidationError(Type ex) {
		// TODO Auto-generated method stub
		
	}
	public void setResult(LogicAndDataPart part) {
		this.part = part;
	}
	public LogicAndDataPart getResult() {
		return part;
	}
}
