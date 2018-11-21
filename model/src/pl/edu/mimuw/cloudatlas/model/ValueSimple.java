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

/**
 * Convenient class for wrapping Java types into <code>Value</code> objects.
 * <p>
 * This class is immutable.
 * 
 * @param <T> a wrapped type
 */
abstract class ValueSimple<T> extends Value {
	private T value;
	
	/**
	 * Constructs a new <code>Value</code> wrapping the specified <code>value</code>.
	 * 
	 * @param value the value to wrap
	 */
	public ValueSimple(T value) {
		setValue(value);
	}
	
	/**
	 * Returns a hash code value for this object. This is a hash code of underlying wrapped object.
	 * 
	 * @return the hash code for this value
	 */
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}
	
	/**
	 * Gets a wrapped object.
	 * 
	 * @return the wrapped value
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Sets a wrapped value. This method is not public to ensure that the underlying value cannot be changed.
	 * 
	 * @param value the value to set
	 */
	void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public boolean isNull() {
		return value == null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Value isEqual(Value v) {
		sameTypesOrThrow(v, Operation.EQUAL);
		if(isNull() && v.isNull())
			return new ValueBoolean(true);
		else if(isNull() || v.isNull())
			return new ValueBoolean(false);
		return new ValueBoolean(value.equals(((ValueSimple<T>)v).getValue()));
	}
}
