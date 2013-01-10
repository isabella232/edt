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
package org.eclipse.edt.compiler.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;

public class DefaultBuildDescriptorResult {

	private IResource resource;
	private Map<Integer, PartWrapper> buildDescriptorMap = new HashMap<Integer, PartWrapper>();
	
	public IResource getResource() {
		return resource;
	}
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	public PartWrapper getBuildDescriptor(int bdType) {
		return buildDescriptorMap.get(bdType);
	}
	public void setBuildDescriptor(int bdType, PartWrapper buildDescriptor) {
		this.buildDescriptorMap.put(bdType, buildDescriptor);
	}	
	public boolean hasDefaultBuildDescriptor(){
		return !buildDescriptorMap.isEmpty();
	}
	public int[] getBuildDescriptorTypes(){
		Set<Integer> keySet = buildDescriptorMap.keySet();
		int[] result = new int[keySet.size()];
		int i = 0;
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();i++) {
			Integer integer = (Integer) iterator.next();
			result[i] = integer.intValue();
		}
		return result;
	}
}
