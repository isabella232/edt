package org.eclipse.edt.ide.core.internal.generation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.generation.IGenerationMessageRequestor;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.utils.StringOutputBuffer;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.InvalidPartTypeException;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.IEnvironment;

import com.ibm.icu.util.StringTokenizer;

/**
 * Generates a set of parts. For each part, it is passed to all the generators configured to run for its corresponding .egl file.
 * A generation queue can only be run on a single project.
 */
public class GenerationQueue {
	
	/**
	 * The parts yet to be generated.
	 */
	protected LinkedHashMap<GenerationUnitKey, GenerationUnit> pendingUnits;
	
	/**
	 * Notifier used to report progress.
	 */
	protected IBuildNotifier notifier;
	
	/**
	 * The project being processed.
	 */
	protected IProject project;
	
	/**
	 * The package fragment roots of the project, used for locating the .egl IFile. 
	 */
	protected IPackageFragmentRoot[] pkgFragmentRoots;
	
	// Progress reporting
	protected int unitsGenerated;
    private int generateThreshold;
	private float increment;
	
	// Debug
	public long startTime;
	public static final boolean DEBUG = false;
	
	public class GenerationUnit {
        public String[] packageName;
        public String caseSensitiveInternedPartName;
       
        GenerationUnit(String[] packageName, String caseSensitiveInternedPartName) {
            this.packageName = packageName;
            this.caseSensitiveInternedPartName = caseSensitiveInternedPartName; 
        }
    }
    
    public class GenerationUnitKey {
    	private String[] packageName;
    	private String caseInsensitiveInternedPartName;
    	
    	public GenerationUnitKey(String[] packageName, String caseInsensitiveInternedPartName) {
    		this.packageName = packageName;
    		this.caseInsensitiveInternedPartName = caseInsensitiveInternedPartName;
    	}
    	public boolean equals(Object otherObject) {
    		if (this == otherObject) {
    			return true;
    		}
    		if (otherObject instanceof GenerationUnitKey) {
    			GenerationUnitKey otherGUKey = (GenerationUnitKey)otherObject;
    			return otherGUKey.packageName == packageName && otherGUKey.caseInsensitiveInternedPartName == caseInsensitiveInternedPartName;
    		}
    		return false;
    	}
    	
    	public int hashCode() {
    		return caseInsensitiveInternedPartName.hashCode();
    	}
    }
    
    public GenerationQueue(IBuildNotifier notifier, IProject project) {
    	if (notifier == null) {
    		this.notifier = NullBuildNotifier.getInstance();
    	}
    	else {
    		this.notifier = notifier;
    	}
    	
    	this.pendingUnits = new LinkedHashMap<GenerationQueue.GenerationUnitKey, GenerationQueue.GenerationUnit>();
    	this.increment = 0.40f;
    	this.project = project;
    	
    	try {
	    	pkgFragmentRoots = EGLCore.create(project).getPackageFragmentRoots();
    	}
    	catch (EGLModelException e) {
    		pkgFragmentRoots = new IPackageFragmentRoot[0];
    	}
    }
	
    /**
     * Adds a part to the generation queue.
     */
	public void addPart(String[] packageName, String caseSensitiveInternedPartName) {
		pendingUnits.put(new GenerationUnitKey(packageName, InternUtil.intern(caseSensitiveInternedPartName)), new GenerationUnit(packageName, caseSensitiveInternedPartName));
	}
	
