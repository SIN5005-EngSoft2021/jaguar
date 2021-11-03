package br.usp.each.saeg.jaguar.core.utils;

public class OperationalSystemUtils {
	
	private static String OperationalSystem = null;
	
	private OperationalSystemUtils() {
	}
	
	public static String getOsName() {
		if(OperationalSystem == null) { OperationalSystem = System.getProperty("os.name"); }
		return OperationalSystem;
	}
	
	public static boolean isWindows()
	{
		return getOsName().startsWith("Windows");
	}
	
	public static boolean isLinux(){
		return getOsName().startsWith("Linux");
	}
	
	public static String systemFileSeparator(){
		return System.getProperty("file.separator");
	}
	
}
