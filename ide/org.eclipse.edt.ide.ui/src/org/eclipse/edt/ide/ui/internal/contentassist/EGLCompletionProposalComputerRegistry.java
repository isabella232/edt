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
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.preferences.Messages;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

public final class EGLCompletionProposalComputerRegistry {
	
	private static final String EXTENSION_POINT = "eglCompletionProposalComputer"; //$NON-NLS-1$
	private static final String NUM_COMPUTERS_PREF_KEY= "content_assist_number_of_computers"; //$NON-NLS-1$
	
	/** The singleton instance. */
	private static EGLCompletionProposalComputerRegistry eglCplProposalRegistrySingleton= null;
	
	public static synchronized EGLCompletionProposalComputerRegistry getDefault() {
		if (null == eglCplProposalRegistrySingleton) {
			eglCplProposalRegistrySingleton= new EGLCompletionProposalComputerRegistry();
		}

		return eglCplProposalRegistrySingleton;
	}
	
	private final Map fDescriptorsByPartition= new HashMap();
	private final Map fPublicDescriptorsByPartition= new HashMap();
	private final List fDescriptors= new ArrayList();
	private final List fPublicDescriptors= Collections.unmodifiableList(fDescriptors);
	
	private final List fCategories= new ArrayList();
	private final List fPublicCategories= Collections.unmodifiableList(fCategories);
	
	/**
	 * <code>true</code> if this registry has been loaded.
	 */
	private boolean fLoaded= false;
	private boolean fHasUninstalledComputers= false;

	/**
	 * Creates a new instance.
	 */
	public EGLCompletionProposalComputerRegistry() {
		
	}
	
	List getProposalComputerDescriptors() {
		ensureExtensionPointRead();
		return fPublicDescriptors;
	}
	
	List getProposalComputerDescriptors(String partition) {
		ensureExtensionPointRead();
		List result= (List) fPublicDescriptorsByPartition.get(partition);
		return result != null ? result : Collections.EMPTY_LIST;
	}
	
	public List getProposalCategories() {
		ensureExtensionPointRead();
		return fPublicCategories;
	}
	
	/**
	 * Ensures that the extensions are read and stored in
	 * <code>fDescriptorsByPartition</code>.
	 */
	private void ensureExtensionPointRead() {
		boolean reload;
		synchronized (this) {
			reload= !fLoaded;
			fLoaded= true;
		}
		if (reload) {
			reload();
			updateUninstalledComputerCount();
		}
	}
	
	private void updateUninstalledComputerCount() {
		IPreferenceStore preferenceStore= EDTUIPreferenceConstants.getPreferenceStore();
		int lastNumberOfComputers= preferenceStore.getInt(NUM_COMPUTERS_PREF_KEY);
		int currNumber= fDescriptors.size();
		fHasUninstalledComputers= lastNumberOfComputers > currNumber;
		preferenceStore.putValue(NUM_COMPUTERS_PREF_KEY, Integer.toString(currNumber));
		EDTUIPlugin.getDefault().savePluginPreferences();
	}
	
	public void reload() {
		IExtensionRegistry registry= Platform.getExtensionRegistry();
		List elements= new ArrayList(Arrays.asList(registry.getConfigurationElementsFor(EDTUIPlugin.getPluginId(), EXTENSION_POINT)));

		Map map= new HashMap();
		List all= new ArrayList();

		List categories= getCategories(elements);
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			IConfigurationElement element= (IConfigurationElement) iter.next();
			try {
				EGLCompletionProposalComputerDescriptor desc= new EGLCompletionProposalComputerDescriptor(element, this, categories);
				Set partitions= desc.getPartitions();
				for (Iterator it= partitions.iterator(); it.hasNext();) {
					String partition= (String) it.next();
					List list= (List) map.get(partition);
					if (list == null) {
						list= new ArrayList();
						map.put(partition, list);
					}
					list.add(desc);
				}
				all.add(desc);

			} catch (InvalidRegistryObjectException x) {
			} catch (CoreException x) {
			}
		}

