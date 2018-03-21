package de.arthurpicht.barnacle.helper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class Helper {

	@Deprecated
	public static Class[] getClassesFromPackage(String pckgname)
	throws ClassNotFoundException {
		ArrayList<Class> classes = new ArrayList<Class>();
//		Get a File object for the package
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be a valid package");
		}
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}
	
	/**
	 * Returns package name derived from given canonical class name.
	 * 
	 * @param canonicalClassName
	 * @return
	 */
	public static String getPackageNameFromCanonicalClassName(String canonicalClassName) {
		
		int lastSeperatorIndex = canonicalClassName.lastIndexOf('.');
		if (lastSeperatorIndex < 0) return new String();
		return canonicalClassName.substring(0, lastSeperatorIndex);
	}
	
	/**
	 * Returns simple class name for passed canonical class name. 
	 * 
	 * @param canonicalClassName
	 * @return
	 */
	public static String getSimpleClassNameFromCanonicalClassName(String canonicalClassName) {
		
		int lastSeparatorIndex = canonicalClassName.lastIndexOf('.');
		if (lastSeparatorIndex < 0) return canonicalClassName;
		return canonicalClassName.substring(lastSeparatorIndex + 1, canonicalClassName.length());
	}
	
	/**
	 * Sets first letter of passed string to upper case.
	 * Returns empty string if passed string is empty.
	 * 
	 * @param string
	 * @return
	 */
	public static String shiftCaseFirstLetter(String string) {
		if (string.equals("")) {
			return "";
		}
		String shiftedString = string.substring(0, 1).toUpperCase();
		if (string.length() == 1) {
			return shiftedString;
		}
		shiftedString += string.substring(1, string.length());
		return shiftedString;
	}
	
}
