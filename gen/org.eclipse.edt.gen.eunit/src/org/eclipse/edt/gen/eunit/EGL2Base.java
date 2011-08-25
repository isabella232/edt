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
package org.eclipse.edt.gen.eunit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.InvalidParameterValueException;
import org.eclipse.edt.gen.MissingParameterValueException;
import org.eclipse.edt.gen.PromptQueryException;
import org.eclipse.edt.gen.UnknownParameterException;
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
		this.installParameter(false, ARG_PARM_GENPARTS, new String[] { ARG_PARM_GENPARTS }, new String[] { null },
		"Use this file to load parts to be generated.");
		generatedLibs = new ArrayList<String>();
		totalCnts = new TestCounter();
	}

	abstract protected EckDriverGenerator getEckDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req, IEUnitGenerationNotifier eckGenerationNotifier);
	abstract protected EckRunAllDriverGenerator getEckRunAllDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req, IEUnitGenerationNotifier eckGenerationNotifier);	
	
	public void generateRunAllDriver(String[] args, EckRunAllDriverGenerator allDriverGenerator, List<String> genedLibs, TestCounter totalCnts){		
		try{
			installOverrides(args);
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
		catch (PromptQueryException e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
		catch (UnknownParameterException e) {
			System.out.print("This parameter is unknown: " + e.getMessage());
		}
		catch (InvalidParameterValueException e) {
			System.out.print("This value for this parameter is incorrect: " + e.getMessage());
		}			
		catch (MissingParameterValueException e) {
			System.out.print("This value for this parameter is missing: " + e.getMessage());
		}	
	}
	
	protected void startGeneration(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier){		
		try {			
			installOverrides(args);			
			generate(args, new EckGenerator(this, generatedLibs, totalCnts, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), null, compiler);
			generate(args, getEckDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), null, compiler);
			generateRunAllDriver(args, getEckRunAllDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), generatedLibs, totalCnts);
			eckGenerationNotifier.updateProgress(1);
		} 
		catch (PromptQueryException e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
		catch (UnknownParameterException e) {
			System.out.print("This parameter is unknown: " + e.getMessage());
			e.printStackTrace();
		}
		catch (InvalidParameterValueException e) {
			System.out.print("This value for this parameter is incorrect: " + e.getMessage());
			e.printStackTrace();
		}		
		catch (MissingParameterValueException e) {	
			//expect to miss -p (part), which we will build from all the parts under the irRootDir	
			if(e.getMessage().equalsIgnoreCase(Constants.parameter_part))
				startGeneration4AllPartsUnderIRRootDir(args, compiler, eckGenerationNotifier);
			else{
				System.out.print("This value for this parameter is missing: " + e.getMessage());
				e.printStackTrace();
			}
			
		} 
		finally {
			eckGenerationNotifier.done();
		}
	}
	
	private IEnvironment createEnvironment(ICompiler compiler) {
		if (compiler == null) {
			return null;
		}
		Environment env = new Environment();
		PartEnvironment partEnv = new PartEnvironment(env);
		partEnv.registerObjectStores(compiler.getSystemEnvironment(null).getStores());
		return partEnv;
	}
	
	protected void startGeneration4AllPartsUnderIRRootDir(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier){
		
		//get the parts either from genFile or calculate all from the irRootfolder
		List<String> parts = getParts2Gen();
		
		//copy existing args, expect to miss -p (part), which we will build from all the parts under the irRootDir
		List<String> argList = new ArrayList<String>();		
		for(String arg : args){
			argList.add(arg);
		}
		IEnvironment env = createEnvironment(compiler);
		eckGenerationNotifier.begin(parts.size() * 2 + 1);
		for(String part: parts){
			if(eckGenerationNotifier.isAborted()) {
				return;
			}
			//let's build up the command line with -p (part)
			argList.add("-p");
			argList.add(part);	
			String[] argArray = argList.toArray(new String[argList.size()]);  
			//generate one part at a time
			generate(argArray, new EckGenerator(this, generatedLibs, totalCnts, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), env, compiler);
			generate(argArray, getEckDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), env, compiler);			
		}		
		generateRunAllDriver(args, getEckRunAllDriverGenerator(this, new AccumulatingGenerationMessageRequestor(), eckGenerationNotifier), generatedLibs, totalCnts);
		eckGenerationNotifier.updateProgress(1);
	}

	protected List<String> getParts2Gen(){
		List<String> parts = new ArrayList<String>();
		String genFileName = (String) parameterMapping.get(ARG_PARM_GENPARTS).getValue();
		if(genFileName != null){
			//get parts from the genFile
			parts = getPartsFromGenFile(genFileName);
		}
		else{
			//get parts from the irRoot directory
			parts = getPartsFromIRRootFolder();
		}		
		return parts;
	}
	
	
	protected List<String> getPartsFromGenFile(String genFileName){
		TestDriverXMLFile driverXMLFile = new TestDriverXMLFile(genFileName);
		return driverXMLFile.getGenerationEntries();		
	}
	
	private List<String> getPartsFromIRRootFolder() {
		List<String> parts = new ArrayList<String>();

		//expect to miss -p (part), which we will build from all the parts under the irRootDir			
		String irRootDir = (String) parameterMapping.get(Constants.parameter_root).getValue();
		
		//loop through the irRootDir, then get each parts
		File irRootDirFile = new File(irRootDir);
		String irRootDirAbsolutePath = irRootDirFile.getAbsolutePath();
		int irRootDirAbsoluatePathLen = irRootDirAbsolutePath.length();
		List <File> irfiles = new ArrayList<File>();
		listAllFiles(irRootDirFile, irfiles);
		for(File irfile : irfiles){
			String irFileAbsoluatePath = irfile.getAbsolutePath();				
			String irFileRelativePath = irFileAbsoluatePath.substring(irRootDirAbsoluatePathLen);
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

	public String[] getNativeTypePath() {
		// this defined the locations of the nativeTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.eunit.nativeTypes" };
	}

	public String[] getPrimitiveTypePath() {
		// this defined the locations of the primitiveTypes.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.eunit.primitiveTypes" };
	}

	public String[] getEGLMessagePath() {
		// this defined the locations of the EGLMessages.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.eunit.EGLMessages" };
	}

	public String[] getTemplatePath() {
		// this defined the locations of the template.properties files to be loaded and used
		return new String[] { "org.eclipse.edt.gen.eunit.templates.templates" };
	}
}
