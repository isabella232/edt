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
package org.eclipse.edt.ide.compiler.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.ide.compiler.EDTCompilerIDEPlugin;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class WorthlessGenerator extends JavaGenerator {
	
	public WorthlessGenerator() {
		runtimeContainers = null;
	}
	
	@Override
	public void generate(String filePath, Part part, IEnvironment env, IGenerationMessageRequestor msgRequestor) throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		EclipseEGL2Java cmd = new EclipseEGL2Java(file, part, this);
		cmd.generate(buildArgs(file, part), new EclipseWorthlessGenerator(cmd, msgRequestor), env, null);
	}
	
	private static class EclipseWorthlessGenerator extends EclipseJavaGenerator {

		public EclipseWorthlessGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
			super(processor, requestor);
		}
		
		public void generate(Part part) throws GenerationException {
			String packageName = CommonUtilities.packageName(part);
			if (packageName != null && packageName.length() > 0) {
				out.print("package ");
				out.print(packageName);
				out.println(';');
			}
			
			out.print("class ");
			String nameOrAlias;
			Annotation annot = part.getAnnotation(IEGLConstants.PROPERTY_ALIAS);
			if (annot != null)
				nameOrAlias = (String) annot.getValue();
			else
				nameOrAlias = part.getId();
			out.print(nameOrAlias);
			out.println(" {");
			out.println("// I'm a completely worthless generator!");
			out.println("}");
		}
	}
	
	@Override
	protected String getGenerationDirectoryPropertyKey() {
		return "worthlessGenDirectory";
	}

	@Override
	protected String getGenerationDirectoryPreferenceKey() {
		return EDTCompilerIDEPlugin.PLUGIN_ID + ".worthlessGenDefaultDirectory";
	}
}
