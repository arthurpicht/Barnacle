package de.arthurpicht.barnacle.vofClassLoader;

import javax.tools.*;
import java.util.HashMap;
import java.util.Map;

public class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager>{
	
	protected final Map<String, MemoryFileOutput> map = new HashMap<>();

    MemoryFileManager(JavaCompiler compiler) {    	
        super(compiler.getStandardFileManager(null, null, null));
    }

    @Override
    public MemoryFileOutput getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind, FileObject source) {
        MemoryFileOutput mc = new MemoryFileOutput(name, kind);
        this.map.put(name, mc);
        return mc;
    }

}
