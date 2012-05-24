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
package org.eclipse.edt.ide.compiler.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;
import org.eclipse.edt.mof.egl.Part;

public class GenerationReport {

	public static void writeFile(Part part, IFile eglFile, Generator generator) throws Exception {
		writeFile(part, eglFile, generator, "");
	}

	public static void writeFile(Part part, IFile eglFile, Generator generator, String suffix) throws Exception {
		// write out generation report if there is one
		TabbedReportWriter report = generator.getReport();
		if (report != null) {
			{
				Object outputFolderParm = generator.getContext().getParameter(Constants.parameter_report_dir);
				String outputFolder = (outputFolderParm == null ? Constants.parameter_report_dir_default
																: outputFolderParm.toString());
				if (EclipseUtilities.shouldWriteFileInEclipse(outputFolder)) {
					String fn = generator.getRelativeFileName(part) + ((suffix == null) ? "" : suffix) + Constants.report_fileExtension;
					String rpt = report.rpt.getWriter().toString();
					EclipseUtilities.writeFileInEclipse(outputFolder, eglFile, rpt, fn);
				}
			}
		}
	}

}
