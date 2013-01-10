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
package org.eclipse.edt.ide.core.search;

import org.eclipse.edt.ide.core.internal.model.search.processing.IJob;
import org.eclipse.edt.ide.core.model.IIndexConstants;

/**
 * <p>
 * This interface defines the constants used by the search engine.
 * </p>
 * <p>
 * This interface declares constants only; it is not intended to be implemented.
 * </p>
 * @see com.ibm.etools.edt.core.ide.search.SearchEngine
 */
public interface IEGLSearchConstants {

	/**
	 * The nature of searched element or the nature
	 * of match in unknown.
	 */
	int UNKNOWN = -1;
	
	/* Nature of searched element */
	// These should be used in the SearchFor field of SearchEngine.search()
	int ALL_ELEMENTS = 0; // All top level parts and nested functions
	int PROGRAM_PART = 1;
	int RECORD_PART = 4;
	int LIBRARY_PART = 9;
	int ALL_FUNCTIONS = 10; // Top level and nested functions
	int HANDLER_PART = 12;
	int SERVICE_PART = 13;
	int INTERFACE_PART = 14;
	int DELEGATE_PART = 15;
	int EXTERNALTYPE_PART = 16;
	int ENUMERATION_PART = 17;
	int ANNOTATION_PART = 18;
	int STEREOTYPE_PART = 19;
	int CLASS_PART = 20;
	/**
	 * The searched element is a package.
	 */
	int PACKAGE= 11;

//	/**
//	 * The searched element is a data declaration.
//	 */
//	int DATA_DECLARATION= 3;
	
//	/**
//	 * The searched element is a use declaration.
//	 */
//	int USE_DECLARATION= 4;
	
	/* Part Types */ 
	// These part types should only be used with SearchEngine.SearchAllPartNames()
	int PART = IIndexConstants.PART_SUFFIX;
	int PROGRAM = IIndexConstants.PROGRAM_SUFFIX;
	int RECORD = IIndexConstants.RECORD_SUFFIX;
	int FUNCTION = IIndexConstants.FUNCTION_SUFFIX;
	int LIBRARY = IIndexConstants.LIBRARY_SUFFIX;
	int HANDLER = IIndexConstants.HANDLER_SUFFIX;
	int SERVICE = IIndexConstants.SERVICE_SUFFIX;
	int INTERFACE = IIndexConstants.INTERFACE_SUFFIX;
	int DELEGATE = IIndexConstants.DELEGATE_SUFFIX;
	int EXTERNALTYPE = IIndexConstants.EXTERNALTYPE_SUFFIX;
	int ENUMERATION = IIndexConstants.ENUMERATION_SUFFIX;
	int ANNOTATION = IIndexConstants.ANNOTATION_SUFFIX;
	int STEREOTYPE = IIndexConstants.STEREOTYPE_SUFFIX;
	int CLASS = IIndexConstants.CLASS_SUFFIX;
	
	/* Nature of match */
	
	/**
	 * The search result is a declaration.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 */
	int DECLARATIONS= 0;

	/**
	 * The search result is a reference.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 * References can contain implementers since they are more generic kind
	 * of matches.
	 */
	int REFERENCES= 1;

	/**
	 * The search result is a declaration, a reference, or an implementer 
	 * of an interface.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 */
	int ALL_OCCURRENCES= 2;

//	/**
//	 * When searching for field matches, it will exclusively find read accesses, as
//	 * opposed to write accesses. Note that some expressions are considered both
//	 * as field read/write accesses: for example, x++; x+= 1;
//	 * 
//	 * @since 2.0
//	 */
//	int READ_ACCESSES = 3;
	
//	/**
//	 * When searching for field matches, it will exclusively find write accesses, as
//	 * opposed to read accesses. Note that some expressions are considered both
//	 * as field read/write accesses: for example,  x++; x+= 1;
//	 * 
//	 * @since 2.0
//	 */
//	int WRITE_ACCESSES = 4;
	


	/* Case sensitivity */
	
	/**
	 * The search pattern matches the search result only
	 * if cases are the same.
	 */
	boolean CASE_SENSITIVE = true;
	/**
	 * The search pattern ignores cases in the search result.
	 */
	boolean CASE_INSENSITIVE = false;
	

	/**
	 * The search operation starts immediately, even if the underlying indexer
	 * has not finished indexing the workspace. Results will more likely
	 * not contain all the matches.
	 */
	int FORCE_IMMEDIATE_SEARCH = IJob.ForceImmediate;
	/**
	 * The search operation throws an <code>org.eclipse.core.runtime.OperationCanceledException</code>
	 * if the underlying indexer has not finished indexing the workspace.
	 */
	int CANCEL_IF_NOT_READY_TO_SEARCH = IJob.CancelIfNotReady;
	/**
	 * The search operation waits for the underlying indexer to finish indexing 
	 * the workspace before starting the search.
	 */
	int WAIT_UNTIL_READY_TO_SEARCH = IJob.WaitUntilReady;
	
	
}
