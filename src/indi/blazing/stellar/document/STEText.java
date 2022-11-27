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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class STEText extends STEDocument implements Iterable<String>{
	
	private String description, type, typeParameters;
	private Charset encoding;
	private ArrayList<String> doclines = new ArrayList<>();
	
	public STEText(Path docPath, String doctype, STEAttribute... attrs) throws IOException {
		super(docPath, attrs);
		
		BufferedReader doc = Files.newBufferedReader(docPath);
		String line = doc.readLine();
		
		ArrayList<String> docDetails = splitWithTab(line, 4);
		if(docDetails.size() < 3) {
			doc.close();
			throw new IOException("<UncorrectFormat: " + docPath + ">");
		}
		encoding = Charset.forName(docDetails.get(1));
		if(!encoding.equals(StandardCharsets.UTF_8)) {
			doc.close();
			doc = Files.newBufferedReader(docPath, encoding);
			line = doc.readLine();
			docDetails = splitWithTab(line, 4);
		}
		
		if(type == null || !doctype.equals(docDetails.get(2))) {
			doc.close();
			throw new IOException("<UncorrectType: " + docDetails.get(2) + ">");
		}
		
		description = docDetails.get(0);
		typeParameters = docDetails.size() == 4? 
				docDetails.get(3): "";
		
		/* split typeParameters to super.setAttr(String, String)*/
		
		doclines.set(0, line);
//		if(!findAttrOrElse("BUFFER", "FALSE").equalsIgnoreCase("TRUE"))
			while((line = doc.readLine()) != null)
				doclines.add(line);
		
		doc.close();
	}  // checked(only comments may need to change)
	
	public STEText(InputStream in, String doctype, STEAttribute... attrs) {
		super(in, attrs);
	}

	public String getDescription() {
		return description;
	}
	
	public boolean setDescription(String description) {
		if(description.indexOf("|") != -1) return false;
		
		this.description = description;
		return true;
	}
	
	public Charset getEncoding() {
		return encoding;
	}
	
	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}
	
	public void setEncoding(String encoding) {
		setEncoding(Charset.forName(encoding));
	}
	
	public String getTypeParaments() {
		return typeParameters;
	}
	
	protected void setTypeParaments(String typeParaments) {
		this.typeParameters = typeParaments;
	}
	
	protected void addLine(String line) {
		doclines.add(line);
	}
	
	protected void removeLine(int index) {
		doclines.remove(index + 1);
	}
	
	protected String getLine(int index) {
		return doclines.get(index + 1);
	}
	
	protected void setLine(int index, String line) {
		doclines.set(index + 1, line);
	}
	
	protected List<String> getAllLines(){
		return doclines.subList(0, doclines.size());
	}
	
	public void save() {
		if(getPath() == null) throw new UnsupportedOperationException("Path of this document is null!");
		
		doclines.set(0, getDetailLine());
		try {
			Path tmpfile = Files.createTempFile(getPath().getParent(), "STE", "");
			Files.write(tmpfile, doclines, encoding, StandardOpenOption.WRITE);
			Files.deleteIfExists(getPath());
			tmpfile.toFile().renameTo(getPath().toFile());
		} catch (IOException e) {}
		
	}
	
	private String getDetailLine() {
		if(typeParameters.length() != 0)
			return mergeWith("|", description, encoding, type, typeParameters);
		else 
			return mergeWith("|", description, encoding, type);
	}
	
	
	public class Itr implements Iterator<String> {
			
		private int i = 0;
		
		Itr() {}
		
		public void remove() {}
		
		public String next() {
			return doclines.get(i);
		}
		
		public boolean hasNext() {
			i++;
			return i < doclines.size();
		}

	}
	
	public Iterator<String> iterator() {
		return new Itr();
	}
	
	public int tSize() {
		return doclines.size() - 1;
	}
	
	public static void main(String[] args) {}
	
	public static ArrayList<String> splitWithTab(String origin, int max) {
		return splitWith(origin, "\t", max);
	}
	
	public static ArrayList<String> splitWith(String origin, String sp, int max) {
		ArrayList<String> result = new ArrayList<>(max);
		int head = 0;
		
		if(max <= 0) max = Integer.MAX_VALUE;
		
		while(result.size() < max && head <= origin.length()) {
			int prev = head;
			
			if((head = origin.indexOf(sp, head)) == -1 || result.size() == max - 1)
				head = origin.length();
			
			result.add(origin.substring(prev, head));
			head += sp.length();
		}
		
		result.trimToSize();
		return result;
	}
	
	public static String mergeWithTab(Object... terms) {
		return mergeWith("\t", terms);
	}
	
	public static String mergeWith(String sp, Object... terms) {
		StringBuilder result = new StringBuilder("" + terms[0]);
		
		for(int i = 1; i < terms.length; i++)
			result.append(sp + terms[i]);
		
		return result.toString();
	}
	
}
