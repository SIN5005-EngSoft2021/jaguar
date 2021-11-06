package br.usp.each.saeg.jaguar.core.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CodeUtils {
	
	private CodeUtils() {
	}
	
	public static String getCodeFromAbsolutePath(String absolutePath) throws IOException {
		File clazz = new File(absolutePath);
		return getCodeFromAbsolutePath(clazz);
	}
	
	public static String getCodeFromAbsolutePath(File classFile) throws IOException {
		return FileUtils.readFileToString(classFile);
	}
	
}
