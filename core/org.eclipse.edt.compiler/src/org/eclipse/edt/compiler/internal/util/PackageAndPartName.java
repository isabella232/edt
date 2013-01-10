/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.util;

import org.eclipse.edt.mof.utils.NameUtile;

public class PackageAndPartName {
	private String caseSensitivePackageName;
	private String caseSensitivePartName;
	private String packageName;
	private String partName;
	
	public String getCaseSensitivePackageName() {
		return caseSensitivePackageName;
	}

	public String getCaseSensitivePartName() {
		return caseSensitivePartName;
	}

	public String getPackageName() {
		if (packageName == null) {
			packageName = NameUtile.getAsName(caseSensitivePackageName);
		}
		return packageName;
	}

	public String getPartName() {
		if (partName == null) {
			partName = NameUtile.getAsName(caseSensitivePartName);
		}
		return partName;
	}

	public PackageAndPartName(String caseSensitivePackageName,
			String caseSensitivePartName) {
		this(caseSensitivePackageName, caseSensitivePartName, null);
	}

	public PackageAndPartName(String caseSensitivePackageName,
			String caseSensitivePartName,
			String caseInsensitivePackagName) {
		super();
		this.caseSensitivePackageName = caseSensitivePackageName;
		this.caseSensitivePartName = caseSensitivePartName;
		this.packageName = caseInsensitivePackagName;
	}

	
}
