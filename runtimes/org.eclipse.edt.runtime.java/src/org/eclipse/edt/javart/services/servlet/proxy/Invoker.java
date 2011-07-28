/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.javart.services.servlet.proxy;

import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Trace;

import eglx._service.ServiceKind;
import eglx.http.HttpRequest;
import eglx.http.HttpResponse;

abstract class Invoker {
	private Trace tracer;
	private ExecutableBase program;
	
	protected Invoker(ExecutableBase program) {
		this.program = program;
	}
	protected Trace tracer()
	{
		if( tracer == null )
		{
			tracer = program()._runUnit().getTrace();
		}
		return tracer;
	}
	
	protected boolean trace()
    {
		return tracer().traceIsOn( Trace.GENERAL_TRACE ); 
    }
	protected ExecutableBase program() {
		return program;
	}

	abstract HttpResponse invoke( RuiBrowserHttpRequest ruiRequest, HttpRequest innerRequest ) throws Exception;
	abstract ServiceKind getServiceKind(HttpRequest innerRequest);
}
