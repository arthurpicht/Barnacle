package de.arthurpicht.barnacle.codeGenerator.java;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Objects of SourceCache class cache generated java source
 * code. Code is indented automatically on flush to
 * PrintWriter.
 * 
 * @author Picht
 *
 */
public class SourceCache {
	
	private final List<String> linesOfCode;
	private final PrintWriter printWriter;
	
	private String bufferString;
	
	/**
	 * Creates SourceCache object.
	 * 
	 * @param printWriter Cached code will be passed to printWriter
	 * on flush.
	 */
	public SourceCache(PrintWriter printWriter) {
		this.printWriter = printWriter;
		this.linesOfCode = new ArrayList<>();
		this.bufferString = "";
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
	 * @param lineOfCode adds specified string as a line to the buffer
	 */
	public void add(String lineOfCode) {
		this.bufferString += lineOfCode;
	}
	
	/**
	 * Flushes the cache to printWriter. Code is going to be
	 * indented automatically.
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
		return "\t".repeat(Math.max(0, tab));
	}

}
