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
package org.eclipse.edt.ide.core.internal.model.bde.target;

import java.net.URI;

import org.osgi.framework.Version;

public class BinaryProjectInfo {
	public static final String EMPTY_VERSION = "0.0.0"; //$NON-NLS-1$

	private String symbolicName = null;
	private String version = EMPTY_VERSION;
	private URI baseLocation = null;
	private URI location = null;

	private boolean resolved = false;
	/**
	 * Create a new BundleInfo object
	 */
	public BinaryProjectInfo() {
	}

	/**
	 * Create a new BundleInfo object
	 * @param location - the location of the bundle
	 */
	public BinaryProjectInfo(URI location) {
		this.location = location;
	}

	/**
	 * Create a new BundleInfo object
	 * @param symbolic  The Bundle-SymbolicName name for this bundle
	 * @param version - The version for this bundle, this must be a valid {@link Version} string, if null is passed {@link #EMPTY_VERSION} will be used instead
	 * @param location - the location of the bundle
	 */
	public BinaryProjectInfo(String symbolic, String version, URI location) {
		this.symbolicName = symbolic;
		this.version = version != null ? version : EMPTY_VERSION;
		this.location = location;
	}
	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public URI getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(URI baseLocation) {
		this.baseLocation = baseLocation;
	}

	public URI getLocation() {
		return location;
	}

	public void setLocation(URI location) {
		this.location = location;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	
}
