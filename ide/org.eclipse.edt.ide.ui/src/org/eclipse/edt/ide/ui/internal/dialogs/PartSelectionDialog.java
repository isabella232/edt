/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.internal.search.AllPartsCache;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.Strings;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.util.PartInfoLabelProvider;
import org.eclipse.edt.ide.ui.internal.util.StringMatcher;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredList;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

/**
 * A dialog to select a type from a list of types.
 */
public class PartSelectionDialog extends TwoPaneElementSelector {

	private static class TypeFilterMatcher implements FilteredList.FilterMatcher {

		private static final char END_SYMBOL = '<';
		private static final char ANY_STRING = '*';

		private StringMatcher fMatcher;
		private StringMatcher fQualifierMatcher;

		/*
		 * @see FilteredList.FilterMatcher#setFilter(String, boolean)
		 */
		public void setFilter(String pattern, boolean ignoreCase, boolean igoreWildCards) {
			int qualifierIndex = pattern.lastIndexOf("."); //$NON-NLS-1$

			// type			
			if (qualifierIndex == -1) {
				fQualifierMatcher = null;
				fMatcher = new StringMatcher(adjustPattern(pattern), ignoreCase, igoreWildCards);

				// qualified type
			} else {
				fQualifierMatcher = new StringMatcher(pattern.substring(0, qualifierIndex), ignoreCase, igoreWildCards);
				fMatcher = new StringMatcher(adjustPattern(pattern.substring(qualifierIndex + 1)), ignoreCase, igoreWildCards);
			}
		}

		/*
		 * @see FilteredList.FilterMatcher#match(Object)
		 */
		public boolean match(Object element) {
			if (!(element instanceof PartInfo))
				return false;

			PartInfo type = (PartInfo) element;

			if (!fMatcher.match(type.getPartName()))
				return false;

			if (fQualifierMatcher == null)
				return true;

			return fQualifierMatcher.match(type.getPartContainerName());
		}

		private String adjustPattern(String pattern) {
			int length = pattern.length();
			if (length > 0) {
				switch (pattern.charAt(length - 1)) {
					case END_SYMBOL :
						pattern = pattern.substring(0, length - 1);
						break;
					case ANY_STRING :
						break;
					default :
						pattern = pattern + ANY_STRING;
				}
			}
			return pattern;
		}
	}

	/*
	 * A string comparator which is aware of obfuscated code
	 * (type names starting with lower case characters).
	 */
	private static class StringComparator implements Comparator {
		public int compare(Object left, Object right) {
			String leftString = (String) left;
			String rightString = (String) right;

			if (Strings.isLowerCase(leftString.charAt(0)) && !Strings.isLowerCase(rightString.charAt(0)))
				return +1;

			if (Strings.isLowerCase(rightString.charAt(0)) && !Strings.isLowerCase(leftString.charAt(0)))
				return -1;

			int result = leftString.compareToIgnoreCase(rightString);
			if (result == 0)
				result = leftString.compareTo(rightString);

			return result;
		}
	}

	private IRunnableContext fRunnableContext;
	private IEGLSearchScope fScope;
	private int fElementKinds;
	private String fSubType;

	/**
	 * Constructs a type selection dialog.
	 * @param parent  the parent shell.
	 * @param context the runnable context.
	 * @param elementKinds <code>IEGLSearchConstants.CLASS</code>, <code>IEGLSearchConstants.INTERFACE</code>
	 * or <code>IEGLSearchConstants.TYPE</code>
	 * @param scope   the java search scope.
	 */
	public PartSelectionDialog(Shell parent, IRunnableContext context, int elementKinds, String subType, IEGLSearchScope scope) {
		super(
			parent,
			new PartInfoLabelProvider(PartInfoLabelProvider.SHOW_TYPE_ONLY),
			new PartInfoLabelProvider(PartInfoLabelProvider.SHOW_TYPE_CONTAINER_ONLY + PartInfoLabelProvider.SHOW_ROOT_POSTFIX));

		Assert.isNotNull(context);
		Assert.isNotNull(scope);

		fRunnableContext = context;
		fScope = scope;
		fElementKinds = elementKinds;
		fSubType = subType;

		setUpperListLabel(UINlsStrings.OpenPartDialog_UpperLabel);
		setLowerListLabel(UINlsStrings.OpenPartDialog_LowerLabel);
	}

