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
package org.eclipse.edt.compiler.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.EGL2IRArgumentProcessor;
import org.eclipse.edt.compiler.EGL2IREnvironment;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.Processor;
import org.eclipse.edt.compiler.Util;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.sdk.IPartRequestor;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.compiler.sdk.compile.BuildPathException;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.FileSystemObjectStore;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;



/**
 * @author svihovec
 *
 * TODO Issues
 * 	- when a source part falls out of the cache, it will be re-compiled if it is needed again
 * 		- should we even be caching them then?
 *  - we compile a part found on the source path, even if the source path contains a class file that is newer than the source file
 * 	- when compiling a part, we compile the part and its file part.  If two parts from the same part are compiled, the file part will be compiled twice
 * 		- see first issue, which comes into play here as well
 * 	- buffering of reading and writing
 *  - a top level function will be compiled generically once for each time it is called
 * 	- error handling for source and classpath entries
 */
public class EGLC {
	public static final String EGLBIN = ".eglbin";
	public static final String EGLXML = ".eglxml";
	
	public static EGL2IREnvironment eglcEnv;
	public static ISystemEnvironment systemEnvironment;

	
	public static void compile(final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs, ICompiler compiler, ISDKProblemRequestorFactory problemRequestorFactory,IPartRequestor partRequestor){
		try {
	        File[] files = processedArgs.getPartFiles();
	        
	        if(files.length > 0){
	        	
	        	if (eglcEnv == null) {
	        		eglcEnv = new EGL2IREnvironment();
	        	}
	        	
    			initializeOutputPath(processedArgs);
    			initializeSystemRoot(processedArgs, compiler);
    			initializeEGLPath(processedArgs);
	        	
	            
	            ISystemEnvironment sysEnv = compiler.getSystemEnvironment(null);
            	eglcEnv.setCompiler(compiler);

			    
			    Processor processor = new Processor(NullBuildNotifier.getInstance(), DefaultCompilerOptions.getInstance(),
			    		problemRequestorFactory, sysEnv, compiler);
			    
			    processor.setEnvironment(eglcEnv);
			    
	        	SourcePathInfo.getInstance().setSourceLocations(resolveSourcePathLocations(processedArgs.getSourcePathEntries(),processedArgs.getIROutputPath()));
	            SourcePathEntry.getInstance().setDeclaringEnvironment(eglcEnv);
	            SourcePathEntry.getInstance().setProcessor(processor);
	            
	            
    				            
			    for (int j = 0; j < files.length;j++){
			    	File file = files[j];
				    org.eclipse.edt.compiler.core.ast.File fileAST = ASTManager.getInstance().getFileAST(file);
		        	String[] packageName = createPackageName(fileAST);
				    processor.addPart(packageName, Util.getCaseSensitiveFilePartName(file));
				    SourcePathInfo.getInstance().addPart(packageName, Util.getFilePartName(file), ITypeBinding.FILE_BINDING, file, Util.getCaseSensitiveFilePartName(file));
				    
		        	for (Iterator iter = fileAST.getParts().iterator(); iter.hasNext();) {
						Part part = (Part) iter.next();
			            processor.addPart(packageName, part.getName().getCaseSensitiveIdentifier());
			            addMessageTable(processor, part, file, processedArgs.getNlsCode(), processedArgs.getMsgTablePrefix());				            
			            SourcePathInfo.getInstance().addPart(packageName, part.getName().getIdentifier(), Util.getPartType(part), file, part.getName().getCaseSensitiveIdentifier());
			            if (part.isGeneratable() && partRequestor != null){
			            	partRequestor.acceptPart(packageName, part.getName().getCaseSensitiveIdentifier());
			            }
		        	}
			    }
				    
		
		        processor.process();
	    		
		      }else{
		            throw new RuntimeException("cannot find target file");
		      }
	        
	    } catch (BuildException e) {
           throw e;
        } catch (BuildPathException e) {
           throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
         }
	}

	private static void addMessageTable(Processor processor, Part part, File file, String nlsCode, String msgTablePrefix) {
		
		String msgTableName = getMsgTableName(part, nlsCode, msgTablePrefix);
		if (msgTableName == null) {
			return;
		}
		
		msgTableName = InternUtil.intern(msgTableName);
		
		String[][] possibleMsgTablePkgs = getPossibleMsgTablePkgs(part, msgTableName);
		
		for (int i = 0; i < possibleMsgTablePkgs.length; i++) {
			String[] pkg = InternUtil.intern(possibleMsgTablePkgs[i]);
			File tblFile = SourcePathInfo.getInstance().getDeclaringFile(pkg, msgTableName);
			if (tblFile != null) {
				processor.addPart(pkg, msgTableName);
			}
		}		
	}
	
