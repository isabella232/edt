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
package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.edt.ide.core.generation.IGenerationUnit;

public class GenerationUnitImpl implements IGenerationUnit {
	private PartWrapper part = null;
	private IEGLPartWrapper buildDescriptor = null;
	private boolean isGenDebug = false;
	private boolean isAutoGenerate = false;

	public GenerationUnitImpl() {
		super();
	}

	public GenerationUnitImpl(PartWrapper part, IEGLPartWrapper buildDescriptor) {
		super();
		
		this.part = part;
		this.buildDescriptor = buildDescriptor;
	}
	public GenerationUnitImpl(PartWrapper part, IEGLPartWrapper buildDescriptor, boolean isGenDebug) {
		this(part, buildDescriptor);
		this.isGenDebug = isGenDebug;
	}
	
	public GenerationUnitImpl(PartWrapper part, IEGLPartWrapper buildDescriptor, boolean isGenDebug, boolean isAutoGenerate) {
		this(part, buildDescriptor, isGenDebug);
		this.isAutoGenerate = isAutoGenerate;
	}
	
	/*
	 * @see IGenerationUnit#setProgram(Part)
	 */
	public void setPart(PartWrapper part) {
		
		this.part = part;
	}

	/*
	 * @see IGenerationUnit#getProgram()
	 */
	public PartWrapper getPart() {
		return part;
	}

	/*
	 * @see IGenerationUnit#setBuildDescriptor(Part)
	 */
	public void setBuildDescriptor(IEGLPartWrapper buildDescriptor) {
		
		this.buildDescriptor = buildDescriptor;
	}

	/*
	 * @see IGenerationUnit#getBuildDescriptor()
	 */
	public IEGLPartWrapper getBuildDescriptor() {
		return buildDescriptor;
	}

	public boolean isGenDebug() {
		return isGenDebug;
	}
	
	public boolean isAutoGenerate() {
		return isAutoGenerate;
	}
}
