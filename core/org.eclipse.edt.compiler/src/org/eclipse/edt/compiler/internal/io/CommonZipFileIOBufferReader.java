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
package org.eclipse.edt.compiler.internal.io;

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


/**
 * @author svihovec
 *
 */
public abstract class CommonZipFileIOBufferReader implements IIOBufferReader {

	public abstract File getFile();
	
	public InputStream getInputStream(String name)throws IOException{
				
		File cacheFile = getFile();
		
		if(cacheFile.exists()){
			ZipFile zipFile = new ZipFile(cacheFile);
			ZipEntry entry = zipFile.getEntry(IRFileNameUtility.toIRFileName(name));
				
			if(entry == null){
				zipFile.close();
				return null;
			}
		
			return zipFile.getInputStream(entry);
		
		}
		
		return null;
		
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

}
