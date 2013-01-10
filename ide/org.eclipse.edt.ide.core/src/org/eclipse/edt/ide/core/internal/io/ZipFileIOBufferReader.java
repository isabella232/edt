/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.io.IIOBufferReader;

/**
 * @author svihovec
 *
 */
public abstract class ZipFileIOBufferReader implements IIOBufferReader {

	public abstract File getFile();
	
	public Object readEntry(String name) throws IOException {
		Object result = null;
		
		File cacheFile = getFile();
		
		if(cacheFile.exists()){
			ZipFile zipFile = new ZipFile(cacheFile);
				
			try{
				ZipEntry entry = zipFile.getEntry(name);
				
				if(entry == null){
					zipFile.close();
					return null;
				}
					
				ObjectInputStream zipStream = new ObjectInputStream(new BufferedInputStream(zipFile.getInputStream(entry)));
				
				try{
					result = zipStream.readObject();
				} catch (ClassNotFoundException e) {
					throw new BuildException("Class Not Found", e); //$NON-NLS-1$
				}finally {
					zipStream.close();
				}
			}finally{
				zipFile.close();
			}
		}
		
		return result;
	}

	public List getEntries() throws IOException {
		ArrayList entries = new ArrayList();
		File file = getFile();
		if(file.exists()){
			ZipFile zipFile = new ZipFile(file);
			
			try{
				Enumeration enumeration = zipFile.entries();
				
				while(enumeration.hasMoreElements()){
					entries.add((((ZipEntry)enumeration.nextElement()).getName()));
				}
			}finally{
				zipFile.close();
			}
		}
	
		return entries;
	}

	public InputStream getInputStream(String name)throws IOException{
		throw new UnsupportedOperationException();
	}
}
