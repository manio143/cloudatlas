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

import java.util.List;

import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueNull;

class ResultColumn extends Result {
	private final List<Value> value;

	public ResultColumn(ValueList value) {
		this.value = value.isNull() ? null : value.getValue();
	}
	
	@Override
	protected Result binaryOperationTyped(BinaryOperation operation, Result right) {
		if (right.isSingle())
			return new ResultColumn(map(new Transform() {
				public Value transform(Value v) {
					return operation.perform(v, right.getValue());
				}
			}));
		return new ResultColumn(zipWith(right, new Zipper() {
			public Value zipf(Value e1, Value e2) {
				return operation.perform(e1, e2);
			}
		}));
	}

	@Override
	public Result unaryOperation(UnaryOperation operation) {
		return new ResultColumn(map(new Transform() {
            public Value transform(Value v) {
                return operation.perform(v);
            }
        }));
	}

	@Override
	public Value getValue() {
		return new ValueList(value, value.size() > 0 ? value.get(0).getType() : ValueNull.getInstance().getType());
	}

	@Override
	public ValueList getList() {
		return (ValueList) getValue();
	}

	@Override
	public ValueList getColumn() {
		return (ValueList) getValue();		
	}

	@Override
	public Result filterNulls() {
		return new ResultList(filterNullsList(getList()));
	}

	@Override
	public Result first(int size) {
		return new ResultList(firstList(getList(), size));
	}

	@Override
	public Result last(int size) {
		return new ResultList(lastList(getList(), size));
	}

	@Override
	public Result random(int size) {
		return new ResultList(randomList(getList(), size));
	}

	@Override
	public Result convertTo(Type to) {
		return new ResultColumn(map(new Transform() {
            public Value transform(Value v) {
                return v.convertTo(to);
            }
        }));
	}

	@Override
	public ResultSingle isNull() {
		return new ResultSingle(new ValueBoolean(value == null));
	}

	@Override
	public Type getType() {
		Type type = value.size() > 0 ? value.get(0).getType() : ValueNull.getInstance().getType();
		return type;
	}
}
