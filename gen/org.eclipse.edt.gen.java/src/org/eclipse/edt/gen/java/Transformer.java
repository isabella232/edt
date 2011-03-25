/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java;

import java.lang.instrument.Instrumentation;

public class Transformer {
	public static void premain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new EglTransformer(agentArguments));
	}

	public static void agentmain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new EglTransformer(agentArguments));
	}
}
