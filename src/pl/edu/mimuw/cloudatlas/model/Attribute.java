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
 * Represents an attribute (without value, name only).
 * <p>
 * A valid attribute name is an identifier starting with a letter and containing only letters and digits. It can
 * additionally start with an ampersand - such names are reserved for queries.
 * <p>
 * This class is immutable.
 */
public class Attribute {
	private final String name;
	
	/**
	 * Creates a new <code>Attribute</code> object with the specified <code>name</code>.
	 * 
	 * @param name the name of the attribute
	 * @throws IllegalArgumentException if the <code>name</code> does not meet the rules
	 */
	public Attribute(String name) {
		if(!name.matches("^&?[a-zA-Z]{1}[a-zA-z0-9_]*$"))
			throw new IllegalArgumentException("Invalid name: may contain only letters, digits, underscores, "
					+ "must start with a letter and may optionally have an ampersand at the beginning.");
		this.name = name;
	}
	
	/**
	 * Indicates whether an <code>attribute</code> represents a query. This is true if and only if attribute name starts with an
	 * ampersand.
	 * 
	 * @param attribute the attribute to check
	 * @return whether the <code>attribute</code> represents a query
	 */
	public static boolean isQuery(Attribute attribute) {
		return attribute.getName().startsWith("&");
	}
	
	/**
	 * Gets the name of this attribute.
	 * 
	 * @return string representing name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a hash code value for this attribute. For a proper behavior when using <code>Attribute</code> objects in
	 * <code>HashMap</code>, <code>HashSet</code> etc. this is the hash code of a string representing attribute name.
	 * 
	 * @return hash code for this attribute
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/**
	 * Indicates whether another object is equal to this attribute.
	 * 
	 * @param object the object to check
	 * @return true if and only if the <code>object</code> is an instance of <code>Attribute</code> class and has
	 * identical name
	 */
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if(getClass() != object.getClass())
			return false;
		return name.equals(((Attribute)object).name);
	}
	
	/**
	 * Returns a textual representation of this attribute.
	 * 
	 * @return the name of this attribute
	 */
	@Override
	public String toString() {
		return name;
	}
}
