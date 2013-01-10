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
package org.eclipse.edt.gen.deployment.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;

/**
 * Retrieves all referenced, Non-System parts ordered by dependency. References of services are not calculated. 
 */
public class RUIDependencyList {
	
	private final Part part;
	private final IEnvironment sysIREnv;
	private Set<Part> list;
	
	public RUIDependencyList(IEnvironment env, Part part) {
		this.part = part;
		this.sysIREnv = env;
	}

	public Set<Part> get() {
		if (list == null) {
			list = new LinkedHashSet<Part>();
			getReferencedParts(part);
			list.remove(part);
		}
		return list;
	}
	
	private void getReferencedParts(Part part) {
		// ExternalType is processed multiple times in order to keep the inherit dependency. 
		// A super type will always appear later in the list then the sub types
		if (list.contains(part)) {
			if (part instanceof ExternalType) {
				list.remove(part);
			}
			else {
				return;
			}
		}
		list.add(part);
		for (Part refPart: IRUtils.getReferencedPartsFor(part)) {
			if (!(refPart instanceof Service) && !IRUtils.isSystemPart(refPart.getFullyQualifiedName(), sysIREnv)) {
				getReferencedParts(refPart);
			}
		}
	}
}
