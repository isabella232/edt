/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal.util;

import org.eclipse.edt.ide.rui.internal.nls.Locale;

/**
 *
 */
public class DeployLocale extends Locale {
	
	boolean isDefault = false;
	Locale locale;

	/**
	 * 
	 */
	public DeployLocale() {
	}
	
	public DeployLocale(String code, String description, String runtimeLocaleCode) {
		super(code, description, runtimeLocaleCode);
	}
	
	public DeployLocale(String code, String description, String runtimeLocaleCode, Locale locale) {
		super(code, description, runtimeLocaleCode);
		this.locale = locale;
	}
	
	public DeployLocale(Locale locale) {
		super(locale.getCode(), locale.getDescription(), locale.getRuntimeLocaleCode());
		this.locale = locale;
	}
	
	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public String toString() {
		return code;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
