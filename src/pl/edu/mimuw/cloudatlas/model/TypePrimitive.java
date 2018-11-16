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
 * Convenient class for types that just wrap ordinary Java types.
 * 
 * @see TypeCollection
 */
public class TypePrimitive extends Type {
	/**
	 * Boolean type.
	 */
	public static final TypePrimitive BOOLEAN = new TypePrimitive(PrimaryType.BOOLEAN);
	
	/**
	 * Contact type.
	 */
	public static final TypePrimitive CONTACT = new TypePrimitive(PrimaryType.CONTACT);
	
	/**
	 * Double type.
	 */
	public static final TypePrimitive DOUBLE = new TypePrimitive(PrimaryType.DOUBLE);
	
	/**
	 * Duration type.
	 */
	public static final TypePrimitive DURATION = new TypePrimitive(PrimaryType.DURATION);
	
	/**
	 * Integer type.
	 */
	public static final TypePrimitive INTEGER = new TypePrimitive(PrimaryType.INT);
	
	/**
	 * A special "null type" that represents null value of an unknown type. It can be converted to any other type.
	 * 
	 * @see Type#isCompatible(Type)
	 * @see ValueNull
	 */
	public static final TypePrimitive NULL = new TypePrimitive(PrimaryType.NULL);
	
	/**
	 * String type.
	 */
	public static final TypePrimitive STRING = new TypePrimitive(PrimaryType.STRING);
	
	/**
	 * Time type.
	 */
	public static final TypePrimitive TIME = new TypePrimitive(PrimaryType.TIME);
	
	private TypePrimitive(PrimaryType primaryType) {
		super(primaryType);
		switch(primaryType) {
			case BOOLEAN:
			case CONTACT:
			case DOUBLE:
			case DURATION:
			case INT:
			case NULL:
			case STRING:
			case TIME:
				break;
			default:
				throw new IllegalArgumentException(
						"This class can represent a primitive type only (boolean, int etc.).");
		}
	}
	
	/**
	 * Gets a textual representation of this type.
	 * 
	 * @return a string representing this type
	 */
	@Override
	public String toString() {
		return getPrimaryType().toString();
	}
	
	@Override
	public boolean isCompatible(Type type) {
		return super.isCompatible(type) || getPrimaryType() == type.getPrimaryType();
	}
}
