package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.Jaguar;
import br.usp.each.saeg.jaguar.core.heuristic.Heuristic;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.DuaTestRequirement;
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
	
	public String build() throws IOException {
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
			requirementHtmlList.append("<div class=\"java-class\" id = \"java-class-")
					.append(className).append("\">");

			requirementHtmlList.append("<h3>").append(className).append("</h3>");

			requirementHtmlList.append("<div style=\"overflow-x:auto;\">")
					.append(buildHTMLTable(abstractTestRequirementList))
					.append("<div class=\"java-class-code\" id = \"java-class-code")
					.append(className).append("\">");
			
			String codeFromClass = getCodeFromAbsolutePath(
					projectBeingTestedDir.getAbsolutePath()
							+ System.getProperty("file.separator") + "src"
							+ System.getProperty("file.separator") + "main"
							+ System.getProperty("file.separator") + "java"
							+ System.getProperty("file.separator") + className + ".java"
			);

			Collection<AbstractTestRequirement> requirementsForThisClass = requirementsGroupByClass.get(className);
			String codeFromClassTransformedForHtml = transformJavaCodeToDisplayInHtml(codeFromClass, requirementsForThisClass);
			
			requirementHtmlList.append("<pre><code class=\"language-java\">")
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

		codeFromClassTransformedForHtml.append("<ol class=\"linenumbers\">");
		
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

			codeFromClassTransformedForHtml.append("<li>")
					.append(codeLine).append(System.lineSeparator())
					.append("</li>");
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

		return wrapData(new String[] {buildTableHeader(), tableLines.toString()}, "table");
	}

	public String wrapData(String[] data, String tag) {
		StringBuilder wrappedData = new StringBuilder();
		boolean enclosed = false;
		String open = "<p>";
		String close = "</p>";
		String begin = null;
		String end = null;

		if (tag.equals("th")){
			open = "<th>";
			close = "</th>";
			enclosed = true;
			begin = "<tr>";
			end = "</tr>";
		} else if ( tag.equals("td")) {
			open = "<td>";
			close = "</td>";
			enclosed = true;
			begin = "<tr>";
			end = "</tr>";
		} else if ( tag.equals("table")) {
			enclosed = true;
			begin = "<table>";
			end = "</table>";
			open = "<tr>";
			close = "</tr>";
		}

		for( String line: data) {
			if (line == null) line ="";
			wrappedData.append(open).append(line).append(close);
		}

		if (enclosed)
			return begin + wrappedData + end;

		return wrappedData.toString();
	}

	public String buildTableHeader() {
		String[] rowData = new String[]{"Element", "Class", "Location", "CEF", "CEP", "CNF",
									"CNP", "Suspiciouness"};
		return wrapData(rowData, "th");
	}

	public String buildTableRowForLineTestRequirement(AbstractTestRequirement atr) {
		Integer location = null;
		if (atr instanceof DuaTestRequirement) {
			DuaTestRequirement duaRequirement = (DuaTestRequirement) atr;
			location = duaRequirement.getDef();
		} else if (atr instanceof LineTestRequirement){
			LineTestRequirement lineRequirement = (LineTestRequirement) atr;
			location = lineRequirement.getLineNumber();
		}

		String[] rowData = new String[]{atr.getClassName(), atr.getMethodSignature(),
				location.toString(), String.valueOf(atr.getCef()),
				String.valueOf(atr.getCep()), String.valueOf(atr.getCnf()),
				String.valueOf(atr.getCnp()), String.valueOf(atr.getSuspiciousness())};

		return wrapData(rowData, "td");
	}

	public StringBuilder buildExecutionInfo() {
		StringBuilder strExecutionInfo = new StringBuilder();
		int total = Jaguar.getnTests();
		int failed = Jaguar.getnTestsFailed();
		int success = total - failed;

		return strExecutionInfo.append("<p>Executed Tests: ")
				.append(total).append("</p>")
				.append("<p> Successful Tests: <span class = 'test-success'>")
				.append(success)
				.append("</span></p><p>Failed Tests:")
				.append("<span class = 'test-fail'>")
				.append(failed)
				.append("</span></p>");
	}
}
