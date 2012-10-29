/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.dependency;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.utils.ReadWriteMonitor;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author svihovec
 */
public class DependencyGraph {
	
	private static final String FUNCTIONS_DIRECTORY = "functions";
	private static final String DEPENDENTS_DIRECTORY = "dependents";
	private static final String DEPENDENCIES_DIRECTORY = "dependencies";
	
    private IPath functionsDirectory;
	private IPath dependentsDirectory;
	private IPath dependenciesDirectory;

	private static final boolean DEBUG = false;
    
	private static final String DEPENDENCY_GRAPH_FILE_EXTENSION = "dg"; //$NON-NLS-1$
	
	private ReadWriteMonitor monitor = new ReadWriteMonitor();
	private IOPool bufferPool = new IOPool();
	
	protected DependencyGraph(IProject project){
		IPath cacheLocation = project.getWorkingLocation(EDTCoreIDEPlugin.getPlugin().getBundle().getSymbolicName());
		
		functionsDirectory = cacheLocation.append(FUNCTIONS_DIRECTORY);
		dependentsDirectory = cacheLocation.append(DEPENDENTS_DIRECTORY);
		dependenciesDirectory = cacheLocation.append(DEPENDENCIES_DIRECTORY);
	}
	
	/**
	 * Simple Name Dependencies are stored as a list of names associated with a particular package.partname
	 * ex: a.b.c.RecordOne -> J, K, L
	 */
	private void recordSimpleNameDependency(String packageName, String partName, SimpleName simpleName){
		Part entryKey = new Part(packageName, partName);
		DependencyEntry dependencyEntry = (DependencyEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
		
		if(dependencyEntry == null){
			dependencyEntry = new DependencyEntry();
		}
		
		dependencyEntry.addSimpleName(simpleName);
		
		putDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY, dependencyEntry);
	}
	
	/**
	 * Qualified Name Dependencies are stored as a list of names associated with a particular package.partname
	 * ex: a.b.c.RecordOne -> i.b.RecordTwo
	 * 	   l.m.LibraryOne  -> j.DataItemOne
	 */
	private void recordQualifiedNameDependency(String packageName, String partName, QualifiedName qualifiedName){
		Part entryKey = new Part(packageName, partName);
		
		DependencyEntry dependencyEntry = (DependencyEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
		
		if(dependencyEntry == null){
			dependencyEntry = new DependencyEntry();
		}
		
		dependencyEntry.addQualifiedName(qualifiedName);
		
		putDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY, dependencyEntry);
	}
	
	/**
	 * Simple Name Dependents are stored by the files that reference the simple names.
	 * ex: J -> a.b.c.File1.egl[RecordOne, RecordTwo]
	 * 		 -> l.m.File2.egl[LibraryOne]
	 */
	private void recordSimpleNameDependent(String packageName, String partName, String filePartName, SimpleName entryKey){
		SimpleNameDependentEntry dependentEntry = (SimpleNameDependentEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY);
		
		if(dependentEntry == null){
			dependentEntry = new SimpleNameDependentEntry();
		}
		
		dependentEntry.addPart(new Part(packageName, filePartName), new SimpleName(partName));
		
		putDependencyEntry(entryKey, IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY, dependentEntry);
	}
	
	/**
	 * Qualified Name Dependents are stored as a list of names associated with a particular package.partname
	 * ex: a.b.c.RecordTwo -> a.b.c.RecordOne
	 * 	   j.DataItemOne -> l.m.LibraryOne
	 */
	private void recordQualifiedNameDependent(String packageName, String partName, QualifiedName entryKey){
		QualifiedNameDependentEntry dependentEntry = (QualifiedNameDependentEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY);
		
		if(dependentEntry == null){
			dependentEntry = new QualifiedNameDependentEntry();
		}
		
		dependentEntry.addPart(new Part(packageName, partName));
		
		putDependencyEntry(entryKey, IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY, dependentEntry);
	}

	private void removeSimpleNameDependency( String packageName,  String partName,  SimpleName simpleName){
		Part entryKey = new Part(packageName, partName);
		
		DependencyEntry dependencyEntry = (DependencyEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
		
		if(dependencyEntry != null){
			dependencyEntry.removeSimpleName(simpleName);
			
			if(dependencyEntry.isEmpty()){
				removeDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
			}else{
				putDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY, dependencyEntry);
			}
		}
	}
	
	private void removeSimpleNameDependent(String packageName, String partName, String filePartName, SimpleName entryKey){
		SimpleNameDependentEntry dependentEntry = (SimpleNameDependentEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY);
		
		if(dependentEntry != null){
			dependentEntry.removePart(new Part(packageName, filePartName), new SimpleName(partName));
			
			if(dependentEntry.isEmpty()){
				removeDependencyEntry(entryKey, IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY);
			}else{
				putDependencyEntry(entryKey, IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY, dependentEntry);
			}
		}
	}

