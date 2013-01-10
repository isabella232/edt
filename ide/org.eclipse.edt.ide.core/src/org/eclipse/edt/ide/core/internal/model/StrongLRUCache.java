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

import java.util.HashMap;
import java.util.Map;

/**
 * @author winghong
 */
public class StrongLRUCache {
    
    private static class OrderedStrongReference {
        Object key;
        Object value;
        
        OrderedStrongReference previous;
        OrderedStrongReference next;
        
        // Used for sentinel values
        OrderedStrongReference() {
        }

        OrderedStrongReference(Object key, Object value, OrderedStrongReference previous, OrderedStrongReference next) {
            this.key = key;
            this.value = value;
            this.previous = previous;
            this.next = next;
            this.previous.next = this;
            this.next.previous = this;
        }
        
        OrderedStrongReference remove() {
            previous.next = next;
            next.previous = previous;
            return this;
        }
    }
    
    private OrderedStrongReference first;
    private OrderedStrongReference last;
    
    private Map strongReferences;
    
    private int maxSize;
    
    public StrongLRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.strongReferences = new HashMap();
        this.first = new OrderedStrongReference();
        this.last = new OrderedStrongReference();
        this.first.next = last;
        this.last.previous = first;
    }
    
    public void put(Object key, Object value) {
        strongReferences.put(key, new OrderedStrongReference(key, value, first, first.next));
        if(strongReferences.size() > maxSize) {
            strongReferences.remove(last.previous.remove().key);
        }
    }
    
    public Object get(Object key) {
        OrderedStrongReference ref = (OrderedStrongReference) strongReferences.get(key);
        
        if(ref == null) { 
            return null;
        }
        else {
	        ref.remove();
	        ref.next = first.next;
	        ref.previous = first;
	        first.next.previous = ref;
	        first.next = ref;
	        
	        return ref.value;
        }
    }
}
