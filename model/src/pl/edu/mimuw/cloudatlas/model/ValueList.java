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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueList;

/**
 * A value representing a list of values of the specified type. Implements <code>List</code> interface.
 * <p>
 * All constructors expect type of elements stored in this list. This type is checked when adding elements to the list
 * and an <code>IllegalArgumentException</code> is thrown in case of error.
 * 
 * @see java.util.List
 */
public class ValueList extends ValueSimple<List<Value>> implements List<Value> {
	private TypeCollection type;
	
	/**
	 * Creates a new <code>ValueList</code> containing all the elements in the specified list.
	 * 
	 * @param value a list which content will be copied to this value
	 * @param elementType type of elements stored in this list
	 */
	public ValueList(List<Value> value, Type elementType) {
		this(elementType);
		if(value != null)
			setValue(value);
	}
	
	/**
	 * Creates an empty list.
	 * 
	 * @param elementType type of elements stored in this list
	 */
	public ValueList(Type elementType) {
		super(new ArrayList<Value>());
		type = new TypeCollection(Type.PrimaryType.LIST, elementType);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueList(((TypeCollection)this.getType()).getElementType());
	}
	
	/**
	 * Gets a <code>List</code> containing all the objects stored in this value. Modifying a return value will cause an
	 * exception.
	 */
	@Override
	public List<Value> getValue() {
		return getList() == null? null : Collections.unmodifiableList(getList());
	}
	
	@Override
	public ValueList addValue(Value value) {
		sameTypesOrThrow(value, Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueList(null, ((TypeCollection)getType()).getElementType());
		List<Value> result = new ArrayList<Value>(getValue());
		result.addAll(((ValueList)value).getValue());
		return new ValueList(result, ((TypeCollection)getType()).getElementType());
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case LIST:
				if(getType().isCompatible(type))
					return this;
				throw new UnsupportedConversionException(getType(), type);
			case SET:
				if(this.type.getElementType().isCompatible(((TypeCollection)type).getElementType())) {
					if(this.isNull())
						return new ValueSet(null, this.type.getElementType());
					Set<Value> l = new HashSet<Value>();
					l.addAll(this);
					return new ValueSet(l, this.type.getElementType());
				}
				throw new UnsupportedConversionException(getType(), type);
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : new ValueString(getValue().toString());
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
	
	@Override
	public ValueInt valueSize() {
		return new ValueInt((getList() == null? null : (long)getList().size()));
	}
	
	@Override
	public void setValue(List<Value> list) {
		if(list == null)
			super.setValue(null);
		else {
			super.setValue(new ArrayList<Value>());
			for(Value e : list)
				add(e);
		}
	}
	
	private List<Value> getList() {
		return super.getValue();
	}
	
	private void checkElement(Value element) {
		if(element == null)
			throw new IllegalArgumentException("If you want to use null, create an object containing null instead.");
		if(!type.getElementType().isCompatible(element.getType()))
			throw new IllegalArgumentException("This list contains elements of type "
					+ type.getElementType().toString() + " only. Not compatibile with elements of type: "
					+ element.getType().toString());
	}
	
	@Override
	public boolean add(Value e) {
		checkElement(e);
		return getList().add(e);
	}
	
	@Override
	public void add(int index, Value element) {
		checkElement(element);
		getList().add(index, element);
	}
	
	@Override
	public boolean addAll(Collection<? extends Value> c) {
		for(Value e : c)
			checkElement(e);
		return getList().addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Value> c) {
		for(Value e : c)
			checkElement(e);
		return getList().addAll(index, c);
	}
	
	@Override
	public void clear() {
		getList().clear();
	}
	
	@Override
	public boolean contains(Object o) {
		return getList().contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return getList().containsAll(c);
	}
	
	@Override
	public Value get(int index) {
		return getList().get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return getList().indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return getList().isEmpty();
	}
	
	@Override
	public Iterator<Value> iterator() {
		return getList().iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return getList().lastIndexOf(o);
	}
	
	@Override
	public ListIterator<Value> listIterator() {
		return getList().listIterator();
	}
	
	@Override
	public ListIterator<Value> listIterator(int index) {
		return getList().listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		return getList().remove(o);
	}
	
	@Override
	public Value remove(int index) {
		return getList().remove(index);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return getList().removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return getList().retainAll(c);
	}
	
	@Override
	public Value set(int index, Value element) {
		checkElement(element);
		return getList().set(index, element);
	}
	
	@Override
	public int size() {
		return getList().size();
	}
	
	@Override
	public List<Value> subList(int fromIndex, int toIndex) {
		return new ValueList(getList().subList(fromIndex, toIndex), type.getElementType());
	}
	
	@Override
	public Object[] toArray() {
		return getList().toArray();
	}
	
	@Override
	public <Y> Y[] toArray(Y[] a) {
		return getList().toArray(a);
	}
}
