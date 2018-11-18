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

package pl.edu.mimuw.cloudatlas.interpreter;

import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueList;

class ResultSingle extends Result {
	private final ValueList value;

	public ResultSingle(ValueList value) {
		this.value = value;
	}
	
	@Override
	protected ResultList binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public ResultList unaryOperation(UnaryOperation operation) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	protected Result callMe(BinaryOperation operation, Result left) {
		return left.binaryOperationTyped(operation, this);
	}

	@Override
	public Value getValue() {
		throw new UnsupportedOperationException("Not a ResultSingle");		
	}

	@Override
	public ValueList getList() {
		return value;
	}

	@Override
	public ValueList getColumn() {
		throw new UnsupportedOperationException("Not a ResultColumn.");
	}

	@Override
	public Result filterNulls() {
		return transformOperation(new TransformOperation {
			public ValueList perform(ValueList values) {
				// TODO
				throw new UnsupportedOperationException("Not yet implemented");
			}
		})
	}

	@Override
	public Result first(int size) {
		for(Value v : value.getList())
			return v;
		return new ResultSingle(new ValueNull());
	}

	@Override
	public Result last(int size) {
		Value r = new ValueNull();
		for(Value v : value.getList())
			r = v;
		return new ResultSingle(r);
	}

	@Override
	public Result random(int size) {
		return new ResultList(randomList(value, size));
	}

	@Override
	public ResultList convertTo(Type to) {
		return new ResultList(value.convertTo(to));
	}

	@Override
	public ResultSingle isNull() {
		return new ResultSingle(new ValueBoolean(value.isNull()));
	}

	@Override
	public Type getType() {
		return value.getType();
	}
}
