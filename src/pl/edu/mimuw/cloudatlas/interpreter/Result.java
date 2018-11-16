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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueList;

abstract class Result {
	public interface BinaryOperation {
		public Value perform(Value v1, Value v2);
	}

	public interface UnaryOperation {
		public Value perform(Value v);
	}

	public interface AggregationOperation {
		public Value perform(ValueList values);
	}

	public interface TransformOperation {
		public ValueList perform(ValueList values);
	}

	private static final BinaryOperation IS_EQUAL = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.isEqual(v2);
		}
	};

	private static final BinaryOperation IS_LOWER_THAN = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.isLowerThan(v2);
		}
	};

	private static final BinaryOperation ADD_VALUE = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.addValue(v2);
		}
	};

	private static final BinaryOperation SUBTRACT = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.subtract(v2);
		}
	};

	private static final BinaryOperation MULTIPLY = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.multiply(v2);
		}
	};

	private static final BinaryOperation DIVIDE = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.divide(v2);
		}
	};

	private static final BinaryOperation MODULO = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.modulo(v2);
		}
	};

	private static final BinaryOperation AND = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.and(v2);
		}
	};

	private static final BinaryOperation OR = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.or(v2);
		}
	};

	private static final BinaryOperation REG_EXPR = new BinaryOperation() {
		@Override
		public Value perform(Value v1, Value v2) {
			return v1.regExpr(v2);
		}
	};

	private static final UnaryOperation NEGATE = new UnaryOperation() {
		@Override
		public Value perform(Value v) {
			return v.negate();
		}
	};

	private static final UnaryOperation VALUE_SIZE = new UnaryOperation() {
		@Override
		public Value perform(Value v) {
			return v.valueSize();
		}
	};

	protected abstract Result binaryOperationTyped(BinaryOperation operation, ResultSingle right);

	public Result binaryOperation(BinaryOperation operation, Result right) {
		return right.callMe(operation, this);
	}

	public abstract Result unaryOperation(UnaryOperation operation);

	protected abstract Result callMe(BinaryOperation operation, Result left);

	public abstract Value getValue();

	public abstract ValueList getList();

	public abstract ValueList getColumn();

	public ResultSingle aggregationOperation(AggregationOperation operation) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Result transformOperation(TransformOperation operation) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Result isEqual(Result right) {
		return right.callMe(IS_EQUAL, this);
	}

	public Result isLowerThan(Result right) {
		return right.callMe(IS_LOWER_THAN, this);
	}

	public Result addValue(Result right) {
		return right.callMe(ADD_VALUE, this);
	}

	public Result subtract(Result right) {
		return right.callMe(SUBTRACT, this);
	}

	public Result multiply(Result right) {
		return right.callMe(MULTIPLY, this);
	}

	public Result divide(Result right) {
		return right.callMe(DIVIDE, this);
	}

	public Result modulo(Result right) {
		return right.callMe(MODULO, this);
	}

	public Result and(Result right) {
		return right.callMe(AND, this);
	}

	public Result or(Result right) {
		return right.callMe(OR, this);
	}

	public Result regExpr(Result right) {
		return right.callMe(REG_EXPR, this);
	}

	public Result negate() {
		return unaryOperation(NEGATE);
	}

	public Result valueSize() {
		return unaryOperation(VALUE_SIZE);
	}

	protected static ValueList filterNullsList(ValueList list) {
		List<Value> result = new ArrayList<Value>();
		if(list.isEmpty())
			return new ValueList(result, ((TypeCollection)list.getType()).getElementType());
		for(Value v : list)
			if(!v.isNull())
				result.add(v);
		return new ValueList(result.isEmpty()? null : result, ((TypeCollection)list.getType()).getElementType());
	}

	public abstract Result filterNulls();

	protected static ValueList firstList(ValueList list, int size) {
		ValueList nlist = filterNullsList(list);
		if(nlist.getValue() == null)
			return nlist;
		List<Value> result = new ArrayList<Value>(size);
		int i = 0;
		for(Value v : nlist) {
			result.add(v);
			if(++i == size)
				break;
		}
		return new ValueList(result, ((TypeCollection)list.getType()).getElementType());
	}

	public abstract Result first(int size);

	protected static ValueList lastList(ValueList list, int size) {
		ValueList nlist = filterNullsList(list);
		if(nlist.getValue() == null)
			return nlist;
		List<Value> result = new ArrayList<Value>(size);
		for(int i = Math.max(0, list.size() - size); i < list.size(); ++i)
			result.add(list.get(i));
		return new ValueList(result, ((TypeCollection)list.getType()).getElementType());
	}

	public abstract Result last(int size);

	protected static ValueList randomList(ValueList list, int size) {
		ValueList nlist = filterNullsList(list);
		if(nlist.getValue() == null || list.size() <= size)
			return nlist;
		Collections.shuffle(nlist);
		return new ValueList(nlist.getValue().subList(0, size), ((TypeCollection)list.getType()).getElementType());
	}

	public abstract Result random(int size);

	public abstract Result convertTo(Type to);

	public abstract ResultSingle isNull();

	public abstract Type getType();
}
