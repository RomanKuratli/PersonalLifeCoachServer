package ch.romankuratli.personallifecoach.server.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Map;

public class VelocityUtil {
    public static String render(String templatePath, Map<String, Object> env) {
        Template t = Velocity.getTemplate(templatePath);
        StringWriter sw = new StringWriter();
        VelocityContext ctx = new VelocityContext(env);
        t.merge(ctx, sw);
        return sw.toString();
    }
}
