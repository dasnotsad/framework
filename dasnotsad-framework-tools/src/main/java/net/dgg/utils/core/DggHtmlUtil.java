package net.dgg.utils.core;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Description: TODO
 * @Author Created by yan.x on 2020-09-08 .
 **/
public class DggHtmlUtil {

    private static final Pattern REG_SCRIPT = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>");
    private static final Pattern REG_STYLE = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>");
    private static final Pattern REG_HTML = Pattern.compile("<[^>]+>");

    public static String filterHTML(String html) {
        return filterHTML(html, true, true, true);
    }

    public static String filterScript(String html) {
        return filterHTML(html, true, false, false);
    }

    public static String filterStyle(String html) {
        return filterHTML(html, false, true, false);
    }

    private static String filterHTML(String html, boolean isScript, boolean isStyle, boolean isHtml) {
        Objects.requireNonNull(html, "html");

        if (isScript) {
            html = REG_SCRIPT.matcher(html).replaceAll("");
        }
        if (isStyle) {
            html = REG_STYLE.matcher(html).replaceAll("");
        }
        if (isHtml) {
            html = REG_HTML.matcher(html).replaceAll("");
        }
        return html.trim();
    }

    /**
     * html符号转义
     *
     * @param html
     * @return
     */
    public static final String escapeHTML(String html) {
        if (html == null || "".equals(html)) {
            return html;
        }
        html = html.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        html = html.replaceAll("'", "&#39;");
        html = html.replaceAll("eval\\((.*)\\)", "");
        html = html.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        html = html.replace("script", "");
        return html;
    }
}
