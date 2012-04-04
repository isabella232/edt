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
package org.eclipse.edt.ide.core.internal.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class ResourceAndTLFMap {
	private IResource resource;
	private IPath[] irPaths = new Path[0];

	public ResourceAndTLFMap(IResource iResource, IPath path) {
		this.resource = iResource;
		this.addPath(path);
	}
	
	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public IPath[] getPaths() {
		return irPaths;
	}

	public void setPaths(IPath[] paths) {
		this.irPaths = paths;
	}
	
	public void addPath(IPath path) {
		IPath[] temp = new Path[irPaths.length + 1];
		System.arraycopy(irPaths, 0, temp, 0, irPaths.length);
		temp[temp.length - 1] = path;
		this.irPaths = temp;
	}
	
	public boolean isSamePackage(IResource resource2Compare) {
		if(resource.getFullPath().segmentCount() < 2 || resource2Compare.getFullPath().segmentCount() < 2) {
			return false;
		}
		IPath temp = resource.getFullPath().removeFirstSegments(2).removeLastSegments(1);
		temp = new Path(temp.toString().toLowerCase());
		IPath toCompare = resource2Compare.getFullPath().removeFirstSegments(2).removeLastSegments(1);
		toCompare = new Path(toCompare.toString().toLowerCase());
		if(temp.equals(toCompare)) {
			return true;
		}
		return false;
	}
	
	public boolean includedPath(IPath path) {
		if(path == null) 
			return false;
		for(IPath p : irPaths) {
			if(p.equals(path)) {
				return true;
			}
		}
		return false;
	}
}
