package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.heuristic.Heuristic;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import br.usp.each.saeg.jaguar.core.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HtmlWriter {
	
	private static final Logger logger = LoggerFactory.getLogger("JaguarLogger");
	
	public static final String HTML_FILES_FOLDER_NAME = ".html_folder";
	public static final String CSS_FILES_FOLDER_NAME = ".css_folder";
	public static final String IMG_FILES_FOLDER_NAME = ".img_folder";
	public static final String JS_FILES_FOLDER_NAME = ".js_folder";
	public static final String HTML_TYPE_FOR_FILE = ".html";
	
	private final HtmlBuilder htmlBuilder;
	private final List<AbstractTestRequirement> testRequirements;
	private Requirement.Type testRequirementType;
	private final Heuristic heuristic;
	private final File projectDirectory;
	private final String outputFile;
	
	public HtmlWriter(
			HtmlBuilder htmlBuilder,
			List<AbstractTestRequirement> testRequirements,
			Requirement.Type testRequirementType,
			Heuristic heuristic,
			File projectDirectory,
			String outputFile
	) {
		this.htmlBuilder = htmlBuilder;
		this.testRequirements = testRequirements;
		this.testRequirementType = testRequirementType;
		this.heuristic = heuristic;
		this.projectDirectory = projectDirectory;
		this.outputFile = outputFile;
	}
	
	public void generateHtmlForLineType() throws IOException {
		File htmlFile = new HtmlWriterLineType(
				htmlBuilder,
				projectDirectory,
				testRequirements,
				testRequirementType,
				heuristic,
				outputFile
		).write();
		logger.info("Output html created at: {}", htmlFile.getAbsolutePath());
	}
	
	public void generateHtmlForDuaType() throws IOException {
		File htmlFile = new HtmlWriterDuaType(
				htmlBuilder,
				projectDirectory,
				testRequirements,
				testRequirementType,
				heuristic,
				outputFile
		).write();
		logger.info("Output html created at: {}", htmlFile.getAbsolutePath());
	}
	
	public static void writeImgFiles(File subHtmlFolder, String imgFilesFolderName) throws IOException {

		File imgFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), imgFilesFolderName);

		FileUtils.copyFile(imgFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/img/jaguar-icon.png");

	}
	
	public static void writeCssFiles(File subHtmlFolder, String cssFilesFolderName) throws IOException {
		
		File cssFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), cssFilesFolderName);
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/stackoverflow-light.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/style.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/test-requirement-code-template.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/test-requiremente-table-template.css");
	}

	public static void writeJsFiles(File subHtmlFolder, String jsFilesFolderName) throws IOException {

		File jsFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), jsFilesFolderName);

		FileUtils.copyFile(jsFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/js/table-controls.js");

		FileUtils.copyFile(jsFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/js/focus-code-line.js");

		FileUtils.copyFile(jsFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/js/highlight.min.js");

	}

	
	
}
