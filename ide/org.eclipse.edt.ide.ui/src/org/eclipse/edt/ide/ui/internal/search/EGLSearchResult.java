/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class EGLSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter
{
	private final Match[] EMPTY_ARR= new Match[0];    
	private EGLSearchQuery fQuery;
	/**
	 * @param description
	 */
	public EGLSearchResult(EGLSearchQuery job) {
		fQuery= job;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#findContainedMatches(org.eclipse.core.resources.IFile)
     */
    public Match[] findContainedMatches(IFile file)
    {
        return getMatches(file);
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFile(java.lang.Object)
     */
    public IFile getFile(Object element)
    {
		if (element instanceof IFile)
			return (IFile)element;
		return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#isShownInEditor(org.eclipse.search.ui.text.Match, org.eclipse.ui.IEditorPart)
     */
    public boolean isShownInEditor(Match match, IEditorPart editor)
    {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			FileEditorInput fi= (FileEditorInput) ei;
			return match.getElement().equals(fi.getFile());
		}
		return false;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#findContainedMatches(org.eclipse.ui.IEditorPart)
     */
    public Match[] findContainedMatches(IEditorPart editor)
    {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			FileEditorInput fi= (FileEditorInput) ei;
			return getMatches(fi.getFile());
		}
		return EMPTY_ARR;
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchResult#getLabel()
     */
    public String getLabel()
    {
		if (getMatchCount() == 1)
			return fQuery.getSingularLabel();
		else return MessageFormat.format(fQuery.getPluralPattern(), new Object[] { new Integer(getMatchCount()) });
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchResult#getTooltip()
     */
    public String getTooltip()
    {
		return getLabel();
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchResult#getQuery()
     */
    public ISearchQuery getQuery()
    {
        return fQuery;
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getEditorMatchAdapter()
     */
    public IEditorMatchAdapter getEditorMatchAdapter()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
     */
    public IFileMatchAdapter getFileMatchAdapter()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.IEditorMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult, org.eclipse.ui.IEditorPart)
     */
    public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor)
    {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			return getMatches(((IFileEditorInput)ei).getFile());
		}
		return EMPTY_ARR;
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.IFileMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult, org.eclipse.core.resources.IFile)
     */
    public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file)
    {
		return getMatches(file);
	}
}
