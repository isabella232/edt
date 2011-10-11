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
package org.eclipse.edt.gen.deployment.javascript;

import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;

public class Context extends EglContext {
	
	protected ISystemEnvironment sysEnv;
	private static String EGL_SCHEMA = Type.EGL_KeyScheme + Type.KeySchemeDelimiter;

	public Context(AbstractGeneratorCommand processor, ISystemEnvironment sysEnv) {
		super(processor);
		this.sysEnv = sysEnv;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8844216173280411827L;

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
	public boolean isSystemPart( String partName ) {
		String key = makeEGLKey(partName);
		if(sysEnv.getIREnvironment().get(key)!=null){
			return true;
		}else{
			return false;
		}
	}
	
	public String makeEGLKey(String key) {
		if (key != null && !key.startsWith(EGL_SCHEMA)) {
			key = EGL_SCHEMA + key;
		}
		return key.toUpperCase().toLowerCase();
	}

}
