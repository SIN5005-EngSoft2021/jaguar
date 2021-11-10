package br.usp.each.saeg.jaguar.core.utils;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class TestRequirementUtils {
	
	private TestRequirementUtils(){
	}
	
	public static Requirement.Type getType(Collection<AbstractTestRequirement> abstractTestRequirementCollection) {
		if (abstractTestRequirementCollection.isEmpty()){
			return null;
		}
		
		if (AbstractTestRequirement.Type.LINE == abstractTestRequirementCollection.iterator().next().getType()){
			return Requirement.Type.LINE;
		}else if(AbstractTestRequirement.Type.DUA == abstractTestRequirementCollection.iterator().next().getType()){
			return Requirement.Type.DUA;
		}
		
		return null;
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
					+ OperationalSystemUtils.systemFileSeparator() + "src"
					+ OperationalSystemUtils.systemFileSeparator() + "main"
					+ OperationalSystemUtils.systemFileSeparator() + "java"
					+ OperationalSystemUtils.systemFileSeparator() + className + ".java");
			
			requirementsGroupByClassFile.putAll(classFile, requirementsGroupByClassName.get(className));
		}
		
		return requirementsGroupByClassFile;
	}
	
}
