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
import java.util.Set;

import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueSet;

/**
 * A value representing a set of values of the specified type. Implements <code>Set</code> interface.
 * <p>
 * All constructors expect type of elements stored in this set. This type is checked when adding elements to the set and
 * an <code>IllegalArgumentException</code> is thrown in case of error.
 * 
 * @see java.util.Set
 */
public class ValueSet extends ValueSimple<Set<Value>> implements Set<Value> {
	private TypeCollection type;
	
	/**
	 * Creates a new <code>ValueSet</code> containing all the elements in the specified set.
	 * 
	 * @param value a set which content will be copied to this value
	 * @param elementType type of elements stored in this set
	 */
	public ValueSet(Set<Value> value, Type elementType) {
		this(elementType);
		if(value != null)
			setValue(value);
	}
	
	/**
	 * Creates an empty set.
	 * 
	 * @param elementType type of elements stored in this set
	 */
	public ValueSet(Type elementType) {
		super(new HashSet<Value>());
		type = new TypeCollection(Type.PrimaryType.SET, elementType);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueSet(((TypeCollection)this.getType()).getElementType());
	}
	
	/**
	 * Gets a <code>Set</code> containing all the objects stored in this value. Modifying a return value will cause an
	 * exception.
	 */
	@Override
	public Set<Value> getValue() {
		return getSet() == null? null : Collections.unmodifiableSet(getSet());
	}
	
	@Override
	public ValueSet addValue(Value value) {
		sameTypesOrThrow(value, Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueSet(null, ((TypeCollection)getType()).getElementType());
		Set<Value> result = new HashSet<Value>(getValue());
		result.addAll(((ValueSet)value).getValue());
		return new ValueSet(result, ((TypeCollection)getType()).getElementType());
	}
	
	@Override
	public ValueInt valueSize() {
		return new ValueInt((getSet() == null? null : (long)getSet().size()));
	}
	
	@Override
	public void setValue(Set<Value> set) {
		if(set == null)
			super.setValue(null);
		else {
			super.setValue(new HashSet<Value>());
			for(Value e : set)
				add(e);
		}
	}
	
	private Set<Value> getSet() {
		return super.getValue();
	}
	
	private void checkElement(Value element) {
		if(element == null)
			throw new IllegalArgumentException("If you want to use null, create an object containing null instead.");
		if(!type.getElementType().isCompatible(element.getType()))
			throw new IllegalArgumentException("This set contains elements of type " + type.getElementType().toString()
					+ " only. Not compatibile with elements of type: " + element.getType().toString());
	}
	
	@Override
	public boolean add(Value e) {
		checkElement(e);
		return getSet().add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends Value> c) {
		for(Value e : c)
			checkElement(e);
		return getSet().addAll(c);
	}
	
	@Override
	public void clear() {
		getSet().clear();
	}
	
	@Override
	public boolean contains(Object o) {
		return getSet().contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return getSet().containsAll(c);
	}
	
	@Override
	public boolean isEmpty() {
		return getSet().isEmpty();
	}
	
	@Override
	public Iterator<Value> iterator() {
		return getSet().iterator();
	}
	
	@Override
	public boolean remove(Object o) {
		return getSet().remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return getSet().removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return getSet().retainAll(c);
	}
	
	@Override
	public int size() {
		return getSet().size();
	}
	
	@Override
	public Object[] toArray() {
		return getSet().toArray();
	}
	
	@Override
	public <U> U[] toArray(U[] a) {
		return getSet().toArray(a);
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case SET:
				if(getType().isCompatible(type))
					return this;
				throw new UnsupportedConversionException(getType(), type);
			case LIST:
				if(this.type.getElementType().isCompatible(((TypeCollection)type).getElementType())) {
					if(this.isNull())
						return new ValueList(null, this.type.getElementType());
					List<Value> l = new ArrayList<Value>();
					l.addAll(this);
					return new ValueList(l, this.type.getElementType());
				}
				throw new UnsupportedConversionException(getType(), type);
			case STRING:
				if(getValue() == null)
					return ValueString.NULL_STRING;
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				boolean notFirst = false;
				for(Value v : getValue()) {
					if(notFirst) {
						sb.append(", ");
					} else
						notFirst = true;
					sb.append(v.toString());
				}
				sb.append("}");
				return new ValueString(sb.toString());
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
}
