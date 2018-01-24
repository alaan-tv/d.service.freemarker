package media.dee.service.template.freemarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import media.dee.core.service.api.ComponentService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentResolver implements TemplateMethodModelEx {
    ComponentService componentService;
    FreeMarkerTemplateService templateService;
    Map context;
    Set dependencies;

    public ComponentResolver(Set dependencies, Map context, ComponentService componentRenderer, FreeMarkerTemplateService templateService){
        this.dependencies = dependencies;
        this.context = context;
        this.componentService = componentRenderer;
        this.templateService = templateService;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        String componentID = String.valueOf(args.get(0));

        Map comp = componentService.getComponent(componentID,context);
        dependencies.add(comp.get("dependency"));

        try {
            return templateService.render(comp.get("name").toString(),comp.get("template").toString(),context,dependencies).toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
