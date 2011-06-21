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

//TODO:
// listen for generator preference changes on project/folder/file and regen the changed heirarchy
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
        	GenerationBuildManager.getInstance().setProjectState(getProject(), isOK);
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
}
