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

import pl.edu.mimuw.cloudatlas.model.Value;

/**
 * A single value stored as an attribute.
 */
public abstract class Value {
	/**
	 * An operation that may be performed on values.
	 */
	public enum Operation {
		EQUAL, COMPARE, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, AND, OR, REG_EXPR, NEGATE, VALUE_SIZE,
	}
	
	/**
	 * Gets type of this value.
	 * 
	 * @return type of this value
	 */
	public abstract Type getType();
	
	/**
	 * Indicates whether this value is null. Distinct from a <code>Value</code> reference that is <code>null</code>
	 * itself.
	 * 
	 * @return true if and only if this value is null
	 */
	public abstract boolean isNull();
	
	protected final void sameTypesOrThrow(Value value, Operation operation) {
		if(!getType().isCompatible(value.getType()))
			throw new IncompatibleTypesException(getType(), value.getType(), operation);
	}
	
	/**
	 * Checks whether this value is equal to the specified one (operator ==).
	 * 
	 * @param value the right side of the operator
	 * @return a <code>ValueBoolean</code> representing true if and only if both values are equal
	 * @throws UnsupportedValueOperationException if this operation is unsupported for these values
	 */
	public Value isEqual(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.EQUAL);
	}
	
	/**
	 * Indicates whether this object is equal to another one.
	 * 
	 * @param object the object to check
	 * @return whether two objects are equal
	 */
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof Value))
			return false;
		return ((ValueBoolean)isEqual((Value)object)).getValue();
	}
	
	/**
	 * Checks whether this value is lower than the specified one (operator <=).
	 * 
	 * @param value the right side of the operator
	 * @return a <code>ValueBoolean</code> representing true if and only if this value is lower than the provided one
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * incompatible or non-numeric types)
	 */
	public Value isLowerThan(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.COMPARE);
	}
	
	/**
	 * Returns a new value created by adding argument to this value (operator +).
	 * 
	 * @param the right side of the operator
	 * @return a sum of two values
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * incompatible or non-numeric types)
	 */
	public Value addValue(Value value) {
		// name clash with add from List interface
		throw new UnsupportedValueOperationException(getType(), Operation.ADD);
	}
	
	/**
	 * Returns a new value created by subtracting argument from this value (operator -).
	 * 
	 * @param the right side of the operator
	 * @return a difference of two values
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * incompatible or non-numeric types)
	 */
	public Value subtract(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.SUBTRACT);
	}
	
	/**
	 * Returns a new value created by multiplying this value by an argument (operator *).
	 * 
	 * @param the right side of the operator
	 * @return a product of two values
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * incompatible or non-numeric types)
	 */
	public Value multiply(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.MULTIPLY);
	}
	
	/**
	 * Returns a new value created by dividing this value by an argument (operator /).
	 * 
	 * @param the right side of the operator
	 * @return a quotient of two values
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * incompatible or non-numeric types)
	 */
	public Value divide(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.DIVIDE);
	}
	
	/**
	 * Returns a remainder of a division of this value by an argument (operator %).
	 * 
	 * @param the right side of the operator
	 * @return a remainder
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * incompatible or non-numeric types)
	 */
	public Value modulo(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.MODULO);
	}
	
	/**
	 * Returns a result of a logical and (operator &&).
	 * 
	 * @param value the right side of the operator
	 * @return a conjunction of two values
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * non-boolean types)
	 */
	public Value and(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.AND);
	}
	
	/**
	 * Returns a result of a logical or (operator ||).
	 * 
	 * @param value the right side of the operator
	 * @return an alternative of two values
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values (for example
	 * non-boolean types)
	 */
	public Value or(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.OR);
	}
	
	/**
	 * Returns a result of applying to this value a regular expression specified as an argument..
	 * 
	 * @param value the regular expression
	 * @return a <code>ValueBoolean</code> representing true if and only if this value matches provided regular
	 * expression
	 * @throws UnsupportedValueOperationException if this operator is unsupported for these values
	 */
	public Value regExpr(Value value) {
		throw new UnsupportedValueOperationException(getType(), Operation.REG_EXPR);
	}
	
	/**
	 * Returns a negation (numeric or logical) of this value. This may refer to operator - or !, depending on type.
	 * 
	 * @return a value that is negation of this value
	 * @throws UnsupportedValueOperationException if this operator is unsupported for this value
	 */
	public Value negate() { // !, -
		throw new UnsupportedValueOperationException(getType(), Operation.NEGATE);
	}
	
	/**
	 * Returns a size of this value. Semantic depends on type.
	 * 
	 * @return a size of this value
	 * @throws UnsupportedValueOperationException if this operation is unsupported for this value
	 */
	public Value valueSize() {
		// name clash with size from List interface
		throw new UnsupportedValueOperationException(getType(), Operation.VALUE_SIZE);
	}
	
	/**
	 * Returns this value converted to another type.
	 * 
	 * @param to a desired type
	 * @return this value converted to the <code>type</code>
	 * @throws UnsupportedConversionException if a requested conversion is unsupported
	 */
	public abstract Value convertTo(Type to);
	
	/**
	 * Returns a textual representation of this value. This method uses conversion to <code>ValueString</code>.
	 * 
	 * @return a textual representation of this value
	 * @see #convertTo(Type)
	 */
	@Override
	public String toString() {
		return ((ValueString)convertTo(TypePrimitive.STRING)).getValue();
	}
	
	/**
	 * Returns a default value (such as uninitialized variable). This may be <code>0</code> for integer types,
	 * <code>false</code> for boolean, <code>null</code> for complex types etc.
	 * 
	 * @return a default value of this type
	 */
	public abstract Value getDefaultValue();
}
