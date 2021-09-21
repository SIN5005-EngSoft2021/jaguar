package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.Jaguar;
import br.usp.each.saeg.jaguar.core.heuristic.Heuristic;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.LineTestRequirement;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class HtmlBuilder {
	
	private static Logger logger = LoggerFactory.getLogger("JaguarLogger");
	
	private String project;
	
	private Heuristic heuristic;
	
	private Requirement.Type requirementType;
	
	private Long timeSpent;
	
	private List<AbstractTestRequirement> abstractTestRequirementList = new ArrayList<>();
	
	private File projectBeingTestedDir;
	
	public void project(String project){
		this.project = project;
	}
	
	public void heuristic(Heuristic heuristic){
		this.heuristic =  heuristic;
	}
	
	public void timeSpent(Long timeSpent){
		this.timeSpent = timeSpent;
	}
	
	public void requirementType(Requirement.Type requirementType){
		this.requirementType = requirementType;
	}
	
	public void abstractTestRequirementList(List<AbstractTestRequirement> abstractTestRequirementList){
		this.abstractTestRequirementList = abstractTestRequirementList;
	}
	
	public void projectBeingTestedDir(File projectBeingTestedDir) {
		this.projectBeingTestedDir = projectBeingTestedDir;
	}
	
	public String build() throws IOException, URISyntaxException {
		File htmlTemplateFile = new File("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/index.html");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile);
		
		htmlString = htmlString.replace("$heuristic$",
				StringUtils.upperCase(
						StringUtils.removeEndIgnoreCase(
								heuristic.getClass().getSimpleName(), "heuristic"
						)
				)
		);
		
		htmlString = htmlString.replace("$requirementType$", requirementType.toString());
		
		htmlString = htmlString.replace("$testRequirementsList$", requirementListHtml());
		
		return htmlString;
	}
	
	public String requirementListHtml() throws IOException {
		
		MultiValuedMap<String, AbstractTestRequirement> requirementsGroupByClass = new ArrayListValuedHashMap<>();
		
		for (AbstractTestRequirement requirement : abstractTestRequirementList){
			requirementsGroupByClass.put(requirement.getClassName(), requirement);
		}
		
		StringBuilder requirementHtmlList = new StringBuilder();
		requirementHtmlList.append(buildExecutionInfo());

		for (String className : requirementsGroupByClass.keySet()){
			requirementHtmlList.append("<div class=\"java-class\" id = \"java-class-").append(className).append("\">");
			requirementHtmlList.append("<h3>").append(className).append("</h3>");
			Collection<AbstractTestRequirement> requirementsForThisClass = requirementsGroupByClass.get(className);
			requirementHtmlList.append(buildHTMLTable(abstractTestRequirementList));
			requirementHtmlList.append("<div class=\"java-class-code\" id = \"java-class-code").append(className).append("\">");
			
			String codeFromClass = getCodeFromAbsolutePath(
					projectBeingTestedDir.getAbsolutePath()
							+ System.getProperty("file.separator") + "src"
							+ System.getProperty("file.separator") + "main"
							+ System.getProperty("file.separator") + "java"
							+ System.getProperty("file.separator") + className + ".java"
			);
			
			String codeFromClassTransformedForHtml = transformJavaCodeToDisplayInHtml(codeFromClass, requirementsForThisClass);
			
			requirementHtmlList.append("<pre> <code>")
					.append(codeFromClassTransformedForHtml)
					.append("</code> </pre>");
			
			requirementHtmlList.append("</div>");
			
			
			requirementHtmlList.append("</div>");
		}
		
		
		return requirementHtmlList.toString();
	}
	
	private String getCodeFromAbsolutePath(String absolutePath) throws IOException {
		File clazz = new File(absolutePath);
		return FileUtils.readFileToString(clazz );
	}
	
	private String transformJavaCodeToDisplayInHtml(String javaCode, Collection<AbstractTestRequirement> requirementsForThisClass){
		
		StringBuilder codeFromClassTransformedForHtml = new StringBuilder();
		
		List<String> rowsInJavaCode = Arrays.asList(javaCode.split("\\n"));
		
		for(int rowIndex = 0; rowIndex < rowsInJavaCode.size(); rowIndex++){
			
			final int rowIndexfinal = rowIndex;
			
			String codeLine = rowsInJavaCode.get(rowIndex);
			
			Optional<AbstractTestRequirement> optionalAbstractTestRequirementForThisLine = requirementsForThisClass.stream().filter(abstractTestRequirement -> {
				if(abstractTestRequirement instanceof LineTestRequirement){
					LineTestRequirement lineTestRequirement = (LineTestRequirement) abstractTestRequirement;
					return Objects.equals(lineTestRequirement.getLineNumber(), rowIndexfinal);
				}else {
					return false;
				}
			}).findFirst();
			
			if(optionalAbstractTestRequirementForThisLine.isPresent()){
				
				AbstractTestRequirement requirement = optionalAbstractTestRequirementForThisLine.get();
				
				codeLine = "<span class=\""+getSuspiciousnessCssColor(requirement.getSuspiciousness())+"\">"
						+ codeLine
						+ "</span>"
						;
			}
			
			codeFromClassTransformedForHtml.append(codeLine + "<br/>");
			
		}
		
		
		return codeFromClassTransformedForHtml.toString();
	}

	public String getSuspiciousnessCssColor(double suspiciousness){
		return "red";
		
	}

	public String buildHTMLTable(List<AbstractTestRequirement> requirementsForThisClass) {
		StringBuilder tableLines = new StringBuilder();
		for (AbstractTestRequirement currentRequirement : requirementsForThisClass) {
			tableLines.append(buildTableRowForLineTestRequirement(currentRequirement));
		}

		return "<table style=\"border:1px solid black;\">" +
					buildTableHeader() +
					tableLines +
				"</table>";
	}

	public String buildTableHeader() {
		return "<tr>" +
				"<th>" + "Element" + "</th>" +
				"<th>" + "CEF" + "</th>" +
				"<th>" + "CEP" + "</th>" +
				"<th>" + "CNF" + "</th>" +
				"<th>" + "CNP" + "</th>" +
				"</tr>";
	}

	public String buildTableRowForLineTestRequirement(AbstractTestRequirement atr) {
		return 	"<tr>" +
				"<td style=\"border:1px solid black;\">" + atr.getMethodLine() + "</td>" +
				"<td style=\"border:1px solid black;\">" + atr.getCef() + "</td>" +
				"<td style=\"border:1px solid black;\">" + atr.getCep() + "</td>" +
				"<td style=\"border:1px solid black;\">" + atr.getCnf() + "</td>" +
				"<td style=\"border:1px solid black;\">" + atr.getCnp() + "</td>" +
				"</tr>";
	}

	public StringBuilder buildExecutionInfo() {
		StringBuilder strExecutionInfo = new StringBuilder();
		return strExecutionInfo.append("<p>Executed Tests: ")
				.append(Jaguar.getnTests())
				.append("</p>")
				.append("<p>Failed Tests: ")
				.append(Jaguar.getnTestsFailed())
				.append("</p>");
	}
}
