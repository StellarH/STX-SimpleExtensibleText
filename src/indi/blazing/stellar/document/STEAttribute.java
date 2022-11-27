/**
 ******************************************************************************
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
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 ******************************************************************************
 */
package indi.blazing.stellar.document;

public class STEAttribute {
	
	public final String key, value;
	
	public STEAttribute(String key, String value) {
		if(key == null || value == null) 
			throw new NullPointerException("<" + key + ": " + value + ">");
		
		this.key = key;
		this.value = value;
	}
	
}  // checked
