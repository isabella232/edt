/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.rename;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;

/**
 * A rename refactoring which can be initialized with refactoring arguments.
 */
public class RenameRefactoring extends org.eclipse.ltk.core.refactoring.participants.RenameRefactoring {

	/**
	 * Creates a new EGL rename refactoring.
	 * 
	 * @param processor
	 *            the rename processor to use
	 */
	public RenameRefactoring(final RenameProcessor processor) {
		super(processor);
	}

	/**
	 * {@inheritDoc}
	 */
	public final RefactoringStatus initialize(final RefactoringArguments arguments) {
		final RefactoringProcessor processor= getProcessor();
		return new RefactoringStatus();
	}
}