	public void generate() {
		
		if (DEBUG) {
			startTime = System.currentTimeMillis();
		}
		
		initProgress();
		
		if (pkgFragmentRoots.length > 0) { // Can't do anything without the source folders
			while (!pendingUnits.isEmpty()) {
				notifier.checkCancel();
				Iterator<GenerationUnit> iterator = pendingUnits.values().iterator();
	            GenerationUnit genUnit = iterator.next();
	            generate(genUnit);
			}
		}
		
		if (DEBUG) {
			System.out.println("Generation Finished:" + " [Time: " + (System.currentTimeMillis() - startTime) + ", Num Parts: " + unitsGenerated); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	private void initProgress(){
		generateThreshold = pendingUnits.size();
		unitsGenerated = 0;
		if (generateThreshold > 0){
			notifier.setProgressPerEGLPart(increment/generateThreshold);
		}
		
		increment = increment/2;	
	}
	
	private void updateProgress (){
   		notifier.compiled();
   		unitsGenerated++;
   		   		
   		if (unitsGenerated == generateThreshold && !pendingUnits.isEmpty()){
   			initProgress();
   		}
	}
	
	private void generate(GenerationUnit genUnit) {
		pendingUnits.remove(new GenerationUnitKey(genUnit.packageName, InternUtil.intern(genUnit.caseSensitiveInternedPartName)));
		
		notifier.subTask(CoreIDEPluginStrings.bind(CoreIDEPluginStrings.GeneratePartsOperation_SubTaskName, genUnit.caseSensitiveInternedPartName));
		
		IGenerationMessageRequestor messageRequestor = createMessageRequestor();
		
		try {
			ProjectEnvironment environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
			Part part = environment.findPart(InternUtil.intern(genUnit.packageName), InternUtil.intern(genUnit.caseSensitiveInternedPartName));
			
			//TODO gather errors reported by the generators, check for errors of dependent parts, etc.
			if (part != null && !part.hasCompileErrors()) {
				for (IPackageFragmentRoot root : pkgFragmentRoots) {
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(root.getPath().append(part.getFileName()));
					if (file != null && file.exists()) {
						invokeGenerators(file, part);
						break;
					}
				}
			}
		} catch (PartNotFoundException e) {
			buildPartNotFoundMessage(e, messageRequestor, genUnit.caseSensitiveInternedPartName);
		} catch (RuntimeException e) {
			handleRuntimeException(e, messageRequestor, genUnit.caseSensitiveInternedPartName, new HashSet());
		} catch (final Exception e) {
			handleUnknownException(e, messageRequestor);
		}
		
		updateProgress();
	}
	
	/**
	 * A file has zero or more generators associated with it. These may be specified directly on the file, or the settings
	 * can be inherited from a parent resource (including the workspace defaults).
	 */
	private void invokeGenerators(IFile file, Part part) throws Exception {
		IGenerator[] generators = ProjectSettingsUtility.getGenerators(file);
		if (generators.length != 0) {
			IEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project).getIREnvironment();
			for (int i = 0; i < generators.length; i++) {
				generators[i].generate(file.getFullPath().toString(), (Part)part.clone(), env, true);
			}
		}
	}
	
	protected void handleRuntimeException(RuntimeException e, IGenerationMessageRequestor messageRequestor, String partName, HashSet seen) {
		if (seen.contains(e)) {
			handleUnknownException(e, messageRequestor);
			return;
		}
		seen.add(e);

		Throwable cause = e.getCause();
		if (cause instanceof PartNotFoundException) {
			buildPartNotFoundMessage((PartNotFoundException) cause, messageRequestor, partName);
			return;
		}
		if (cause instanceof InvalidPartTypeException) {
			buildInvalidPartTypeMessage((InvalidPartTypeException) cause, messageRequestor, partName);
			return;
		}

		if (cause instanceof RuntimeException) {
			handleRuntimeException((RuntimeException) cause, messageRequestor, partName, seen);
			return;
		}

		handleUnknownException(e, messageRequestor);
		return;
	}

	protected void handleUnknownException(Exception e, IGenerationMessageRequestor messageRequestor) {
		buildExceptionMessage(e, messageRequestor);
		buildStackTraceMessages(e, messageRequestor);
		Logger.log(this, "GenerationQueue.generate():  Error during generation", e); //$NON-NLS-1$

	}
	
	protected void buildPartNotFoundMessage(PartNotFoundException e, IGenerationMessageRequestor result, String partName) {
		EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PARTNOTFOUND, null,
				new String[] { partName, e.getMessage() });
		result.addMessage(message);
	}

	protected void buildInvalidPartTypeMessage(InvalidPartTypeException e, IGenerationMessageRequestor result, String partName) {
		EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PARTNOTFOUND, null, new String[] { partName,
				e.getMessage() });
		result.addMessage(message);
	}
	
	public void buildExceptionMessage(Exception e, IGenerationMessageRequestor result) {
		String text = e.getMessage();
		if (text != null) {
			EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EXCEPTION_MESSAGE, null, text);
			result.addMessage(message);
		}
	}
	
	public static void buildStackTraceMessages(Throwable e, IGenerationMessageRequestor result) {
		StringOutputBuffer buffer = new StringOutputBuffer();
		PrintWriter writer = new PrintWriter(buffer);
		e.printStackTrace(writer);
		writer.flush();
		String text = buffer.toString();
		char[] token;
		StringTokenizer tokenizer = new StringTokenizer(text, "\n\r\f"); //$NON-NLS-1$
		while (tokenizer.hasMoreElements()) {
			token = tokenizer.nextToken().toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < token.length; i++) {
				if (token[i] == '\t') {
					stringBuffer.append("      "); //$NON-NLS-1$
				} else {
					stringBuffer.append(token[i]);
				}
			}

			EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EXCEPTION_STACKTRACE, null, stringBuffer.toString());
			result.addMessage(message);
		}
	}
	
	protected static IGenerationMessageRequestor createMessageRequestor() {
		return new IGenerationMessageRequestor() {
			ArrayList list = new ArrayList();

			boolean error = false;

			public void addMessage(EGLMessage message) {
				list.add(message);
				if (message.isError()) {
					error = true;
				}
			}

			public void addMessages(List list) {
				Iterator i = list.iterator();
				while (i.hasNext()) {
					addMessage((EGLMessage) i.next());
				}
			}

			public List getMessages() {
				return list;
			}

			public boolean isError() {
				return error;
			}

			public void clear() {
				error = false;
				list = new ArrayList();
			}
		};
	}
}
