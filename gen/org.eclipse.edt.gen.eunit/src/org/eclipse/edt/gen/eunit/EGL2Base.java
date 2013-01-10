/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EGLMessages.AccumulatingGenerationMessageRequestor;
import org.eclipse.edt.mof.egl.lookup.PartEnvironment;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;

public abstract class EGL2Base extends AbstractGeneratorCommand {
	public static final String ARG_PARM_GENPARTS = "genParts";
	
	protected List<String> generatedLibs;
	protected TestCounter totalCnts;

	public EGL2Base() {
		super();
		generatedLibs = new ArrayList<String>();
		totalCnts = new TestCounter();
	}

	abstract protected EUnitDriverGenerator getEckDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req, IEUnitGenerationNotifier eckGenerationNotifier);
	abstract protected EUnitRunAllDriverGenerator getEckRunAllDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req, IEUnitGenerationNotifier eckGenerationNotifier);	
	
	public void generateRunAllDriver(String[] args, EUnitRunAllDriverGenerator allDriverGenerator, List<String> genedLibs, TestCounter totalCnts){		
			allDriverGenerator.generateRunAllDriver(genedLibs, totalCnts);
			// now try to write out the file, based on the output location and the part's type signature
			try {
				// only write the data, if there was some
				if (allDriverGenerator.getResult().length() > 0)
					writeFile(null, allDriverGenerator);
				
				allDriverGenerator.dumpErrorMessages();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
	}
	
	protected void startGeneration(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier){
		// if we are missing the -part argument, then process the ir directory
		boolean hasPart = false;
		for(String arg : args) {
			if (arg.equalsIgnoreCase("-p") || arg.equalsIgnoreCase("-part")) {
				hasPart = true;
				break;
			}
		}
		if (!hasPart)
			startGeneration4AllPartsUnderIRRootDir(args, compiler, eckGenerationNotifier);
		else {
			generate(args, new EUnitGenerator(this, generatedLibs, totalCnts, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), null, compiler);
			generate(args, getEckDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), null, compiler);
			generateRunAllDriver(args, getEckRunAllDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), generatedLibs, totalCnts);
			eckGenerationNotifier.updateProgress(1);
		}
		eckGenerationNotifier.done();
	}
	
	private IEnvironment createEnvironment(ICompiler compiler) {
		if (compiler == null) {
			return null;
		}
		Environment env = new Environment();
		PartEnvironment partEnv = new PartEnvironment(env);
		for (ZipFileBindingBuildPathEntry entry : compiler.getSystemBuildPathEntries()) {
			partEnv.registerObjectStore(entry.getObjectStore().getKeyScheme(), entry.getObjectStore());
		}			
		
		
		return partEnv;
	}
	
	protected void startGeneration4AllPartsUnderIRRootDir(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier){
		//get the parts either from genFile or calculate all from the irRootfolder
		List<String> parts = getParts2Gen(args);
		//copy existing args, expect to miss -p (part), which we will build from all the parts under the irRootDir
		List<String> argList = new ArrayList<String>();		
		for(String arg : args){
			argList.add(arg);
		}
		IEnvironment env = createEnvironment(compiler);
		eckGenerationNotifier.begin(parts.size() * 2 + 1);
		for(String part: parts){
			if (eckGenerationNotifier.isAborted())
				return;
			//let's build up the command line with -p (part)
			argList.add("-p");
			argList.add(part);	
			String[] argArray = argList.toArray(new String[argList.size()]);  
			//generate one part at a time
			generate(argArray, new EUnitGenerator(this, generatedLibs, totalCnts, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), env, compiler);
			generate(argArray, getEckDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), env, compiler);			
		}		
		generateRunAllDriver(args, getEckRunAllDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), generatedLibs, totalCnts);
		eckGenerationNotifier.updateProgress(1);
	}

	protected List<String> getParts2Gen(String[] args){
		// if we are missing the -genPart argument, then process the ir directory
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("-" + ARG_PARM_GENPARTS))
				return getPartsFromGenFile(args[i+1]);
		}
		//get parts from the irRoot directory
		return getPartsFromIRRootFolder(args);
	}
	
	
	protected List<String> getPartsFromGenFile(String genFileName){
		GenPartsXMLFile driverXMLFile = new GenPartsXMLFile(genFileName);
		return driverXMLFile.getGenerationEntries();		
	}
	
	private List<String> getPartsFromIRRootFolder(String[] args) {
		List<String> parts = new ArrayList<String>();
		// get the -root parameter
		String irRootDir = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("-r") || arg.equalsIgnoreCase("-root")) {
				//get parts from the genFile
				irRootDir = args[i+1];
				break;
			}
		}
		//loop through the irRootDir, then get each parts
		File irRootDirFile = new File(irRootDir);
		String irRootDirAbsolutePath = irRootDirFile.getAbsolutePath();
		int irRootDirAbsolutePathLen = irRootDirAbsolutePath.length();
		List <File> irfiles = new ArrayList<File>();
		listAllFiles(irRootDirFile, irfiles);
		for(File irfile : irfiles){
			String irFileAbsolutePath = irfile.getAbsolutePath();				
			String irFileRelativePath = irFileAbsolutePath.substring(irRootDirAbsolutePathLen);
			boolean isIRFile = IRUtils.isEGLIRFileName(irFileRelativePath);
			if(isIRFile){
				if(irFileRelativePath.startsWith(File.separator))
					irFileRelativePath = irFileRelativePath.substring(File.separator.length());
				//let's get rid of the file extension
				int dotPos = irFileRelativePath.lastIndexOf('.');
				irFileRelativePath = irFileRelativePath.substring(0, dotPos);
				String partQName = irFileRelativePath.replace(File.separatorChar, '.');
				parts.add(partQName);
				System.out.println(partQName);				
			}
		}
		return parts;
	}
		
	private void listAllFiles(File parent, List <File> files){
		if(parent.isDirectory()){
			File[] children = parent.listFiles();
			for (File child : children) {
				if(child.isFile())
					files.add(child);				
				listAllFiles(child, files);
			}			
		}
	}
}
