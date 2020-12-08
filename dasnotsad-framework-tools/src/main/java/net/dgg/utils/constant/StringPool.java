package net.dgg.utils.constant;

/**
 * @Description: 字符池
 * @Author Created by yan.x on 2019-04-02 .
 * @Copyright © HOT SUN group.All Rights Reserved.
 **/
public interface StringPool {
    String EQ = "=";
    String CR = "\r";
    String LF = "\n";
    String CRLF = "\r\n";
    String QUOTE = "\"";
    String UNDERLINE = "_";
    String DELIM_START = "{";
    String DELIM_END = "}";
    String BRACKET_START = "[";
    String BRACKET_END = "]";

    String AMPERSAND = "&";
    String AND = "and";
    String AT = "@";
    String ASTERISK = "*";
    String STAR = ASTERISK;
    String BACK_SLASH = "\\";
    String COLON = ":";
    String COMMA = ",";
    String DASH = "-";
    String DOLLAR = "$";
    String DOT = ".";
    String DOTDOT = "..";
    String DOT_CLASS = ".class";
    String DOT_JAVA = ".java";
    String DOT_XML = ".xml";
    String EMPTY = "";
    String EQUALS = "=";
    String FALSE = "false";
    String SLASH = "/";
    String HASH = "#";
    String HAT = "^";
    String LEFT_BRACE = "{";
    String LEFT_BRACKET = "(";
    String LEFT_CHEV = "<";
    String N = "n";
    String NO = "no";
    String NULL = "null";
    String OFF = "off";
    String ON = "on";
    String PERCENT = "%";
    String PIPE = "|";
    String PLUS = "+";
    String QUESTION_MARK = "?";
    String EXCLAMATION_MARK = "!";
    String TAB = "\t";
    String RIGHT_BRACE = "}";
    String RIGHT_BRACKET = ")";
    String RIGHT_CHEV = ">";
    String SEMICOLON = ";";
    String SINGLE_QUOTE = "'";
    String BACKTICK = "`";
    String SPACE = " ";
    String TILDA = "~";
    String LEFT_SQ_BRACKET = "[";
    String RIGHT_SQ_BRACKET = "]";
    String TRUE = "true";
    String UNDERSCORE = "_";
    String UTF_8 = "UTF-8";
    String US_ASCII = "US-ASCII";
    String ISO_8859_1 = "ISO-8859-1";
    String Y = "y";
    String YES = "yes";
    String ONE = "1";
    String ZERO = "0";
    String DOLLAR_LEFT_BRACE = "${";
    String HASH_LEFT_BRACE = "#{";
    String EMPTY_JSON = "{}";

    String HTML_NBSP = "&nbsp;";
    String HTML_AMP = "&amp";
    String HTML_QUOTE = "&quot;";
    String HTML_LT = "&lt;";
    String HTML_GT = "&gt;";

    String SIGN = "sign";

    String APPLICATION_JSON = "application/json";
    String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
    String APPLICATION_OCTET_STREAM = "application/octet-stream";
    String APPLICATION_PDF = "application/pdf";
    String APPLICATION_XML = "application/xml";
    String IMAGE_GIF = "image/gif";
    String IMAGE_JPEG = "image/jpeg";
    String IMAGE_PNG = "image/png";
    String MULTIPART_FORM_DATA = "multipart/form-data";
    String TEXT_HTML = "text/html";
    String TEXT_JSON_UTF8 = "text/json;charset=UTF-8";

    String FILE_BASE64_TYPE = "data:%s;base64,%s";
    // ---------------------------------------------------------------- array

    String[] EMPTY_ARRAY = new String[0];

    byte[] BYTES_NEW_LINE = StringPool.LF.getBytes();
}
