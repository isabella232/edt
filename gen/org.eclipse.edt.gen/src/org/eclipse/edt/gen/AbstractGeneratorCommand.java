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

import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRLoader;
import org.eclipse.edt.mof.egl.utils.LoadPartException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public abstract class AbstractGeneratorCommand extends CommandProcessor {

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
		this.installParameter(false, Constants.parameter_trace, new String[] { "trace" }, new Boolean[] { false, true },
			"Trace must be defined as true or false");
		// accept the overriden properties file lists
		String[] templateList = this.getTemplatePath();
		for (String template : templateList) {
			templates = templates + template + ";";
		}
		String[] nativeTypeList = this.getNativeTypePath();
		for (String nativeType : nativeTypeList) {
			nativeTypes = nativeTypes + nativeType + ";";
		}
		String[] primitiveTypeList = this.getPrimitiveTypePath();
		for (String primitiveType : primitiveTypeList) {
			primitiveTypes = primitiveTypes + primitiveType + ";";
		}
		String[] EGLMessageList = this.getEGLMessagePath();
		for (String EGLMessage : EGLMessageList) {
			EGLMessages = EGLMessages + EGLMessage + ";";
		}
	}

	public String getTemplates() {
		return templates;
	}

	public String getNativeTypes() {
		return nativeTypes;
	}

	public String getPrimitiveTypes() {
		return primitiveTypes;
	}

	public String getEGLMessages() {
		return EGLMessages;
	}

	// the command processor must implement the method for providing the file extention to write files out as
	public abstract String getFileExtention();

	// the command processor must implement the method for providing the location of the nativeTypes.properties files
	public abstract String[] getNativeTypePath();

	// the command processor must implement the method for providing the location of the primitiveTypes.properties files
	public abstract String[] getPrimitiveTypePath();

	// the command processor must implement the method for providing the location of the EGLMessages.properties files
	public abstract String[] getEGLMessagePath();

	// the command processor must implement the method for providing the location of the templates.properties files
	public abstract String[] getTemplatePath();

	public void generate(String[] args, Generator generator, IEnvironment environment) {
		try {
			if (environment != null)
				Environment.pushEnv(environment);
			this.installOverrides(args);
			// start up the generator, passing the command processor
			try {
				List<Part> parts = loadEGLParts();
				for (Part part : parts) {
					generator.generate(part);
					// now try to write out the file, based on the output location and the part's type signature
					try {
						// only write the data, if there was some
						if (generator.getResult().length() > 0)
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
		catch (PromptQueryException e) {
			System.out.print(e.getMessage());
		}
		catch (UnknownParameterException e) {
			System.out.print("This parameter is unknown: " + e.getMessage());
		}
		catch (MissingParameterValueException e) {
			System.out.print("This value for this parameter is missing: " + e.getMessage());
		}
		catch (InvalidParameterValueException e) {
			System.out.print("This value for this parameter is incorrect: " + e.getMessage());
		}
		finally {
			if (environment != null) {
				Environment.popEnv();
			}
		}
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

	protected List<Part> loadEGLParts() throws LoadPartException {
		List<Part> parts = new ArrayList<Part>();
		// check to see if the part has an asterisk, indicating that all matching files are desired
		String rootName = (String) parameterMapping.get(Constants.parameter_root).getValue();
		String partName = (String) parameterMapping.get(Constants.parameter_part).getValue();
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
					.add(IRLoader.loadEGLPart(rootName, irFileRelativePath.substring(0, irFileRelativePath.lastIndexOf(".")).replace(File.separatorChar, '.')));
			}
		} else
			parts.add(IRLoader.loadEGLPart(rootName, partName));
		return parts;
	}

	protected void writeFile(Part part, Generator generator) throws Exception {
		String fileName = ((String) parameterMapping.get(Constants.parameter_output).getValue()).replaceAll("\\\\", "/");
		if (!fileName.endsWith("/"))
			fileName = fileName + "/";
		fileName = fileName + getRelativeFileName(part);
		int offset = fileName.lastIndexOf("/");
		if (offset > 0)
			new File(fileName.substring(0, offset)).mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(generator.getResult());
		writer.close();
		// call back to the generator, to see if it wants to do any supplementary tasks
		generator.processFile(fileName);
	}

	/**
	 * By default the relative file name will be the same as the source file, with the generator's file extension. This is
	 * intended to be overridden as necessary.
	 */
	protected String getRelativeFileName(Part part) {
		return part.getTypeSignature().replaceAll("\\.", "/") + this.getFileExtention();
	}
}
