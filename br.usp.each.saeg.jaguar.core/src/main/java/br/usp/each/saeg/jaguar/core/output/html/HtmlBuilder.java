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
import java.util.*;

import static br.usp.each.saeg.jaguar.core.output.html.HtmlDomTree.*;

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

		int[] testsData = getGeneralTestsNumbers();
		htmlString = htmlString.replace("$numberOfTests$", String.valueOf(testsData[0]));
		htmlString = htmlString.replace("$numFailTests$", String.valueOf(testsData[1]));


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

		for (String className : requirementsGroupByClass.keySet()){
			requirementHtmlList.append("<div class=\"java-class\" id = \"java-class-")
					.append(className).append("\">");

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


			String htmlCode = new HtmlTagBuilder(HtmlDomTree.CODE)
									.setCssClass("language-java")
									.setInnerHtml(codeFromClassTransformedForHtml)
									.build();

			String preCode = new HtmlTagBuilder(HtmlDomTree.PRE)
									.setInnerHtml(htmlCode)
									.build();

			requirementHtmlList.append(preCode);

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

				codeLine= new HtmlTagBuilder(HtmlDomTree.SPAN)
								.setCssClass(getSuspiciousnessCssColor(requirement.getSuspiciousness()))
								.setInnerHtml(codeLine)
								.build();
			}

			String newListItem = new HtmlTagBuilder(HtmlDomTree.LI)
										.setInnerHtml(codeLine)
										.setSiblingHtml(System.lineSeparator())
										.build();

			codeFromClassTransformedForHtml.append(newListItem);
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
		boolean isEnclosed = false;
		String openTag = P;
		String beginTag = null;

		if (isHeaderCell(tag)) {
			isEnclosed = true;
			openTag = TH;
			beginTag = TR;
		} else if (isDataCell(tag)) {
			isEnclosed = true;
			openTag = TD;
			beginTag = TR;
		} else if (isTable(tag)) {
			isEnclosed = true;
			openTag = TD;
			beginTag = TABLE;
		}

		StringBuilder wrappedData = buildWrappedData(data, openTag);
		if (isEnclosed) {
			return new HtmlTagBuilder(beginTag)
							.setInnerHtml(wrappedData.toString())
							.build();
		}

		return wrappedData.toString();
	}

	public String buildTableHeader() {
		String[] rowData = new String[]{"Class", "Method", "Location", "CEF", "CEP", "CNF",
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

	public int[] getGeneralTestsNumbers() {
		int[] temp = new int[]{0,0};
		if(Jaguar.getnTests() > 0) temp[0] = Jaguar.getnTests();
		if(Jaguar.getnTestsFailed() > 0) temp[1] = Jaguar.getnTestsFailed();
		return temp;
	}

	private StringBuilder buildWrappedData(String[] data, String openTag) {
		StringBuilder wrappedData = new StringBuilder();

		for (String line : data) {
			if (line == null)
				line = "";

			String currentWrappedData = new HtmlTagBuilder(openTag)
					.setInnerHtml(line)
					.build();

			wrappedData.append(currentWrappedData);
		}

		return wrappedData;
	}

}
