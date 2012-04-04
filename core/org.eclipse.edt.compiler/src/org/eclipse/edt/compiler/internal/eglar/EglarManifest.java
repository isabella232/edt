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
import java.util.jar.Manifest;

public class EglarManifest extends Manifest {
	
	public EglarManifest() {
		super();
		getMainAttributes().put( EglarAttributes.MANIFEST_VERSION, EglarAttributes.CURRENT_MANIFEST_VERSION );
		getMainAttributes().put( EglarAttributes.CREATED_BY, EglarAttributes.CURRENT_CREATED_BY );
	}
	
	public EglarManifest( InputStream is ) throws IOException {
		super( is );
	}
	
	public String getManifestVersion() {
		return this.getMainAttributes().getValue( EglarAttributes.MANIFEST_VERSION );
	}
	
	public String getMenifestVersion() {
		return this.getMainAttributes().getValue( EglarAttributes.MANIFEST_VERSION );
	}
	
	public String getCreatedBy() {
		return this.getMainAttributes().getValue( EglarAttributes.CREATED_BY );
	}
	
	public void setCreatedBy(String createdBy) {
		this.getMainAttributes().put(EglarAttributes.CREATED_BY, String.valueOf( createdBy ) );
	}
	
	public String getVendor() {
		return this.getMainAttributes().getValue( EglarAttributes.VENDOR );
	}
	
	public void setVendor(String vendor) {
		this.getMainAttributes().put(EglarAttributes.VENDOR, String.valueOf( vendor ) );
	}
	
	public String getVersion() {
		return this.getMainAttributes().getValue( EglarAttributes.Version );
	}
	
	public void setVersion(String version) {
		this.getMainAttributes().put(EglarAttributes.Version, String.valueOf( version ) );
	}
	
	public void setJavaJars(String jarsFolder) {
		this.getMainAttributes().put(EglarAttributes.JAVA_JARS, String.valueOf( jarsFolder ) );
	}
	
	public String[] getJavaJars() {
		return toValueList(this.getMainAttributes().getValue( EglarAttributes.JAVA_JARS ));
	}
	
	private String[] toValueList(String valueStr){
		if(valueStr == null)
			return null;
		return valueStr.split("(\\s)*;(\\s)*");
	}
}
