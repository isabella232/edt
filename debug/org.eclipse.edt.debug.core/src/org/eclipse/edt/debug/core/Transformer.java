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
package org.eclipse.edt.debug.core;

import java.lang.instrument.Instrumentation;

/**
 * Adds instrumentation support for the SMAP files. Put this and {@link SMAPTransformer} in a jar and add the jar to the -javaagent VM argument.
 * 
 * @see SMAPTransformer
 */
public class Transformer
{
	public static void premain( String agentArguments, Instrumentation instrumentation )
	{
		instrumentation.addTransformer( new SMAPTransformer( agentArguments ) );
	}
	
	public static void agentmain( String agentArguments, Instrumentation instrumentation )
	{
		instrumentation.addTransformer( new SMAPTransformer( agentArguments ) );
	}
}
