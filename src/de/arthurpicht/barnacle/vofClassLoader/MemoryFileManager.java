package de.arthurpicht.barnacle.vofClassLoader;

import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager>{
	
	protected final Map<String, MemoryFileOutput> map = new HashMap<String, MemoryFileOutput>();

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
