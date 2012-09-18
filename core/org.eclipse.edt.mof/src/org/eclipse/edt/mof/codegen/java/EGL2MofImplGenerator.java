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
package org.eclipse.edt.mof.codegen.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TemplateFactory;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.Template;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.FileSystemObjectStore;
import org.eclipse.edt.mof.serialization.ObjectStore;


public class EGL2MofImplGenerator {

	public static void main(String[] args) {
		String eglroot = null;
		String javaroot = null;
		String fileName = null;
		String factoryName = null;
		boolean factoryOnly = false;
		String[][] packageMappings = null;
		if (args.length < 2) return;
		eglroot = args[0];
		if (args.length > 1) {
			javaroot = args[1];
		}
		if (args.length > 2) {
			fileName = args[2];
			if (fileName.equals("null")) {
				fileName = null;
			}
		}
		if (args.length > 3) {
			factoryName = args[3];
		}
		factoryOnly = args.length > 4;
		
		EGL2MofImplGenerator generator = new EGL2MofImplGenerator(eglroot, javaroot, packageMappings);
		if (fileName == null) 
			generator.generateFiles(new File(eglroot));
		else {
			String filePath = eglroot+"/"+fileName.replace('.', '/');
			File file = new File(filePath);
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (f.getName().endsWith(".mofxml")) {
						String partName = f.getName().split("[.]")[0];
						String typeSignature = (fileName+"."+partName).toLowerCase();
						if (factoryOnly) {
							generator.readClassifier(typeSignature);
						} else {
							generator.generate(typeSignature, new File(javaroot));
						}
					}
				}
				if (factoryName != null) {
					generator.generateFactory(factoryName, new File(javaroot));
				}
			} else {
				if (factoryOnly && fileName != null) {
					generator.readClassifier(fileName);
					generator.generateFactory(factoryName, new File(javaroot));
				}
				else if (fileName != null) {
					generator.generate(fileName, new File(javaroot));
				}
			}
		}
	}
	
	EGL2MofImplGenerator(String irPaths, String javaSrc, String[][] packageMappings) {
		env = Environment.INSTANCE;
		String[] roots = irPaths.split("[;]");
		for (String root : roots) {
			File dir = new File(root);
			ObjectStore store = new FileSystemObjectStore(dir, env, "XML");
			env.registerObjectStore("mof", store);
		}
		outputDir = new File(javaSrc);
		pathFromRoot = "";
		this.packageMappings = packageMappings;
		processed = new ArrayList<EClassifier>();
	}
	
	Environment env;
	File outputDir;
	String pathFromRoot;
	String[][] packageMappings;
	List<EClassifier> processed;
	
	void generateFiles(File parentDir) {
		for (File f : parentDir.listFiles()) {
			if (f.isDirectory()) {
				if (pathFromRoot.startsWith("/azure")) continue;
				String previousPath = pathFromRoot;
				pathFromRoot += "/"+f.getName();
				generateFiles(f);
				pathFromRoot = previousPath;
			}	
			else {
				String fileName = f.getName();
				if (fileName.endsWith(".eglbin")) {
					fileName = fileName.split("[.]")[0];
					String path = pathFromRoot+"/"+fileName;
					path = path.replace('/', '.').substring(1);
					generate(path, outputDir);
				}
			}
		}
	}

	void generate(String typeSignature, File root) {
		try {
			TemplateFactory factory = getTemplateFactory();
			TemplateContext ctx = new TemplateContext();
			if (packageMappings != null)
				for (String[] mapping : packageMappings) {
					ctx.put(mapping[0], mapping[1]);
				}
			ctx.setTemplateFactory(factory);
			EClassifier eClass = (EClassifier)env.findType(typeSignature);
			String path;
			String packageName = (String)ctx.get(eClass.getPackageName());
			if (packageName == null) {
				path = eClass.getPackageName().replace('.', '/');
			}
			else {
				path = packageName.replace('.', '/');
			}
			if (eClass.getEClass().getName().equals("EEnum")) {
				Template template = ctx.getTemplateFor(eClass.getEClass());
				genTemplate(template, "genImpl", eClass, root, path, eClass.getName()+".java", ctx);
			}
			else if	(eClass instanceof EClass) {
				Template template = ctx.getTemplateFor(eClass.getEClass());				
				genTemplate(template, "genInterface", eClass, root, path, eClass.getName()+".java", ctx);
				if (!((EClass)eClass).isInterface()) {
					// genTemplate(template, "genImplBase", eClass, root, path+"/impl", eClass.getName()+"Base.java", ctx);
					if (!((EClass)eClass).getEFunctions().isEmpty()) {
						File outDir = new File(root, path+"/impl");
						File file = new File(outDir, eClass.getName()+"Base.java");
						genTemplate(template, "genImplBase", eClass, root, path+"/impl", eClass.getName()+"ImplBase.java", ctx);
			
						outDir = new File(root, path+"/impl");
						file = new File(outDir, eClass.getName()+"Impl.java");
						// Generate the extended Impl class only if it does not exist as it contains code written by user
						if (!(file.exists())) {
							genTemplate(template, "genImplExtendsBase", eClass, root, path+"/impl", eClass.getName()+"Impl.java", ctx);	
						}
					}
					else {
						File outDir = new File(root, path+"impl");
						genTemplate(template, "genImpl", eClass, root, path+"/impl", eClass.getName()+"Impl.java", ctx);
					}
				}
			}
			processed.add(eClass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void generateFactory(String factoryName, File root) {
		try {
			TemplateContext ctx = new TemplateContext();
			EFactoryImplTemplate template = new EFactoryImplTemplate();
			EClassifier classifier = processed.get(0);
			String path;
			String packageName = (String)ctx.get(classifier.getPackageName());
			if (packageName == null) {
				packageName = classifier.getPackageName();
				path = classifier.getPackageName().replace('.', '/');
			}
			else {
				path = packageName.replace('.', '/');
			}
			String name = factoryName;
			if (factoryName.equals("default")) {
				String[] names = packageName.split("[.]");
				name = names[names.length-1].substring(0,1).toUpperCase();
				name += names[names.length-1].substring(1);
				name += "Factory";
			}
			StringWriter writer = new StringWriter();
			TabbedWriter out = new TabbedWriter(writer);
			template.genFactory(processed, ctx, out);
			out.flush();
			writeToFile(root, path, name+".java", writer.toString());
			writer = new StringWriter();
			out = new TabbedWriter(writer);
			template.genFactoryImpl(processed, ctx, out);
			out.flush();
			writeToFile(root, path+"/impl", name+"Base.java", writer.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	void readClassifier(String typeSignature) {
		try {
			EClassifier classifier = (EClassifier)env.findType(typeSignature);
			processed.add(classifier);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void genTemplate(
			Template template, 
			String genMethod, 
			EObject obj, 
			File root, 
			String path, 
			String fileName, 
			TemplateContext ctx) throws TemplateException {
		StringWriter writer = new StringWriter();
		TabbedWriter out = new TabbedWriter(writer);
		out.setAutoIndent(false);
		template.gen(genMethod, obj, ctx, out);
		out.flush();
		writeToFile(root, path, fileName, writer.toString());

	}
	
	private void writeToFile(File parent, String path, String fileName, String content) {
		File outDir = new File(parent, path);
		File file = new File(outDir, fileName);
		try {
			outDir.mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected TemplateFactory getTemplateFactory() throws TemplateException{
		return new MofImplTemplateFactory();
	}
}
