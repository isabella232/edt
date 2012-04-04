package org.eclipse.edt.compiler;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public abstract class AbstractProcessingQueue {
	public static final int LEVEL_THREE = 3;
	public static final int LEVEL_TWO = 2;
	public static final int LEVEL_ONE = 1;
	
	protected LinkedHashMap pendingUnits = new LinkedHashMap();
    private Stack processingStack = new Stack();
    private HashMap unitsBeingProcessedToCompileLevel = new HashMap();
    
    private HashSet processedUnits = new HashSet();
    
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
        public String[] packageName;
        public String caseSensitiveInternedPartName;
       
        ProcessingUnit(String[] packageName, String caseSensitiveInternedPartName) {
            super();
            this.packageName = packageName;
            this.caseSensitiveInternedPartName = caseSensitiveInternedPartName; 
        }
    }
    
    public class ProcessingUnitKey{
    	private String[] packageName;
    	private String caseInsensitiveInternedPartName;
    	
    	public ProcessingUnitKey(String[] packageName, String caseInsensitiveInternedPartName){
    		this.packageName = packageName;
    		this.caseInsensitiveInternedPartName = caseInsensitiveInternedPartName;
    	}
    	public boolean equals(Object otherObject){
    		if(this == otherObject){
    			return true;
    		}
    		if(otherObject instanceof ProcessingUnitKey){
    			ProcessingUnitKey otherPUKey = (ProcessingUnitKey)otherObject;
    			return otherPUKey.packageName == packageName && otherPUKey.caseInsensitiveInternedPartName == caseInsensitiveInternedPartName;
    		}
    		return false;
    	}
    	
    	public int hashCode(){
    		return caseInsensitiveInternedPartName.hashCode();
    	}
    }
    
    public AbstractProcessingQueue(IBuildNotifier notifier, ICompilerOptions compilerOptions) {
        super();
        this.notifier = notifier;
        this.compilerOptions = compilerOptions;
    }
    
    public void addPart(String[] packageName, String caseSensitiveInternedPartName) {
    	pendingUnits.put(new ProcessingUnitKey(packageName, InternUtil.intern(caseSensitiveInternedPartName)), new ProcessingUnit(packageName, caseSensitiveInternedPartName));
    }
    
   public boolean isPending(String[] packageName, String caseInsensitiveInternedPartName) {
        return pendingUnits.containsKey(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
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
   			
   			Iterator iterator = pendingUnits.values().iterator();
            ProcessingUnit processingUnit = (ProcessingUnit) iterator.next();
            process(processingUnit, LEVEL_THREE);
        }
		
		if(DEBUG){
		    System.out.println("Compile Finished:" + " [Time: " + (System.currentTimeMillis() - startTime) + ", Num Parts: " + totalUnitsCompiled + ", Requests Denied: " + numRequestsDenied + ", Compiles Aborted: " + numCompilesAborted + ", Functions: " + numTopLevelFunctionsCompiled + ", Errors Created: " + numErrorsReported + "]");
		}
	}
    
    public IPartBinding process(ProcessingUnit processingUnit, int compileLevel) {
    	IPartBinding result = null;
        processingStack.add(new ProcessingUnitKey(processingUnit.packageName, InternUtil.intern(processingUnit.caseSensitiveInternedPartName)));
        unitsBeingProcessedToCompileLevel.put(new ProcessingUnitKey(processingUnit.packageName, InternUtil.intern(processingUnit.caseSensitiveInternedPartName)), new Integer(compileLevel));
        
        try{
            switch(compileLevel){
            	case LEVEL_THREE: 
            	    		pendingUnits.remove(new ProcessingUnitKey(processingUnit.packageName, InternUtil.intern(processingUnit.caseSensitiveInternedPartName)));
                       		result = level03Compile(processingUnit.packageName, processingUnit.caseSensitiveInternedPartName);
                       		updateProgress();
                       		processedUnits.add(new ProcessingUnitKey(processingUnit.packageName, InternUtil.intern(processingUnit.caseSensitiveInternedPartName)));
                            break;
            	case LEVEL_TWO:
            	    		result = level02Compile(processingUnit.packageName, processingUnit.caseSensitiveInternedPartName);
            	    		break;
            	case LEVEL_ONE:
            	    		result = level01Compile(processingUnit.packageName, processingUnit.caseSensitiveInternedPartName);
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
        
        unitsBeingProcessedToCompileLevel.remove(new ProcessingUnitKey(processingUnit.packageName, InternUtil.intern(processingUnit.caseSensitiveInternedPartName)));
        processingStack.remove(processingStack.size() - 1);
        return result;
    }
    
    protected abstract IPartBinding level03Compile(String[] packageName, String caseSensitiveInternedPartName);
	
    protected abstract IPartBinding level02Compile(String[] packageName, String caseSensitiveInternedPartName);
    
    protected abstract IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName);
    
    protected abstract IPartBinding getPartBindingFromCache(String[] packageName, String partName);
    
	public IPartBinding requestCompilationFor(String[] packageName, String caseInsensitiveInternedPartName, boolean force) {
        if(isBeingProcessed(packageName, caseInsensitiveInternedPartName)) {
            if(isBeingCompiled(packageName, caseInsensitiveInternedPartName)){
                return getPartBindingFromCache(packageName, caseInsensitiveInternedPartName);
            }else{
                if(getCurrentCompileLevel() == LEVEL_TWO){
                    return getPartBindingFromCache(packageName, caseInsensitiveInternedPartName);
                }
                else{
	                if(DEBUG){
	                    System.out.println("Request denied because " + caseInsensitiveInternedPartName + " is already being processed"); //$NON-NLS-1$ //$NON-NLS-2$
	                    System.out.print("\t");
	                    for (Iterator iter = processingStack.iterator(); iter.hasNext();) {
	                        ProcessingUnitKey unit = (ProcessingUnitKey) iter.next();
	                        System.out.print(unit.caseInsensitiveInternedPartName + ", ");
	                    }
	                    System.out.print("\n");
	                    numCompilesAborted++;
	                }        
	                throw new CircularBuildRequestException();
                }
            }               
        }
        else{
            if(isPending(packageName, caseInsensitiveInternedPartName)){
	            if(processingStack.size() >= 10) {
		            if(DEBUG){
		                System.out.println("Requested denied because the processing stack is too deep"); //$NON-NLS-1$
		                numRequestsDenied++;
		            }
		            ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
		            if(force){
		                return process(processingUnit, LEVEL_TWO);
		            }
		            
		            return process(processingUnit, getCurrentCompileLevel() - 1);
	            }
		        else {
		        	try{
		        		IPartBinding partBindingFromCache = getPartBindingFromCache(packageName, caseInsensitiveInternedPartName);
		        		if(Binding.isValidBinding(partBindingFromCache)){
			        		//System.out.println("Found part from cache: " + caseInsensitiveInternedPartName);
			        		return partBindingFromCache;
			        	}else{
				            ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
				            return process(processingUnit, LEVEL_THREE);
			        	}
		            }catch(CircularBuildRequestException e){
		                reschedulePart(packageName, caseInsensitiveInternedPartName);
		                
		                ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
			            return process(processingUnit, LEVEL_TWO);
		            }
		        }
            }        
        }
        return null;
	}
	
	private boolean isBeingProcessed(String[] packageName, String caseInsensitiveInternedPartName) {
       return processingStack.contains(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
    }

    public void reschedulePart(String[] packageName, String caseInsensitiveInternedPartName) {
        processingStack.remove(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
        unitsBeingProcessedToCompileLevel.remove(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
        
        doAddPart(packageName, caseInsensitiveInternedPartName);
    }
    
    protected abstract void doAddPart(String[] packageName, String caseInsensitiveInternedPartName);
    
    private int getCurrentCompileLevel(){
        return ((Integer)unitsBeingProcessedToCompileLevel.get(processingStack.get(processingStack.size() - 1))).intValue();
    }

	private boolean isBeingCompiled(String[] packageName, String caseInsensitiveInternedPartName) {
	    return processingStack.indexOf(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName)) == (processingStack.size() - 1);
	}
}
