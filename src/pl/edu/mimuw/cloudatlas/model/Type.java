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
 * A type of a value that may be stored as an attribute.
 */
public abstract class Type {
	/**
	 * A primary type. This is a characteristic that every type has. It can be extended: for instance a collection may
	 * be parameterized with a type of stored values.
	 */
	public static enum PrimaryType {
		BOOLEAN, CONTACT, DOUBLE, DURATION, INT, LIST, NULL, SET, STRING, TIME,
	}
	
	private final PrimaryType primaryType;
	
	/**
	 * Creates a <code>Type</code> object with a given primary type.
	 * 
	 * @param primaryType a primary type for this type
	 */
	public Type(PrimaryType primaryType) {
		this.primaryType = primaryType;
	}
	
	/**
	 * Returns a primary type of this type.
	 * 
	 * @return a primary type
	 */
	public PrimaryType getPrimaryType() {
		return primaryType;
	}
	
	/**
	 * Indicates whether this type can be implicitly "cast" to given one and vice verse. This is introduced to deal with
	 * null values. In practice, two types are compatible either if they are the same or if one them is a special
	 * "null type".
	 * 
	 * @param type a type to check
	 * @return whether two types are compatible with each other
	 * @see TypePrimitive#NULL
	 * @see ValueNull
	 */
	public boolean isCompatible(Type type) {
		return getPrimaryType() == PrimaryType.NULL || type.getPrimaryType() == PrimaryType.NULL;
	}
	
	/**
	 * Indicates whether this type represents a collection.
	 * 
	 * @return true for collections, false otherwise
	 */
	public boolean isCollection() {
		return false;
	}
}
