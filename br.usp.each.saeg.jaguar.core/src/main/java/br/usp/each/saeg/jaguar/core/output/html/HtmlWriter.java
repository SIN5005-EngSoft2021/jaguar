package br.usp.each.saeg.jaguar.core.output.html;

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
	
	
	private final List<AbstractTestRequirement> testRequirements;
	private final Heuristic heuristic;
	
	private final HtmlBuilder htmlBuilder;
	
	public HtmlWriter(
			List<AbstractTestRequirement> testRequirements,
			Heuristic heuristic,
			HtmlBuilder htmlBuilder
	) {
		super();
		this.testRequirements = testRequirements;
		this.heuristic = heuristic;
		this.htmlBuilder = htmlBuilder;
	}
	
	public void generateHtmlForLineType(File projectDirectory, String outputFile) throws IOException {
		File htmlFile = new HtmlWriterLineType(
				htmlBuilder,
				projectDirectory,
				testRequirements,
				heuristic,
				outputFile
		).write();
		logger.info("Output html created at: {}", htmlFile.getAbsolutePath());
	}
	
	public void generateHtmlForDuaType(File projectDirectory) throws IOException {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	public static void writeImgFiles(File subHtmlFolder, String imgFilesFolderName) throws IOException {

		File imgFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), imgFilesFolderName);

		FileUtils.copyFile(imgFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/img/jaguar-icon.svg");

	}
	
	public static void writeCssFiles(File subHtmlFolder, String cssFilesFolderName) throws IOException {
		
		File cssFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), cssFilesFolderName);
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/stackoverflow-dark.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/stackoverflow-light.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/style.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/test-requirement-type-line-code-model.css");
		
		FileUtils.copyFile(cssFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/css/test-requiremente-table-model.css");
	}

	public static void writeJsFiles(File subHtmlFolder, String jsFilesFolderName) throws IOException {

		File jsFolder = FileUtils.createOrGetFolder(subHtmlFolder.getAbsolutePath(), jsFilesFolderName);

		FileUtils.copyFile(jsFolder, "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/js/table-controls.js");

	}
	
	
}