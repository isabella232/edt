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
package org.eclipse.edt.gen.deployment.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;
/**
 * The PartRefernceCache retrieves all referenced, Non-System part ordered by dependency. 
 * The result is cached
 *  
 */
public class PartReferenceCache {
	private Map<String,Set<Part>> cache;
	private IEnvironment sysIREnv=null;
	
	public PartReferenceCache(IEnvironment env){
		this.sysIREnv = env;
		cache = new HashMap<String, Set<Part>>();
	}

	public Set<Part> getReferencedPartsFor(Part part) {
		
		Set<Part> parts = cache.get(part.getFullyQualifiedName());
		if(parts == null){
			parts = new LinkedHashSet<Part>();
			getReferencedParts(part, parts);
			parts.remove(part);
			cache.put(part.getFullyQualifiedName(), parts);
		}
		return parts;
	}
	
	private void getReferencedParts(Part part, Set<Part> allParts){
		// ExternalType is processed multiple times in order to keep the inherit dependency. 
		// A super type will always appear later in the list then the sub types
		if(allParts.contains(part)){
			if(part instanceof ExternalType){
				allParts.remove(part);
			}else{
				return;
			}
		}
		allParts.add(part);
		for(Part refPart: IRUtils.getReferencedPartsFor(part)){
			if(!IRUtils.isSystemPart(refPart.getFullyQualifiedName(), sysIREnv)){
				getReferencedParts(refPart, allParts);
			}
		}
	}
}
