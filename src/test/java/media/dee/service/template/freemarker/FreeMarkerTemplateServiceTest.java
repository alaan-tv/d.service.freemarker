package media.dee.service.template.freemarker;


import com.sun.tools.javac.util.List;
import freemarker.template.TemplateModelException;
import media.dee.core.repository.api.ComponentRepository;
import media.dee.core.service.api.ComponentService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FreeMarkerTemplateServiceTest {

    private static final String html_component = "" +
            "<html>\n"+
            "  <head>\n"+
            "    ${Dependencies}\n"+
            "  </head>\n"+
            "  <body>\n"+
            "    This is Component example." +
            "  ${Container('1234')}"+
            "  </body>\n"+
            "</html>\n";

    private static final String nested_component = "<div> Here is Component. Now try nested component. ${Component('2222')}</div>";
    private static final String nested_child = "<b> Nested child ${Component('1111')} </b>";


    ComponentResolver componentResolver = mock(ComponentResolver.class);
    ComponentRepository componentRepository = mock(ComponentRepository.class);

    FreeMarkerTemplateService templateService = new FreeMarkerTemplateService();

    @Test
    public void renderComponent() throws TemplateModelException {
        FreeMarkerTemplateService resolver = Mockito.spy(templateService);
        doReturn(componentResolver).when(resolver).getComponentResolver(any(Set.class), any(Map.class));

        Mockito.when(componentResolver.exec(any(List.class))).thenReturn((Object) "${Compnent('TTTT')}");
        try {
            StringBuffer buffer = resolver.render("name",html_component, null);
            System.out.println(buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void renderNestedComponent() throws IOException {
        Map context = new HashMap();


        FreeMarkerTemplateService resolver = Mockito.spy(templateService);
        Map<String,Object> comp1 = new HashMap();
        comp1.put("name","comp1");
        comp1.put("template",nested_component);
        comp1.put("dependency","<script src=\"javascript1.js\"></script>");
        Mockito.when(componentRepository.findOne(eq("1234"),anyMap())).thenReturn(comp1);

        Map<String,Object> comp2 = new HashMap();
        comp2.put("name","comp2");
        comp2.put("template",nested_child);
        comp2.put("dependency","<script src=\"javascript2.js\"></script>");
        Mockito.when(componentRepository.findOne(eq("2222"),anyMap())).thenReturn(comp2);

        Map<String,Object> comp3 = new HashMap();
        comp3.put("name","comp3");
        comp3.put("template","Here!!!!!");
        comp3.put("dependency","<script src=\"javascript3.js\"></script>");
        Mockito.when(componentRepository.findOne(eq("1111"),anyMap())).thenReturn(comp3);

        resolver.setComponentRenderer(componentRepository);
        StringBuffer buffer = resolver.render("name",html_component, null);

        System.out.println(buffer);

    }

    @Test
    public void simpleTest() throws IOException {
        FreeMarkerTemplateService resolver = Mockito.spy(templateService);
        StringBuffer buffer = resolver.render("test","Hello World!",null);
        System.out.println(buffer);
    }


}