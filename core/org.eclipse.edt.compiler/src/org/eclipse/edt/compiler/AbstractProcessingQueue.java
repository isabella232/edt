package org.eclipse.edt.compiler;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public abstract class AbstractProcessingQueue {
	public static final int LEVEL_THREE = 3;
	public static final int LEVEL_TWO = 2;
	public static final int LEVEL_ONE = 1;
	
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
        public String packageName;
        public String caseSensitivePartName;
       
        ProcessingUnit(String packageName, String caseSensitivePartName) {
            super();
            this.packageName = packageName;
            this.caseSensitivePartName = caseSensitivePartName; 
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
    
    public void addPart(String packageName, String caseSensitivePartName) {
    	pendingUnits.put(new ProcessingUnitKey(packageName, NameUtile.getAsName(caseSensitivePartName)), new ProcessingUnit(packageName, caseSensitivePartName));
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
            ProcessingUnit processingUnit =  iterator.next();
            process(processingUnit, LEVEL_THREE);
        }
		
		if(DEBUG){
		    System.out.println("Compile Finished:" + " [Time: " + (System.currentTimeMillis() - startTime) + ", Num Parts: " + totalUnitsCompiled + ", Requests Denied: " + numRequestsDenied + ", Compiles Aborted: " + numCompilesAborted + ", Functions: " + numTopLevelFunctionsCompiled + ", Errors Created: " + numErrorsReported + "]");
		}
	}
    
    public IPartBinding process(ProcessingUnit processingUnit, int compileLevel) {
    	IPartBinding result = null;
    	String name =  NameUtile.getAsName(processingUnit.caseSensitivePartName);
        processingStack.add(new ProcessingUnitKey(processingUnit.packageName, name));
        unitsBeingProcessedToCompileLevel.put(new ProcessingUnitKey(processingUnit.packageName, name), new Integer(compileLevel));
        
        try{
            switch(compileLevel){
            	case LEVEL_THREE: 
            	    		pendingUnits.remove(new ProcessingUnitKey(processingUnit.packageName, name));
                       		result = level03Compile(processingUnit.packageName, processingUnit.caseSensitivePartName);
                       		updateProgress();
                       		processedUnits.add(new ProcessingUnitKey(processingUnit.packageName, name));
                            break;
            	case LEVEL_TWO:
            	    		result = level02Compile(processingUnit.packageName, processingUnit.caseSensitivePartName);
            	    		break;
            	case LEVEL_ONE:
            	    		result = level01Compile(processingUnit.packageName, processingUnit.caseSensitivePartName);
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
        
        unitsBeingProcessedToCompileLevel.remove(new ProcessingUnitKey(processingUnit.packageName, name));
        processingStack.remove(processingStack.size() - 1);
        return result;
    }
    
    protected abstract IPartBinding level03Compile(String packageName, String caseSensitivePartName);
	
    protected abstract IPartBinding level02Compile(String packageName, String caseSensitivePartName);
    
    protected abstract IPartBinding level01Compile(String packageName, String caseSensitivePartName);
    
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
		        		IPartBinding partBindingFromCache = getPartBindingFromCache(packageName, caseInsensitivePartName);
		        		if(partBindingFromCache != null){
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

    public void reschedulePart(String packageName, String caseInsensitivePartName) {
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
}
