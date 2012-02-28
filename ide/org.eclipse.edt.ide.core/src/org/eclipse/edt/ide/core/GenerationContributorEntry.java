/*******************************************************************************
 * Copyright Â© 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core;

public class GenerationContributorEntry {
	private String className;
	private String provider;
	private String identifier;
	private String requires;
	private EDTRuntimeContainer[] runtimeContainers;

	public EDTRuntimeContainer[] getRuntimeContainers() {
		return runtimeContainers;
	}

	public void setRuntimeContainers(EDTRuntimeContainer[] runtimeContainers) {
		this.runtimeContainers = runtimeContainers;
	}

	public GenerationContributorEntry() {
		super();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getRequires() {
		return requires;
	}

	public void setRequires(String requires) {
		this.requires = requires;
	}
}
