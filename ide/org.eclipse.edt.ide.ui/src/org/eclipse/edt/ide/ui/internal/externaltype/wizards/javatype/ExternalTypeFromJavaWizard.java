/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.externaltype.NewExternalTypeWizard;
import org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaObjectsToEglSource;
import org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaTypeConstants;
import org.eclipse.edt.ide.ui.internal.externaltype.util.ReflectionUtil;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class ExternalTypeFromJavaWizard extends TemplateWizard 
      implements IWorkbenchWizard, IPageChangingListener,IMessageHandler {
	private ExternalTypeFromJavaWizardConfiguration config;
	
	protected List<String> messages = new ArrayList<String>();
	protected IStructuredSelection selection;
	protected ExternalTypeFromJavaPage javaTypeSelectionPage;
	protected ExternalTypeSummaryPage summaryPage;
	
	
	public ExternalTypeFromJavaWizard() {
		super();
		setNeedsProgressMonitor(true);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());
		
		config = new ExternalTypeFromJavaWizardConfiguration();
		javaTypeSelectionPage = new ExternalTypeFromJavaPage(config);
		summaryPage = new ExternalTypeSummaryPage(selection);
	}
	
	@Override
	public void addPages(){
		addPage(javaTypeSelectionPage);
		addPage(summaryPage);
	}
	
	@Override
	public void init(IWorkbench arg0, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer != null) {
			((WizardDialog) wizardContainer).addPageChangingListener(this);
		}
	}
	
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, createFinishOperation());
		} catch (InterruptedException ex) {
			return false;
		} catch (Exception ex) {
			// ex.printStackTrace();
		}

		return true;
	}
	
	protected IRunnableWithProgress createFinishOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				final String results = generateExternalTypes(monitor,true);
				((NewExternalTypeWizard) getParentWizard()).setContentObj(results);
			}
		};
	}
	
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		if (event.getTargetPage() == summaryPage) {
			IRunnableWithProgress op = createPreviewOperation();
			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException ex) {
				event.doit = false;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected IRunnableWithProgress createPreviewOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if(messages != null) {
					messages.clear();
				}
				final String results = generateExternalTypes(monitor,false);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						summaryPage.setContent(results);
						List<String> messages = getMessages();
					    if(messages!=null && messages.size() > 0) {
					    	summaryPage.setMessages(messages);
					    }
					}
				});

			}
		};
	}
	
	protected String generateExternalTypes(IProgressMonitor monitor,boolean isFinished) 
			throws InterruptedException {
		StringBuilder buffer = new StringBuilder(200);
		JavaObjectsToEglSource d = new JavaObjectsToEglSource();
		
		JavaType selectedJavaType = config.getToBeGenerated().get(config.getSelectedClazz());
		config.getToBeGenerated().clear();
		config.getToBeGenerated().put(config.getSelectedClazz(), selectedJavaType);
		
		ReflectionUtil.getInnerTypes(config.getSelectedClazz(), config.getToBeGenerated());
		Map<Class<?>, JavaType> tempMap = new HashMap<Class<?>,JavaType>(20);
		
		if(config.isAllSuperTypesGenerated) {
			Set<Class<?>> superTypes; 
			Iterator<Map.Entry<Class<?>,JavaType>> entries = config.getToBeGenerated().entrySet().iterator();
			while(entries.hasNext()) {
				Map.Entry<Class<?>,JavaType> entry = entries.next();
				if(JavaType.SelectedType == entry.getValue().getSource()) {
					superTypes = ReflectionUtil.getAllSuperTypes(entry.getKey());
					for(Class<?> superType : superTypes) {
						if(!tempMap.containsKey(superType)) {
							tempMap.put(superType, JavaType.DUMMY_REFERENCED_JAVATYPE);
						}
					}
				}
			}
			
			for(Map.Entry<Class<?>, JavaType> entry : tempMap.entrySet()) {
				if(!config.getToBeGenerated().containsKey(entry.getKey())) {
					config.getToBeGenerated().put(entry.getKey(), entry.getValue());
				}
			}
			tempMap.clear();
		}
		
		if(config.isAllReferencedTypesGenerated) {
			Iterator<Map.Entry<Class<?>,JavaType>> entries = config.getToBeGenerated().entrySet().iterator();
			
			while(entries.hasNext()) {
				Map.Entry<Class<?>,JavaType> entry = entries.next();
			
				if(JavaType.SelectedType == entry.getValue().getSource()) {
					selectedJavaType = entry.getValue();
					
					if(selectedJavaType != null) {
						Set<Class<?>> referencedTypes = new HashSet<Class<?>>(30);
						
						List<Field> fields = selectedJavaType.getFields();
						if(fields.size() > 0) {
							referencedTypes.addAll(ReflectionUtil.getFieldReferencedTypes(fields));
						}
						
						List<Constructor<?>> constructors = selectedJavaType.getConstructors();
						if(constructors.size() > 0) {
							referencedTypes.addAll(ReflectionUtil.getConReferencedTypes(constructors));
						}
						
						List<Method> methods = selectedJavaType.getMethods();
						if(methods.size() > 0) {
							referencedTypes.addAll(ReflectionUtil.getMethodReferencedTypes(methods));
						}
						
						for(Class<?> refType : referencedTypes) {
							if(!tempMap.containsKey(refType)) {
								tempMap.put(refType, JavaType.DUMMY_REFERENCED_JAVATYPE);
							}
						}
					}
					
				}
			}// while
			
			for(Map.Entry<Class<?>, JavaType> entry : tempMap.entrySet()) {
				if(!config.getToBeGenerated().containsKey(entry.getKey())) {
					config.getToBeGenerated().put(entry.getKey(), entry.getValue());
				}
			}
			tempMap = null;
		}
		
		monitor.beginTask("Generating external type parts", config.getToBeGenerated().size());
		buffer.append("import eglx.java.*;\n");
		
		for(Map.Entry<Class<?>,JavaType> entry : config.getToBeGenerated().entrySet()) {
			if (monitor.isCanceled()) {
				break;
			} else {
				EglSourceGenerator generator = new EglSourceGenerator(d);
				EglSourceContext context = generator.makeContext(d);
				context.put(JavaTypeConstants.TO_BE_GENERATED_TYPE, entry.getValue());
				context.put(JavaTypeConstants.CONTAINING_EGL_PACKAGE, 
						  ((NewExternalTypeWizard) getParentWizard()).getConfiguration().getFPackage());
				context.put(JavaTypeConstants.ALL_CLASS_META, config.getToBeGenerated().keySet());
				
				Class<?> clazz = entry.getKey();
				monitor.subTask(clazz.getName());
				
				d.generate(clazz, generator, null);
				buffer.append(context.getTabbedWriterContent());
				
				monitor.worked(1);
			}
		}
		
		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}
		
		return buffer.toString();
	}
	
	@Override
	public void addMessage(String message) {
		if (this.messages == null) {
			messages = new ArrayList<String>();
		}
		this.messages.add(message);
	}
	
	@Override
	public List<String> getMessages() {
		return this.messages;
	}
}
