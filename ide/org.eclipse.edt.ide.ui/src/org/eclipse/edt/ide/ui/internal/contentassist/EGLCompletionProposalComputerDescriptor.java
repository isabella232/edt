/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.editor.EGLContentAssistInvocationContext;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposalComputer;
import org.eclipse.edt.ide.ui.internal.editor.IPartitions;
import org.eclipse.edt.ide.ui.internal.preferences.Messages;
import org.eclipse.jface.text.IDocument;
import org.osgi.framework.Bundle;

final class EGLCompletionProposalComputerDescriptor {
	/** The default category id. */
	private static final String DEFAULT_CATEGORY_ID= "org.eclipse.edt.ide.ui.defaultProposalCategory"; //$NON-NLS-1$
	/** The extension schema name of the category id attribute. */
	private static final String CATEGORY_ID= "categoryId"; //$NON-NLS-1$
	/** The extension schema name of the partition type attribute. */
	private static final String TYPE= "type"; //$NON-NLS-1$
	/** The extension schema name of the class attribute. */
	private static final String CLASS= "class"; //$NON-NLS-1$
	/** The extension schema name of the activate attribute. */
	private static final String ACTIVATE= "activate"; //$NON-NLS-1$
	/** The extension schema name of the partition child elements. */
	private static final String PARTITION= "partition"; //$NON-NLS-1$
	/** Set of Java partition types. */
	private static final Set PARTITION_SET;

	static {
		Set partitions= new HashSet();
		partitions.add(IDocument.DEFAULT_CONTENT_TYPE);
		partitions.add(IPartitions.EGL_PARTITIONING);
		partitions.add(IPartitions.EGL_SINGLE_LINE_COMMENT);
		partitions.add(IPartitions.EGL_MULTI_LINE_COMMENT);
		partitions.add(IPartitions.SQL_CONTENT_TYPE);
		partitions.add(IPartitions.SQL_CONDITION_CONTENT_TYPE);

		PARTITION_SET= Collections.unmodifiableSet(partitions);
	}

	/** The identifier of the extension. */
	private final String fId;
	/** The name of the extension. */
	private final String fName;
	/** The class name of the provided <code>IEGLCompletionProposalComputer</code>. */
	private final String fClass;
	/** The activate attribute value. */
	private final boolean fActivate;
	/** The partition of the extension (element type: {@link String}). */
	private final Set fPartitions;
	/** The configuration element of this extension. */
	private final IConfigurationElement fElement;
	/** The registry we are registered with. */
	private final EGLCompletionProposalComputerRegistry fRegistry;
	/** The computer, if instantiated, <code>null</code> otherwise. */
	private IEGLCompletionProposalComputer fComputer;
	/** The ui category. */
	private final EGLCompletionProposalCategory fCategory;
	/** The first error message in the most recent operation, or <code>null</code>. */
	private String fLastError;
	/**
	 * Tells whether to inform the user when <code>MAX_DELAY</code> has been exceeded.
	 * We start timing execution after the first session because the first may take
	 * longer due to plug-in activation and initialization.
	 */
	private boolean fIsReportingDelay= false;
	/** The start of the last operation. */
	private long fStart;
	/**
	 * Tells whether we tried to load the computer.
	 * @since 3.4
	 */
	boolean fTriedLoadingComputer= false;


