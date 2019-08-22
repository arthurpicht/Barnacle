package de.arthurpicht.barnacle.generator.java;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Objects of SourceCache class cache generated java source
 * code. Code is indentated automatically on flush to 
 * PrintWriter.
 * 
 * @author Picht
 *
 */
public class SourceCache {
	
	private List<String> linesOfCode;
	private PrintWriter printWriter;
	
	private String bufferString;
	
	/**
	 * Creates SourceCache object.
	 * 
	 * @param printWriter Cached code will be passed to printWriter
	 * on flush.
	 */
	public SourceCache(PrintWriter printWriter) {
		this.printWriter = printWriter;
		this.linesOfCode = new ArrayList<String>();
		this.bufferString = new String();
	}
	
	/**
	 * Adds line of code to the cache.
	 * 
	 * @param lineOfCode
	 */
	public void addLine(String lineOfCode) {
		this.linesOfCode.add(this.bufferString + lineOfCode);
		this.bufferString = "";
	}
	
	/**
	 * Flushes the buffered line of code to the cache
	 * or adds empty line if buffer is empty.
	 *
	 */
	public void addLine() {
		this.linesOfCode.add(this.bufferString);
		this.bufferString = "";
	}
	
	/**
	 * Adds line of code to the buffer.
	 * 
	 * @param lineOfCode
	 */
	public void add(String lineOfCode) {
		this.bufferString += lineOfCode;
	}
	
	/**
	 * Flushes the cache to printWriter. Code is going to be
	 * indentated automatically.
	 *
	 */
	public void flush() {
		int tab=0;
		for (String line : linesOfCode) {
			if (line.startsWith("}")) {
				tab -= 1;
			}
			if (line.startsWith("+ ")) {
				tab += 1;
			}
			this.printWriter.println(this.getTabs(tab) + line);
			if (line.endsWith("{")) {
				tab += 1;
			}
			if (line.startsWith("+ ")) {
				tab -= 1;
			}
		}
	}
	
	private String getTabs(int tab) {
		String tabs = new String();
		for (int i=0; i<tab; i++) {
			tabs += "\t";
		}
		return tabs;
	}

}
