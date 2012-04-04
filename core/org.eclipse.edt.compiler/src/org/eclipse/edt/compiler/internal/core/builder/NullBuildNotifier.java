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
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#setAborted(boolean)
     */
    public void setAborted(boolean aborted) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#compiled()
     */
    public void compiled() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#begin()
     */
    public void begin() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#checkCancel()
     */
    public void checkCancel() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#done()
     */
    public void done() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#setProgressPerEGLPart(float)
     */
    public void setProgressPerEGLPart(float progress) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#subTask(java.lang.String)
     */
    public void subTask(String message) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#updateProgress(float)
     */
    public void updateProgress(float percentComplete) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier#updateProgressDelta(float)
     */
    public void updateProgressDelta(float percentWorked) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public IBuildNotifier createSubNotifier(float percentFromParent) {
		return null;
	}
}
