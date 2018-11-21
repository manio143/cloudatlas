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

import java.net.InetAddress;

import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueContact;

/**
 * A class that represents a contact to a node. The contact consists of a full path name of this node and its IP
 * address.
 * <p>
 * This class is immutable.
 */
public class ValueContact extends Value {
	private final PathName name;
	private final InetAddress address;
	
	/**
	 * Constructs a new <code>ValueContact</code> with the specified path name and IP address.
	 * 
	 * @param name the full path name of a node
	 * @param address the IP address of the node
	 */
	public ValueContact(PathName name, InetAddress address) {
		this.name = name;
		this.address = address;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueContact(null, null);
	}
	
	/**
	 * Returns a name stored in this object.
	 * 
	 * @return the name of a node
	 */
	public PathName getName() {
		return name;
	}
	
	/**
	 * Returns an IP address stored in this object.
	 * 
	 * @return the IP address of a node
	 */
	public InetAddress getAddress() {
		return address;
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.CONTACT;
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case CONTACT:
				return this;
			case STRING:
				if(isNull())
					return ValueString.NULL_STRING;
				else
					return new ValueString("(" + name.toString() + ", " + address.toString() + ")");
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
	
	@Override
	public boolean isNull() {
		return name == null || address == null;
	}
}
