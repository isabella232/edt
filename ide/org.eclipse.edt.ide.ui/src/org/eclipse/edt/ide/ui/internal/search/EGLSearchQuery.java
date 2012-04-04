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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.search.internal.ui.text.SearchResultUpdater;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

public class EGLSearchQuery implements ISearchQuery, IEGLSearchConstants
{
    private ISearchResult fResult;  
    
	private String fSearchString;
	private boolean fCaseSensitive;
	private int fSearchFor;	    
    private int fLimitTo;
    private IEGLSearchScope fScope;
    private String fScopeDescription;
    private boolean forceQualification;
    
    public EGLSearchQuery(String sPattern, boolean isCaseSensitive,
            			  int searchFor, int limitTo,
            			  IEGLSearchScope scope, String scopeDescription,boolean forceQualification)
    {
        fSearchString = sPattern;
        fCaseSensitive = isCaseSensitive;
        fSearchFor = searchFor;
        fLimitTo = limitTo;
        fScope = scope;
        fScopeDescription = scopeDescription;
        this.forceQualification = forceQualification;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus run(final IProgressMonitor monitor)
    {
        final EGLSearchResult textResult = (EGLSearchResult) getSearchResult();
        textResult.removeAll();
        
        IEGLSearchResultCollector collector = new IEGLSearchResultCollector(){
			public IProgressMonitor getProgressMonitor() {
				return monitor;
			}

			public void aboutToStart() {
				// do nothing
			}

			public void accept(IResource resource, int start, int end, IEGLElement enclosingElement, int accuracy) {
				if (start < 0)
					start= 0;
				if (end < start)
					end = start;
				textResult.addMatch(new Match(resource, start, end-start));
			}

			public void done() {
				// do nothing
			}

			public void accept(IEGLElement element, int start, int end,
					IResource resource, int accuracy) throws CoreException {
				if (start < 0)
					start= 0;
				if (end < start)
					end = start;
				textResult.addMatch(new Match(element, start, end-start));				
			}
		};

		try
		{
        SearchEngine engine = new SearchEngine();       
        engine.search(EDTUIPlugin.getWorkspace(), SearchEngine.createSearchPattern(fSearchString, fSearchFor, fLimitTo, fCaseSensitive), fScope,false,forceQualification, collector);
		}
		catch(EGLModelException ex)
		{
			System.out.println("EGLSearchQuery"+ex);
		}
		catch(NullPointerException e){
			System.out.println("EGLSearchQuery"+e);
		}
	
		String message= EGLSearchMessages.EGLSearchQueryStatusOkMessage;
		MessageFormat.format(message, new Object[] { new Integer(textResult.getMatchCount()) });
		return new Status(IStatus.OK, EDTUIPlugin.getPluginId(), 0, message, null);
		
		
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchQuery#getLabel()
     */
    public String getLabel()
    {
        if(fLimitTo == IEGLSearchConstants.REFERENCES)
            return EGLSearchMessages.EGLSearchQuerySearchfor_references;
        else if(fLimitTo == IEGLSearchConstants.DECLARATIONS)
            return EGLSearchMessages.EGLSearchQuerySearchfor_declarations;
        
        return EGLSearchMessages.EGLSearchQuerySearch_label;
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchQuery#canRerun()
     */
    public boolean canRerun()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
     */
    public boolean canRunInBackground()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
     */
    public ISearchResult getSearchResult()
    {
		if (fResult == null){
			fResult= new EGLSearchResult(this);
			new SearchResultUpdater((AbstractTextSearchResult)fResult);
		}
		return fResult;
    }
    
	String getSingularLabel() {
		String[] args= new String[] { fSearchString, fScopeDescription };		
		return EGLSearchMessages.bind(EGLSearchMessages.FileSearchQuery_singularLabel, args);
	}
	
	String getPluralPattern() {
		String[] args= new String[] { fSearchString, "{0}", fScopeDescription }; //$NON-NLS-1$
		return EGLSearchMessages.bind(EGLSearchMessages.FileSearchQuery_pluralPattern, args);
	}

}