	private static String[][] getPossibleMsgTablePkgs(Part part, String msgTableName) {
		
		String[] parsed = NameUtil.toStringArray(msgTableName);

		if (parsed.length == 1) {
			return getPossibleMsgTablePkgsFromImports(part, msgTableName);
		} else {
			String[] pkg = new String[parsed.length - 1];
			System.arraycopy(parsed, 0, pkg, 0, parsed.length - 1);
			return new String[][] {pkg};
		}
	}
	
	private static String[][] getPossibleMsgTablePkgsFromImports(Part part, String msgTableName) {
		Node node = part.getParent();
		if (node instanceof org.eclipse.edt.compiler.core.ast.File) {
			final List<String[]> onDemand = new ArrayList<String[]>();
			final List<String[]> singleType = new ArrayList<String[]>();
			node.accept(new DefaultASTVisitor() {
				public boolean visit(org.eclipse.edt.compiler.core.ast.File file) {
					return true;
				}
				public boolean visit(org.eclipse.edt.compiler.core.ast.ImportDeclaration importDeclaration) {
					if (importDeclaration.isOnDemand()) {
						onDemand.add(NameUtil.toStringArray(importDeclaration.getName().getCanonicalName()));
					}
					else {
						singleType.add(NameUtil.toStringArray(importDeclaration.getName().getCanonicalName()));
					}
					return false;
				}
				public boolean visit(org.eclipse.edt.compiler.core.ast.PackageDeclaration packageDeclaration) {
					onDemand.add(NameUtil.toStringArray(packageDeclaration.getName().getCanonicalName()));
					return false;
				}
			});
			
			
			List<String[]> pkgs = new ArrayList<String[]>();
			
			for (String[] imp : singleType) {
				if (imp.length > 0) {
					if (msgTableName.equalsIgnoreCase(imp[imp.length - 1])) {
						
						String[] newArr = new String[imp.length - 1];
						if (newArr.length > 0) {
							System.arraycopy(imp, 0, newArr, 0, newArr.length);
						}
						pkgs.add(newArr);
					}
				}
			}
			
			pkgs.addAll(onDemand);
			
			return (String[][])pkgs.toArray(new String[pkgs.size()][]);
						
			
		} else {
			return new String[0][];
		}
	}

