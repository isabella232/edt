package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * Generates all IRs in the project.
 */
public class CleanGenerator extends AbstractGenerator {
	
	public CleanGenerator(GenerationBuilder builder, IBuildNotifier notifier) {
		super(builder, notifier);
	}
	
	@Override
	protected void addAdditionalParts() {
		notifier.subTask(CoreIDEPluginStrings.analyzingAllIRs);
		try {
			final int segmentCount = outputLocation.getFullPath().segmentCount();
			outputLocation.accept(new IResourceProxyVisitor() {
				@Override
				public boolean visit(IResourceProxy proxy) throws CoreException {
					IResource resource = proxy.requestResource();
					
					switch (proxy.getType()) {
						case IResource.FILE:
							if (!processedFiles.contains(resource)) {
								IPath fullPath = resource.getFullPath();
								IPath packagePath = fullPath.removeFirstSegments(segmentCount).removeLastSegments(1);
								generationQueue.addPart(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(packagePath)), InternUtil.intern(fullPath.removeFileExtension().lastSegment()));
							}
							return false;
					}
					return true;
				}
			}, IResource.NONE);
		}
		catch (CoreException e) {
			throw new BuildException(e);
		}
	}
}
