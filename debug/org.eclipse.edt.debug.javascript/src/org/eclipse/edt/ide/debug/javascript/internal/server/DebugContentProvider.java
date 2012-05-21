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
package org.eclipse.edt.ide.debug.javascript.internal.server;

import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.ide.debug.javascript.internal.generators.DebugHTMLGenerator;
import org.eclipse.edt.ide.rui.server.SavedContentProvider;

public class DebugContentProvider extends SavedContentProvider
{
	@Override
	protected HTMLGenerator getDevelopmentGenerator( AbstractGeneratorCommand processor, List egldds, HashMap eglProperties, String userMsgLocale,
			String runtimeMsgLocale )
	{
		return new DebugHTMLGenerator( processor, egldds, eglProperties, userMsgLocale, runtimeMsgLocale );
	}
}
