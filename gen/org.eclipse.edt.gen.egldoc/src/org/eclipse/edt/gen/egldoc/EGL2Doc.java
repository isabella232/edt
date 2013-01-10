/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egldoc;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EGL2Doc extends AbstractGeneratorCommand {
	
	private IFile eglFile;
	
	public EGL2Doc(){
		super();
	}
	
	public EGL2Doc(IFile eglFile) {
		super();
		this.eglFile = eglFile;		
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		EGL2Doc genContent = new EGL2Doc();
		genContent.generate(args, new EGLDocGenerator(genContent), null, null);
	}
	
	public IFile getEGLFile(){
		return eglFile;
	}
	
}
