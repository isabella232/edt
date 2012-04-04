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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

public class EglarFileCache {

	public static final EglarFileCache instance = new EglarFileCache();
	private static final int CACHE_SIZE = 50;
	
	public LinkedList eglarFilesIndex;
	public Hashtable eglarFiles;
	
	private EglarFileCache() {
		eglarFilesIndex = new LinkedList();
		eglarFiles = new Hashtable();
	}
	
	public EglarFile getEglarFile( String fileName ) throws IOException {
		return getEglarFile( new File( fileName ) );
	}
	
	public synchronized EglarFile getEglarFile( File file ) throws IOException {
		return new EglarFile( file );
/*		//cache the EglarFile will cause the eglar file can not be delete, see Defect 77176
		String fileName = file.getAbsolutePath();
		if ( !eglarFilesIndex.contains( fileName ) ) {
			EglarFile eglar = new EglarFile( file );
			if ( eglarFilesIndex.size() > CACHE_SIZE ) {
				String lastEglarName = (String)eglarFilesIndex.removeLast();
				eglarFiles.remove( lastEglarName );
			}
			eglarFilesIndex.add( 0, fileName );
			eglarFiles.put( fileName, new EglarFileEntry( eglar, file.lastModified() ) );
		} else {
			int index = eglarFilesIndex.indexOf( fileName );
			eglarFilesIndex.remove( index );
			eglarFilesIndex.add( 0, fileName );
		}
		
		EglarFileEntry entry = (EglarFileEntry)eglarFiles.get( fileName );
		if ( entry.timestamp != file.lastModified() ) {
			EglarFile eglar = new EglarFile( file );
			entry =  new EglarFileEntry( eglar, file.lastModified() );
			eglarFiles.put( fileName, entry);
		}
		return entry.eglar;
*/
	}
	
	private static class EglarFileEntry {
		EglarFile eglar;
		long timestamp;
		
		EglarFileEntry( EglarFile eglar, long timestamp ) {
			this.eglar = eglar;
			this.timestamp = timestamp;
		}
	}
}
