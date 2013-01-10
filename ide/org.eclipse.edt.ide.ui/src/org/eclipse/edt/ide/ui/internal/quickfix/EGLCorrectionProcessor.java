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

package org.eclipse.edt.ide.ui.internal.quickfix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposal;
import org.eclipse.edt.ide.ui.editor.IProblemLocation;
import org.eclipse.edt.ide.ui.editor.IQuickAssistProcessor;
import org.eclipse.edt.ide.ui.editor.IQuickFixProcessor;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.IAnnotation;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.ChangeCorrectionProposal;
import org.eclipse.edt.ide.ui.internal.util.Messages;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerHelpRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

public class EGLCorrectionProcessor implements org.eclipse.jface.text.quickassist.IQuickAssistProcessor {

	private static final String QUICKFIX_PROCESSOR_CONTRIBUTION_ID= "quickFixProcessors"; //$NON-NLS-1$
	private static final String QUICKASSIST_PROCESSOR_CONTRIBUTION_ID= "quickAssistProcessors"; //$NON-NLS-1$
	private static ContributedProcessorDescriptor[] fgContributedCorrectionProcessors= null;
	private static ContributedProcessorDescriptor[] fgContributedAssistProcessors= null;
	
	private static ContributedProcessorDescriptor[] getProcessorDescriptors(String contributionId, boolean testMarkerTypes) {
		IConfigurationElement[] elements= Platform.getExtensionRegistry().getConfigurationElementsFor(EDTUIPlugin.PLUGIN_ID, contributionId);
		ArrayList res= new ArrayList(elements.length);

		for (int i= 0; i < elements.length; i++) {
			ContributedProcessorDescriptor desc= new ContributedProcessorDescriptor(elements[i], testMarkerTypes);
			IStatus status= desc.checkSyntax();
			if (status.isOK()) {
				res.add(desc);
			} else {
				EDTUIPlugin.log(status);
			}
		}
		return (ContributedProcessorDescriptor[]) res.toArray(new ContributedProcessorDescriptor[res.size()]);
	}

	private static ContributedProcessorDescriptor[] getCorrectionProcessors() {
		if (fgContributedCorrectionProcessors == null) {
			fgContributedCorrectionProcessors= getProcessorDescriptors(QUICKFIX_PROCESSOR_CONTRIBUTION_ID, true);
		}
		return fgContributedCorrectionProcessors;
	}
	
	private static ContributedProcessorDescriptor[] getAssistProcessors() {
		if (fgContributedAssistProcessors == null) {
			fgContributedAssistProcessors= getProcessorDescriptors(QUICKASSIST_PROCESSOR_CONTRIBUTION_ID, false);
		}
		return fgContributedAssistProcessors;
	}

	private String fErrorMessage;
	private EGLCorrectionAssistant fAssistant;
	
	public EGLCorrectionProcessor(EGLCorrectionAssistant assistant) {
		fAssistant= assistant;
		fAssistant.addCompletionListener(new ICompletionListener() {

			public void assistSessionEnded(ContentAssistEvent event) {
				fAssistant.setStatusLineVisible(false);
			}

			public void assistSessionStarted(ContentAssistEvent event) {
				fAssistant.setStatusLineVisible(true);
				fAssistant.setStatusMessage(getJumpHintStatusLineMessage());
			}

			public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
				if (proposal instanceof IStatusLineProposal) {
					IStatusLineProposal statusLineProposal= (IStatusLineProposal)proposal;
					String message= statusLineProposal.getStatusMessage();
					if (message != null) {
						fAssistant.setStatusMessage(message);
						return;
					}
				}
				fAssistant.setStatusMessage(getJumpHintStatusLineMessage());
			}

			private String getJumpHintStatusLineMessage() {
				if (fAssistant.isUpdatedOffset()) {
					String key= getQuickAssistBinding();
					if (key == null)
						return CorrectionMessages.EGLCorrectionProcessor_go_to_original_using_menu;
					else
						return Messages.format(CorrectionMessages.EGLCorrectionProcessor_go_to_original_using_key, key);
				} else if (fAssistant.isProblemLocationAvailable()) {
					String key= getQuickAssistBinding();
					if (key == null)
						return CorrectionMessages.EGLCorrectionProcessor_go_to_closest_using_menu;
					else
						return Messages.format(CorrectionMessages.EGLCorrectionProcessor_go_to_closest_using_key, key);
				} else
					return ""; //$NON-NLS-1$
			}

			private String getQuickAssistBinding() {
				final IBindingService bindingSvc= (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
				return bindingSvc.getBestActiveBindingFormattedFor(ITextEditorActionDefinitionIds.QUICK_ASSIST);
			}
		});
	}

	
	@Override
	public String getErrorMessage() {
		return fErrorMessage;
	}

