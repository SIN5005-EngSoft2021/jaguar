package br.usp.each.saeg.jaguar.core.utils;

public class OperationalSystemUtils {
	
	private static String operationalSystem = null;
	
	private static String fileSeparator = null;
	
	private OperationalSystemUtils() {
	}
	
	public static String getOsName() {
		if(operationalSystem == null) { operationalSystem = System.getProperty("os.name"); }
		return operationalSystem;
	}
	
	public static boolean isWindows()
	{
		return getOsName().startsWith("Windows");
	}
	
	public static boolean isLinux(){
		return getOsName().startsWith("Linux");
	}
	
	public static String systemFileSeparator(){
		
		if(fileSeparator == null){
			fileSeparator = System.getProperty("file.separator");
		}
		
		return fileSeparator;
	}
	
}
