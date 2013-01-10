/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.viewsupport.ElementImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class EditorErrorTickUpdater implements IProblemChangedListener {

	private EGLEditor fEGLEditor;
	private ElementImageProvider fImageLabelProvider = new ElementImageProvider();

	public EditorErrorTickUpdater(EGLEditor editor) {
		Assert.isNotNull(editor);
		fEGLEditor = editor;
		EDTUIPlugin.getDefault().getProblemMarkerManager().addListener(this);
	}

	/* (non-Javadoc)
	 * @see IProblemChangedListener#problemsChanged(IResource[], boolean)
	 */
	public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
		if (isMarkerChange) {
			return;
		}

		// Get the input for the editor I belong to
		IEditorInput input = fEGLEditor.getEditorInput();

		if (input != null) { // might run async, tests needed

			// Check to make sure this editor is operating on a file editor input
			if (input instanceof IFileEditorInput) {
				for (int i = 0; i < changedResources.length; i++) {
					// if the changed resource matches my editor resource, update the tab icon
					if (changedResources[i].equals(((FileEditorInput) input).getFile())) {
						updateEditorImage(input);
					}
				}
			}
		}
	}

	public void updateEditorImage(IEditorInput input) {
		// Get the current image of the editor 
		Image titleImage = fEGLEditor.getTitleImage();
		if (titleImage == null) {
			return;
		}

		// Don't just check for the highest severity - also update the
		// error/warning hashMaps that update the outline view icons. 
		int fImageFlags = EditorUtility.populateNodeErrorWarningHashMaps(fEGLEditor);

		ImageDescriptor nodeIcon = PluginImages.DESC_OBJS_EGLFILE;
		Image newImage = fImageLabelProvider.getImageLabel(nodeIcon, fImageFlags);

		if (titleImage != newImage)
			postImageChange(newImage);

	}

	private void postImageChange(final Image newImage) {
		Shell shell = fEGLEditor.getEditorSite().getShell();
		if (shell != null && !shell.isDisposed()) {
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					fEGLEditor.updatedTitleImage(newImage);
				}
			});
		}
	}

	public void dispose() {
		EDTUIPlugin.getDefault().getProblemMarkerManager().removeListener(this);
	}

}
