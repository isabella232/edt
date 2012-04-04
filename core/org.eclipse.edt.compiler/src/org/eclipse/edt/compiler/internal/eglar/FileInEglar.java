/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.eglar;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class FileInEglar {
	public static final String EGLAR_PREFIX = "eglar://"; //$NON-NLS-1$
	public static final String EGLAR_SEPARATOR = "|"; //$NON-NLS-1$
	public static final String EGLAR_EXTENSION = ".eglar"; //$NON-NLS-1$
	
	private EglarFile eglarFile;
	private ZipEntry entry;
	
	
	public FileInEglar( String path ) {
		if ( path.startsWith( EGLAR_PREFIX ) ) {
			try {
				int index = path.indexOf( EGLAR_SEPARATOR );
				eglarFile = EglarFileCache.instance.getEglarFile( path.substring( 8, index ) );
				entry = new ZipEntry( path.substring( index + 1 ) );
			} catch (IOException e) {
				//do nothing
			}
		}
	}
	
	public EglarFile getEglarFile() {
		return eglarFile;
	}
	
	public ZipEntry getEntry() {
		return entry;
	}
	
	public boolean exists() {
		return eglarFile != null && entry != null;
	}
	
	public InputStream getInputStream() throws IOException  {
		return eglarFile.getInputStream( entry );
	}
	
	public String getFullPath(boolean withProtocol) {
		return (withProtocol ? EGLAR_PREFIX : "") + eglarFile.getName() + EGLAR_SEPARATOR + entry.getName();
	}
	
	public String getEglarPath() {
		return eglarFile.getName();
	}
	
	public String getEntryPath() {
		return entry.getName();
	}
}
