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
package org.eclipse.edt.ide.internal.testserver;

import org.eclipse.jetty.util.log.StdErrLog;

/**
 * Logger that allows us to be really quiet.
 */
public class Logger extends StdErrLog {
	
	public Logger() {
		super();
	}
	
	public Logger(String name) {
		super(name);
	}
	
	@Override
	protected org.eclipse.jetty.util.log.Logger newLogger(String name) {
		Logger log = new Logger(name);
		log.setDebugEnabled(isDebugEnabled());
		log.setHideStacks(isHideStacks());
		log.setPrintLongNames(isPrintLongNames());
		log.setSource(isSource());
		return log;
	}
	
	@Override
	public void debug(Throwable t) {
		if (isDebugEnabled()) {
			super.debug(t);
		}
	}

	@Override
	public void debug(String s, Object... o) {
		if (isDebugEnabled()) {
			super.debug(s, o);
		}
	}

	@Override
	public void debug(String s, Throwable t) {
		if (isDebugEnabled()) {
			super.debug(s, t);
		}
	}

	@Override
	public void info(Throwable t) {
		if (isDebugEnabled()) {
			super.info(t);
		}
	}

	@Override
	public void info(String s, Object... o) {
		if (isDebugEnabled()) {
			super.info(s, o);
		}
	}

	@Override
	public void info(String s, Throwable t) {
		if (isDebugEnabled()) {
			super.info(s, t);
		}
	}

	@Override
	public void warn(Throwable t) {
		if (isDebugEnabled()) {
			super.warn(t);
		}
	}

	@Override
	public void warn(String s, Object... o) {
		if (isDebugEnabled()) {
			super.warn(s, o);
		}
	}

	@Override
	public void warn(String s, Throwable t) {
		if (isDebugEnabled()) {
			super.warn(s, t);
		}
	}
}
