package media.dee.template.freemarker;

import freemarker.template.TemplateException;
import media.dee.dcms.core.layout.RenderException;

import java.io.IOException;

public class FreeMarkerRenderException extends RenderException {
    public FreeMarkerRenderException(TemplateException ex){
        super(ex);
    }

    public FreeMarkerRenderException(IOException ex){
        super(ex);
    }
}
