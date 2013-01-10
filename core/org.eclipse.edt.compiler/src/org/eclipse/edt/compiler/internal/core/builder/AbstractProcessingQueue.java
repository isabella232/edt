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
package org.eclipse.edt.compiler.internal.core.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public abstract class AbstractProcessingQueue {
	private static final int LEVEL_THREE = 3;
	private static final int LEVEL_TWO = 2;
	private static final int LEVEL_ONE = 1;
	
	protected LinkedHashMap<ProcessingUnitKey, ProcessingUnit> pendingUnits = new LinkedHashMap<ProcessingUnitKey, ProcessingUnit>();
    private Stack<ProcessingUnitKey> processingStack = new Stack<ProcessingUnitKey>();
    private HashMap<ProcessingUnitKey, Integer> unitsBeingProcessedToCompileLevel = new HashMap<ProcessingUnitKey, Integer>();
    
    private HashSet<ProcessingUnitKey> processedUnits = new HashSet<ProcessingUnitKey>();
    
    protected ICompilerOptions compilerOptions;
    
    //progress
    protected IBuildNotifier notifier;
    protected int	unitsCompiled = 0;
    private int compileThreshold = 0;
    protected int compileLoop = 0;
    
    // TODO Move increment somewhere else?
    private float increment = 0.40f;
    
    // Debug
    public int  totalUnitsCompiled = 0;
    public int numRequestsDenied = 0;
    public int numCompilesAborted = 0;
    public long startTime = 0;
    public int numTopLevelFunctionsCompiled = 0;
    public int numErrorsReported = 0;
        
    public static final boolean DEBUG = false;
    
    public class ProcessingUnit  {
    	PackageAndPartName ppName;
    	
        ProcessingUnit(PackageAndPartName ppName) {
            super();
            this.ppName = ppName;
        }
    }
    
    public class ProcessingUnitKey{
    	private String packageName;
    	private String caseInsensitivePartName;
    	
    	public ProcessingUnitKey(String packageName, String caseInsensitivePartName){
    		this.packageName = packageName;
    		this.caseInsensitivePartName = caseInsensitivePartName;
    	}
    	public boolean equals(Object otherObject){
    		if(this == otherObject){
    			return true;
    		}
    		if(otherObject instanceof ProcessingUnitKey){
    			ProcessingUnitKey otherPUKey = (ProcessingUnitKey)otherObject;
    			return NameUtile.equals(otherPUKey.packageName, packageName) && NameUtile.equals(otherPUKey.caseInsensitivePartName, caseInsensitivePartName);
    		}
    		return false;
    	}
    	
    	public int hashCode(){
    		return caseInsensitivePartName.hashCode();
    	}
    }
    
    public AbstractProcessingQueue(IBuildNotifier notifier, ICompilerOptions compilerOptions) {
        super();
        this.notifier = notifier;
        this.compilerOptions = compilerOptions;
    }
    
    public void addPart(PackageAndPartName ppName) {
    	pendingUnits.put(new ProcessingUnitKey(ppName.getPackageName(), ppName.getPartName()), new ProcessingUnit(ppName));
    }
    
   public boolean isPending(String packageName, String caseInsensitivePartName) {
        return pendingUnits.containsKey(new ProcessingUnitKey(packageName, caseInsensitivePartName));
    }
    
   private void initProgress(){
   		compileLoop++;
		compileThreshold = pendingUnits.size();
		unitsCompiled = 0;
		if (compileThreshold > 0){
			notifier.setProgressPerEGLPart(increment/compileThreshold);
		}
		
		increment = increment/2;	
   }
   
   private void updateProgress (){
   		notifier.compiled();
   		unitsCompiled++;
   		   		
   		if (unitsCompiled == compileThreshold && !pendingUnits.isEmpty()){
   			initProgress();
   		}
   		
   }
    
   protected abstract boolean hasExceededMaxLoop();
   
   public void process() {
       
       if(DEBUG){
           startTime = System.currentTimeMillis();
       }
       
       initProgress();
   		
		while(!pendingUnits.isEmpty()) {
   			
   			if (hasExceededMaxLoop()){
   	    		notifier.setAborted(true);
   	    		return;
   	    	}
   			
   			Iterator<ProcessingUnit> iterator = pendingUnits.values().iterator();
            ProcessingUnit processingUnit = iterator.next();
            process(processingUnit, LEVEL_THREE);
        }
		
		if(DEBUG){
		    System.out.println("Compile Finished:" + " [Time: " + (System.currentTimeMillis() - startTime) + ", Num Parts: " + totalUnitsCompiled + ", Requests Denied: " + numRequestsDenied + ", Compiles Aborted: " + numCompilesAborted + ", Functions: " + numTopLevelFunctionsCompiled + ", Errors Created: " + numErrorsReported + "]");
		}
	}
    
    private IPartBinding process(ProcessingUnit processingUnit, int compileLevel) {
    	IPartBinding result = null;
    	String name = processingUnit.ppName.getPartName();
    	String pkg = processingUnit.ppName.getPackageName();
        processingStack.add(new ProcessingUnitKey(pkg, name));
        unitsBeingProcessedToCompileLevel.put(new ProcessingUnitKey(pkg, name), new Integer(compileLevel));
        
        try{
            switch(compileLevel){
            	case LEVEL_THREE: 
            	    		pendingUnits.remove(new ProcessingUnitKey(pkg, name));
                       		result = level03Compile(processingUnit.ppName);
                       		updateProgress();
                       		processedUnits.add(new ProcessingUnitKey(pkg, name));
                            break;
            	case LEVEL_TWO:
            	    		result = level02Compile(processingUnit.ppName);
            	    		break;
            	case LEVEL_ONE:
            	    		result = level01Compile(processingUnit.ppName);
            	    		break;
            }
        }catch(CircularBuildRequestException e){
            throw e;      
        }catch(CancelledException e){
            throw e;
        }catch(BuildException e){
            throw e;
        }catch(RuntimeException e){
            throw new BuildException(e);            
        }
        
        unitsBeingProcessedToCompileLevel.remove(new ProcessingUnitKey(pkg, name));
        processingStack.remove(processingStack.size() - 1);
        return result;
    }
    
    protected abstract IPartBinding level03Compile(PackageAndPartName ppName);
	
    protected abstract IPartBinding level02Compile(PackageAndPartName ppName);
    
    protected abstract IPartBinding level01Compile(PackageAndPartName ppName);
    
    protected abstract IPartBinding getPartBindingFromCache(String packageName, String partName);
    
	public IPartBinding requestCompilationFor(String packageName, String caseInsensitivePartName, boolean force) {
        if(isBeingProcessed(packageName, caseInsensitivePartName)) {
            if(isBeingCompiled(packageName, caseInsensitivePartName)){
                return getPartBindingFromCache(packageName, caseInsensitivePartName);
            }else{
                if(getCurrentCompileLevel() == LEVEL_TWO){
                    return getPartBindingFromCache(packageName, caseInsensitivePartName);
                }
                else{
	                if(DEBUG){
	                    System.out.println("Request denied because " + caseInsensitivePartName + " is already being processed"); //$NON-NLS-1$ //$NON-NLS-2$
	                    System.out.print("\t");
	                    for (ProcessingUnitKey unit : processingStack) {
	                        System.out.print(unit.caseInsensitivePartName + ", ");
	                    }
	                    System.out.print("\n");
	                    numCompilesAborted++;
	                }        
	                throw new CircularBuildRequestException();
                }
            }               
        }
        else{
            if(isPending(packageName, caseInsensitivePartName)){
	            if(processingStack.size() >= 10) {
		            if(DEBUG){
		                System.out.println("Requested denied because the processing stack is too deep"); //$NON-NLS-1$
		                numRequestsDenied++;
		            }
		            ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitivePartName));
		            if(force){
		                return process(processingUnit, LEVEL_TWO);
		            }
		            
		            return process(processingUnit, getCurrentCompileLevel() - 1);
	            }
		        else {
		        	try{
		        		// Try to get the part from the cache, in case it has been compiled at a level 2 already
		        		IPartBinding partBindingFromCache = getPartBindingFromCache(packageName, caseInsensitivePartName);
		        		if(partBindingFromCache != null && partBindingFromCache.isValid()){
			        		//System.out.println("Found part from cache: " + caseInsensitivePartName);
			        		return partBindingFromCache;
			        	}else{
				            ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitivePartName));
				            return process(processingUnit, LEVEL_THREE);
			        	}
		            }catch(CircularBuildRequestException e){
		                reschedulePart(packageName, caseInsensitivePartName);
		                
		                ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitivePartName));
			            return process(processingUnit, LEVEL_TWO);
		            }
		        }
            }        
        }
        return null;
	}
	
	private boolean isBeingProcessed(String packageName, String caseInsensitivePartName) {
       return processingStack.contains(new ProcessingUnitKey(packageName, caseInsensitivePartName));
    }

    private void reschedulePart(String packageName, String caseInsensitivePartName) {
        processingStack.remove(new ProcessingUnitKey(packageName, caseInsensitivePartName));
        unitsBeingProcessedToCompileLevel.remove(new ProcessingUnitKey(packageName, caseInsensitivePartName));
        
        doAddPart(packageName, caseInsensitivePartName);
    }
    
    protected abstract void doAddPart(String packageName, String caseInsensitivePartName);
    
    private int getCurrentCompileLevel(){
        return ((Integer)unitsBeingProcessedToCompileLevel.get(processingStack.get(processingStack.size() - 1))).intValue();
    }

	private boolean isBeingCompiled(String packageName, String caseInsensitivePartName) {
	    return processingStack.indexOf(new ProcessingUnitKey(packageName, caseInsensitivePartName)) == (processingStack.size() - 1);
	}
	
	protected boolean canSave(String partName) {
		return !EGLNameValidator.isWindowsReservedFileName(partName);
	}
	
	public IBuildNotifier getNotifier() {
		return this.notifier;
	}
}
