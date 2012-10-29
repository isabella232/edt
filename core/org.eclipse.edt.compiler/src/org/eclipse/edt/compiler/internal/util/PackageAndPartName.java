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
		super();
		this.caseSensitivePackageName = caseSensitivePackageName;
		this.caseSensitivePartName = caseSensitivePartName;
	}

	
}
