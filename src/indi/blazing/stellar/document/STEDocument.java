/******************************************************************************
 * Copyright (c) 2022 BlazingStellar
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the 
 * 		Eclipse Public License 2.0 
 * which is available at 
 * 		http://www.eclipse.org/legal/epl-2.0 
 * or the 
 * 		BSD 3-Clause License 
 * which is available at 
 * 		https://opensource.org/licenses/BSD-3-Clause 
 * 
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 ******************************************************************************
 */
package indi.blazing.stellar.document;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class STEDocument {
	
	private Path docPath;
	private ArrayList<STEAttribute> attrs;
	
	private boolean readOnly = true;
	
	public STEDocument(Path docPath, STEAttribute... attrs) 
			throws FileNotFoundException {
		if(!Files.exists(docPath) || Files.isDirectory(docPath)) 
			throw new FileNotFoundException("<Path: " + docPath + ">");
		
		this.docPath = docPath;
		this.attrs = new ArrayList<>(Arrays.asList(attrs));
	}
	
	public STEDocument(InputStream in, STEAttribute... attrs) {
		docPath = null;
		
		for(STEAttribute attr: attrs) 
			if(attr.key.equals("READONLY")) {
				readOnly = attr.value.equals("TRUE");
				break;
			}
		throw new UnsupportedOperationException();
	}
	
	public final Path getPath() {
		return docPath;
	}
	
	public String findAttrOrElse(String key, String or) {
		for(STEAttribute attr: attrs) 
			if(attr.key.equalsIgnoreCase(key)) 
				return attr.value;
		
		return or;
	}
	
	protected final STEAttribute setAttr(String key, String value) {
		for(int i = 0; i < attrs.size(); i++) 
			if(attrs.get(i).key.equals(key)) 
				return attrs.set(i, new STEAttribute(key, value));
		
		attrs.add(new STEAttribute(key, value));
		return null;
	}
	
	protected final STEAttribute removeAttr(String key) {
		for(int i = 0; i < attrs.size(); i++) 
			if(attrs.get(i).key.equals(key)) 
				return attrs.remove(i);
		
		return null;
	}
	
	public final boolean isReadOnly() {
		return readOnly;
	}
	
	protected void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public abstract void save();
	
	public static void main(String[] args) {}
	
}
