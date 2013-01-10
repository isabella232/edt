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
package org.eclipse.edt.ide.core.internal.model;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author winghong
 */
public class SoftLRUCache {
    
    private static final boolean DEBUG = false;
    
    private static class OrderedSoftReference extends SoftReference {
        OrderedSoftReference previous;
        OrderedSoftReference next;
        
        Object key;

        // Used for sentinel values
        OrderedSoftReference() {
            super(null);
        }
        
        OrderedSoftReference(Object key, Object referent, ReferenceQueue q, OrderedSoftReference previous, OrderedSoftReference next) {
            super(referent, q);
            this.key = key;
            
            this.previous = previous;
            this.next = next;
            this.previous.next = this;
            this.next.previous = this;
        }
        
        OrderedSoftReference remove() {
            if(DEBUG) System.out.println("Remove: " + key); //$NON-NLS-1$

            previous.next = next;
            next.previous = previous;
            return this;
        }
    }

    private OrderedSoftReference first;
    private OrderedSoftReference last;
    
    private Map softReferences;
    
    private ReferenceQueue referenceQueue;
    
    private int maxSize;
    
    public SoftLRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.softReferences = new HashMap();
        this.referenceQueue = new ReferenceQueue();
        this.first = new OrderedSoftReference();
        this.last = new OrderedSoftReference();
        this.first.next = last;
        this.last.previous = first;
    }
    
    public void put(Object key, Object value) {
        if(DEBUG) System.out.println("Put: " + key); //$NON-NLS-1$
        
        removeClearedReferences();
        softReferences.put(key, new OrderedSoftReference(key, value, referenceQueue, first, first.next));
        if(softReferences.size() > maxSize) {
            if(DEBUG) System.out.println("Removing oldest key"); //$NON-NLS-1$
            softReferences.remove(last.previous.remove().key);
        }
    }
    
    public Object get(Object key) {
        removeClearedReferences();
        
        OrderedSoftReference ref = (OrderedSoftReference) softReferences.get(key);
        
        if(ref == null) {
            return null;
        }
        
        Object result = ref.get();
        if(result != null) {
            ref.remove();
            ref.next = first.next;
            ref.previous = first;
            first.next.previous = ref;
            first.next = ref;
        }

        if(DEBUG && result == null) System.out.println("Key " + key + " has been cleared"); //$NON-NLS-1$ //$NON-NLS-2$
        
        return result;
    }
    
    private void removeClearedReferences() {
        OrderedSoftReference ref;
        while((ref = (OrderedSoftReference) referenceQueue.poll()) != null) {
            if(DEBUG) System.out.println("Removing cleared soft reference"); //$NON-NLS-1$
            softReferences.remove(ref.remove().key);
        }
    }
    
    public static void main(String[] args) {
        StrongLRUCache cache = new StrongLRUCache(5);
        
        for(int i = 0; i < 100; i++) {
            Integer key = new Integer((int) Math.floor(Math.random() * 100));

            if(Math.round(Math.random()) == 0) {
	            int sizeInKilobytes = (int) Math.floor(Math.random() * 100);
	            int[] value = new int[sizeInKilobytes * 1024];
	            cache.put(key, value);
            }
            else {
                if(cache.get(key) != null) {
                    System.out.println("Get: " + key); //$NON-NLS-1$
                }
                else {
                    System.out.println("Key Not found: " + key); //$NON-NLS-1$
                }
            }
        }
    }
}
