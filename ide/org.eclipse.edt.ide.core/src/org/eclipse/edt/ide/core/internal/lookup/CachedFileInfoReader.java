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
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * 
 * Read the specified CachedFileInfo segment from the disk.
 * 
 * @author svihovec
 *
 */
public class CachedFileInfoReader {

	private IPath fileInfoPath;
	
	public CachedFileInfoReader(IPath fileInfoPath){
		this.fileInfoPath = fileInfoPath;
	}
	
	public IFileInfo read() {

		FileInfo fileInfo = new FileInfo();
		
		try{
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileInfoPath.toFile())));

		    try{
		    	// read line offsets
		    	int numLines = inputStream.readInt();
		    	int[] lineOffsets = new int[numLines];
		    	
		    	for(int i=0; i<numLines; i++){
		    		lineOffsets[i] = inputStream.readInt();
		    	}
		    	
		    	fileInfo.setLineOffsets(lineOffsets);
		    	
		    	String caseSensitivePackageName = inputStream.readUTF();
		    	
		    	// read parts
				int numParts = inputStream.readInt();
						
				String partName = null;
				int partType;
				int elementOffset;
				int elementLength;
				String caseSensitivePartName = null;
				int md5KeyLength;
				byte[] md5Key;
				
				// read part info
				for(int i=0; i<numParts; i++){
					partName = inputStream.readUTF();
					caseSensitivePartName = inputStream.readUTF();
					partType = inputStream.readInt();
					elementOffset = inputStream.readInt();
					elementLength = inputStream.readInt();
					
					md5KeyLength = inputStream.readInt();
					md5Key = new byte[md5KeyLength];
					inputStream.read(md5Key, 0, md5KeyLength);
					
					fileInfo.addPart(NameUtile.getAsName(partName), partType, elementOffset, elementLength, NameUtile.getAsCaseSensitiveName(caseSensitivePartName), md5Key);
				}
			}finally{
			   inputStream.close();
			}
		} catch(IOException e){
		    throw new BuildException("IOException", e); //$NON-NLS-1$
		}
		
		return fileInfo;
	}
}
