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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represent a fully qualified name of a zone, also known as a global name or a path name. This class is immutable.
 */
public class PathName {
	/**
	 * The object representing a name of the root zone (/).
	 */
	public static final PathName ROOT = new PathName("/");
	
	private final List<String> components;
	private final String name;
	
	/**
	 * Creates a <code>PathName</code> object representing the specified path. For the root zone, there are three
	 * accepted forms: <code>null</code> reference, empty string or slash. Any other zone is represented by a string
	 * starting with slash and containing names of zones at each level of hierarchy, separated by slashes. Zone name
	 * must contain only letters and digits.
	 * 
	 * @param name path name of a zone, for instance: <code>/warsaw/uw/violet07</code>
	 * @throws IllegalArgumentException if the <code>name</code> is incorrect
	 */
	public PathName(String name) {
		// we accept null and "/" as names of a root zone, however, we convert all of them to ""
		name = name == null || name.equals("/")? "" : name.trim();
		if(!name.matches("(/\\w+)*"))
			throw new IllegalArgumentException("Incorrect fully qualified name: " + name + ".");
		this.name = name;
		components = name.equals("")? new ArrayList<String>() : Arrays.asList(name.substring(1).split("/"));
	}
	
	/**
	 * Creates a <code>PathName</code> object from a collection of zones names. Every zone name must contain only
	 * letters and digits.
	 * 
	 * @param components a collection of zones names at subsequent levels of hierarchy (starting from root); empty
	 * collection represents the root zone
	 * @throws IllegalArgumentException if any zone name is incorrect
	 */
	public PathName(Collection<String> components) {
		this.components = new ArrayList<String>(components);
		if(components.isEmpty())
			this.name = "";
		else {
			String currentName = "";
			for(String c : components) {
				currentName += "/" + c;
				if(!c.matches("\\w+"))
					throw new IllegalArgumentException("Incorrect component " + c + ".");
			}
			this.name = currentName;
		}
	}
	
	/**
	 * Gets zones names at subsequent levels of hierarchy, starting from root. For the root zone, this method returns an
	 * empty collection. Modifying returned list will throw an exception.
	 * 
	 * @return a collection of zones names
	 */
	public List<String> getComponents() {
		return Collections.unmodifiableList(components);
	}
	
	/**
	 * Gets a full path name. For the root zone, this method returns an empty string.
	 * 
	 * @return a path name represented by this object
	 * @see #toString()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets a name one level up in a hierarchy. For the root zone, this method returns a new instance of the same zone.
	 * 
	 * @return a new <code>PathName</code> object representing a zone one level up in the hierarchy
	 */
	public PathName levelUp() {
		List<String> componentsUp = new ArrayList<String>(components);
		if(!componentsUp.isEmpty())
			componentsUp.remove(componentsUp.size() - 1);
		return new PathName(componentsUp);
	}
	
	/**
	 * Gets a name one level down in a hierarchy.
	 * 
	 * @param son zone name at a lower level
	 * @return a new <code>PathName</code> object representing a zone one level down in the hierarchy
	 */
	public PathName levelDown(String son) {
		return new PathName(name + "/" + son);
	}
	
	/**
	 * Gets a zone name at the lowest level (highest number) in a hierarchy.
	 * 
	 * @return a leaf zone
	 * @throws UnsupportedOperationException if this object represents the root zone
	 */
	public String getSingletonName() {
		try {
			return components.get(components.size() - 1);
		} catch(IndexOutOfBoundsException exception) {
			throw new UnsupportedOperationException("getSingletonName() is not supported for the root zone.");
		}
	}
	
	/**
	 * Returns a hash code value for this object. This method returns a hash code of a string representing full path
	 * name.
	 * 
	 * @return a hash code for this object
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/**
	 * Indicates whether this object is equal to another. A <code>PathName</code> object is equal to other objects of
	 * the same class representing identical path name.
	 * 
	 * @object the object to check
	 * @return whether the <code>object</code> is equal to this name
	 */
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if(getClass() != object.getClass())
			return false;
		return name.equals(((PathName)object).name);
	}
	
	/**
	 * Returns a textual representation for this <code>PathName</code>. For the root zone, unlike {@link #getName()},
	 * this method returns slash.
	 * 
	 * @return a path name for this object
	 * @see #getName()
	 */
	@Override
	public String toString() {
		return name.equals("")? "/" : getName();
	}
}
