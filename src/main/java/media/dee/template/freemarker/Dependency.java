package media.dee.template.freemarker;

import freemarker.template.TemplateHashModel;
import org.jsoup.nodes.Node;

import java.net.URL;

public interface Dependency extends TemplateHashModel {
    String asEmbedded();
    URL asExternal();
    Node asNode();
}
