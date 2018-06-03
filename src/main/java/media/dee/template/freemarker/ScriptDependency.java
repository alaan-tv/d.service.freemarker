package media.dee.template.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class ScriptDependency implements Dependency {
    private String code;

    public ScriptDependency(String code){
        this.code = code;
    }

    @Override
    public String asEmbedded() {
        return String.format("<style>%s</style>", this.code);
    }

    @Override
    public URL asExternal() {
        try {
            return new URL("http","localhost", 80,UUID.randomUUID().toString());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public Node asNode() {
        return new Element("script")
                .attr("type", "text/javascript")
                .appendChild(new DataNode(code, ""));
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof ScriptDependency){
            ScriptDependency other = (ScriptDependency) obj;
            return code.equals(other.code);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return this.code.hashCode();
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        switch (s){
            case "embedded":
                return new StringModel(asEmbedded(), new BeansWrapper(new Version("2.3.28")));
            case "external":
                return new StringModel(asExternal().toString(), new BeansWrapper(new Version("2.3.28")));
            default:
                return null;
        }
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }
}
