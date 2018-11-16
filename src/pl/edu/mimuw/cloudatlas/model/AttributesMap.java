/**
 * Copyright (c) 2014, University of Warsaw
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pl.edu.mimuw.cloudatlas.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a map from <code>Attribute</code> to <code>Value</code>. It cannot contain duplicate keys.
 */
public class AttributesMap implements Iterable<Entry<Attribute, Value>>, Cloneable {
	private Map<Attribute, Value> map = new HashMap<Attribute, Value>();
	
	private void checkNulls(Attribute attribute, Value value) {
		if(attribute == null)
			throw new NullPointerException("The attribute cannot be null.");
		if(value == null)
			throw new NullPointerException(
					"The value cannot be null. You may want create a Value object that contains null.");
	}
	
	/**
	 * Adds to this map a new <code>attribute</code> mapping to the specified <code>value</code>. The
	 * <code>attribute</code> cannot already exist in the map. To overwrite an existing attribute, use method
	 * {@link #addOrChange(Attribute, Value)} instead.
	 * 
	 * @param attribute the attribute to add
	 * @param value the value for the <code>attribute</code>
	 * @throws IllegalArgumentException if the <code>attribute</code> already exists in this map
	 * @throws NullPointerException if either the <code>attribute</code> or the <code>value</code> is <code>null</code>;
	 * for null value, create a <code>Value</code> object containing <code>null</code>
	 * @see #addOrChange(Attribute, Value)
	 * @see #add(String, Value)
	 * @see #add(Entry)
	 * @see #add(AttributesMap)
	 */
	public void add(Attribute attribute, Value value) {
		if(getOrNull(attribute) != null)
			throw new IllegalArgumentException("Attribute \"" + attribute.getName()
					+ "\" already exists. Use method addOrChange(Attribute, Value) instead.");
		checkNulls(attribute, value);
		map.put(attribute, value);
	}
	
	/**
	 * Adds a new attribute mapping to the specified <code>value</code>. Convenient version of
	 * {@link #add(Attribute, Value)}.
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 * @see #add(Attribute, Value)
	 * @see #addOrChange(String, Value)
	 */
	public void add(String name, Value value) {
		add(new Attribute(name), value);
	}
	
