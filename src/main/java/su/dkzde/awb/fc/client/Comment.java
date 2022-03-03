package su.dkzde.awb.fc.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Comment {

    private static final Pattern p_quote_link = Pattern.compile("#p(?<post>\\d+)");

    public static Comment create(String comment) {
        return new Comment(
                Objects.requireNonNull(comment),
                Jsoup.parseBodyFragment(comment));
    }

    public String text() {
        StringBuilder target = new StringBuilder();
        NodeTraversor.traverse(new PlainTextFormatter(target), document);
        return target.toString();
    }

    public List<Long> quotes() {
        ArrayList<Long> references = new ArrayList<>();
        Elements quotes = document.select(".quotelink");
        for (Element element : quotes) {
            String link = element.attr("href");
            if (link.startsWith("#p")) {
                Matcher matcher = p_quote_link.matcher(link);
                if (matcher.matches()) {
                    references.add(Long.parseLong(matcher.group("post")));
                }
            }
        }
        return references;
    }

    private final String source;
    private final Document document;

    private Comment(String source, Document document) {
        this.source = source;
        this.document = document;
    }

    private static class PlainTextFormatter implements NodeVisitor {

        private final StringBuilder target;

        private PlainTextFormatter(StringBuilder target) {
            this.target = target;
        }

        @Override
        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode t) {
                target.append(t.text());
            } else switch (name) {
                case "p" -> {
                    target.append("\n");
                }
            }
        }

        @Override
        public void tail(Node node, int depth) {
            String name = node.nodeName();
            switch (name) {
                case "br", "p" -> {
                    target.append("\n");
                }
            }
        }
    }
}
