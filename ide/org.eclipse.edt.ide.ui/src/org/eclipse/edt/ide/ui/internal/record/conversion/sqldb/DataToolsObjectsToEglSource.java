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

package org.eclipse.edt.ide.ui.internal.record.conversion.sqldb;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class DataToolsObjectsToEglSource extends AbstractGeneratorCommand {
	
	public static final String DATA_DEFINITION_OBJECT = "dataDefinition";
	public static final String TABLE_NAME_QUALIFIED = "tableNameQualified";
	public static final String DB_MESSAGE_HANDLER = "dbMessageHandler";

	public DataToolsObjectsToEglSource() {
		super();
	}

	public void generate(Object[] objects, EglSourceGenerator generator, IEnvironment environment) {
		try {
			if (environment != null){
				Environment.pushEnv(environment);
			}
			//this.installOverrides(args);
			// start up the generator, passing the command processor
			try {				
				for (Object obj : objects) {
					generator.generate(obj);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				if (generator != null)
					System.out.print(generator.getResult());
			}
			generator.dumpErrorMessages();
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
		finally {
			if (environment != null){
				Environment.popEnv();
			}
		}
	}
}
