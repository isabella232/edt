/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EGLFileResource implements EGLResource {

	private File resource;
	
	public EGLFileResource( File resource ) {
		this.resource = resource;
	}
	
	public boolean exists() {
		return ( resource != null && resource.exists() );
	}
	
	public boolean isFile() {
		return resource.isFile();
	}

	public File toFile() {
		return resource;
	}
	
	public String getName() {
		return resource.getName();
	}
	
	public String getFullName() {
		return resource.getName();
	}

	public InputStream getInputStream() throws IOException{
		return new FileInputStream( resource );
	}
	
	public long getLocalTimeStamp() {
		return resource.lastModified();
	}

}
