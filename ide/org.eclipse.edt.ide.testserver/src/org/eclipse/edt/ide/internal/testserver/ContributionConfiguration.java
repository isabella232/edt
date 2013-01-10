/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.edt.ide.testserver.AbstractConfigurator;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * A Jetty Configuration that invokes the registered {@link AbstractConfigurator}s during
 * the various configure phases.
 */
public class ContributionConfiguration extends AbstractConfiguration {
	
	private List<AbstractConfigurator> contributions;
	
	public void setContributions(List<AbstractConfigurator> contributions) {
		this.contributions = contributions;
	}
	
	public List<AbstractConfigurator> getContributions() {
		return contributions;
	}
	
	@Override
	public void preConfigure(WebAppContext context) throws Exception {
		if (contributions != null) {
			for (AbstractConfigurator contrib : contributions) {
				contrib.preConfigure();
			}
		}
	}
	
	@Override
	public void configure(WebAppContext context) throws Exception {
		if (contributions != null) {
			for (AbstractConfigurator contrib : contributions) {
				contrib.configure();
			}
		}
	}
	
	@Override
	public void postConfigure(WebAppContext context) throws Exception {
		if (contributions != null) {
			for (AbstractConfigurator contrib : contributions) {
				contrib.postConfigure();
			}
		}
	}
}
