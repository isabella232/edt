/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * A runtime container contains zero or more classpath entries (though an empty container is useless).
 */
public class EDTRuntimeContainer {
	
	/**
	 * A unique ID (will be appended to {@link EDTCoreIDEPlugin#EDT_CONTAINER_ID}).
	 */
	private final String id;
	
	/**
	 * The display name.
	 */
	private final String name;
	
	/**
	 * A brief description for what this provides.
	 */
	private final String description;
	
	/**
	 * The classpath entries.
	 */
	private final IClasspathEntry[] entries;
	
	/**
	 * The path stored in the .classpath file, based on the id.
	 */
	private final IPath path;
	
	public EDTRuntimeContainer(String id, String name, String description, EDTRuntimeContainerEntry[] entries) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.path = new Path(EDTCoreIDEPlugin.EDT_CONTAINER_ID).append(this.id);
		
		if (entries == null || entries.length == 0) {
			this.entries = new IClasspathEntry[0];
		}
		else {
			List<IClasspathEntry> resolvedEntries = new ArrayList<IClasspathEntry>(entries.length);
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i].getClasspathEntry();
				if (entry != null) {
					resolvedEntries.add(entry);
				}
			}
			this.entries = resolvedEntries.toArray(new IClasspathEntry[resolvedEntries.size()]);
		}
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public IClasspathEntry[] getEntries() {
		return this.entries;
	}
	
	public IPath getPath() {
		return path;
	}
}
