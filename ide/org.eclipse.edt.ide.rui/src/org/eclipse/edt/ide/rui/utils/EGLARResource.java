/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
package org.eclipse.edt.ide.rui.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.edt.compiler.internal.eglar.FileInEglar;

public class EGLARResource implements EGLResource {

	private static final String EGLAR_PREFIX = "eglar://";
	
	private ZipFile eglar;
	private ZipEntry entry;
	
	public EGLARResource( ZipFile eglar, ZipEntry resource ) {
		this.eglar = eglar;
		this.entry = resource;
	}
	
	public boolean exists() {
		return entry != null;
	}

	public boolean isFile() {
		return !entry.isDirectory();
	}
	
	public File toFile() {
		return new File( eglar.getName() );
	}
	
	public String getName() {
		return entry.getName();
	}
	
	public String getFullName() {
		return EGLAR_PREFIX + eglar.getName() + FileInEglar.EGLAR_SEPARATOR + entry.getName();
	}
	
	public InputStream getInputStream() throws IOException {
		return eglar.getInputStream( entry );
	}
	
	public long getLocalTimeStamp() {
		return this.entry.getTime();
	}

}
