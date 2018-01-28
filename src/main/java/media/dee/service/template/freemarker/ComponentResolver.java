package media.dee.service.template.freemarker;

import com.google.gson.Gson;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import media.dee.core.repository.api.ComponentRepository;
import media.dee.core.service.api.ComponentService;

import java.io.IOException;
import java.util.*;

public class ComponentResolver implements TemplateMethodModelEx {
    ComponentRepository componentRepository;
    FreeMarkerTemplateService templateService;
    Map context;
    Set dependencies;
    private static Gson gson = new Gson();

    public ComponentResolver(Set dependencies, Map context, ComponentRepository componentRepository, FreeMarkerTemplateService templateService){
        this.dependencies = dependencies;
        this.context = context;
        this.componentRepository = componentRepository;
        this.templateService = templateService;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {

        String componentID = String.valueOf(args.get(0));
        Map context = (args.size()> 1) ? gson.fromJson(String.valueOf( args.get(1)),Map.class): Collections.EMPTY_MAP;
        Map comp = componentRepository.findOne(componentID,context);

        dependencies.add(comp.get("dependency"));

        Map data = comp.get("data") != null ? (Map<String, Object>) comp.get("data") :new HashMap();

        try {
            return templateService.render(comp.get("name").toString(),comp.get("template").toString(), data,dependencies).toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
