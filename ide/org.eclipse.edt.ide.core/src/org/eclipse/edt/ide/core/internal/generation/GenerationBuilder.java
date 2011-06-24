package org.eclipse.edt.ide.core.internal.generation;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.builder.BuildNotifier;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;

//TODO:
// should we generate binary projects?
/**
 * Builder that runs generation on compiled EGL parts.
 */
public class GenerationBuilder extends IncrementalProjectBuilder {
	
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IBuildNotifier notifier;
		if (monitor == null) {
			notifier = NullBuildNotifier.getInstance();
		}
		else {
			notifier = new BuildNotifier(monitor);
		}
		
		boolean isOK = false;
        IResourceDelta delta = getDelta(getProject());
        notifier.begin();
        try {
        	if (kind == IncrementalProjectBuilder.FULL_BUILD) {
				doClean();
		        cleanBuild(delta, notifier);
        	}
        	else if (!GenerationBuildManager.getInstance().getProjectState(getProject())) {
		     	doClean();
		     	cleanBuild(null, notifier);
        	}
        	else if (needFullBuild()) {
		     	doClean();
		     	cleanBuild(null, notifier);
        	}
        	else {
	        	if (delta == null) {
		        	doClean();
		        	cleanBuild(delta, notifier);
		        } else{
		        	incrementalBuild(delta, notifier);
		        }
        	}
        	
        	isOK = true;
        } catch (CancelledException canceledException) {
			throw new OperationCanceledException();
        } finally {
        	if (!isOK) {
        		GenerationBuildManager.getInstance().setProjectState(getProject(), false);
			}
        	else {
				GenerationBuildManager.getInstance().putProject(
						getProject(),
						// only store the default gen IDs if they're being used for this project
						ProjectSettingsUtility.getCompilerId(getProject()) == null ? ProjectSettingsUtility.getWorkspaceGeneratorIds() : null);
			}
        	
        	notifier.done();
        }
		return null;
	}
	
	protected void clean(IProgressMonitor monitor) {
     	try {
     		doClean();
     	} catch (Exception e) {
     		EDTCoreIDEPlugin.getPlugin().log("EDT Generation Clean Failure",e); //$NON-NLS-1$
     		GenerationBuildManager.getInstance().clear(getProject());	
     	}
    }
	
	protected boolean cleanBuild(IResourceDelta delta, IBuildNotifier notifier) {
		GenerationBuildManager.getInstance().setProjectState(getProject(), false);
		CleanGenerator gen = new CleanGenerator(this, notifier);
		return gen.build(delta);
	}
	
	protected boolean incrementalBuild(IResourceDelta delta, IBuildNotifier notifier) {
		GenerationBuildManager.getInstance().setProjectState(getProject(), false);
		IncrementalGenerator gen = new IncrementalGenerator(this, notifier);
		return gen.build(delta);
	}
	
	protected void doClean() {
		GenerationBuildManager.getInstance().clear(getProject());
		deleteAllMarkers();
		
		//TODO What else should be done here? Tell the generators to "clean up" their generated artifacts?
	}
	
	protected void deleteAllMarkers() {
		try {
			getProject().deleteMarkers(EDTCoreIDEPlugin.GENERATION_PROBLEM, true, IResource.DEPTH_INFINITE);
		}
		catch (CoreException e) {
			throw new BuildException(e);
		}
	}
	
	private boolean needFullBuild() {
		if (ProjectSettingsUtility.getCompilerId(getProject()) == null) {
			// When using the workspace settings, regenerate everything if the default generators changed.
			String[] oldIds = GenerationBuildManager.getInstance().getDefaultGenIDs(getProject());
			
			// When using the workspace settings, regenerate everything we previously weren't using the workspace settings.
			if (oldIds == null) {
				return true;
			}
			
			// When using the workspace settings, regenerate everything if the default generators changed.
			String[] currIds = ProjectSettingsUtility.getWorkspaceGeneratorIds();
			if (currIds.length != oldIds.length) {
				return true;
			}
			
			for (String curr : currIds) {
				boolean foundMatch = false;
				for (String old : oldIds) {
					if (curr.equals(old)) {
						foundMatch = true;
						break;
					}
				}
				
				if (!foundMatch) {
					return true;
				}
			}
		}
		else {
			if (GenerationBuildManager.getInstance().getDefaultGenIDs(getProject()) != null) {
				// When using project-specific settings, regenerate everything if we previously used the workspace settings.
				return true;
			}
		}
		
		return false;
	}
}
