package net.dgg.utils.request;


import com.alibaba.fastjson.JSONObject;
import net.dgg.utils.exception.DggUtilException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Description: 请求工具类
 * @Author Created by yan.x on 2020-03-01 .
 **/
public final class DggRequestUtil {

    /**
     * 获取HttpServletRequest
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取HttpServletResponse
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

    }

    /**
     * 获取请求basePath
     */
    public static String getBasePath(HttpServletRequest request) {
        StringBuffer basePath = new StringBuffer();
        String scheme = request.getScheme();
        String domain = request.getServerName();
        int port = request.getServerPort();
        basePath.append(scheme);
        basePath.append("://");
        basePath.append(domain);
        if ("http".equalsIgnoreCase(scheme) && 80 != port) {
            basePath.append(":").append(String.valueOf(port));
        } else if ("https".equalsIgnoreCase(scheme) && port != 443) {
            basePath.append(":").append(String.valueOf(port));
        }
        return basePath.toString();
    }

    /**
     * 格式化参数
     *
     * @param key
     * @param value
     * @return
     */
    public static String formatParameter(final String key, final String value) throws UnsupportedEncodingException {
        return key + "=" + URLEncoder.encode(value, "UTF-8");
    }


    /**
     * 移除request指定参数
     */
    public static String removeParamter(HttpServletRequest request, String paramName) {
        String queryString = "";
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key.equals(paramName)) {
                continue;
            }
            if ("".equals(queryString)) {
                queryString = key + "=" + request.getParameter(key);
            } else {
                queryString += "&" + key + "=" + request.getParameter(key);
            }
        }
        return queryString;
    }

    /**
     * 获取请求参数并以JSON格式返回
     *
     * @param request
     * @return
     */
    public static JSONObject getReqParameter(HttpServletRequest request) {
        JSONObject valueMap = new JSONObject();
        try {
            Enumeration parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String parameterName = (String) parameterNames.nextElement();
                valueMap.put(parameterName, request.getParameter(parameterName));
            }
        } catch (Exception e) {
            return valueMap;
        }
        return valueMap;
    }

    /**
     * map转字符串，转换后的字符串格式为 {@code xxx=xxx&xxx=xxx}
     *
     * @param params 待转换的map
     * @param encode 是否转码
     * @return str
     */
    public static String parseMapToString(Map<String, Object> params, boolean encode) {
        List<String> paramList = new ArrayList<>();
        params.forEach((k, v) -> {
            if (ObjectUtils.allNotNull(v)) {
                String valueString = v.toString();
                paramList.add(k + "=" + (encode ? urlEncode(valueString) : valueString));
            } else {
                paramList.add(k + "=");
            }
        });
        return StringUtils.join(paramList, "&");
    }

    /**
     * unicode 转换成 中文
     *
     * @param theString
     * @return
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     * 获取IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
     *
     * @param params 需要转化的键值对集合
     * @return 字符串
     */
    public static String convertStringParamter(Map<String, Object> params) {
        try {
            if (MapUtils.isEmpty(params)) {
                return "";
            }
            StringBuffer parameterBuffer = new StringBuffer();
            Iterator iterator = params.keySet().iterator();
            String key = null, value = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (params.get(key) != null) {
                    value = String.valueOf(params.get(key));
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
            return parameterBuffer.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 参数编码
     *
     * @param name
     * @param param
     * @return
     */
    public static String toEncodedParamter(String name, Object param) {
        StringBuffer sb = new StringBuffer();
        if (null != name) {
            sb.append(name).append("=");
        }
        sb.append(urlEncode(param.toString()));
        return sb.toString();
    }


    /**
     * 编码
     *
     * @param value str
     * @return encode str
     */
    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, "UTF-8");
            return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new DggUtilException("Failed To Encode Uri", e);
        }
    }
}
