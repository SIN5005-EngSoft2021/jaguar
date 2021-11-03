package br.usp.each.saeg.jaguar.core.utils;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;

import java.util.Collection;

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
	
}