		synchronized (this) {
			fCategories.clear();
			fCategories.addAll(categories);

			Set partitions= map.keySet();
			fDescriptorsByPartition.keySet().retainAll(partitions);
			fPublicDescriptorsByPartition.keySet().retainAll(partitions);
			for (Iterator it= partitions.iterator(); it.hasNext();) {
				String partition= (String) it.next();
				List old= (List) fDescriptorsByPartition.get(partition);
				List current= (List) map.get(partition);
				if (old != null) {
					old.clear();
					old.addAll(current);
				} else {
					fDescriptorsByPartition.put(partition, current);
					fPublicDescriptorsByPartition.put(partition, Collections.unmodifiableList(current));
				}
			}

			
			fDescriptors.clear();
			fDescriptors.addAll(all);
		}
	}
	
	boolean hasUninstalledComputers(String partition, List included) {
		return(fHasUninstalledComputers);
	}
	
	private List getCategories(List elements) {
		IPreferenceStore store= EDTUIPlugin.getDefault().getPreferenceStore();
		String preference= store.getString(EDTUIPreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		Set disabled= new HashSet();
		StringTokenizer tok= new StringTokenizer(preference, "\0");  //$NON-NLS-1$
		while (tok.hasMoreTokens())
			disabled.add(tok.nextToken());
		Map ordered= new HashMap();
		preference= store.getString(EDTUIPreferenceConstants.CODEASSIST_CATEGORY_ORDER);
		tok= new StringTokenizer(preference, "\0"); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			StringTokenizer inner= new StringTokenizer(tok.nextToken(), ":"); //$NON-NLS-1$
			String id= inner.nextToken();
			int rank= Integer.parseInt(inner.nextToken());
			ordered.put(id, new Integer(rank));
		}

		EGLCompletionProposalCategory allProposals= null;
		
		List categories= new ArrayList();
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			IConfigurationElement element= (IConfigurationElement) iter.next();
			try {
				if (element.getName().equals("proposalCategory")) { //$NON-NLS-1$
					iter.remove(); // remove from list to leave only computers

					EGLCompletionProposalCategory category= new EGLCompletionProposalCategory(element, this);
					categories.add(category);
					category.setIncluded(!disabled.contains(category.getId()));
					Integer rank= (Integer) ordered.get(category.getId());
					if (rank != null) {
						int r= rank.intValue();
						boolean separate= r < 0xffff;
						if (!separate)
							r= r - 0xffff;
						category.setSeparateCommand(separate);
						category.setSortOrder(r);
					}
				}
			} catch (InvalidRegistryObjectException x) {
				Object[] args= {element.toString()};
				String message= Messages.format(EGLTextMessages.CompletionProposalComputerRegistry_invalid_message, args);
				IStatus status= new Status(IStatus.WARNING, EDTUIPlugin.getPluginId(), IStatus.OK, message, x);
				informUser(status);
			} catch (CoreException x) {
				informUser(x.getStatus());
			}
		}
		
		preventDuplicateCategories(store, disabled, allProposals);
		return categories;
	}
	
	private void preventDuplicateCategories(IPreferenceStore store, Set disabled, EGLCompletionProposalCategory allProposals) {
		if (allProposals == null || !allProposals.isIncluded())
			return;
		StringBuffer buf= new StringBuffer(50 * disabled.size());
		Iterator iter= disabled.iterator();
		while (iter.hasNext()) {
			buf.append(iter.next());
			buf.append('\0');
		}
			store.putValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, buf.toString());
	}
	
	void resetUnistalledComputers() {
		fHasUninstalledComputers= false;
	}
	
	private void informUser(IStatus status) {
		EDTUIPlugin.log(status);
		String title= EGLTextMessages.CompletionProposalComputerRegistry_error_dialog_title;
		String message= status.getMessage();
		MessageDialog.openError(EDTUIPlugin.getActiveWorkbenchShell(), title, message);
	}
	
	void informUser(EGLCompletionProposalComputerDescriptor descriptor, IStatus status) {
		EDTUIPlugin.log(status);
        String title= EGLTextMessages.CompletionProposalComputerRegistry_error_dialog_title;
        EGLCompletionProposalCategory category= descriptor.getCategory();
        IContributor culprit= descriptor.getContributor();
        Set affectedPlugins= getAffectedContributors(category, culprit);

		final String avoidHint;
		final String culpritName= culprit == null ? null : culprit.getName();
		if (affectedPlugins.isEmpty())
			avoidHint= Messages.format(EGLTextMessages.CompletionProposalComputerRegistry_messageAvoidanceHint, new Object[] {culpritName, category.getDisplayName()});
		else
			avoidHint= Messages.format(EGLTextMessages.CompletionProposalComputerRegistry_messageAvoidanceHintWithWarning, new Object[] {culpritName, category.getDisplayName(), toString(affectedPlugins)});

		String message= status.getMessage();
        // inlined from MessageDialog.openError
        MessageDialog dialog = new MessageDialog(EDTUIPlugin.getActiveWorkbenchShell(), title, null /* default image */, message, MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0) {
        	protected Control createCustomArea(Composite parent) {
        		Link link= new Link(parent, SWT.NONE);
        		link.setText(avoidHint);
        		link.addSelectionListener(new SelectionAdapter() {
        			public void widgetSelected(SelectionEvent e) {
        				PreferencesUtil.createPreferenceDialogOn(getShell(), "org.eclipse.edt.ide.ui.ContentAssistAdvancedPreferences", null, null).open(); //$NON-NLS-1$
        			}
        		});
        		GridData gridData= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        		gridData.widthHint= this.getMinimumMessageWidth();
				link.setLayoutData(gridData);
        		return link;
        	}
        };
        dialog.open();
	}
	
	private Set getAffectedContributors(EGLCompletionProposalCategory category, IContributor culprit) {
	    Set affectedPlugins= new HashSet();
        for (Iterator it= getProposalComputerDescriptors().iterator(); it.hasNext();) {
	        EGLCompletionProposalComputerDescriptor desc= (EGLCompletionProposalComputerDescriptor) it.next();
	        EGLCompletionProposalCategory cat= desc.getCategory();
	        if (cat.equals(category)) {
	        	IContributor contributor= desc.getContributor();
	        	if (contributor != null && !culprit.equals(contributor))
	        		affectedPlugins.add(contributor.getName());
	        }
        }
	    return affectedPlugins;
    }
	
    private Object toString(Collection collection) {
    	// strip brackets off AbstractCollection.toString()
    	String string= collection.toString();
    	return string.substring(1, string.length() - 1);
    }
}
