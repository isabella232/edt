/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.debug.javascript.internal.generators;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.deployment.javascript.ContextAwareHTMLGenerator;
import org.eclipse.edt.gen.deployment.util.PartReferenceCache;

public class DebugHTMLGenerator extends ContextAwareHTMLGenerator
{
	public DebugHTMLGenerator( AbstractGeneratorCommand processor, List egldds, Set<String> propFiles, HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale,
			ISystemEnvironment sysEnv, PartReferenceCache partRefCache )
	{
		super( processor, egldds, propFiles, eglParameters, userMsgLocale, runtimeMsgLocale, sysEnv, partRefCache );
	}
	
	@Override
	protected boolean isDebug()
	{
		return true;
	}
}
