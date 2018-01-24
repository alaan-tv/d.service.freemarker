package media.dee.service.template.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import media.dee.core.service.api.ComponentService;
import media.dee.core.service.api.TemplateService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component(immediate = true, service = TemplateService.class)
public class FreeMarkerTemplateService implements TemplateService {
    private static final String DEPENDENCY = "##INSERT_DEPENDENCIES_HERE##";
    private static final String COMPONENT = "Component";
    private static final String CONTAINER = "Container";

    private static Configuration cfg = null;

    static{
        cfg = new Configuration(new Version("2.3.0"));
    }

    private ComponentService componentRenderer;

    public StringBuffer render(String name, String html, Map<String,Object> dataModel) throws IOException {
        if(dataModel == null)
            dataModel = new HashMap();

        Set<String> dependencies = new HashSet<>();
        String output = this.render(name, html, dataModel, dependencies);
        output = output.replace(DEPENDENCY,dependencies.stream().collect(Collectors.joining("\n")));
        return new StringBuffer(output);

    }

    public String render(String name, String html, Map<String, Object> dataModel, Set dependencies) throws IOException {
        StringWriter out = new StringWriter();

        ComponentResolver resolver = getComponentResolver( dependencies,dataModel);
        dataModel.put(COMPONENT,resolver);
        dataModel.put(CONTAINER,resolver);

        try {
            Template template = new Template(name, html, cfg);
            template.process(dataModel,out);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return out.toString();
    }


    @Reference
    public void setComponentRenderer(ComponentService componentRenderer){
        this.componentRenderer = componentRenderer;
    }


    public ComponentResolver getComponentResolver(Set<String> dependencies, Map<String,Object> dataModel){

        return  new ComponentResolver( dependencies,dataModel, componentRenderer,this);
    }
}

