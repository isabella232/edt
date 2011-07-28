/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services.bindings;

public class NativeBinding extends Binding {

	private static final long serialVersionUID = 70L;
	
	private Protocol protocol;
	private boolean isServiceProgram;
	
	public NativeBinding(String name)
	{
		super(name);
		this.name = name;
		isServiceProgram = true;
	}

	public NativeBinding(String name, boolean isServiceProgram, Protocol protocol )
	{
		super(name);
		this.name = name;
		this.isServiceProgram = isServiceProgram;
		this.protocol = protocol;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	
	public int getBindingType()
	{
		return NATIVEBINDING;
	}
	
	public boolean isServiceProgram()
	{
		return isServiceProgram;
	}
}
