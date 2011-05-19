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
package org.eclipse.edt.ide.core.internal.model.bde;

public class BinaryProjectDescription {
	private String fProviderName;
	
	private long fId;
	private String fVersion;
	private String fName;
	private String location;
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getProviderName() {
		return fProviderName;
	}
	public void setProviderName(String providerName) {
		this.fProviderName = providerName;
	}
	public long getId() {
		return fId;
	}
	public void setId(long id) {
		this.fId = id;
	}
	public String getVersion() {
		return fVersion;
	}
	public void setVersion(String version) {
		this.fVersion = version;
	}
	public String getName() {
		return fName;
	}
	public void setName(String name) {
		this.fName = name;
	}
	
}
