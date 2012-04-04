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
package org.eclipse.edt.compiler.internal;

import org.eclipse.core.resources.IResource;


/**
 * @author Harmon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IEGLBuildDescriptorLocator {

	// RBD 8.0 Build Descriptor Types
	public static final int RUNTIME_DEFAULT_JAVA_BUILD_DESCRIPTOR_TYPE = 20;
	public static final int RUNTIME_DEFAULT_JAVASCRIPT_BUILD_DESCRIPTOR_TYPE = 21;
	public static final int RUNTIME_DEFAULT_COBOL_BUILD_DESCRIPTOR_TYPE = 22;
	public static final int DEBUG_DEFAULT_JAVASCRIPT_BUILD_DESCRIPTOR_TYPE = 23;
	public static final int DEBUG_DEFAULT_NON_JAVASCRIPT_BUILD_DESCRIPTOR_TYPE = 24;
	
	IEGLPartWrapper locateDefaultBuildDescriptor(int bdType, IResource defaultResource);
	DefaultBuildDescriptorResult locateRuntimeDefaultBuildDescriptors(IResource defaultResource);
	DefaultBuildDescriptorResult locateDebugDefaultBuildDescriptors(IResource defaultResource);
}