	@Override
	public boolean canFix(Annotation annotation) {
		return hasCorrections(annotation);
	}

	@Override
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		return false;
	}

	@Override
	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext quickAssistContext) {
		ISourceViewer viewer= quickAssistContext.getSourceViewer();
		int documentOffset= quickAssistContext.getOffset();
		IEditorPart part= fAssistant.getEditor();
		IEGLFile eglModelFile = getEGLFileModelFile(part);
		IAnnotationModel model= EGLUI.getDocumentProvider().getAnnotationModel(part.getEditorInput());

		int length= viewer != null ? viewer.getSelectedRange().y : 0;
		AssistContext context= new AssistContext(eglModelFile, viewer, part, documentOffset, length);
		Annotation[] annotations= fAssistant.getAnnotationsAtOffset();
		fErrorMessage= null;
		ICompletionProposal[] res= null;
		
		if (model != null && annotations != null) {
			ArrayList proposals= new ArrayList(10);
			IStatus status= collectProposals(context, model, annotations, true, !fAssistant.isUpdatedOffset(), proposals);
			res= (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
			if (!status.isOK()) {
				fErrorMessage= status.getMessage();
				EDTUIPlugin.log(status);
			}
		}
		
		if (res == null || res.length == 0) {
			return new ICompletionProposal[] { new ChangeCorrectionProposal(CorrectionMessages.NoCorrectionProposal_description, new NullChange(""), 0, null) }; //$NON-NLS-1$
		}
		
		if (res.length > 1) {
			Arrays.sort(res, new CompletionProposalComparator());
		}
		
		return res;
	}
	
	private IEGLFile getEGLFileModelFile(IEditorPart part){
		IFile ifile = ((IFileEditorInput)part.getEditorInput()).getFile();
		return(EGLCore.createEGLFileFrom(ifile));
	}
	
	public static IStatus collectProposals(IInvocationContext context, IAnnotationModel model, Annotation[] annotations, boolean addQuickFixes, boolean addQuickAssists, Collection proposals) {
		ArrayList problems= new ArrayList();

		// collect problem locations and corrections from marker annotations
		for (int i= 0; i < annotations.length; i++) {
			Annotation curr= annotations[i];
			ProblemLocation problemLocation= null;
			if (curr instanceof IAnnotation) {
				problemLocation= getProblemLocation((IAnnotation) curr, model);
				if (problemLocation != null) {
					problems.add(problemLocation);
				}
			}
		}
		MultiStatus resStatus= null;

		IProblemLocation[] problemLocations= (IProblemLocation[]) problems.toArray(new IProblemLocation[problems.size()]);
		if (addQuickFixes) {
			IStatus status= collectCorrections(context, problemLocations, proposals);
			if (!status.isOK()) {
				resStatus= new MultiStatus(EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, CorrectionMessages.EGLCorrectionProcessor_error_quickfix_message, null);
				resStatus.add(status);
			}
		}
		
		if (addQuickAssists) {
			IStatus status= collectAssists(context, problemLocations, proposals);
			if (!status.isOK()) {
				if (resStatus == null) {
					resStatus= new MultiStatus(EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, CorrectionMessages.EGLCorrectionProcessor_error_quickassist_message, null);
				}
				resStatus.add(status);
			}
		}
		
		if (resStatus != null) {
			return resStatus;
		}
		return Status.OK_STATUS;
	}
	
	public static IStatus collectCorrections(IInvocationContext context, IProblemLocation[] locations, Collection proposals) {
		ContributedProcessorDescriptor[] processors= getCorrectionProcessors();
		SafeCorrectionCollector collector= new SafeCorrectionCollector(context, proposals);
		for (int i= 0; i < processors.length; i++) {
			ContributedProcessorDescriptor curr= processors[i];
			IProblemLocation[] handled= getHandledProblems(locations, curr);
			if (handled != null) {
				collector.setProblemLocations(handled);
				collector.process(curr);
			}
		}
		return collector.getStatus();
	}
	
	public static IStatus collectAssists(IInvocationContext context, IProblemLocation[] locations, Collection proposals) {
		ContributedProcessorDescriptor[] processors= getAssistProcessors();
		SafeAssistCollector collector= new SafeAssistCollector(context, locations, proposals);
		collector.process(processors);

		return collector.getStatus();
	}
	
	private static IProblemLocation[] getHandledProblems(IProblemLocation[] locations, ContributedProcessorDescriptor processor) {
		// implementation tries to avoid creating a new array
		boolean allHandled= true;
		ArrayList res= null;
		for (int i= 0; i < locations.length; i++) {
			IProblemLocation curr= locations[i];
			if (processor.canHandleMarkerType(curr.getMarkerType())) {
				if (!allHandled) { // first handled problem
					if (res == null) {
						res= new ArrayList(locations.length - i);
					}
					res.add(curr);
				}
			} else if (allHandled) {
				if (i > 0) { // first non handled problem
					res= new ArrayList(locations.length - i);
					for (int k= 0; k < i; k++) {
						res.add(locations[k]);
					}
				}
				allHandled= false;
			}
		}
		if (allHandled) {
			return locations;
		}
		if (res == null) {
			return null;
		}
		return (IProblemLocation[]) res.toArray(new IProblemLocation[res.size()]);
	}
	
	private static ProblemLocation getProblemLocation(IAnnotation eglAnnotation, IAnnotationModel model) {
		int problemId= eglAnnotation.getProblemKind();
		if (problemId != -1) {
			Position pos= model.getPosition((Annotation) eglAnnotation);
			if (pos != null) {
				return new ProblemLocation(pos.getOffset(), pos.getLength(), eglAnnotation); // java problems all handled by the quick assist processors
			}
		}
		return null;
	}
	

	public static boolean hasCorrections(IEGLFile eglfile, int problemId, String markerType) {
		ContributedProcessorDescriptor[] processors= getCorrectionProcessors();
		SafeHasCorrections collector= new SafeHasCorrections(eglfile, problemId);
		for (int i= 0; i < processors.length; i++) {
			if (processors[i].canHandleMarkerType(markerType)) {
				collector.process(processors[i]);
				if (collector.hasCorrections()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isQuickFixableType(Annotation annotation) {
		return (annotation instanceof IAnnotation || annotation instanceof SimpleMarkerAnnotation) && !annotation.isMarkedDeleted();
	}
	
	public static boolean hasCorrections(Annotation annotation) {
		if (annotation instanceof IAnnotation) {
			IAnnotation eglAnnotation= (IAnnotation) annotation;
			int problemId= eglAnnotation.getProblemKind();
			if (problemId != -1) {
				return hasCorrections(null, problemId, eglAnnotation.getType());
			}
		}
		if (annotation instanceof SimpleMarkerAnnotation) {
			return hasCorrections(((SimpleMarkerAnnotation) annotation).getMarker());
		}
		return false;
	}
	
	public static boolean hasAssists(IInvocationContext context) {
		ContributedProcessorDescriptor[] processors= getAssistProcessors();
		SafeHasAssist collector= new SafeHasAssist(context);

		for (int i= 0; i < processors.length; i++) {
			collector.process(processors[i]);
			if (collector.hasAssists()) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasCorrections(IMarker marker) {
		if (marker == null || !marker.exists())
			return false;

		IMarkerHelpRegistry registry= IDE.getMarkerHelpRegistry();
		return registry != null && registry.hasResolutions(marker);
	}
	
	
	private static abstract class SafeCorrectionProcessorAccess implements ISafeRunnable {
		private MultiStatus fMulti= null;
		private ContributedProcessorDescriptor fDescriptor;

		public void process(ContributedProcessorDescriptor[] desc) {
			for (int i= 0; i < desc.length; i++) {
				fDescriptor= desc[i];
				SafeRunner.run(this);
			}
		}

		public void process(ContributedProcessorDescriptor desc) {
			fDescriptor= desc;
			SafeRunner.run(this);
		}

		public void run() throws Exception {
			safeRun(fDescriptor);
		}

		protected abstract void safeRun(ContributedProcessorDescriptor processor) throws Exception;

		public void handleException(Throwable exception) {
			if (fMulti == null) {
				fMulti= new MultiStatus(EDTUIPlugin.PLUGIN_ID, IStatus.OK, CorrectionMessages.EGLCorrectionProcessor_error_status, null);
			}
			fMulti.merge(new Status(IStatus.ERROR, EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, CorrectionMessages.EGLCorrectionProcessor_error_status, exception));
		}

		public IStatus getStatus() {
			if (fMulti == null) {
				return Status.OK_STATUS;
			}
			return fMulti;
		}

	}
	
	private static class SafeCorrectionCollector extends SafeCorrectionProcessorAccess {
		private final IInvocationContext fContext;
		private final Collection fProposals;
		private IProblemLocation[] fLocations;

		public SafeCorrectionCollector(IInvocationContext context, Collection proposals) {
			fContext= context;
			fProposals= proposals;
		}

		public void setProblemLocations(IProblemLocation[] locations) {
			fLocations= locations;
		}

		public void safeRun(ContributedProcessorDescriptor desc) throws Exception {
			IQuickFixProcessor curr= (IQuickFixProcessor) desc.getProcessor(/*fContext.getEGLFile(),*/ IQuickFixProcessor.class);
			if (curr != null) {
				IEGLCompletionProposal[] res= curr.getCorrections(fContext, fLocations);
				if (res != null) {
					for (int k= 0; k < res.length; k++) {
						fProposals.add(res[k]);
					}
				}
			}
		}
	}
	
	private static class SafeAssistCollector extends SafeCorrectionProcessorAccess {
		private final IInvocationContext fContext;
		private final IProblemLocation[] fLocations;
		private final Collection fProposals;

		public SafeAssistCollector(IInvocationContext context, IProblemLocation[] locations, Collection proposals) {
			fContext= context;
			fLocations= locations;
			fProposals= proposals;
		}

		public void safeRun(ContributedProcessorDescriptor desc) throws Exception {
			IQuickAssistProcessor curr= (IQuickAssistProcessor) desc.getProcessor(IQuickAssistProcessor.class);
			if (curr != null) {
				IEGLCompletionProposal[] res= curr.getAssists(fContext, fLocations);
				if (res != null) {
					for (int k= 0; k < res.length; k++) {
						fProposals.add(res[k]);
					}
				}
			}
		}
	}
	
	
	private static class SafeHasCorrections extends SafeCorrectionProcessorAccess {
		private final IEGLFile fCu;
		private final int fProblemId;
		private boolean fHasCorrections;

		public SafeHasCorrections(IEGLFile cu, int problemId) {
			fCu= cu;
			fProblemId= problemId;
			fHasCorrections= false;
		}

		public boolean hasCorrections() {
			return fHasCorrections;
		}

		public void safeRun(ContributedProcessorDescriptor desc) throws Exception {
			IQuickFixProcessor processor= (IQuickFixProcessor) desc.getProcessor(/*fCu,*/ IQuickFixProcessor.class);
			if (processor != null && processor.hasCorrections(/*fCu,*/ fProblemId)) {
				fHasCorrections= true;
			}
		}
	}
	
	private static class SafeHasAssist extends SafeCorrectionProcessorAccess {
		private final IInvocationContext fContext;
		private boolean fHasAssists;

		public SafeHasAssist(IInvocationContext context) {
			fContext= context;
			fHasAssists= false;
		}

		public boolean hasAssists() {
			return fHasAssists;
		}

		public void safeRun(ContributedProcessorDescriptor desc) throws Exception {
			IQuickAssistProcessor processor= (IQuickAssistProcessor) desc.getProcessor(IQuickAssistProcessor.class);
			if (processor != null && processor.hasAssists(fContext)) {
				fHasAssists= true;
			}
		}
	}

}
