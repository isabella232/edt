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
package org.eclipse.edt.compiler.internal.core.builder;

/**
 * @author svihovec
 *
 */
public class NullBuildNotifier implements IBuildNotifier {

    private static final NullBuildNotifier INSTANCE = new NullBuildNotifier();
    
    private NullBuildNotifier(){}
    
    public static NullBuildNotifier getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#isAborted()
     */
    public boolean isAborted() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#setAborted(boolean)
     */
    public void setAborted(boolean aborted) {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#compiled()
     */
    public void compiled() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#begin()
     */
    public void begin() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#checkCancel()
     */
    public void checkCancel() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#done()
     */
    public void done() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#setProgressPerEGLPart(float)
     */
    public void setProgressPerEGLPart(float progress) {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#subTask(java.lang.String)
     */
    public void subTask(String message) {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#updateProgress(float)
     */
    public void updateProgress(float percentComplete) {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#updateProgressDelta(float)
     */
    public void updateProgressDelta(float percentWorked) {
        
    }

	@Override
	public IBuildNotifier createSubNotifier(float percentFromParent) {
		return null;
	}
}