	/**
	 * Creates a new descriptor.
	 *
	 * @param element the configuration element to read
	 * @param registry the computer registry creating this descriptor
	 * @param categories the categories
	 * @throws InvalidRegistryObjectException if this extension is no longer valid
	 * @throws CoreException if the extension contains invalid values
	 */
	EGLCompletionProposalComputerDescriptor(IConfigurationElement element, EGLCompletionProposalComputerRegistry registry, List categories) throws InvalidRegistryObjectException, CoreException {
		Assert.isLegal(registry != null);
		Assert.isLegal(element != null);

		fRegistry= registry;
		fElement= element;
		IExtension extension= element.getDeclaringExtension();
		fId= extension.getUniqueIdentifier();
		checkNotNull(fId, "id"); //$NON-NLS-1$

		String name= extension.getLabel();
		if (name.length() == 0)
			fName= fId;
		else
			fName= name;

		Set partitions= new HashSet();
		IConfigurationElement[] children= element.getChildren(PARTITION);
		if (children.length == 0) {
			fPartitions= PARTITION_SET; // add to all partition types if no partition is configured
		} else {
			for (int i= 0; i < children.length; i++) {
				String type= children[i].getAttribute(TYPE);
				checkNotNull(type, TYPE);
				partitions.add(type);
			}
			fPartitions= Collections.unmodifiableSet(partitions);
		}

		String activateAttribute= element.getAttribute(ACTIVATE);
		fActivate= Boolean.valueOf(activateAttribute).booleanValue();

		fClass= element.getAttribute(CLASS);
		checkNotNull(fClass, CLASS);

		String categoryId= element.getAttribute(CATEGORY_ID);
		if (categoryId == null)
			categoryId= DEFAULT_CATEGORY_ID;
		EGLCompletionProposalCategory category= null;
		for (Iterator it= categories.iterator(); it.hasNext();) {
			EGLCompletionProposalCategory cat= (EGLCompletionProposalCategory) it.next();
			if (cat.getId().equals(categoryId)) {
				category= cat;
				break;
			}
		}
		if (category == null) {
			// create a category if it does not exist
			fCategory= new EGLCompletionProposalCategory(categoryId, fName, registry);
			categories.add(fCategory);
		} else {
			fCategory= category;
		}
	}

	private void checkNotNull(Object value, String attribute) throws InvalidRegistryObjectException, CoreException {
		if(null == value){
			Object[] args= { getId(), fElement.getContributor().getName(), attribute };
			String message= Messages.format(EGLTextMessages.CompletionProposalComputerDescriptor_illegal_attribute_message, args);
			IStatus status= new Status(IStatus.WARNING, EDTUIPlugin.getPluginId(), IStatus.OK, message, null);
			throw new CoreException(status);
		}
	}

	/**
	 * Returns the identifier of the described extension.
	 *
	 * @return Returns the id
	 */
	public String getId() {
		return fId;
	}

	/**
	 * Returns the name of the described extension.
	 *
	 * @return Returns the name
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Returns the partition types of the described extension.
	 *
	 * @return the set of partition types (element type: {@link String})
	 */
	public Set getPartitions() {
		return fPartitions;
	}

	private synchronized IEGLCompletionProposalComputer getComputer(boolean canCreate) throws CoreException, InvalidRegistryObjectException {
		if (fComputer == null && canCreate && !fTriedLoadingComputer && (fActivate || isPluginLoaded())) {
			fTriedLoadingComputer= true;
			fComputer= createComputer();
		}
		return fComputer;
	}

	private boolean isPluginLoaded() {
		Bundle bundle= getBundle();
		return bundle != null && bundle.getState() == Bundle.ACTIVE;
	}

	private Bundle getBundle() {
		String namespace= fElement.getDeclaringExtension().getContributor().getName();
		Bundle bundle= Platform.getBundle(namespace);
		return bundle;
	}

	public IEGLCompletionProposalComputer createComputer() throws CoreException, InvalidRegistryObjectException {
		return (IEGLCompletionProposalComputer) fElement.createExecutableExtension(CLASS);
	}

