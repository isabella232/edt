/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class DTO2EglSource extends AbstractGeneratorCommand {
	
	public static final String DATA_DEFINITION_OBJECT = "dataDefinition";
	public static final String TABLE_NAME_QUALIFIED = "tableNameQualified";
	public static final String DB_MESSAGE_HANDLER = "dbMessageHandler";
	public static final String PROGRESS_MONITOR = "monitor";
	
	private String contributionClass;

	public DTO2EglSource() {
		super();
	}
	
	public DTO2EglSource(String contributionClass) {
		super();
		this.setContributionClass(contributionClass);
	}

	public void generate(Object object, EglSourceGenerator generator, IEnvironment environment) {
		try {
			if (environment != null){
				Environment.pushEnv(environment);
			}
			
			// process the arguments and load the contributions
			if (initialize(buildArgs(), generator)) {
				// start up the generator, passing the command processor
				try {				
					generator.generate(object);
				}
				catch (Exception e) {
					e.printStackTrace();
					if (generator != null)
						System.out.print(generator.getResult());
				}
				generator.dumpErrorMessages();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (environment != null){
				Environment.popEnv();
			}
		}
	}
	
	private String[] buildArgs() {
		String[] args = new String[8];

		// this isn't used but it's a required parameter.
		args[0] = "-o";
		args[1] = "notused";

		// this isn't used but it's a required parameter.
		args[2] = "-p";
		args[3] = "notused";

		// this isn't used but it's a required parameter.
		args[4] = "-r";
		args[5] = "notused";

		// add the contribution
		args[6] = "-c";
		args[7] = contributionClass;

		return args;
	}	

	public String getContributionClass() {
		return contributionClass;
	}

	public void setContributionClass(String contributionClass) {
		this.contributionClass = contributionClass;
	}
}
