package de.arthurpicht.barnacle.vofClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemoryClassLoader extends ClassLoader {
	
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private final MemoryFileManager manager = new MemoryFileManager(this.compiler);

    public MemoryClassLoader(List<File> classFiles) {
    	
    	// StandardJavaFileManager aus dem Compiler bezogen, 
    	// nur um Source-Dateien zu referenzieren.
    	StandardJavaFileManager managerJavaSourceFiles = compiler.getStandardFileManager(null, null, null);
    	Iterable<? extends JavaFileObject> units = managerJavaSourceFiles.getJavaFileObjectsFromFiles(classFiles);
    	
    	// Aufruf des Compilers unter Verwendung des eigenen
    	// MemoryFileManagers um class-Files nicht speichern zu müssen.
        this.compiler.getTask(null, this.manager, null, null, null, units).call();
        
        // DEBUG
//        System.out.println("Anzahl der enthaltenen Klassen: " + this.manager.map.size());
//        for (String className : this.manager.map.keySet()) {
//        	System.out.print(className + " ");
//        	if (this.manager.map.get(className).toByteArray().length > 0) {
//        		System.out.println("OK");
//        	} else {
//        		System.out.println("NULL");
//        	}
//        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        synchronized (this.manager) {
            MemoryFileOutput mc = this.manager.map.remove(name);
            if (mc != null) {
                byte[] array = mc.toByteArray();
                return defineClass(name, array, 0, array.length);
            }
        }
        return super.findClass(name);
    }
    
    /**
     * Liefert alle enthaltenen Klassenbezeichnungen.
     * 
     * @return
     */
    public Set<String> getClassNames() {
    	
    	// Deep Clone des Keyset erstellen, um java.util.ConcurrentModificationException zu vermeiden.
    	Set<String> classNameSet = new HashSet<>();
    	for (String className : this.manager.map.keySet()) {
    		classNameSet.add(className);
    	}
    	
    	return classNameSet;
    }

}
