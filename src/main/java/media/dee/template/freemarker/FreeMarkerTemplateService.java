package media.dee.template.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import media.dee.dcms.core.db.GraphDatabaseService;
import media.dee.dcms.core.layout.RenderException;
import media.dee.dcms.core.services.ComponentService;
import media.dee.dcms.core.services.TemplateService;
import media.dee.dcms.layout.model.Dependency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component(immediate = true, service = TemplateService.class, scope = ServiceScope.SINGLETON)
public class FreeMarkerTemplateService implements TemplateService {
    private static final String COMPONENT = "Component";
    private static final String CONTAINER = "Container";

    private final Configuration cfg = new Configuration(new Version("2.3.28"));
    private ComponentService componentService;

    public StringBuffer render(String html, GraphDatabaseService.GraphNode dataModel)  throws RenderException{
        Map<DependencyType, Set<Dependency>> dependencies = new HashMap<>();
        StringWriter out = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        TemplateComponentMethod resolver = new TemplateComponentMethod( dependencies,model, componentService,this);
        model.put("model", dataModel);
        model.put(COMPONENT,resolver);
        model.put(CONTAINER,resolver);

        try {
            Template template = new Template("", html, cfg);
            template.process(model,out);
            Document document = Jsoup.parse(out.getBuffer().toString());

            dependencies.values()
                    .forEach( s -> s
                            .forEach( (d)->{
                                Elements matched = document.select(d.injectSelector().getSelector());
                                Element elem = matched.isEmpty()? null : matched.first();
                                if( elem == null )
                                    return;
                                switch (d.injectSelector().getOperation()){
                                    case APPEND:
                                        elem.appendChild(d.asNode());
                                        break;
                                    case PREAPPEND:
                                        elem.prependChild(d.asNode());
                                        break;
                                    case INSERT_AFTER:
                                        elem.parent()
                                                .insertChildren(elem.elementSiblingIndex(), Collections.singleton(d.asNode()));
                                        break;
                                    case INSERT_BEFORE:
                                        elem.before(d.asNode());
                                        break;
                                }
                            })
                    );

            return new StringBuffer(document.outerHtml());

        } catch (TemplateException e) {
            throw new FreeMarkerRenderException(e);
        } catch (IOException e){
            throw new FreeMarkerRenderException(e);
        }
    }

    @Reference
    public void setComponentService(ComponentService componentService){
        this.componentService = componentService;
    }

    public StringBuffer renderFragment(String html, Map<String,Object> model) {
        StringWriter out = new StringWriter();
        try {
            Template template = new Template("", html, cfg);
            template.process(model,out);
            return out.getBuffer();

        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

