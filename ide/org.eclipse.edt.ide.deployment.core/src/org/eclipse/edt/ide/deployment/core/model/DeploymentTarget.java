/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.javart.resources.egldd.Parameter;

public class DeploymentTarget
{
	public static final int TARGET_PROJECT = 3;
	
	protected List parameters;
	
	public DeploymentTarget()
	{
		this.parameters = new ArrayList();
	}
	
	public void addParameter(Parameter param)
	{
		parameters.add(param);
	}

	public List getParameters()
	{
		return parameters;
	}
	
	public int getTargetType()
	{
		return 0;
	}
}
