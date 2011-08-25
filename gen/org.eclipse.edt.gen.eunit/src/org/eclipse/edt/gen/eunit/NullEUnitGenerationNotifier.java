package org.eclipse.edt.gen.eunit;

/**
 * This notifier update nothing, and user can use this class if doesn't want to 
 * print the progress.
 *
 */
public class NullEUnitGenerationNotifier implements IEUnitGenerationNotifier{

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#isAborted()
	 */
	public boolean isAborted() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#setAborted(boolean)
	 */
	public void setAborted(boolean aborted) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#begin()
	 */
	public void begin() {
	}

	/**
	 * 
	 */
	public void checkCancel() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#done()
	 */
	public void done() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#setTaskName(java.lang.String)
	 */
	public void setTaskName(String message) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#updateProgress(int)
	 */
	public void updateProgress(int percentComplete) {
	}

	/**
	 * @param percentWorked
	 */
	public void updateProgressDelta(float percentWorked) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#begin(int)
	 */
	public void begin(int totalWork) {
	}

}
