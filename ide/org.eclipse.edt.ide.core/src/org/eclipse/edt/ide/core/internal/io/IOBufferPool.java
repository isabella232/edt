/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.io;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.utils.ReadWriteMonitor;
import org.eclipse.edt.compiler.internal.io.IOBuffer;

/**
 * @author svihovec
 *
 */
public class IOBufferPool {

	private static final int MAX_NUMBER_OF_BUFFERS = 10000;
	
	private ReadWriteMonitor poolMonitor = new ReadWriteMonitor();
	private HashMap bufferMap = new HashMap();
	private HashMap monitorMap = new HashMap();
	
	private IIOBufferReaderFactory readerFactory;
	private IIOBufferWriterFactory writerFactory;
	
	public IOBufferPool(IIOBufferReaderFactory readerFactory, IIOBufferWriterFactory writerFactory){
		this.readerFactory = readerFactory;
		this.writerFactory = writerFactory;
	}
	
	public Object get(IPath bufferPath, String key){
		Object value = null;
		poolMonitor.enterRead();
		try{
			IOBuffer buffer = getBuffer(bufferPath);
			ReadWriteMonitor bufferMonitor = (ReadWriteMonitor)monitorMap.get(bufferPath);
			bufferMonitor.enterRead();
			try{
				value = buffer.get(key);
			}finally{
				bufferMonitor.exitRead();
			}
		}finally{
			poolMonitor.exitRead();
		}
		requestCleanup();
		
		return value;
	}
	
	public void put(IPath bufferPath, String key, Object value){
		poolMonitor.enterRead();
		
		try{
			IOBuffer buffer = getBuffer(bufferPath);
			ReadWriteMonitor bufferMonitor = (ReadWriteMonitor)monitorMap.get(bufferPath);
			bufferMonitor.enterWrite();
			try{
				buffer.put(key, value);
			}finally{
				bufferMonitor.exitWrite();
			}
		}finally{
			poolMonitor.exitRead();
		}
		requestCleanup();
	}
	
	public void remove(IPath bufferPath, String key){
		poolMonitor.enterRead();
			
		try{
			IOBuffer buffer = getBuffer(bufferPath);
			ReadWriteMonitor bufferMonitor = (ReadWriteMonitor)monitorMap.get(bufferPath);
			bufferMonitor.enterWrite();
			
			try{
				buffer.remove(key);
			}finally{
				bufferMonitor.exitWrite();
			}
		}finally{
			poolMonitor.exitRead();
		}
		requestCleanup();
	}
	
	public void remove(IPath bufferPath){
		poolMonitor.enterRead();
		try{
			removeBuffer(bufferPath);
		}finally{
			poolMonitor.exitRead();
		}
	}
	
	public void flush(){
		cleanup();
	}
	
	private synchronized IOBuffer getBuffer(IPath bufferPath){
		IOBuffer buffer = (IOBuffer)bufferMap.get(bufferPath);
		
		if(buffer == null){
			buffer = new IOBuffer(readerFactory.getReader(bufferPath), writerFactory.getWriter(bufferPath));
			bufferMap.put(bufferPath, buffer);
			monitorMap.put(bufferPath, new ReadWriteMonitor());
		}
		
		return buffer;
	}
	
	private synchronized void removeBuffer(IPath bufferPath){
		IOBuffer buffer = (IOBuffer)bufferMap.get(bufferPath);
		
		if(buffer != null){
			bufferMap.remove(bufferPath);	
			monitorMap.remove(bufferPath);		
		}
	}
	
	private synchronized void requestCleanup() {
		if(bufferMap.size() > MAX_NUMBER_OF_BUFFERS){
			cleanup();
		}
	}	
	
	private void cleanup(){
		try{
			poolMonitor.enterWrite();
			
			for (Iterator iter = bufferMap.keySet().iterator(); iter.hasNext();) {
				Object key = iter.next();
				IOBuffer buffer = (IOBuffer)bufferMap.get(key);
				buffer.writeCache();
			}
		}finally{
			clearMembers();
			poolMonitor.exitWrite();
		}
	}
	
	public void clear(){
		poolMonitor.enterWrite();
		try{
			clearMembers();
		}finally{
			poolMonitor.exitWrite();
		}
	}
	
	private void clearMembers(){
		bufferMap.clear();
		monitorMap.clear();
	}
}
