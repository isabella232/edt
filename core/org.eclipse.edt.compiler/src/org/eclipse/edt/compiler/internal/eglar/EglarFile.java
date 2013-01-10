/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class EglarFile extends JarFile {
	private EglarManifest manifest; 
	private HashMap entries;

	public EglarFile(String fileName) throws IOException {
		super( fileName );
	}
	
	public EglarFile(File file) throws IOException {
		super( file );
	}
	
	public EglarManifest getManifest() {
		if ( manifest == null ) {
			try {
				manifest = new EglarManifest( this.getInputStream( new ZipEntry( MANIFEST_NAME ) ) );
			} catch (Exception e) {
				manifest = new EglarManifest();
			}
		}
		return manifest;
	}
	
	public ZipEntry getEntry( String entryName ) {
		if ( entryName.indexOf( '\\') > 0 ) {
			entryName = entryName.replaceAll( "\\\\", "/" );
		}
		return super.getEntry( entryName );
	}
	
	public LinkedHashSet getChildrenOf( String parent ) {

		if ( entries == null ) {
			entries = new HashMap();
	        Enumeration zipEntries = entries();
	        while (zipEntries.hasMoreElements()) {
	        	ZipEntry entry = (ZipEntry)zipEntries.nextElement();
        		String name = entry.getName();
        		int index = name.indexOf( "/" );
        		String parentName = "";
        		Set children = null;
        		if(index <= 0) {
        			children = (LinkedHashSet)entries.get( parentName );
        			if(children == null){
        				children = new LinkedHashSet();
        				entries.put(parentName, children);
        			}
        		}
        		boolean firstSeg = true;
        		while ( index > 0 ) {
        			index ++;
        			if(firstSeg){
        				firstSeg = false;
        				children = (LinkedHashSet)entries.get( parentName );
	        			if(children == null){
	        				children = new LinkedHashSet();
	        				entries.put(parentName, children);
	        			}
	        			children.add( name.substring( 0, index ) );
        			}
        			parentName = name.substring( 0, index );
            		children = (LinkedHashSet)entries.get( parentName );
            		if ( children == null ) {
            			children = new LinkedHashSet();
            			entries.put( parentName, children );
            		}
            		if ( name.indexOf( "/", index ) > 0 ) {
            			children.add( name.substring(0, name.indexOf( "/", index ) + 1 ) );
            		}
            		index = name.indexOf( "/", index );
        		}
        		children.add( entry.getName() );
	        }

		}
		return (LinkedHashSet)entries.get( parent );
	}
	
	public boolean isManifestExist(){
		return (this.getEntry(MANIFEST_NAME) != null);
	}
}