	private static String getMsgTableName(Part part, String nlsCode, String msgTablePrefix) {

		final String[] prefix = new String[1];
		final String msgTblPrefixProp = InternUtil.intern(IEGLConstants.PROPERTY_MSGTABLEPREFIX);
		part.accept(new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.Program program) {
				return true;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.Library library) {
				return true;
			}
			
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.SetValuesExpression setValuesExpression) {				
				return true;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
				
				if (assignment.getLeftHandSide() instanceof Name) {
					String id = ((Name)assignment.getLeftHandSide()).getIdentifier();
					if (InternUtil.intern(id) == msgTblPrefixProp && assignment.getRightHandSide() instanceof StringLiteral) {
						prefix[0] = ((StringLiteral)assignment.getRightHandSide()).getValue();
					}
				}
				return false;
			}
		});

		if (prefix[0] != null) {
			return prefix[0] + nlsCode;
		}
		
		if (msgTablePrefix != null && msgTablePrefix.trim().length() > 0 && isVGUIRecord(part)) {
			return msgTablePrefix + nlsCode;
		}
		return null;
	}
	
	private static boolean isVGUIRecord(Part part) {
		final boolean[] isRecord = new boolean[1];
		final String vgUIRecProp = InternUtil.intern(IEGLConstants.PROPERTY_VGUIRECORD);
		
		part.accept(new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.Record record) {
				if (record.hasSubType()) {
					String id = record.getSubType().getIdentifier();
					if (InternUtil.intern(id) == vgUIRecProp) {
						isRecord[0] = true;
					}
				}
				
				return !isRecord[0];
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.SetValuesExpression setValuesExpression) {
				setValuesExpression.getExpression().accept(this);
				return false;
			}
			
			public boolean visit(AnnotationExpression annotationExpression) {
				if (InternUtil.intern(annotationExpression.getName().getIdentifier()) == vgUIRecProp) {
					isRecord[0] = true;
				}
				return false;
			}
			
			
			
			
		});
		
		
		return isRecord[0];
		
	}
		
	private static void deleteIRs(File path){
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()){
				deleteIRs(files[i]);
			}else if(files[i].getName().indexOf(".ir") > 0){
				files[i].delete();
			}
		}	
	}
		
	private static void initializeEGLPath (final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs){
		File[] paths = processedArgs.getSourcePathEntries();
		for (File path : paths) {
			if (path.exists()) {
				String outType = processedArgs.xmlOut ? ObjectStore.XML : ObjectStore.BINARY;
				String fileext = processedArgs.xmlOut ? EGLXML : EGLBIN;
				ObjectStore store = new FileSystemObjectStore(path, eglcEnv, outType, fileext);
				eglcEnv.registerObjectStore(Type.EGL_KeyScheme, store);
				eglcEnv.addRoot(path);
			}
		}
	}
	
	private static void initializeOutputPath (final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs){
		File path = processedArgs.getIROutputPath();
		if (path != null){
			if (!path.exists()){
				path.mkdirs();
			}else if (processedArgs.isClean()){
				deleteIRs(path);
			}
		}else if (processedArgs.isClean()){
			File[] sourcelocs = processedArgs.getSourcePathEntries();
			for (int i = 0; i < sourcelocs.length; i++){
				deleteIRs(sourcelocs[i]);
			}
		}
		String outType = processedArgs.getXMLOut() ? ObjectStore.XML : ObjectStore.BINARY;
		String fileext = processedArgs.xmlOut ? EGLXML : EGLBIN;
		
		// Default file extension for looking up EGL Model types which are instances of the MOF model
		ObjectStore store = new FileSystemObjectStore(path, eglcEnv, outType);
		eglcEnv.registerObjectStore(IEnvironment.DefaultScheme, store);
		eglcEnv.setDefaultSerializeStore(IEnvironment.DefaultScheme, store);
		
		// EGL file extension for looking up EGL parts which are instances of the EGL model
		store = new FileSystemObjectStore(path, eglcEnv, outType, fileext);
		eglcEnv.registerObjectStore(Type.EGL_KeyScheme, store);	
		eglcEnv.setDefaultSerializeStore(Type.EGL_KeyScheme, store);
		
		eglcEnv.addRoot(path);
	}
	
	private static void initializeSystemRoot (final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs, ICompiler compiler){
		
		String outType = processedArgs.getXMLOut() ? ObjectStore.XML : ObjectStore.BINARY;

		File path = processedArgs.getSystemRoot();
		if (path != null && path.exists()) {
			ObjectStore store = new FileSystemObjectStore(path, eglcEnv, outType);
			eglcEnv.registerObjectStore(IEnvironment.DefaultScheme, store);
			
			String fileext = processedArgs.xmlOut ? EGLXML : EGLBIN;
	//		store = new FileSystemObjectStore(path, PartEnvironment.INSTANCE,outType);
			store = new FileSystemObjectStore(path, eglcEnv,outType, fileext);
			eglcEnv.registerObjectStore(Type.EGL_KeyScheme, store);
			eglcEnv.addRoot(path);
		}
		else if (compiler != null) {
			ISystemEnvironment env = compiler.getSystemEnvironment(null);
			Map<String, List<ObjectStore>> systemMap = env.getStores();
			for (Map.Entry<String, List<ObjectStore>> entry : systemMap.entrySet()) {
				String scheme = entry.getKey();
				List<ObjectStore> stores = entry.getValue();
				
				for (ObjectStore store : stores) {
					eglcEnv.registerObjectStore(scheme, store);
				}
			}
		}
	}

	
	private static String[] createPackageName(org.eclipse.edt.compiler.core.ast.File fileAST) {

	    if(fileAST.hasPackageDeclaration()){
	        return fileAST.getPackageDeclaration().getName().getNameComponents();
	    }
	    
	    return InternUtil.intern(new String[0]);
    }

	 private static File resolveSourcePathLocation(File sourcePathEntryLocation) {
		 try{
             return sourcePathEntryLocation.getCanonicalFile();
         }catch(IOException e){
             System.out.println("Could not find source path location: " + sourcePathEntryLocation);
             throw new RuntimeException(e);
         }
	 }
	 
    private static File[] resolveSourcePathLocations(File[] sourcePathEntryLocations,File outpath) throws Exception{
		
        File[] canonicalLocations = new File[sourcePathEntryLocations.length];
        
        for (int i = 0; i < sourcePathEntryLocations.length; i++) {
            canonicalLocations[i] = resolveSourcePathLocation(sourcePathEntryLocations[i]);
		}	
		
		return canonicalLocations;
	}
    	
}
