package org.eclipse.edt.gen.eck;

/**
 * This notifier update nothing, and user can use this class if doesn't want to 
 * print the progress.
 *
 */
public class NullEckGenerationNotifier implements IEckGenerationNotifier{

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#isAborted()
	 */
	public boolean isAborted() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#setAborted(boolean)
	 */
	public void setAborted(boolean aborted) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#begin()
	 */
	public void begin() {
	}

	/**
	 * 
	 */
	public void checkCancel() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#done()
	 */
	public void done() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#setTaskName(java.lang.String)
	 */
	public void setTaskName(String message) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#updateProgress(int)
	 */
	public void updateProgress(int percentComplete) {
	}

	/**
	 * @param percentWorked
	 */
	public void updateProgressDelta(float percentWorked) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eck.IEckGenerationNotifier#begin(int)
	 */
	public void begin(int totalWork) {
	}

}
