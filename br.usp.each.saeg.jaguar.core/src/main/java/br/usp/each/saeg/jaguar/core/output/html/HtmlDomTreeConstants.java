package br.usp.each.saeg.jaguar.core.output.html;

public final class HtmlDomTreeConstants {
    public static class Div {
        public static final String OPEN = "<div>";
        public static final String CLOSE = "</div>";
    }

    public static class Paragraph {
        public static final String OPEN = "<p>";
        public static final String CLOSE = "</p>";
    }

    public static class Heading3 {
        public static final String OPEN = "<h3>";
        public static final String CLOSE = "</h3>";
    }

    public static class Table {
        public static class Row {
            public static final String OPEN = "<tr>";
            public static final String CLOSE = "</tr>";
        }

        public static class HeaderCell {
            public static final String OPEN = "<th>";
            public static final String CLOSE = "</th>";
        }

        public static class DataCell {
            public static final String OPEN = "<td>";
            public static final String CLOSE = "</td>";
        }
    }
}