	private void removeQualifiedNameDependency( String packageName,  String partName,  QualifiedName qualifiedName){
		Part entryKey = new Part(packageName, partName);
		
		DependencyEntry dependencyEntry = (DependencyEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
		
		if(dependencyEntry != null){
			dependencyEntry.removeQualifiedName(qualifiedName);
			
			if(dependencyEntry.isEmpty()){
				removeDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
			}else{
				putDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY, dependencyEntry);
			}
		}
	}
	
	private void removeQualifiedNameDependent(String packageName, String partName, QualifiedName entryKey){
		QualifiedNameDependentEntry dependentEntry = (QualifiedNameDependentEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY);
		
		if(dependentEntry != null){
			dependentEntry.removePart(new Part(packageName, partName));
			
			if(dependentEntry.isEmpty()){
				removeDependencyEntry(entryKey, IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY);
			}else{
				putDependencyEntry(entryKey, IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY, dependentEntry);
			}
		}
	}
	
	public void findDependents(String packageName, String partName, IPartRequestor requestor){
		
		monitor.enterRead();
		
		try{
			long startTime = System.currentTimeMillis();
			if(DEBUG){
				System.out.println("\r\nYou asked for dependents of " + Util.appendToQualifiedName(packageName, partName, ".") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if(packageName.length() == 0){
				findDependents(partName, requestor);
			}else{
				findQualifiedNameDependents(NameUtile.getAsName(Util.appendToQualifiedName(packageName, partName, ".")), requestor);
				
				findMatchingDependents(packageName, partName, requestor);
			}
			
			if(DEBUG){
				System.out.println("Finished finding dependents of " + Util.appendToQualifiedName(packageName, partName, ".") + "(" + (System.currentTimeMillis() - startTime) + "ms)\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}finally{
			monitor.exitRead();
		}
	}
	
	private void findMatchingDependents( String packageName,  String partName, final IPartRequestor requestor) {
		
		final SimpleNameDependentEntry dependentEntry = (SimpleNameDependentEntry)getDependencyEntry(new SimpleName(partName), IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY);
		
		if(dependentEntry != null){
			final ArrayList fileParts = new ArrayList();
			findDependents(packageName, new IPartRequestor(){
				
				public void acceptPart(String packageName, String partName) {
					Part filePart = new Part(packageName, partName);
					fileParts.add(filePart);
				}
			});	
			
			for (Iterator iter = fileParts.iterator(); iter.hasNext();) {
				Part filePart = (Part) iter.next();
				if(filePart != null){
					HashSet partsPerFile = dependentEntry.getParts(filePart);
					if(partsPerFile != null){
						for (Iterator iterator = partsPerFile.iterator(); iterator.hasNext();) {
							SimpleName	part = (SimpleName) iterator.next();
							requestor.acceptPart(filePart.getPackageName(), part.getSimpleName());
							
							if(DEBUG){
							    System.out.println("\tPart \"" + part + "\" depends on \"" +  partName + "\", and File \"" + filePart + "\" depends on \"" + packageName + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							}
						}
					}
				}
			}
		}
	}
	
	private void findQualifiedNameDependents(String qualifiedName, IPartRequestor requestor) {
		monitor.enterRead();
		
		try{
			long startTime = System.currentTimeMillis();
			if(DEBUG){
			    System.out.println("\r\nYou asked for dependents of " + qualifiedName + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			QualifiedName entryKey = new QualifiedName(qualifiedName);
			QualifiedNameDependentEntry dependentEntry = (QualifiedNameDependentEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY);
			
			if(dependentEntry != null){
				Set dependents = dependentEntry.getParts();
				for (Iterator iter = dependents.iterator(); iter.hasNext();) {
					Part dependent = (Part) iter.next();
					
					if(DEBUG){
					    System.out.println("\t\"" + dependent + "\" depends directly upon \"" + qualifiedName + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					requestor.acceptPart(dependent.getPackageName(), dependent.getPartName());
				}
			}
			
			if(DEBUG){
			    System.out.println("Finished finding dependents of " + qualifiedName + "(" + (System.currentTimeMillis() - startTime) + "ms)\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}finally{
			monitor.exitRead();
		}
	}
	
	public void findDependents(String name, IPartRequestor requestor){
		String[] segments = Util.qualifiedNameToStringArray(name);
		if (segments.length > 1) {
			findQualifiedNameDependents(name, requestor );
			return;
		}
		
		SimpleName entryKey = new SimpleName(name);
		SimpleNameDependentEntry dependentEntry = (SimpleNameDependentEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY);
		
		if(dependentEntry != null){
			HashSet dependents = dependentEntry.getFileParts();
			for (Iterator iterator = dependents.iterator(); iterator.hasNext();) {
				Part filePart = (Part)iterator.next();
				HashSet partsPerFile = dependentEntry.getParts(filePart);
				for (Iterator iter = partsPerFile.iterator(); iter.hasNext();) {
					 SimpleName	part = (SimpleName) iter.next();
					 
					 if(DEBUG){
					     System.out.println("\t\"" + part + "\" in file \"" + filePart + "\" depends on \"" +  name + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					 }
					 
					 requestor.acceptPart(filePart.getPackageName(), part.getSimpleName());
				}
			}
		}
	}
	
	public void findFunctionDependencies(String packageName, String partName, IFunctionRequestor requestor){
	    monitor.enterRead();
		
		try{
			long startTime = System.currentTimeMillis();
			if(DEBUG){
			    System.out.println("\nYou asked for dependencies of function: " + Util.appendToQualifiedName(packageName, partName, ".") + ":");
			}
			
			Part entryKey = new Part(packageName, partName);
			
			FunctionEntry functionEntry = (FunctionEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.FUNCTION_ENTRY);
			
			if(functionEntry != null){
				Set functions = functionEntry.getFunctions();
				
				for (Iterator iter = functions.iterator(); iter.hasNext();) {
                    Function function = (Function) iter.next();
                    requestor.acceptFunction(function.getProjectName(), function.getPackageName(), function.getPartName());
                }
			}
			
			if(DEBUG){
			    System.out.println("Finished finding dependencies of function: " + Util.appendToQualifiedName(packageName, partName, ".") + "(" + (System.currentTimeMillis() - startTime) + "ms)\n");
			}
		}finally{
			monitor.exitRead();
		}
	}
	
	public void removePart(String packageName, String partName, String filePartName){
		
		monitor.enterWrite();
		
		try{
			Part entryKey = new Part(packageName, partName);
			DependencyEntry dependencies = (DependencyEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
			
			if(dependencies != null){
				removeSimpleNames(packageName, partName, filePartName, dependencies.getSimpleNames());
				removeQualifiedNames(packageName, partName, dependencies.getQualifiedNames());
			}
			removeDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
			
			removeDependencyEntry(entryKey, IDependencyGraphEntry.FUNCTION_ENTRY);
			
		}finally{
			monitor.exitWrite();
		}
		
	}
	
	private void removeQualifiedNames(String packageName, String partName, Set qualifiedNames) {
		for (Iterator iter = qualifiedNames.iterator(); iter.hasNext();) {
			QualifiedName qualifiedName = (QualifiedName) iter.next();
			removeQualifiedNameDependent(packageName, partName, qualifiedName);
		}
	}

	private void removeSimpleNames(String packageName, String partName, String filePartName, Set simpleNames) {
		for (Iterator iter = simpleNames.iterator(); iter.hasNext();) {
			SimpleName simpleName = (SimpleName) iter.next();
			removeSimpleNameDependent(packageName, partName, filePartName, simpleName);					
		}
	}

	public void putPart(String packageName, String partName, String filePartName, IDependencyInfo dependencyInfo){
		
		monitor.enterWrite();
		
		try{
			if(DEBUG){
			    System.out.println("\nBegin processing dependency information for: " + Util.appendToQualifiedName(packageName, partName, ".")); //$NON-NLS-1$
			}
			
			Part entryKey = new Part(packageName, partName);
			DependencyEntry dependencies = (DependencyEntry)getDependencyEntry(entryKey, IDependencyGraphEntry.DEPENDENCY_ENTRY);
			
			putSimpleNames(packageName, partName, filePartName, dependencyInfo, dependencies);
			putQualifiedNames(packageName, partName, dependencyInfo, dependencies);
			
			if(DEBUG){
			    System.out.println("Finished processing dependency information for: " + Util.appendToQualifiedName(packageName, partName, ".") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
		}finally{
			monitor.exitWrite();
		}
	}
	
	private void putQualifiedNames(String packageName, String partName, IDependencyInfo newDependencies, DependencyEntry previousDependencies) {
		Set previousQualifiedNames = previousDependencies != null ? new HashSet(previousDependencies.getQualifiedNames()) : Collections.EMPTY_SET;
		for (Iterator<String> iter = newDependencies.getQualifiedNames().iterator(); iter.hasNext();) {
			QualifiedName qualifiedName = new QualifiedName(iter.next());
			
			if(DEBUG){
			    System.out.println("\tRecord Qualified Name: " + qualifiedName); //$NON-NLS-1$
			}
			if(!previousQualifiedNames.contains(qualifiedName)){
				recordQualifiedNameDependency(packageName, partName, qualifiedName);
				recordQualifiedNameDependent(packageName, partName, qualifiedName);
			}else{
				previousQualifiedNames.remove(qualifiedName);
			}
		}
		
		for (Iterator iter = previousQualifiedNames.iterator(); iter.hasNext();) {
			QualifiedName qualifiedName = (QualifiedName) iter.next();
			
			if(DEBUG){
			    System.out.println("\tRemoving unused qualified dependency: " + qualifiedName); //$NON-NLS-1$
			}
			removeQualifiedNameDependency(packageName, partName, qualifiedName);
			removeQualifiedNameDependent(packageName, partName, qualifiedName);
		}
	}

	private void putSimpleNames(String packageName, String partName, String filePartName, IDependencyInfo newDependencies, DependencyEntry previousDependencies) {
		
		Set previousSimpleNames = previousDependencies != null ? new HashSet(previousDependencies.getSimpleNames()) : Collections.EMPTY_SET;
		for (Iterator iter = newDependencies.getSimpleNames().iterator(); iter.hasNext();) {
			SimpleName simpleName = new SimpleName((String) iter.next());
			
			if(DEBUG){
			    System.out.println("\tRecord Simple Name: " + simpleName); //$NON-NLS-1$
			}
			if(!previousSimpleNames.contains(simpleName)){
				recordSimpleNameDependency(packageName, partName, simpleName);
				recordSimpleNameDependent(packageName, partName, filePartName, simpleName);
			}else{
				previousSimpleNames.remove(simpleName);
			}
		}
		
		for (Iterator iter = previousSimpleNames.iterator(); iter.hasNext();) {
			SimpleName simpleName = (SimpleName) iter.next();
			
			if(DEBUG){
			    System.out.println("\tRemoving unused dependency: " + simpleName); //$NON-NLS-1$
			}
			removeSimpleNameDependency(packageName, partName, simpleName);
			removeSimpleNameDependent(packageName, partName, filePartName, simpleName);
		}
	}
	
	public void save(){
		monitor.enterWrite();
		try{
			bufferPool.flush();
		}finally{
			monitor.exitWrite();
		}
	}
	
	public void clear(){
		monitor.enterWrite();
		
		bufferPool.clear();
		try{
			clearDirectory(dependenciesDirectory.toFile());
			clearDirectory(dependentsDirectory.toFile());
			clearDirectory(functionsDirectory.toFile());
		}finally{
			monitor.exitWrite();
		}
	}

	private void clearDirectory(File cacheDir) {
		if(cacheDir.exists()){
			File[] graphFiles = cacheDir.listFiles(new FilenameFilter(){
	
				public boolean accept(File dir, String name) {
					if(name.endsWith(DEPENDENCY_GRAPH_FILE_EXTENSION)){
						return true;
					}
					return false;
				}
			});
			
			for (int i = 0; i < graphFiles.length; i++) {
				graphFiles[i].delete();
			}
		}
	}
	
	private IPath getBufferName(IDependencyGraphValue key, int dependencyType) {
		
		IPath outputFolder;
		int numBuffers;
	
		switch(dependencyType){
			case IDependencyGraphEntry.DEPENDENCY_ENTRY:
				outputFolder = dependenciesDirectory;
				numBuffers = 500;
				break;
			case IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY:
			case IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY:
				outputFolder = dependentsDirectory;
				numBuffers = 500;
				break;
			case IDependencyGraphEntry.FUNCTION_ENTRY:
				outputFolder = functionsDirectory;
				numBuffers = 10;
				break;
			default:
				throw new BuildException("Invalid Dependency Type");
		}
		
		StringBuffer outputFileName = new StringBuffer(String.valueOf(Math.abs(key.getNormalizedHashCode()) % numBuffers)).append(DEPENDENCY_GRAPH_FILE_EXTENSION);
		return outputFolder.append(outputFileName.toString());
	}
	
	private IDependencyGraphEntry getDependencyEntry(IDependencyGraphValue key, int type){
		HashMap dependencyMap = bufferPool.get(getBufferName(key, type));
		
		if(dependencyMap != null){
			return (IDependencyGraphEntry)dependencyMap.get(key);	
		}
		return null;
	}
	
	private void putDependencyEntry(IDependencyGraphValue key, int type, IDependencyGraphEntry value){
		HashMap dependencyMap = bufferPool.get(getBufferName(key, type));
		
		if(dependencyMap == null){
			dependencyMap = new HashMap();
		}
		
		dependencyMap.put(key, value);
		
		bufferPool.put(getBufferName(key, type), dependencyMap);
	}
	
	private void removeDependencyEntry(IDependencyGraphValue key, int type){
		HashMap dependencyMap = bufferPool.get(getBufferName(key, type));
		
		if(dependencyMap != null){
			dependencyMap.remove(key);	
			
			if(dependencyMap.size() == 0){
				bufferPool.remove(getBufferName(key, type));
			}else{
				bufferPool.put(getBufferName(key, type), dependencyMap);
			}
		}
	}
}

