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

import pl.edu.mimuw.cloudatlas.model.Value.Operation;

/**
 * An exception thrown when an unsupported binary operation on values is requested.
 * 
 * @see UnsupportedValueOperationException
 * @see UnsupportedConversionException
 */
@SuppressWarnings("serial")
public class IncompatibleTypesException extends UnsupportedOperationException {
	private final Type left;
	private final Type right;
	private final Operation operation;
	
	/**
	 * Creates a new object representing this exception.
	 * 
	 * @param left type of a left operand
	 * @param right type of a right operand
	 * @param operation an operation that caused this exception
	 */
	protected IncompatibleTypesException(Type left, Type right, Operation operation) {
		super("Incompatible types: " + left + " and " + right + " in operation " + operation + ".");
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
	
	/**
	 * Gets a type of a left operand in an operation that caused this exception.
	 * 
	 * @return the type of the left operand
	 */
	public Type getLeft() {
		return left;
	}
	
	/**
	 * Gets a type of a right operand in an operation that caused this exception.
	 * 
	 * @return the type of the right operand
	 */
	public Type getRight() {
		return right;
	}
	
	/**
	 * Gets an object representing an operation that caused this exception.
	 * 
	 * @return an operation that caused this exception
	 */
	public Operation getOperation() {
		return operation;
	}
}
