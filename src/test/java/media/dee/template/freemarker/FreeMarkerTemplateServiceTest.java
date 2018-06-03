package media.dee.template.freemarker;


import freemarker.template.TemplateModelException;
import media.dee.dcms.core.services.ComponentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class FreeMarkerTemplateServiceTest {

    @Test
    void renderComponent() throws TemplateModelException {

        FreeMarkerTemplateService templateService = spy(FreeMarkerTemplateService.class);
        ComponentService componentService = mock(ComponentService.class);
        templateService.setComponentService(componentService);


        Map<String,Object> comp1 = new HashMap<>();
        comp1.put("name","comp1");
        comp1.put("template","<div> Here is Component. Now try nested component.</div>");
        comp1.put("script","<script src=\"javascript1.js\"></script>");

        Map<String,Object> comp2 = new HashMap<>();
        comp2.put("name","comp2");
        comp2.put("template","<b> Nested child ${Component(1)} </b>");
        comp2.put("style","#id{ font-size: 1px; color: #122ffeee;}");
        Mockito.when(componentService.findComponentById(any(Long.class))).thenAnswer( i -> {
            if( i.getArgumentAt(0, Long.class) == 1L)
                return comp1;
            else return comp2;
        });


        HashMap<String, Object> model = new HashMap<>();
        model.put("@id", 0L);
        StringBuffer buffer = templateService.render(
                "<html><head><script type=\"text/javascript\">console.log('>>hello<<');</script></head><body>${Container(2)}</body></html>",
                model
        );
        assertEquals("<html><head><script type=\"text/javascript\">console.log('>>hello<<');</script></head><body><b> Nested child <div> Here is Component. Now try nested component.</div> </b></body></html>", buffer.toString());
    }

}