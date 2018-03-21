package de.arthurpicht.barnacle.vofClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.arthurpicht.barnacle.exceptions.VofClassLoaderException;

/**
 * 
 * 
 * @author Arthur Picht, Arthur Picht GmbH, 5.12.2012
 *
 */
public class VofClassLoader {
	
	public static Class<?>[] getClassesFromPackage(String srcDir, String pckgname) throws VofClassLoaderException {
		
		String path = pckgname.replace('.', '/');
		File directory = new File(srcDir, path);
		
		if (!directory.exists()) throw new VofClassLoaderException("Package directory " + directory.getAbsolutePath() + " does not exist.");
		
		// Erzeuge Liste von java-files im übergebenen Package.
		String[] files = directory.list();
		List<File> javaFileList = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			// we are only interested in .java files
			if (files[i].endsWith(".java")) {			
				
//				System.out.println("gefunden: " + files[i]);
				
				javaFileList.add(new File(directory, files[i]));
			}
		}

		// Erzeuge speziellen ClassLoader, der SourceDateien kompiliert
		// und im Speicher hält.
		MemoryClassLoader memoryClassLoader = new MemoryClassLoader(javaFileList);
		
		// Beziehe alle Klassen und baue Array zur Rückgabe
		Set<String> classNameSet = memoryClassLoader.getClassNames();
		
		Class<?>[] classArray = new Class<?>[classNameSet.size()];
		
		try {
			int i=0;
			for (String className : classNameSet) {
				Class<?> curClass = memoryClassLoader.findClass(className);
				classArray[i] = curClass;
				i++;
			}
		} catch (ClassNotFoundException e) {
			throw new VofClassLoaderException("Internal Error! " + e.getMessage(), e);
		}
		
		return classArray;
	}

}
