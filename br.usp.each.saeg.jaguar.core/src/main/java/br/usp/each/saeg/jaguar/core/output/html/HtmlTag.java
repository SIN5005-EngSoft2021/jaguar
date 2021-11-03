package br.usp.each.saeg.jaguar.core.output.html;

public class HtmlTag {
    private String id = "";
    private String tag = "";
    private String cssClass = "";
    private String innerHtml = "";
    private String siblingHtml = "";
    private String inlineStyle = "";

    private StringBuilder finalTag;

    public HtmlTag(String id, String tag, String cssClass, String siblingHtml, String innerHtml, String inlineStyle) {
        this.id = id;
        this.tag = tag;
        this.cssClass = cssClass;
        this.innerHtml = innerHtml;
        this.siblingHtml = siblingHtml;
        this.inlineStyle = inlineStyle;

        this.finalTag = new StringBuilder();
    }

    public String buildTag() {
        finalTag.append("<")
                .append(tag);

        if (!id.isEmpty()) {
            finalTag.append(" id=\"")
                    .append(id)
                    .append("\"");
        }

        if (!cssClass.isEmpty()) {
            finalTag.append(" class=\"")
                    .append(cssClass)
                    .append("\"");
        }

        if (!inlineStyle.isEmpty()) {
            finalTag.append(" style=\"")
                    .append(inlineStyle)
                    .append("\"");
        }

        finalTag.append(">");

        if (!innerHtml.isEmpty()) {
            finalTag.append(innerHtml);
        }

        finalTag.append("</")
                .append(tag)
                .append(">");

        if (!siblingHtml.isEmpty()) {
            finalTag.append(siblingHtml);
        }

        return this.finalTag.toString();
    }
}
