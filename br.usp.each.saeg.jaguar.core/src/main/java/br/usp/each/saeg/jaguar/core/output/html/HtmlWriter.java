package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.heuristic.Heuristic;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlWriter {
	
	private static final Logger logger = LoggerFactory.getLogger("JaguarLogger");
	private static final String FOLDER_NAME = "Reports";
	
	private final List<AbstractTestRequirement> testRequirements;
	private final Heuristic currentHeuristic;
	private final Long coverageTime;
	
	public HtmlWriter(List<AbstractTestRequirement> testRequirements, Heuristic heuristic, Long coverageTime) {
		super();
		this.testRequirements = testRequirements;
		this.currentHeuristic = heuristic;
		this.coverageTime =  coverageTime;
	}
	
	public void generateHtml(File projectDir, String outputFileName) throws Exception {
		HtmlBuilder htmlBuilder = createHtmlBuilder(projectDir);
		File htmlFile = write(htmlBuilder, projectDir, outputFileName);
		logger.info("Output html created at: {}", htmlFile.getAbsolutePath());
	}
	
	private File write(HtmlBuilder htmlBuilder, File projectDir, String outputFileName) throws Exception {
		projectDir = new File(projectDir.getPath() + System.getProperty("file.separator") + FOLDER_NAME);
		if (!projectDir.exists()){
			projectDir.mkdirs();
		}
		
		File htmlFile = new File(projectDir.getAbsolutePath() + System.getProperty("file.separator") + outputFileName + ".html");
		try {
			FileUtils.writeStringToFile(htmlFile, htmlBuilder.build());
		} catch (IOException e) {
			throw new IOException("Error during transition content to html file", e);
		}
		
		return htmlFile;
	}
	
	private HtmlBuilder createHtmlBuilder(File projectDir) {
		
		HtmlBuilder htmlBuilder = new HtmlBuilder();
		htmlBuilder.project("fault localization");
		htmlBuilder.heuristic(currentHeuristic);
		htmlBuilder.timeSpent(coverageTime);
		htmlBuilder.requirementType(getType());
		htmlBuilder.abstractTestRequirementList(testRequirements);
		htmlBuilder.projectBeingTestedDir(projectDir);
		return htmlBuilder;
		
	}
	
	private Requirement.Type getType() {
		if (testRequirements.isEmpty()){
			return null;
		}
		
		if (AbstractTestRequirement.Type.LINE == testRequirements.iterator().next().getType()){
			return Requirement.Type.LINE;
		}else if(AbstractTestRequirement.Type.DUA == testRequirements.iterator().next().getType()){
			return Requirement.Type.DUA;
		}
		
		return null;
	}
}
