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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.util.log.StdErrLog;

/**
 * Logger that keeps track of all the named loggers that are requested, so that when the debug setting changes,
 * we can apply it to all loggers. It's also extra quiet when debug messages are disabled.
 */
public class Logger extends StdErrLog {
	
	private final Map<String, Logger> loggers;
	
	public Logger() {
		super();
		this.loggers = new HashMap<String, Logger>();
	}
	
	public Logger(String name) {
		super(name);
		this.loggers = new HashMap<String, Logger>();
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
	public org.eclipse.jetty.util.log.Logger getLogger(String s) {
		// If our name matches, return this.
		if ((s == null && getName() == null) || (s != null && s.equals(getName()))) {
			return this;
		}
		
		Logger log = loggers.get(s);
		if (log != null) {
			return log;
		}
		
		// Need a new one.
		log = new Logger(s);
		log.setDebugEnabled(isDebugEnabled());
		loggers.put(s, log);
		return log;
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
	public void setDebugEnabled(boolean b) {
		super.setDebugEnabled(b);
		
		for (Logger log : loggers.values()) {
			log.setDebugEnabled(b);
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
