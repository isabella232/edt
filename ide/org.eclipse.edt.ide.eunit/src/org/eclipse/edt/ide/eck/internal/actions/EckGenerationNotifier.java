package org.eclipse.edt.ide.eck.internal.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.eck.IEckGenerationNotifier;
/**
 * The adapter for updating the test driver progress with Eclipse progress view. 
 *
 */
public class EckGenerationNotifier implements IEckGenerationNotifier {

	private IProgressMonitor monitor;
	protected int workDone;
	protected int totalWork;
	protected String previousSubtask;
	protected float percentComplete;
	
	/**
	 * @param monitor
	 */
	public EckGenerationNotifier(IProgressMonitor monitor) {
		this(monitor, 1000000);
	}
	
	/**
	 * @param monitor
	 * @param totalWork
	 */
	public EckGenerationNotifier(IProgressMonitor monitor, int totalWork) {
		this.monitor = monitor;
		this.workDone = 0;
		this.totalWork = totalWork;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#isAborted()
	 */
	public boolean isAborted() {
		if(this.monitor != null) {
			return monitor.isCanceled();
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#setAborted(boolean)
	 */
	public void setAborted(boolean aborted) {
		if(this.monitor != null)
			monitor.setCanceled(aborted);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#begin()
	 */
	public void begin() {
		if (monitor != null)
			monitor.beginTask("", totalWork); //$NON-NLS-1$
		this.previousSubtask = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#begin(int)
	 */
	public void begin(int totalWork) {
		this.totalWork = totalWork;
		begin();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#done()
	 */
	public void done() {
		if (monitor != null)
			monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#setTaskName(java.lang.String)
	 */
	public void setTaskName(String message) {
		this.monitor.setTaskName(message);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#updateProgress(int)
	 */
	public void updateProgress(int workCount) {
		monitor.worked(workCount);
	}
}
