package media.dee.template.freemarker;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import media.dee.dcms.core.services.ComponentService;

import java.util.*;

public class TemplateComponentMethod implements TemplateMethodModelEx {

    private ComponentService componentService;
    private FreeMarkerTemplateService templateService;
    private Map<String,Object> model;
    private Map<DependencyType, Set<Dependency>> dependencies;


    public TemplateComponentMethod(Map<DependencyType, Set<Dependency>> dependencies, Map<String,Object> model, ComponentService componentService, FreeMarkerTemplateService templateService){
        this.dependencies = dependencies;
        this.model = model;
        this.componentService = componentService;
        this.templateService = templateService;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        long componentID = ((SimpleNumber)args.get(0)).getAsNumber().longValue();
        Map<String,Object> component = componentService.findComponentById(componentID);
        if( component.containsKey("style") )
            dependencies.compute(DependencyType.Style, (key, value)->{
                String evStyle = templateService.renderFragment((String)component.get("style"), model).toString();
                Dependency dependency = new StyleDependency(evStyle);
                if( value == null )
                    return Collections.singleton(dependency);
                Set<Dependency> rv = new HashSet<>(value);
                rv.add(dependency);
                return rv;
            });

        if( component.containsKey("script") )
            dependencies.compute(DependencyType.Script, (key, value)->{
                String evScript = templateService.renderFragment((String)component.get("script"), model).toString();
                Dependency dependency = new ScriptDependency(evScript);
                if( value == null )
                    return Collections.singleton(dependency);
                Set<Dependency> rv = new HashSet<>(value);
                rv.add(dependency);
                return rv;
            });

        return templateService.renderFragment((String)component.get("template"), model).toString();
    }
}
