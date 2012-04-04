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

import java.util.jar.Attributes;

public class EglarAttributes {
	public static final Attributes.Name MANIFEST_VERSION = new Attributes.Name("Manifest-Version");
	public static final Attributes.Name CREATED_BY = new Attributes.Name("Created-By");
	public static final Attributes.Name VENDOR = new Attributes.Name("Vendor");
	public static final Attributes.Name Version = new Attributes.Name("Version");
	public static final Attributes.Name JAVA_JARS = new Attributes.Name("Java-Jars");
	
	public static final String CURRENT_MANIFEST_VERSION = "1.0";
	public static final String CURRENT_CREATED_BY = "Eclipse EDT";
	public static final String MANIFEST_VENDOR_VALUE = "IBM";
	public static final String MANIFEST_JAVA_JARS_FOLDER_DEFAULT = "Java_Jars/";
}
