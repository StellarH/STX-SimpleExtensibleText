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