	/**
	 * Adds a new mapping to this map. Convenient version of {@link #add(Attribute, Value)}.
	 * 
	 * @param entry a pair containing both an attribute and a value
	 * @see #add(Attribute, Value)
	 * @see #addOrChange(Entry)
	 */
	public void add(Entry<Attribute, Value> entry) {
		add(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Adds all entries from another map to this map. This method uses {@link #add(Attribute, Value)}, so it throws an
	 * exception when trying to overwrite an existing attribute.
	 * 
	 * @param attributes the map to add
	 * @see #add(Attribute, Value)
	 * @see #addOrChange(AttributesMap)
	 */
	public void add(AttributesMap attributes) {
		for(Entry<Attribute, Value> entry : attributes.map.entrySet())
			add(entry);
	}
	
	/**
	 * Adds to this map a new <code>attribute</code> mapping to the specified <code>value</code>. Unlike
	 * {@link #add(Attribute, Value)}, this method overwrites an existing attribute with the same name.
	 * 
	 * @param attribute the attribute to add or overwrite
	 * @param value the value for the <code>attribute</code>
	 * @throws NullPointerException if either the <code>attribute</code> or the <code>value</code> is <code>null</code>;
	 * for null value create a <code>Value</code> object containing <code>null</code>
	 * @see #add(Attribute, Value)
	 * @see #addOrChange(String, Value)
	 * @see #addOrChange(Entry)
	 * @see #addOrChange(AttributesMap)
	 */
	public void addOrChange(Attribute attribute, Value value) {
		map.put(attribute, value);
		checkNulls(attribute, value);
	}
	
	/**
	 * Adds a new attribute< mapping to the specified <code>value</code> or overwrites an existing one. Convenient
	 * version of {@link #addOrChange(Attribute, Value)}.
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 * @see #addOrChange(Attribute, Value)
	 * @see #add(String, Value)
	 */
	public void addOrChange(String name, Value value) {
		addOrChange(new Attribute(name), value);
	}
	
	/**
	 * Adds a new mapping to this map or overwrites an existing one with the same attribute name. Convenient version of
	 * {@link #addOrChange(Attribute, Value)}.
	 * 
	 * @param entry a pair containing both an attribute and a value
	 * @see #addOrChange(Attribute, Value)
	 * @see #add(Entry)
	 */
	public void addOrChange(Entry<Attribute, Value> entry) {
		addOrChange(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Adds all entries from another map to this map. If any attribute with the same name exists in this map, it will be
	 * overwritten.
	 * 
	 * @param attributes the map to add
	 * @see #addOrChange(Attribute, Value)
	 * @see #add(AttributesMap)
	 */
	public void addOrChange(AttributesMap attributes) {
		for(Entry<Attribute, Value> entry : attributes.map.entrySet())
			addOrChange(entry);
	}
	
	private void checkAttribute(Attribute attribute) {
		if(attribute == null)
			throw new NullPointerException("The attribute cannot be null.");
	}
	
	/**
	 * Gets the value mapped to the specified <code>attribute</code>. If such mapping does not exist, this method throws
	 * an exception. If this is not an expected behavior, use method {@link #getOrNull(Attribute)} instead.
	 * 
	 * @param attribute the attribute to obtain
	 * @return the value mapped to the <code>attribute</code>
	 * @throws IllegalArgumentException if no value is mapped to the <code>attribute</code>
	 * @throws NullPointerException if the <code>attribute</code> is <code>null</code>
	 * @see #getOrNull(Attribute)
	 * @see #get(String)
	 */
	public Value get(Attribute attribute) {
		Value value = getOrNull(attribute);
		if(value == null)
			throw new IllegalArgumentException("Attribute " + attribute.getName()
					+ " does not exist. Use method getOrNull(Attribute) instead.");
		return value;
	}
	
	/**
	 * Gets the value mapped to the specified attribute. Convenient version of {@link #get(Attribute)}.
	 * 
	 * @param name name of the attribute
	 * @return the value mapped to the specified attribute
	 * @see #get(Attribute)
	 * @see #getOrNull(String)
	 */
	public Value get(String name) {
		return get(new Attribute(name));
	}
	
	/**
	 * Gets the value mapped to the specified <code>attribute</code>. Unlike {@link #get(Attribute)}, this method
	 * returns <code>null</code> if the requested mapping does not exist.
	 * 
	 * @param attribute the attribute to obtain
	 * @return the value mapped to the <code>attribute</code> or <code>null</code> if it does not exist
	 * @throws NullPointerException if the <code>attribute</code> is <code>null</code>
	 * @see #get(Attribute)
	 * @see #getOrNull(String)
	 */
	public Value getOrNull(Attribute attribute) {
		checkAttribute(attribute);
		return map.get(attribute);
	}
	
	/**
	 * Gets the value mapped to the specified attribute. Convenient version of {@link #getOrNull(Attribute)}.
	 * 
	 * @param name name of the attribute
	 * @return the value mapped to specified attribute or <code>null</code> if it does not exist
	 * @see #getOrNull(Attribute)
	 * @see #getOr(String)
	 */
	public Value getOrNull(String name) {
		return getOrNull(new Attribute(name));
	}
	
	/**
	 * Removes the specified <code>attribute</code> and its value from this map. If the <code>attribute</code> does not
	 * exist, this method performs nothing.
	 * 
	 * @param attribute the attribute to remove
	 * @throws NullPointerException if the <code>attribute</code> is <code>null</code>
	 * @see #remove(String)
	 */
	public void remove(Attribute attribute) {
		checkAttribute(attribute);
		map.remove(attribute);
	}
	
	/**
	 * Removes the specified attribute and its value from this map. Convenient version of {@link #remove(Attribute)}.
	 * 
	 * @param name the name of the attribute to remove
	 * @see #remove(Attribute)
	 */
	public void remove(String name) {
		map.remove(new Attribute(name));
	}
	
	/**
	 * Returns an iterator over all entries stored in this map.
	 * 
	 * @return an iterator for this map
	 * @see java.util.Iterator
	 * @see java.lang.Iterable
	 */
	@Override
	public Iterator<Entry<Attribute, Value>> iterator() {
		return map.entrySet().iterator();
	}
	
	/**
	 * Creates a copy of this map. Since <code>Value</code> and <code>Attribute</code> are immutable classes, this
	 * method does not clone them.
	 * 
	 * @return a copy of this map containing identical entries
	 */
	@Override
	public AttributesMap clone() {
		AttributesMap result = new AttributesMap();
		result.add(this);
		return result;
	}
	
	/**
	 * Returns a string representation of this map listing all key-value pairs stored in it.
	 * 
	 * @return a string representation of this object
	 */
	@Override
	public String toString() {
		return map.toString();
	}
}
