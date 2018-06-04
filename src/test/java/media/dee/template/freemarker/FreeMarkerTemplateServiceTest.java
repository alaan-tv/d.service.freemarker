package media.dee.template.freemarker;


import media.dee.dcms.core.db.GraphDatabaseService;
import media.dee.dcms.core.layout.RenderException;
import media.dee.dcms.core.services.ComponentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class FreeMarkerTemplateServiceTest {

    @Test
    void renderComponent() throws RenderException {

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


        GraphDatabaseService.GraphNode model = new MapGraphNode(100L, Collections.singleton("Post"), new HashMap<>());
        StringBuffer buffer = templateService.render(
                "<html><head><script type=\"text/javascript\">console.log('>>hello<<');</script></head><body>${Container(2)}</body></html>",
                model
        );
        assertEquals("<html>\n" +
                " <head>\n" +
                "  <script type=\"text/javascript\">console.log('>>hello<<');</script>\n" +
                "  <style>#id{ font-size: 1px; color: #122ffeee;}</style>\n" +
                "  <script type=\"text/javascript\"><script src=\"javascript1.js\"></script></script>\n" +
                " </head>\n" +
                " <body>\n" +
                "  <b> Nested child \n" +
                "   <div>\n" +
                "     Here is Component. Now try nested component.\n" +
                "   </div> </b>\n" +
                " </body>\n" +
                "</html>", buffer.toString());
    }

}