package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.heuristic.Heuristic;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import br.usp.each.saeg.jaguar.core.utils.FileUtils;
import br.usp.each.saeg.jaguar.core.utils.ModelMapUtils;
import br.usp.each.saeg.jaguar.core.utils.OperationalSystemUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HtmlWriter {
	
	private static final Logger logger = LoggerFactory.getLogger("JaguarLogger");
	
	private static final String FOLDER_NAME = "Reports";
	private static final String HTML_FILES_FOLDER_NAME = ".html_folder";
	private static final String CSS_FILES_FOLDER_NAME = ".css_folder";
	private static final String IMG_FILES_FOLDER_NAME = ".img_folder";
	private static final String JS_FILES_FOLDER_NAME = ".js_folder";
	private static final String HTML_TYPE_FOR_FILE = ".html";
	
	
	private final List<AbstractTestRequirement> testRequirements;
	private final Heuristic currentHeuristic;
	private final Long coverageTime;
	private final Requirement.Type testRequirementType;
	
	private final HtmlBuilder htmlBuilder;
	
	public HtmlWriter(
			List<AbstractTestRequirement> testRequirements,
			Heuristic heuristic,
			Long coverageTime,
			Requirement.Type testRequirementType,
			HtmlBuilder htmlBuilder
	) {
		super();
		this.testRequirements = testRequirements;
		this.currentHeuristic = heuristic;
		this.coverageTime = coverageTime;
		this.testRequirementType = testRequirementType;
		this.htmlBuilder = htmlBuilder;
	}
	
	public void generateHtml(File projectDirectory, String outputFileName) throws IOException {
		File htmlFile = write(htmlBuilder, projectDirectory, outputFileName);
		logger.info("Output html created at: {}", htmlFile.getAbsolutePath());
	}
	
	private File write(HtmlBuilder htmlBuilder, File projectDir, String outputFileName) throws IOException {
		
		File reportsFolder = FileUtils.createOrGetFolder(projectDir.getAbsolutePath(), FOLDER_NAME);
		
		File subHtmlFolder = FileUtils.createOrGetHiddenFolder(reportsFolder.getAbsolutePath(), HTML_FILES_FOLDER_NAME);
		writeCssFiles(subHtmlFolder, CSS_FILES_FOLDER_NAME);
		
		writeImgFiles(subHtmlFolder, IMG_FILES_FOLDER_NAME);
		
		MultiValuedMap<File, AbstractTestRequirement> requirementsGroupByClassFile = ModelMapUtils
				.testRequirementsGroupByClassFile(testRequirements, projectDir.getAbsolutePath());
		
		List<File> htmlClassFilesList = new ArrayList<>(requirementsGroupByClassFile.size());
		
		for (File classFile : requirementsGroupByClassFile.keySet()) {
			htmlClassFilesList.add(
					createClassHtmlAndSubHtmls(subHtmlFolder, classFile, requirementsGroupByClassFile.get(classFile))
			);
			
			
			
		}
		
		
		// old code
		/*File baseHtml = new File(
				reportsFolder.getAbsolutePath()
						+ System.getProperty("file.separator")
						+ outputFileName + ".html");
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(baseHtml, htmlBuilder.build());
		} catch (IOException e) {
			throw new IOException("Error during transition content to html file", e);
		}*/
		
		return null;
	}
	
	private void writeImgFiles(File subHtmlFolder, String imgFilesFolderName) throws IOException {
		
		File imgFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), imgFilesFolderName);
		
		File jaguarIcon = new File("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/img/Jaguar_Icon.svg");
		File newJaguarIcon = new File(
				imgFolder.getAbsolutePath() + OperationalSystemUtils.systemFileSeparator()
						+ jaguarIcon.getName()
		);
		org.apache.commons.io.FileUtils.copyFile(jaguarIcon, newJaguarIcon);
		
	}
	
	private void writeCssFiles(File subHtmlFolder, String cssFilesFolderName) throws IOException {
		
		File cssFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), cssFilesFolderName);
		
		File stackoverflowDarkCss = new File("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/stackoverflow-dark.css");
		File newStackoverflowDarkCss = new File(
				cssFolder.getAbsolutePath() + OperationalSystemUtils.systemFileSeparator()
				+ stackoverflowDarkCss.getName()
		);
		org.apache.commons.io.FileUtils.copyFile(stackoverflowDarkCss, newStackoverflowDarkCss);
		
		File stackoverflowLightCss = new File("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/stackoverflow-light.css");
		File newStackoverflowLightCss = new File(
				cssFolder.getAbsolutePath() + OperationalSystemUtils.systemFileSeparator()
						+ stackoverflowLightCss.getName()
		);
		org.apache.commons.io.FileUtils.copyFile(stackoverflowLightCss, newStackoverflowLightCss);
		
		File styleCss = new File("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/style.css");
		File newStyleCss = new File(
				cssFolder.getAbsolutePath() + OperationalSystemUtils.systemFileSeparator()
						+ styleCss.getName()
		);
		org.apache.commons.io.FileUtils.copyFile(styleCss, newStyleCss);
	}
	
	
	private File createClassHtmlAndSubHtmls(File subHtmlFolder, File classFile, Collection<AbstractTestRequirement> abstractTestRequirements) throws IOException {
		
		List<File> htmlTesteRequirementFilesList = new ArrayList<>(abstractTestRequirements.size());
		
		File testRequirementsFolder = FileUtils.createOrGetFolder(
				subHtmlFolder.getAbsolutePath(),
				classFile.getName()
		);
		
		String javaCode = htmlBuilder.getCodeFromAbsolutePath(classFile);
		
		for (AbstractTestRequirement abstractTestRequirement : abstractTestRequirements) {
			htmlTesteRequirementFilesList.add(
					createTestRequirementHtml(javaCode, abstractTestRequirement, testRequirementsFolder)
			);
		}
		
		return null;
	}
	
	private File createTestRequirementHtml(String javaCode, AbstractTestRequirement abstractTestRequirement, File testRequirementsFolder) throws IOException {
		
		String[] nameBreakBySeparator = abstractTestRequirement.getClassName().split("\\/");
		String classNameOnly = nameBreakBySeparator[nameBreakBySeparator.length - 1];
		
		File testRequirementHmtl = new File(
				testRequirementsFolder.getAbsolutePath() + OperationalSystemUtils.systemFileSeparator()
						+ classNameOnly +"-"+ abstractTestRequirement.getMethodSignature()+"-"+ new Date().getTime() + HTML_TYPE_FOR_FILE
		);
		
		org.apache.commons.io.FileUtils.writeStringToFile(testRequirementHmtl, htmlBuilder.buildTestRequirementHtml(javaCode, abstractTestRequirement));
		
		return testRequirementHmtl;
	}
	
	
}
