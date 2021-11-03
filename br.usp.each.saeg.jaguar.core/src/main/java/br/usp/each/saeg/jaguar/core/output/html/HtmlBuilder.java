package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.core.Jaguar;
import br.usp.each.saeg.jaguar.core.heuristic.Heuristic;
import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.DuaTestRequirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.LineTestRequirement;
import br.usp.each.saeg.jaguar.core.utils.ModelMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static br.usp.each.saeg.jaguar.core.output.html.HtmlDomTree.*;

public class HtmlBuilder {
	
	private static final String TEST_REQUIREMENT_HTML_TEMPLATE_PATH = "br.usp.each.saeg.jaguar.core/src/main/resources/html-output/html/test-requirement-model.html";
	
	private Heuristic heuristic;
	
	private Requirement.Type requirementType;
	
	private Long timeSpent;
	
	private List<AbstractTestRequirement> abstractTestRequirementList = new ArrayList<>();
	
	private File projectBeingTestedDir;
	
	public HtmlBuilder(Heuristic heuristic, Requirement.Type requirementType, Long timeSpent, List<AbstractTestRequirement> abstractTestRequirementList, File projectBeingTestedDir) {
		this.heuristic = heuristic;
		this.requirementType = requirementType;
		this.timeSpent = timeSpent;
		this.abstractTestRequirementList = abstractTestRequirementList;
		this.projectBeingTestedDir = projectBeingTestedDir;
	}
	
	public void heuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
	}
	
	public void timeSpent(Long timeSpent) {
		this.timeSpent = timeSpent;
	}
	
	public void requirementType(Requirement.Type requirementType) {
		this.requirementType = requirementType;
	}
	
	public void abstractTestRequirementList(List<AbstractTestRequirement> abstractTestRequirementList) {
		this.abstractTestRequirementList = abstractTestRequirementList;
	}
	
	public void projectBeingTestedDirectory(File projectBeingTestedDir) {
		this.projectBeingTestedDir = projectBeingTestedDir;
	}
	
	public String build() throws IOException {
		File htmlTemplateFile = new File("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/index.html");
		String htmlString = getStringFromHtmlTemplate("br.usp.each.saeg.jaguar.core/src/main/resources/html-output/index.html");
		
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
		
		MultiValuedMap<String, AbstractTestRequirement> requirementsGroupByClass = ModelMapUtils.testRequirementsGroupByClassName(abstractTestRequirementList);
		
		StringBuilder requirementHtmlList = new StringBuilder();
		
		for (String className : requirementsGroupByClass.keySet()) {
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
			String codeFromClassTransformedForHtml = transformJavaCodeToHtml(codeFromClass, requirementsForThisClass);
			
			
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
	
	public String getCodeFromAbsolutePath(String absolutePath) throws IOException {
		File clazz = new File(absolutePath);
		return getCodeFromAbsolutePath(clazz);
	}
	
	public String getCodeFromAbsolutePath(File classFile) throws IOException {
		return FileUtils.readFileToString(classFile);
	}
	
	private String transformJavaCodeToHtml(String javaCode, AbstractTestRequirement abstractTestRequirement) {
		return transformJavaCodeToHtml(javaCode, Collections.singleton(abstractTestRequirement));
	}
	
	private String transformJavaCodeToHtml(String javaCode, Collection<AbstractTestRequirement> requirementsForThisClass) {
		List<String> rowsInJavaCode = Arrays.asList(javaCode.split("\\n"));
		
		HtmlTagBuilder olHtmlTagBuilder = new HtmlTagBuilder(OL);
		olHtmlTagBuilder.setCssClass("linenumbers");
		
		for (int rowIndex = 0; rowIndex < rowsInJavaCode.size() - 1 ; rowIndex++) {
			
			final int rowIndexFinal = rowIndex /* To use in lambda need to be final */;
			
			String codeLine = rowsInJavaCode.get(rowIndex);
			
			Optional<AbstractTestRequirement> optionalTestRequirementWithMostHighSuspeciosForThisLine = requirementsForThisClass.stream()
					.filter(abstractTestRequirement -> {
						if (abstractTestRequirement instanceof LineTestRequirement) {
							LineTestRequirement lineTestRequirement = (LineTestRequirement) abstractTestRequirement;
							return Objects.equals(lineTestRequirement.getLineNumber(), rowIndexFinal + 1);
						} else {
							return false;
						}
					}).max(Comparator.comparingDouble(AbstractTestRequirement::getSuspiciousness));
			
			if (optionalTestRequirementWithMostHighSuspeciosForThisLine.isPresent()) {
				
				AbstractTestRequirement requirement = optionalTestRequirementWithMostHighSuspeciosForThisLine.get();
				
				HtmlTagBuilder codeLineHtmlTagBuilder = new HtmlTagBuilder(HtmlDomTree.SPAN)
						.addCssClass(getSuspiciousnessCssColor(requirement.getSuspiciousness()))
						.setInnerHtml(codeLine)
						;
				
				if(requirementsForThisClass.size() == 1){
					codeLineHtmlTagBuilder.addCssClass("focus-item");
				}
				
				codeLine = codeLineHtmlTagBuilder.build();
			}
			
			String newListItem = new HtmlTagBuilder(HtmlDomTree.LI)
					.setInnerHtml(codeLine)
					.setSiblingHtml(System.lineSeparator())
					.build();
			
			olHtmlTagBuilder.addInnerHtml(newListItem);
		}
		
		
		return new HtmlTagBuilder(PRE)
				.setInnerHtml(
						new HtmlTagBuilder(CODE)
								.setCssClass("language-java")
								.setInnerHtml(olHtmlTagBuilder.build())
								.build()
				).build();
	}
	
	public String getSuspiciousnessCssColor(double suspiciousness) {
		if (suspiciousness >= 0.75D) {
			return "red";
		} else if (suspiciousness >= 0.5D) {
			return "orange";
		} else if (suspiciousness >= 0.25D) {
			return "yellow";
		} else {
			return "green";
		}
	}
	
	public String buildHTMLTable(List<AbstractTestRequirement> requirementsForThisClass) {
		StringBuilder tableLines = new StringBuilder();
		for (AbstractTestRequirement currentRequirement : requirementsForThisClass) {
			tableLines.append(buildTableRowForLineTestRequirement(currentRequirement));
		}
		
		return wrapData(new String[]{buildTableHeader(), tableLines.toString()}, "table");
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
	
	public String buildTestRequirementHtml(String javaCode, AbstractTestRequirement abstractTestRequirement) throws IOException {
		String htmlTemplateForTestRequirement = getStringFromHtmlTemplate(TEST_REQUIREMENT_HTML_TEMPLATE_PATH);
		
		htmlTemplateForTestRequirement = htmlTemplateForTestRequirement.replace("$javaCode$", transformJavaCodeToHtml(javaCode, abstractTestRequirement));
		
		return htmlTemplateForTestRequirement;
	}
	
	public String getStringFromHtmlTemplate(String templatePath) throws IOException {
		File htmlTemplateFile = new File(templatePath);
		return FileUtils.readFileToString(htmlTemplateFile);
	}
}
