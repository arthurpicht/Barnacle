package de.arthurpicht.barnacle.vofClassLoader;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.net.URI;

public class MemoryFileOutput extends SimpleJavaFileObject {

	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    MemoryFileOutput(String name, Kind kind) {
        super(URI.create("memo:///" + name.replace('.', '/') + kind.extension), kind);
    }

    byte[] toByteArray() {
        return this.baos.toByteArray();
    }

    @Override
    public ByteArrayOutputStream openOutputStream() {
        return this.baos;
    }
	
}
