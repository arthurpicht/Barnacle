package de.arthurpicht.barnacle.vofClassLoader;

import de.arthurpicht.barnacle.exceptions.VofClassLoaderException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VofClassLoader {
	
	public static Class<?>[] getClassesFromPackage(String srcDir, String packageName) throws VofClassLoaderException {
		
		String path = packageName.replace('.', '/');
		File directory = new File(srcDir, path);
		
		if (!directory.exists()) throw new VofClassLoaderException(
				"Package directory [" + directory.getAbsolutePath() + "] does not exist.");

		List<File> javaFileList = getJavaFiles(directory);

		// Erzeuge speziellen ClassLoader, der SourceDateien kompiliert
		// und im Speicher hält.
		MemoryClassLoader memoryClassLoader = new MemoryClassLoader(javaFileList);
		
		// Beziehe alle Klassen und baue Array zur Rückgabe
		List<String> classNameSet = memoryClassLoader.getClassNames();
		
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

	private static List<File> getJavaFiles(File directory) {
		String[] files = directory.list();
		List<File> javaFileList = new ArrayList<>();
		//noinspection DataFlowIssue
		for (String file : files) {
			if (file.endsWith(".java"))
				javaFileList.add(new File(directory, file));
		}
		Collections.sort(javaFileList);
		return javaFileList;
	}

}
