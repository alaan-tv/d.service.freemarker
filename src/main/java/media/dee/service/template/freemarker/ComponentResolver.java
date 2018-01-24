package media.dee.service.template.freemarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import media.dee.core.repository.api.ComponentRepository;
import media.dee.core.service.api.ComponentService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentResolver implements TemplateMethodModelEx {
    ComponentRepository componentRepository;
    FreeMarkerTemplateService templateService;
    Map context;
    Set dependencies;

    public ComponentResolver(Set dependencies, Map context, ComponentRepository componentRepository, FreeMarkerTemplateService templateService){
        this.dependencies = dependencies;
        this.context = context;
        this.componentRepository = componentRepository;
        this.templateService = templateService;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        String componentID = String.valueOf(args.get(0));

        Map comp = componentRepository.findOne(componentID,context);
        dependencies.add(comp.get("dependency"));

        try {
            return templateService.render(comp.get("name").toString(),comp.get("template").toString(),context,dependencies).toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