	public List computeCompletionProposals(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (!isEnabled())
			return Collections.EMPTY_LIST;
		IStatus status;
		try {
			IEGLCompletionProposalComputer computer= getComputer(true);
			if (computer == null) // not active yet
				return Collections.EMPTY_LIST;

			try {
				List proposals= computer.computeCompletionProposals(context, monitor);

				if (proposals != null) {
					fLastError= computer.getErrorMessage();
					return proposals;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				fIsReportingDelay= true;
			}
		} catch (InvalidRegistryObjectException x) {
			status= createExceptionStatus(x);
		} catch (CoreException x) {
			status= createExceptionStatus(x);
		} catch (RuntimeException x) {
			status= createExceptionStatus(x);
		} finally {
			monitor.done();
		}

		return Collections.EMPTY_LIST;
	}

	public List computeContextInformation(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (!isEnabled())
			return Collections.EMPTY_LIST;

		try {
			IEGLCompletionProposalComputer computer= getComputer(true);
			if (computer == null) // not active yet
				return Collections.EMPTY_LIST;

			List proposals= computer.computeContextInformation(context, monitor);

			if (proposals != null) {
				fLastError= computer.getErrorMessage();
				return proposals;
			}

		} catch (InvalidRegistryObjectException x) {
		} catch (CoreException x) {
		} catch (RuntimeException x) {
		} finally {
			monitor.done();
		}

		return Collections.EMPTY_LIST;
	}


	public void sessionStarted() {
		if (!isEnabled())
			return;

		IStatus status;
		try {
			IEGLCompletionProposalComputer computer= getComputer(true);
			if (computer == null) // not active yet
				return;

			computer.sessionStarted();

			return;
		} catch (InvalidRegistryObjectException x) {
			status= createExceptionStatus(x);
		} catch (CoreException x) {
			status= createExceptionStatus(x);
		} catch (RuntimeException x) {
			status= createExceptionStatus(x);
		}
	}

	public void sessionEnded() {
		if (!isEnabled())
			return;

		IStatus status;
		try {
			IEGLCompletionProposalComputer computer= getComputer(false);
			if (computer == null) // not active yet
				return;

			computer.sessionEnded();

			return;
		} catch (InvalidRegistryObjectException x) {
			status= createExceptionStatus(x);
		} catch (CoreException x) {
			status= createExceptionStatus(x);
		} catch (RuntimeException x) {
			status= createExceptionStatus(x);
		}
		fRegistry.informUser(this, status);
	}

	private boolean isEnabled() {
		return fCategory.isEnabled();
	}

	EGLCompletionProposalCategory getCategory() {
		return fCategory;
	}

	/**
	 * Returns the error message from the described extension.
	 *
	 * @return the error message from the described extension
	 */
	public String getErrorMessage() {
		return fLastError;
	}

	/**
	 * Returns the contributor of the described extension.
	 *
	 * @return the contributor of the described extension
	 */
    IContributor getContributor()  {
        try {
	        return fElement.getContributor();
        } catch (InvalidRegistryObjectException e) {
        	return null;
        }
    }
    
	private IStatus createExceptionStatus(InvalidRegistryObjectException x) {
		// extension has become invalid - log & disable
		String blame= createBlameMessage();
		String reason= EGLTextMessages.CompletionProposalComputerDescriptor_reason_invalid;
		return new Status(IStatus.INFO, EDTUIPlugin.getPluginId(), IStatus.OK, blame + " " + reason, x); //$NON-NLS-1$
	}
	
	private String createBlameMessage() {
		Object[] args= { getName(), fElement.getDeclaringExtension().getContributor().getName() };
		String disable= Messages.format( EGLTextMessages.CompletionProposalComputerDescriptor_blame_message, args);
		return disable;
	}
	
	private IStatus createExceptionStatus(CoreException x) {
		// unable to instantiate the extension - log
		String blame= createBlameMessage();
		String reason= EGLTextMessages.CompletionProposalComputerDescriptor_reason_instantiation;
		return new Status(IStatus.ERROR, EDTUIPlugin.getPluginId(), IStatus.OK, blame + " " + reason, x); //$NON-NLS-1$
	}

	private IStatus createExceptionStatus(RuntimeException x) {
		// misbehaving extension - log
		String blame= createBlameMessage();
		String reason= EGLTextMessages.CompletionProposalComputerDescriptor_reason_runtime_ex;
		return new Status(IStatus.WARNING, EDTUIPlugin.getPluginId(), IStatus.OK, blame + " " + reason, x); //$NON-NLS-1$
	}

}
