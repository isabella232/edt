/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.ide.core.internal.binding.BinaryFileManager;
import org.eclipse.edt.ide.core.internal.generation.GenerationQueue.GenerationUnit;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectIREnvironment;

/**
 * Generates just the IRs that are part of the build deltas.
 */
public class IncrementalGenerator extends AbstractGenerator {
	
	public IncrementalGenerator(GenerationBuilder builder, IBuildNotifier notifier) {
		super(builder, notifier);
	}

	@Override
	protected void addAdditionalParts() {
		// For incremental builds we must force the IRs to be re-read from disk by clearing out the cached entries.
		// This allows users to hand-modify an IR, or run a tool on IRs, and get the latest contents generated.
		ProjectIREnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(builder.getProject()).getIREnvironment();
		for (GenerationUnit unit : this.generationQueue.pendingUnits.values()) {
			env.remove(BinaryFileManager.eglIRKey(unit.packageName, unit.caseSensitiveInternedPartName));
			env.remove(BinaryFileManager.mofIRKey(unit.packageName, unit.caseSensitiveInternedPartName));
		}
	}
}
