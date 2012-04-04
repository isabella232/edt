/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.nls;

public class Locale {
	
	protected String code;
	protected String description;
	
	protected String runtimeLocaleCode;

	/**
	 * 
	 */
	public Locale() {
	}
	
	public Locale(String code, String description, String runtimeLocaleCode) {
		this.code = code;
		this.description = description;
		this.runtimeLocaleCode = runtimeLocaleCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return code + "," + description + "," + runtimeLocaleCode; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getRuntimeLocaleCode() {
		return runtimeLocaleCode;
	}

	public void setRuntimeLocaleCode(String runtimeLocaleCode) {
		this.runtimeLocaleCode = runtimeLocaleCode;
	}

}
