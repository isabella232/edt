/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

/**
 * A move refactoring which can be initialized with refactoring arguments.
 */
public class MoveRefactoring extends org.eclipse.ltk.core.refactoring.participants.MoveRefactoring {

	/**
	 * Creates a new EGL move refactoring.
	 * 
	 * @param processor
	 *            the move processor to use
	 */
	public MoveRefactoring(final org.eclipse.ltk.core.refactoring.participants.MoveProcessor processor) {
		super(processor);
	}
}
