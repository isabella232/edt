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
package org.eclipse.edt.gen;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.tools.IRLoader;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.LoadPartException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public abstract class AbstractGeneratorCommand extends CommandProcessor implements Configurable {

	private String templates = "";
	private String nativeTypes = "";
	private String primitiveTypes = "";
	private String EGLMessages = "";

	public AbstractGeneratorCommand() {
		super();
		// define basic command parameters
		this.installParameter(true, Constants.parameter_output, new String[] { "output", "out", "o" }, new String[] { null },
			"Output must identify the location to write the output");
		this.installParameter(true, Constants.parameter_part, new String[] { "part", "p" }, new String[] { null },
			"Part must identify the part to be generated, which can contain an * for all matching parts");
		this.installParameter(true, Constants.parameter_root, new String[] { "root", "r" }, new String[] { null },
			"Root must identify the root location to be used in generation");
		this.installParameter(true, Constants.parameter_configuration, new String[] { "configuration", "config", "c" }, new Object[] { null },
			"Configuration must identify the configurator classes used in generation");
	}

	public String getTemplates() {
		String[] templateList = getTemplatePath().toArray(new String[getTemplatePath().size()]);
		for (String template : templateList) {
			templates = templates + template + ";";
		}
		return templates;
	}

	public String getNativeTypes() {
		String[] nativeTypeList = getNativeTypePath().toArray(new String[getNativeTypePath().size()]);
		for (String nativeType : nativeTypeList) {
			nativeTypes = nativeTypes + nativeType + ";";
		}
		return nativeTypes;
	}

	public String getPrimitiveTypes() {
		String[] primitiveTypeList = getPrimitiveTypePath().toArray(new String[getPrimitiveTypePath().size()]);
		for (String primitiveType : primitiveTypeList) {
			primitiveTypes = primitiveTypes + primitiveType + ";";
		}
		return primitiveTypes;
	}

	public String getEGLMessages() {
		String[] EGLMessageList = getMessagePath().toArray(new String[getMessagePath().size()]);
		for (String EGLMessage : EGLMessageList) {
			EGLMessages = EGLMessages + EGLMessage + ";";
		}
		return EGLMessages;
	}

	public void generate(String[] args, Generator generator, IEnvironment environment, ICompiler compiler) {
		// process the arguments and load the configurators
		if (initialize(args, generator)) {
			try {
				if (environment != null) {
					Environment.pushEnv(environment);
					generator.getContext().setEnvironment(environment);
				}
				// start up the generator, passing the command processor
				try {
					List<Part> parts = loadEGLParts(compiler);
					for (Part part : parts) {
						generator.generate(part);
						// now try to write out the file, based on the output location and the part's type signature
						try {
							// only write the data, if there was some
							if (generator.getResult() instanceof String && ((String) generator.getResult()).length() > 0)
								writeFile(part, generator);
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
				if (environment != null) {
					Environment.popEnv();
				}
			}
		}
	}

	protected boolean initialize(String[] args, Generator generator) {
		// process the arguments and load the configurators
		if (processBase(args)) {
			// process all of the configuration modules
			Object[] configurators = (Object[]) getParameter(Constants.parameter_configuration).getValue();
			for (Object configurator : configurators) {
				// obtain and load the requested configurator
				try {
					// load the configurator class
					Class<?> clazz = Class.forName((String) configurator, true, getClass().getClassLoader());
					// process the configuration module by invoking the configure method
					Configurator config = (Configurator) clazz.newInstance();
					config.configure(this);
				}
				catch (Exception x) {
					System.out.println("Exception: " + x.getMessage());
					System.out.println("Unable to load: " + configurator + ". Generation aborted.");
					return false;
				}
			}
			// now process the non-base (user) command line options
			if (processUser(args)) {
				// load the template path and template factories
				generator.initialize(this);
				return true;
			}
		}
		return false;
	}
	
	private void listAllFiles(File parent, List<File> files, String pattern) {
		if (parent.isDirectory()) {
			File[] children = parent.listFiles();
			for (File child : children) {
				if (child.isFile()
					&& (child.getName().toLowerCase(Locale.ENGLISH).endsWith(".eglxml") || child.getName().toLowerCase(Locale.ENGLISH).endsWith(".ir"))) {
					// get the full name of the file
					String name = child.getPath();
					// get the prefix length to check, which is the amount up to the asterisk (only 1 asterisk allowed)
					int prefixLength = pattern.indexOf("*");
					// does the file we are trying match up until the asterisk
					if (name.length() >= prefixLength && name.substring(0, prefixLength).equalsIgnoreCase(pattern.substring(0, prefixLength))) {
						// the prefix matches, so now check to see if there is any suffix and check it as well
						if (pattern.length() <= (prefixLength + 1)
							|| name.toLowerCase(Locale.ENGLISH).endsWith(pattern.substring(prefixLength + 1) + ".eglxml")
							|| name.toLowerCase(Locale.ENGLISH).endsWith(pattern.substring(prefixLength + 1) + ".ir"))
							files.add(child);
					}
				}
				listAllFiles(child, files, pattern);
			}
		}
	}

	protected List<Part> loadEGLParts(ICompiler compiler) throws LoadPartException {
		List<Part> parts = new ArrayList<Part>();
		// check to see if the part has an asterisk, indicating that all matching files are desired
		String rootName = (String) getParameter(Constants.parameter_root).getValue();
		String partName = (String) getParameter(Constants.parameter_part).getValue();
		if (partName.indexOf("*") >= 0) {
			// we need to locate the root ir location and can it's directory for parts matching the pattern defined
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
			File irRootDirFile = new File(rootName);
			List<File> irfiles = new ArrayList<File>();
			listAllFiles(irRootDirFile, irfiles, totalName);
			for (File irfile : irfiles) {
				String irFileRelativePath = irfile.getAbsolutePath().substring(irRootDirFile.getAbsolutePath().length());
				if (irFileRelativePath.startsWith(File.separator))
					irFileRelativePath = irFileRelativePath.substring(File.separator.length());
				parts
				// TODO should pass the compiler to IRLoader
					.add(IRLoader.loadEGLPart(rootName, irFileRelativePath.substring(0, irFileRelativePath.lastIndexOf(".")).replace(File.separatorChar, '.'),
						null));
			}
		} else
			parts.add(IRLoader.loadEGLPart(rootName, partName, compiler));
		return parts;
	}

	protected void writeFile(Part part, Generator generator) throws Exception {
		String fileName = ((String) getParameter(Constants.parameter_output).getValue()).replaceAll("\\\\", "/");
		if (!fileName.endsWith("/"))
			fileName = fileName + "/";
		fileName = fileName + generator.getRelativeFileName(part);
		int offset = fileName.lastIndexOf("/");
		if (offset > 0)
			new File(fileName.substring(0, offset)).mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(generator.getResult().toString());
		writer.close();
		// call back to the generator, to see if it wants to do any supplementary tasks
		generator.processFile(fileName);
	}

	public void registerCommandOptions(CommandOption[] options) {
		for (CommandOption option : options) {
			this.installParameter(option.getParameter().isRequired(), option.getInternalName(), option.getAliases(), option.getParameter().getPossibleValues(),
				option.getParameter().getPromptText());
		}
	}

	public void registerTemplatePath(String[] paths) {
		for (String path : paths) {
			getTemplatePath().add(path);
		}
	}

	public void registerNativeTypePath(String[] paths) {
		for (String path : paths) {
			getNativeTypePath().add(path);
		}
	}

	public void registerPrimitiveTypePath(String[] paths) {
		for (String path : paths) {
			getPrimitiveTypePath().add(path);
		}
	}

	public void registerMessagePath(String[] paths) {
		for (String path : paths) {
			getMessagePath().add(path);
		}
	}
}
