/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core.model;

public class DeploymentBuildDescriptor extends DeploymentTarget 
{
	private String name;
	public DeploymentBuildDescriptor(String name, String fileName) {
		super();
		this.name = name;
		this.fileName = fileName;
	}
	private String fileName;

	public String getName() {
		return name;
	}
	public String getFileName() {
		return fileName;
	}
	public int getTargetType() {
		return DeploymentTarget.TARGET_BUILD_DESCRIPTOR;
	}
}
