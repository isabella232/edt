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
package org.eclipse.edt.ide.ui.internal.outline;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;

public class OutlineAdapterFactory {

	private HashMap adapterMap = new HashMap(500);
	private GenericOutlineAdapter genericOutlineAdapter = new GenericOutlineAdapter(null);
	private EGLEditor editor;
	
	public OutlineAdapterFactory(IEGLDocument document, EGLEditor editor) {
		super();
		this.editor = editor; 
		createAdapters(document);
	}

	private void createAdapters(IEGLDocument document) {
		//adapterMap.put(IEGLDocument.class, new DocumentOutlineAdapter(editor));
		adapterMap.put(File.class, new FileOutlineAdapter(document, editor));
		adapterMap.put(ImportGroup.class, new ImportGroupOutlineAdapter(editor));//
		adapterMap.put(Program.class, new ProgramOutlineAdapter(editor));//
		adapterMap.put(Interface.class, new InterfaceOutlineAdapter(editor));//
		adapterMap.put(Service.class, new ServiceOutlineAdapter(editor));//
		adapterMap.put(PackageDeclaration.class, new PackageDeclarationOutlineAdapter(editor));//
		adapterMap.put(ImportDeclaration.class, new ImportStatementOutlineAdapter(editor));//
		adapterMap.put(ClassDataDeclaration.class, new ClassFieldDeclarationOutlineAdapter(editor));
		adapterMap.put(Constructor.class, new ConstructorOutlineAdapter(editor));
		//adapterMap.put(EGLFunctionDeclaration.class, new FunctionOutlineAdapter(editor));//		
		adapterMap.put(NestedFunction.class, new FunctionOutlineAdapter(editor));
		//adapterMap.put(EGLAbstractFunctionDeclaration.class, new FunctionOutlineAdapter(editor));//
		adapterMap.put(DataItem.class, new DataItemOutlineAdapter(editor));//
		adapterMap.put(Record.class, new RecordOutlineAdapter(editor));//
		//adapterMap.put(EGLFillerStructureItem.class, new StructureContentOutlineAdapter(editor));//
		//adapterMap.put(EGLUntypedFillerStructureItem.class, new StructureContentOutlineAdapter(editor));//
		adapterMap.put(StructureItem.class, new StructureContentOutlineAdapter(editor));//
		//adapterMap.put(EGLUntypedStructureItem.class, new StructureContentOutlineAdapter(editor));//
		//adapterMap.put(EGLEmbeddedRecordStructureItem.class, new StructureContentOutlineAdapter(editor));//
		adapterMap.put(UseStatement.class, new UseStatementOutlineAdapter(editor));//
		adapterMap.put(UseStatement.class, new UseFormStatementOutlineAdapter(editor));//
		adapterMap.put(Library.class, new LibraryOutlineAdapter(editor)); //
		adapterMap.put(Handler.class, new HandlerOutlineAdapter(editor));//
		adapterMap.put(Enumeration.class, new EnumerationOutlineAdapter(editor));//
		adapterMap.put(Delegate.class, new DelegateOutlineAdapter(editor));//
		adapterMap.put(ExternalType.class, new ExternalTypeOutlineAdapter(editor));//
		adapterMap.put(EGLClass.class, new ClassOutlineAdapter(editor));//
	}
	
	public boolean isDisplayableElement(Object element) {
		return adapterMap.containsKey(element.getClass());
	} 

	public IOutlineAdapter adapt(Object element) {
		IOutlineAdapter adapter = (IOutlineAdapter) adapterMap.get(element.getClass());
		return adapter == null ? genericOutlineAdapter : adapter;
	}
	
	public boolean hasOutlineAdapter(Object element) {
		return adapterMap.get(element.getClass()) != null;
	}
	
	// this method cleans up all of the icons associated with the adapters 
	public void dispose() {
		IOutlineAdapter adapt = null;
		for (Iterator i = adapterMap.values().iterator(); i.hasNext();) {		
			adapt = (IOutlineAdapter)i.next();
			adapt.dispose();
		}
	}

}
