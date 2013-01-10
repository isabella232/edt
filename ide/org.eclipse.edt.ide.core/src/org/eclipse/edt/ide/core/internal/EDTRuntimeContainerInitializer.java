/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.EDTRuntimeContainerEntry;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Does the conversion of the container ID to a resolved classpath container for EDTRuntimeContainers.
 */
public class EDTRuntimeContainerInitializer extends ClasspathContainerInitializer {

	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		if (isValidEDTContainerPath(containerPath)) {
			// Find the corresponding contributed path.
			IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
			if (gens == null || gens.length == 0) {
				return;
			}
			
			String containerId = containerPath.segment(1);
			for (int i = 0; i < gens.length; i++) {
				EDTRuntimeContainer[] containers = gens[i].getRuntimeContainers();
				if (containers != null && containers.length != 0) {
					for (int j = 0; j < containers.length; j++) {
						if (containerId.equals(containers[j].getId())) {
							EDTContainer container = new EDTContainer(containerPath, containers[j].getEntries(), containers[j].getName());
							JavaCore.setClasspathContainer(containerPath, new IJavaProject[]{project}, 	new IClasspathContainer[]{container}, null);
							return;
						}
					}
				}
			}
		}
	}
	
	private boolean isValidEDTContainerPath(IPath path) {
		return path != null && path.segmentCount() == 2 && EDTCoreIDEPlugin.EDT_CONTAINER_ID.equals(path.segment(0));
	}
	
	@Override
	public Object getComparisonID(IPath containerPath, IJavaProject project) {
		return containerPath;
	}
	
	private static class EDTContainer implements IClasspathContainer {
		private final IPath path;
		private final IClasspathEntry[] classpathEntries;
		private final String description;
		
		public EDTContainer(IPath path, EDTRuntimeContainerEntry[] entries, String description) {
			this.path = path;
			this.description = description;
			
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>(entries.length);
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i].getClasspathEntry();
				if (entry != null) {
					list.add(entry);
				}
			}
			this.classpathEntries = list.toArray(new IClasspathEntry[list.size()]);
		}
		public IClasspathEntry[] getClasspathEntries() {
			return classpathEntries;
		}

		public String getDescription() {
			return description;
		}

		public int getKind() {
			return IClasspathContainer.K_APPLICATION;
		}

		public IPath getPath() {
			return path;
		}
	}
}
