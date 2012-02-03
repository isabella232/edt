/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.gen.generator.egl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.egl.EglGenerator;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class Java2EGL extends AbstractGeneratorCommand {

	public Java2EGL() {
		super();
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		Java2EGL genPart = new Java2EGL();
		genPart.generate(args, new EglGenerator(genPart), null);
	}

	public void generate(String[] args, EglGenerator generator, IEnvironment environment) {
		// process the arguments and load the configurators
		if (initialize(args, generator)) {
			try {
				if (environment != null)
					Environment.pushEnv(environment);
				// start up the generator, passing the command processor
				try {
					List<Class<?>> clazzes = loadClasses();
					for (Class<?> clazz : clazzes) {
						generator.generate(clazz);
						// now try to write out the file, based on the output location and the part's type signature
						try {
							// only write the data, if there was some
							if (generator.getResult() != null)
								writeFile(clazz, generator);
						}
						catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					if (generator != null)
						System.out.print(generator.getResult());
				}
				generator.dumpErrorMessages();
			}
			finally {
				if (environment != null)
					Environment.popEnv();
			}
		}
	}

	protected void writeFile(Class<?> clazz, EglGenerator generator) throws Exception {
		IEnvironment env = Environment.getCurrentEnv();
		if (env != null && generator.getResult() != null) {
			env.save(generator.getResult());
		}
	}

	protected List<Class<?>> loadClasses() throws Exception {
		List<Class<?>> parts = new ArrayList<Class<?>>();
		// check to see if the part has an asterisk, indicating that all matching files are desired
		String rootName = (String) getParameterMapping().get(Constants.parameter_root).getValue();
		String partName = (String) getParameterMapping().get(Constants.parameter_part).getValue();
		if (partName.indexOf("*") >= 0) {
			// we need to locate the root class location and can it's directory for classes matching the pattern defined
			String totalName;
			if (rootName.endsWith(File.separator))
				totalName = rootName;
			else
				totalName = rootName + File.separator;
			if (partName.startsWith(File.separator))
				totalName += partName.substring(File.separator.length());
			else
				totalName += partName;
			totalName = totalName.replace('\\', File.separatorChar);
			totalName = totalName.replace('/', File.separatorChar);
			totalName = totalName.replace('.', File.separatorChar);
			File classRootDirFile = new File(rootName);
			List<File> classfiles = new ArrayList<File>();
			listAllFiles(classRootDirFile, classfiles, totalName);
			for (File classfile : classfiles) {
				String classFileRelativePath = classfile.getAbsolutePath().substring(classRootDirFile.getAbsolutePath().length());
				if (classFileRelativePath.startsWith(File.separator))
					classFileRelativePath = classFileRelativePath.substring(File.separator.length());
				parts.add(getClassLoader().loadClass(
					classFileRelativePath.substring(0, classFileRelativePath.lastIndexOf(".")).replace(File.separatorChar, '.')));
			}
		} else
			parts.add(getClassLoader().loadClass(partName));
		return parts;
	}

	protected ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	private void listAllFiles(File parent, List<File> files, String pattern) {
		if (parent.isDirectory()) {
			File[] children = parent.listFiles();
			for (File child : children) {
				if (child.isFile() && (child.getName().toLowerCase(Locale.ENGLISH).endsWith(".class"))) {
					// get the full name of the file
					String name = child.getPath();
					// get the prefix length to check, which is the amount up to the asterisk (only 1 asterisk allowed)
					int prefixLength = pattern.indexOf("*");
					// does the file we are trying match up until the asterisk
					if (name.length() >= prefixLength && name.substring(0, prefixLength).equalsIgnoreCase(pattern.substring(0, prefixLength))) {
						// the prefix matches, so now check to see if there is any suffix and check it as well
						if (pattern.length() <= (prefixLength + 1) || name.toLowerCase(Locale.ENGLISH).endsWith(pattern.substring(prefixLength + 1) + ".class"))
							files.add(child);
					}
				}
				listAllFiles(child, files, pattern);
			}
		}
	}
}
