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

import java.util.HashSet;
import java.util.Set;

/**
 * Logger that simply wraps the given logger and keeps track of all the named loggers that are requested.
 * This way when the debug setting changes, we can apply it to all loggers.
 */
public class Logger implements org.eclipse.jetty.util.log.Logger {
	
	private final org.eclipse.jetty.util.log.Logger logger;
	private final Set<String> names;
	
	public Logger(org.eclipse.jetty.util.log.Logger realLogger) {
		this.logger = realLogger;
		this.names = new HashSet<String>();
	}
	
	@Override
	public void debug(Throwable t) {
		logger.debug(t);
	}

	@Override
	public void debug(String s, Object... o) {
		logger.debug(s, o);
	}

	@Override
	public void debug(String s, Throwable t) {
		logger.debug(s, t);
	}

	@Override
	public org.eclipse.jetty.util.log.Logger getLogger(String s) {
		names.add(s);
		return logger.getLogger(s);
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public void ignore(Throwable t) {
		logger.ignore(t);
	}

	@Override
	public void info(Throwable t) {
		logger.info(t);
	}

	@Override
	public void info(String s, Object... o) {
		logger.info(s, o);
	}

	@Override
	public void info(String s, Throwable t) {
		logger.info(s, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void setDebugEnabled(boolean b) {
		logger.setDebugEnabled(b);
		
		for (String name : names) {
			logger.getLogger(name).setDebugEnabled(b);
		}
	}

	@Override
	public void warn(Throwable t) {
		logger.warn(t);
	}

	@Override
	public void warn(String s, Object... o) {
		logger.warn(s, o);
	}

	@Override
	public void warn(String s, Throwable t) {
		logger.warn(s, t);
	}
}
