package br.usp.each.saeg.jaguar.core.output.html;

import br.usp.each.saeg.jaguar.core.model.core.requirement.AbstractTestRequirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.DuaTestRequirement;
import br.usp.each.saeg.jaguar.core.model.core.requirement.LineTestRequirement;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static br.usp.each.saeg.jaguar.core.output.html.HtmlDomTree.*;

public class HtmlBuilder {
	
	public String transformJavaCodeToHtml(String javaCode, Collection<AbstractTestRequirement> requirementsForThisClass) {
		List<String> rowsInJavaCode = Arrays.asList(javaCode.split("\\n"));
		
		HtmlTagBuilder olHtmlTagBuilder = new HtmlTagBuilder(OL);
		olHtmlTagBuilder.setCssClass("linenumbers");
		
		for (int rowIndex = 0; rowIndex < rowsInJavaCode.size() - 1; rowIndex++) {
			
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
				Double currentSuspiciousness =requirement.getSuspiciousness();
				String cssClass = getSuspiciousnessCssColor(currentSuspiciousness);

				codeLine = new HtmlTagBuilder(HtmlDomTree.SPAN)
						.addCssClass(cssClass)
						.setId(requirement.getUuid().toString())
						.setAriaLabel(cssClass)
						.setData("suspiciousness", String.valueOf(currentSuspiciousness))
						.setInnerHtml(new HtmlTagBuilder(ABBR)
										.setTitle("Suspiciousness of this line is " + currentSuspiciousness)
										.setInnerHtml(codeLine).build()
									)
						.build()
				;
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
			return "suspiciousness-more-than-or-equal-75";
		} else if (suspiciousness >= 0.5D) {
			return "suspiciousness-more-than-or-equal-50";
		} else if (suspiciousness >= 0.25D) {
			return "suspiciousness-more-than-or-equal-25";
		} else {
			return "suspiciousness-below-25";
		}
	}
	
	public String buildTDTagForClassList(Map<File, File> htmlFileMapByClassFile, MultiValuedMap<File, AbstractTestRequirement> requirementsGroupByClassFile) {
		
		StringBuilder allTableDatas = new StringBuilder();
		
		for (Map.Entry<File, File> classFileAndHtmlClassFile : htmlFileMapByClassFile.entrySet()) {
			allTableDatas.append(
					buildTDTagForClassFile(
							classFileAndHtmlClassFile.getValue(),
							requirementsGroupByClassFile.get(classFileAndHtmlClassFile.getKey()))
			);
		}
		
		return allTableDatas.toString();
	}
	
	public String buildTDTagForClassFile(File htmlClassFile, Collection<AbstractTestRequirement> testRequirementsForTheClass) {
		String htmlClassFileAbsolutePath = htmlClassFile.getAbsolutePath();
		
		AbstractTestRequirement abstractTestRequirementWithHigherSuspiciousness = testRequirementsForTheClass
				.stream()
				.max(Comparator.comparingDouble(AbstractTestRequirement::getSuspiciousness))
				.orElseThrow(NullPointerException::new);

		double suspValue = abstractTestRequirementWithHigherSuspiciousness.getSuspiciousness();
		
		return new HtmlTagBuilder(TR).addInnerHtml(
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(htmlClassFileAbsolutePath)
										.addInnerHtml(abstractTestRequirementWithHigherSuspiciousness.getClassName())
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD) //add bar
						.setInnerHtml(
								new HtmlTagBuilder(DIV)
										.setCssClass("suspBar")
										.addInnerHtml(
											new HtmlTagBuilder(DIV)
													.setCssClass("redBar")
													.setInlineStyle(defineSuspBarCss((int) Math.round((suspValue+0.1)*10)))
													.build()
										).build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(htmlClassFileAbsolutePath)
										.addInnerHtml(String.valueOf(suspValue))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(htmlClassFileAbsolutePath)
										.addInnerHtml(String.valueOf(abstractTestRequirementWithHigherSuspiciousness.getCef()))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(htmlClassFileAbsolutePath)
										.addInnerHtml(String.valueOf(abstractTestRequirementWithHigherSuspiciousness.getCep()))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(htmlClassFileAbsolutePath)
										.addInnerHtml(String.valueOf(abstractTestRequirementWithHigherSuspiciousness.getCnf()))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(htmlClassFileAbsolutePath)
										.addInnerHtml(String.valueOf(abstractTestRequirementWithHigherSuspiciousness.getCnp()))
										.build()
						)
						.build()
		).build();
	}
	
	public String buildTDTagForTestRequirementList(Collection<AbstractTestRequirement> abstractTestRequirementList, String linkToAnchor) {
		StringBuilder allTableDatas = new StringBuilder();
		
		for (AbstractTestRequirement abstractTestRequirement : abstractTestRequirementList) {
			allTableDatas.append(
					buildTDTagForTestRequirement(abstractTestRequirement, linkToAnchor)
			);
		}
		
		return allTableDatas.toString();
	}
	
	public String buildTDTagForTestRequirement(AbstractTestRequirement abstractTestRequirement, String linkToAnchor) {
		Integer location;
		
		if (abstractTestRequirement instanceof DuaTestRequirement) {
			DuaTestRequirement duaRequirement = (DuaTestRequirement) abstractTestRequirement;
			location = duaRequirement.getDef();
		} else {
			LineTestRequirement lineRequirement = (LineTestRequirement) abstractTestRequirement;
			location = lineRequirement.getLineNumber();
		}

		double suspValue = abstractTestRequirement.getSuspiciousness();
		return new HtmlTagBuilder(TR).addInnerHtml(
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(location.toString())
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(abstractTestRequirement.getMethodSignature())
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD) //add bar
						.setInnerHtml(
								new HtmlTagBuilder(DIV)
										.setCssClass("suspBar")
										.addInnerHtml(
												new HtmlTagBuilder(DIV)
														.setCssClass("redBar")
														.setInlineStyle(defineSuspBarCss((int) Math.round((suspValue+0.1)*10)))
														.build()
										).build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(String.valueOf(suspValue))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(String.valueOf(abstractTestRequirement.getCef()))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(String.valueOf(abstractTestRequirement.getCep()))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(String.valueOf(abstractTestRequirement.getCnf()))
										.build()
						)
						.build(),
				new HtmlTagBuilder(TD)
						.setInnerHtml(
								new HtmlTagBuilder(A)
										.setHref(linkToAnchor + "#" + abstractTestRequirement.getUuid())
										.addInnerHtml(String.valueOf(abstractTestRequirement.getCnp()))
										.build()
						)
						.build()
		).build();
	}
	
	public String getStringFromHtmlTemplate(String templatePath) throws IOException {
		File htmlTemplateFile = new File(templatePath);
		return FileUtils.readFileToString(htmlTemplateFile);
	}

	public String defineSuspBarCss(int value){
		if (value <= 1)
			return "display: none";
		return "grid-column-end: " + value;
	}
}
