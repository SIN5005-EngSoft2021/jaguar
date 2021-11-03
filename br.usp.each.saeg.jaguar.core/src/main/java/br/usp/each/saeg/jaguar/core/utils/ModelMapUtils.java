package br.usp.each.saeg.jaguar.core.utils;

import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.File;
import java.util.List;

public class ModelMapUtils {
	
	private ModelMapUtils() {
	}
	
	public static MultiValuedMap<String, AbstractTestRequirement> testRequirementsGroupByClassName(List<AbstractTestRequirement> testRequirements){
		
		MultiValuedMap<String, AbstractTestRequirement> requirementsGroupByClass = new ArrayListValuedHashMap<>();
		
		for (AbstractTestRequirement requirement : testRequirements){
			requirementsGroupByClass.put(requirement.getClassName(), requirement);
		}
		
		return requirementsGroupByClass;
	}
	
	public static MultiValuedMap<File, AbstractTestRequirement> testRequirementsGroupByClassFile(List<AbstractTestRequirement> testRequirements, String directoryOfProjectBeingTested){
		
		MultiValuedMap<String, AbstractTestRequirement> requirementsGroupByClassName = testRequirementsGroupByClassName(testRequirements);
		
		MultiValuedMap<File, AbstractTestRequirement> requirementsGroupByClassFile = new ArrayListValuedHashMap<>(requirementsGroupByClassName.size());
		
		for (String className : requirementsGroupByClassName.keySet()){
			
			File classFile = new File(directoryOfProjectBeingTested
					+ System.getProperty("file.separator") + "src"
					+ System.getProperty("file.separator") + "main"
					+ System.getProperty("file.separator") + "java"
					+ System.getProperty("file.separator") + className + ".java");
			
			requirementsGroupByClassFile.putAll(classFile, requirementsGroupByClassName.get(className));
		}
		
		return requirementsGroupByClassFile;
	}
	
}
