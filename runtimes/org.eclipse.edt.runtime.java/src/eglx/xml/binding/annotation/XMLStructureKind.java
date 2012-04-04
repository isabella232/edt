/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.xml.binding.annotation;
public enum XMLStructureKind {
	choice(1),
	sequence(2),
	simpleContent(3),
	unordered(4);
	private final int value;
	private XMLStructureKind(int value) {
		this.value = value;
	}
	private XMLStructureKind() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
