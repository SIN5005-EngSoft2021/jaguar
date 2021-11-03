package br.usp.each.saeg.jaguar.core.output.html;

public class HtmlTagBuilder {
    private String id = "";
    private String tag = "";
    private String cssClass = "";
    private String innerHtml = "";
    private String inlineStyle = "";
    private String siblingHtml = "";

    private HtmlTagBuilder() {}

    public HtmlTagBuilder(String tag) {
        this.tag = tag;
    }

    public HtmlTagBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public HtmlTagBuilder setCssClass(String cssClass) {
        this.cssClass = cssClass;
        return this;
    }
    
    public HtmlTagBuilder addCssClass(String newCssClass) {
        this.cssClass = this.cssClass == null ?
            newCssClass : this.cssClass + " " + newCssClass;
        return this;
    }

    public HtmlTagBuilder setInnerHtml(String innerHtml) {
        this.innerHtml = innerHtml;
        return this;
    }
    
    public HtmlTagBuilder addInnerHtml(String newInnerHtml) {
        this.innerHtml = this.innerHtml == null ?
                newInnerHtml :
                this.innerHtml + newInnerHtml
        ;
        return this;
    }

    public HtmlTagBuilder setInlineStyle(String inlineStyle) {
        this.inlineStyle = inlineStyle;
        return this;
    }

    public HtmlTagBuilder setSiblingHtml(String siblingHtml) {
        this.siblingHtml = siblingHtml;
        return this;
    }

    public String build() {
        HtmlTag tag = new HtmlTag(id, this.tag, cssClass, siblingHtml, innerHtml, inlineStyle);
        return tag.buildTag();
    }
}
