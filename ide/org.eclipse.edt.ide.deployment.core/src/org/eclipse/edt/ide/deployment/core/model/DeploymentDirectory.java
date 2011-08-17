/*******************************************************************************
 * Copyright © 2009, 2011 IBM Corporation and others.
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

public class DeploymentDirectory extends DeploymentTarget
{
	private String location;
	
	public DeploymentDirectory( String location )
	{
		super();
		this.location = location;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation( String location )
	{
		this.location = location;
	}
	
	public int getTargetType() {
		return TARGET_DIRECTORY;
	}
}
