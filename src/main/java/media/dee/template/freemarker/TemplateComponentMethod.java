package media.dee.template.freemarker;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import media.dee.dcms.core.db.GraphDatabaseService.GraphNode;
import media.dee.dcms.core.db.Record;
import media.dee.dcms.core.services.ComponentService;
import media.dee.dcms.layout.model.Dependency;
import media.dee.dcms.layout.model.ScriptDependency;
import media.dee.dcms.layout.model.StyleDependency;

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
        Record component = componentService.findComponentById(componentID);
        GraphNode templateNode = component.get("template");
        if( component.containsKey("style") )
            dependencies.compute(DependencyType.Style, (key, value)->{
                GraphNode styleNode = component.get("style");
                String evStyle = templateService.renderFragment(styleNode.get("value"), model).toString();
                Dependency dependency = new StyleDependency(evStyle);
                if( value == null )
                    return Collections.singleton(dependency);
                Set<Dependency> rv = new HashSet<>(value);
                rv.add(dependency);
                return rv;
            });

        if( component.containsKey("script") )
            dependencies.compute(DependencyType.Script, (key, value)->{
                GraphNode scriptNode = component.get("script");
                String evScript = templateService.renderFragment(scriptNode.get("value"), model).toString();
                Dependency dependency = new ScriptDependency(evScript, scriptNode.get("injectionPoint"));
                if( value == null )
                    return Collections.singleton(dependency);
                Set<Dependency> rv = new HashSet<>(value);
                rv.add(dependency);
                return rv;
            });


        return templateService.renderFragment(templateNode.get("template"), model).toString();
    }
}
