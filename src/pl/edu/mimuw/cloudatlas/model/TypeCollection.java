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

import java.util.Collection;

import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.TypePrimitive;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueNull;

/**
 * Represents a collection type with specified element type.
 * 
 * @see TypePrimitve
 */
public class TypeCollection extends Type {
	private final Type elementType;
	
	/**
	 * Creates a new collection type.
	 * 
	 * @param primaryType a type of this collection (set, list etc.)
	 * @param elementType a type of an element of this collection; may be a complex type (for instance
	 * <code>TypeCollection</code>)
	 */
	public TypeCollection(PrimaryType primaryType, Type elementType) {
		super(primaryType);
		switch(primaryType) {
			case LIST:
			case SET:
				break;
			default:
				throw new IllegalArgumentException("This class can represent a collection only (list, set etc.).");
		}
		this.elementType = elementType;
	}
	
	/**
	 * Gets a type of elements stored in this collection.
	 * 
	 * @return type of element in this collection
	 */
	public Type getElementType() {
		return elementType;
	}
	
	/**
	 * Returns a friendly textual representation of this collection, for instance: "SET of (STRING)".
	 * 
	 * @return a textual representation of this type
	 */
	@Override
	public String toString() {
		return getPrimaryType().toString() + " of (" + elementType.toString() + ")";
	}
	
	@Override
	public boolean isCompatible(Type type) {
		return super.isCompatible(type)
				|| (getPrimaryType() == type.getPrimaryType() && elementType
						.isCompatible(((TypeCollection)type).elementType));
	}
	
	@Override
	public boolean isCollection() {
		return true;
	}
	
	/**
	 * Returns a type of all elements in the specified collection. If the collection is empty, this method returns
	 * {@link TypePrimitive#NULL}. If the collection contains at least two elements of distinct types that are not nulls,
	 * an exception is thrown.
	 * 
	 * @param collection a collection of values to check
	 * @return type of elements in this collection
	 * @throws IllegalArgumentException if the collection contains non-null elements of different types
	 */
	public static Type computeElementType(Collection<Value> collection) {
		Type mainType = null;
		
		for(Value v : collection) {
			if(v.isNull())
				v = ValueNull.getInstance();
			if(mainType == null) {
				if(v.getType().getPrimaryType() != Type.PrimaryType.NULL)
					mainType = v.getType();
			} else if(!mainType.isCompatible(v.getType()))
				throw new IllegalArgumentException("Collection has non-null elements of different types.");
		}
		
		return mainType == null? TypePrimitive.NULL : mainType;
	}
}
