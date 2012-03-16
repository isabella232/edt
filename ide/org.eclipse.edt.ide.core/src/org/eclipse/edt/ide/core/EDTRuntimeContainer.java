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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A runtime container contains zero or more classpath entries (though an empty container is useless).
 */
public class EDTRuntimeContainer implements Comparable<Object> {
	
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
	 * The container's entries.
	 */
	private final EDTRuntimeContainerEntry[] entries;
	
	/**
	 * The path stored in the .classpath file, based on the id.
	 */
	private final IPath path;
	
	public EDTRuntimeContainer(String id, String name, String description, EDTRuntimeContainerEntry[] entries) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.path = new Path(EDTCoreIDEPlugin.EDT_CONTAINER_ID).append(this.id);
		this.entries = entries;
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
	
	public EDTRuntimeContainerEntry[] getEntries() {
		return this.entries;
	}
	
	public IPath getPath() {
		return path;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof EDTRuntimeContainer) {
			return this.name.compareTo(((EDTRuntimeContainer)o).name);
		}
		return 0;
	}
}
