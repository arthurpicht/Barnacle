package de.arthurpicht.barnacle.codeGenerator;

import de.arthurpicht.barnacle.generator.GeneratorException;

public class CodeGeneratorException extends GeneratorException {

	private static final long serialVersionUID = 7286506474703547393L;

	public CodeGeneratorException() {
		super();
	}
	
	public CodeGeneratorException(Throwable cause) {
		super(cause);
	}
	
	public CodeGeneratorException(String s) {
		super(s);
	}
	
	public CodeGeneratorException(String s, Throwable cause) {
		super(s, cause);
	}

}