	/*
	 * @see AbstractElementListSelectionDialog#createFilteredList(Composite)
	 */
	protected FilteredList createFilteredList(Composite parent) {
		FilteredList list = super.createFilteredList(parent);

		fFilteredList.setFilterMatcher(new TypeFilterMatcher());
		fFilteredList.setComparator(new StringComparator());

		return list;
	}

	/*
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {
		ArrayList typeList = new ArrayList();

		if (addParts(typeList, fScope, fElementKinds, fSubType) == CANCEL) {
			return CANCEL;
		}

		if (typeList.isEmpty()) {
			String title = getNoPartsTitle();
			String message = getNoPartsMessage();
			MessageDialog.openInformation(getShell(), title, message);
			return CANCEL;
		}

		PartInfo[] typeRefs = (PartInfo[]) typeList.toArray(new PartInfo[typeList.size()]);
		setElements(typeRefs);
		
		return super.open();
	}
	
	protected String getNoPartsTitle() {
		return UINlsStrings.OpenPartDialog_NoParts_Title;
	}

	protected String getNoPartsMessage() {
		return UINlsStrings.OpenPartDialog_NoParts_Message;
	}

	protected int addParts(ArrayList partsList, final IEGLSearchScope scope, final int elementKinds, final String subType) {
		final ArrayList typeList = new ArrayList();

		if (AllPartsCache.isCacheUpToDate()) {
			// run without progress monitor
			try {
				AllPartsCache.getParts(scope, elementKinds, subType, null, typeList);
			} catch (EGLModelException e) {
				EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage);
				return CANCEL;
			}
		} else {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						AllPartsCache.getParts(scope, elementKinds, subType, monitor, typeList);
					} catch (EGLModelException e) {
						throw new InvocationTargetException(e);
					}
					if (monitor.isCanceled()) {
						throw new InterruptedException();
					}
				}
			};

			try {
				fRunnableContext.run(true, true, runnable);
			} catch (InvocationTargetException e) {
				EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage);
				return CANCEL;
			} catch (InterruptedException e) {
				// cancelled by user
				return CANCEL;
			}
		}

		partsList.addAll(typeList);

		return OK;
	}
	
	protected IPart getPartFromPartInfo(PartInfo ref)
	{
	    IPart type = null;
		try {
			type = ref.resolvePart(fScope);
			if (type == null) {
				// not a class file or compilation unit
				String title = UINlsStrings.OpenPartErrorTitle;
				//				String message= JavaUIMessages.getFormattedString("TypeSelectionDialog.dialogMessage", ref.getPath()); //$NON-NLS-1$
				String message = UINlsStrings.OpenPartErrorMessage;
				MessageDialog.openError(getShell(), title, message);
			} else {
				List result = new ArrayList(1);
				result.add(type);
			}

		} catch (EGLModelException e) {
			String title = UINlsStrings.OpenPartErrorTitle;
			String message = UINlsStrings.OpenPartDialog_ErrorMessage;
			ErrorDialog.openError(getShell(), title, message, e.getStatus());
			setResult(null);
		}
	    return type;
	}

	/*
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
		PartInfo ref = (PartInfo) getLowerSelectedElement();

		if (ref == null)
			return;

		IPart type = getPartFromPartInfo(ref);
		if(type == null)
		    setResult(null);
		else
		{
			List result = new ArrayList(1);
			result.add(type);
			setResult(result);		    
		}
	}

}
