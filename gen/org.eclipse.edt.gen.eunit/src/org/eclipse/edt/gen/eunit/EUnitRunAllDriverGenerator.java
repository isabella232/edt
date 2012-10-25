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
package org.eclipse.edt.gen.eunit;

import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.mof.egl.Part;

public abstract class EUnitRunAllDriverGenerator extends EUnitGenerator {

	protected static final String RunAllTest = "RunAllTests";
	
	public EUnitRunAllDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor msgReq, String driverPartNameAppend, IEUnitGenerationNotifier eckGenerationNotifier) {
		super(processor, msgReq, eckGenerationNotifier);
		fDriverPartNameAppend = driverPartNameAppend;
	}
	
	@Override
	public String getRelativeFileName(Part part) {
		String fileName = RunAllTest;
		fileName += fDriverPartNameAppend;
		fileName = CommonUtilities.prependECKGen(fileName);
		return fileName.replaceAll("\\.", "/") + this.getFileExtension();		
	}

	protected void genImports() {
		out.println("import eglx.ui.rui.RUIHandler;");
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestExecutionLib;");
		out.println();
	}

	protected void genPackageDeclaration() {
		out.println("package " + CommonUtilities.EUNITGEN_ROOT + ";");
		out.println();
	}
	
	public abstract void generateRunAllDriver(List<String> listOfGenedLibs, TestCounter totalCnts);
}