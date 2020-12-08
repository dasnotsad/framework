package net.dgg.utils.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import net.dgg.utils.core.DggMapUtil;
import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.string.DggStringUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 构造URL
 * @Author Created by yan.x on 2020-02-29 .
 **/
@Setter
public class UrlBuilder {

    private final Map<String, Object> params = new LinkedHashMap<>(7);
    private final StringBuilder url = new StringBuilder();
    private String baseUrl = "";
    private final static String separator = "/";
    private final static String DOMAIN_HTTP = "http://";

    private UrlBuilder() {

    }

    /**
     * @param baseUrl 基础路径
     * @return the new {@code UrlBuilder}
     */
    public static UrlBuilder builder() {
        UrlBuilder builder = new UrlBuilder();
        return builder;
    }

    /**
     * @param baseUrl 基础路径
     * @return the new {@code UrlBuilder}
     */
    public static UrlBuilder from(String uri) {
        UrlBuilder builder = builder();
        builder.setBaseUrl(uri);
        return builder;
    }

    /**
     * @param baseUrl 基础路径
     * @return the new {@code UrlBuilder}
     */
    public static UrlBuilder fromBaseUrl(String baseUrl) {
        UrlBuilder builder = new UrlBuilder();
        builder.setBaseUrl(baseUrl);
        return builder;
    }

    /**
     * 添加参数
     *
     * @param key   参数名称
     * @param value 参数值
     * @return this UrlBuilder
     */
    public UrlBuilder uri(String uri) {
        this.appendUri(uri);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key   参数名称
     * @param value 参数值
     * @return this UrlBuilder
     */
    public UrlBuilder queryParam(String key, Object value) {
        if (DggStringUtil.isEmpty(key)) {
            throw new DggUtilException("参数名不能为空");
        }
        String valueAsString = (value != null ? value.toString() : null);
        this.params.put(key, valueAsString);
        return this;
    }

    public boolean isDomain(String uri) {
        if (uri == null) {
            return false;
        }
        return uri.contains("://");
    }

    private boolean isDomain() {
        return this.url.indexOf("://") > 0;
    }

    public void setBaseUrl(String baseUrl) {
        baseUrl = endsReplace(baseUrl.trim());
        if (!isDomain(baseUrl)) {
            baseUrl = new StringBuilder(DOMAIN_HTTP).append(startsReplace(baseUrl)).toString();
        }
        this.baseUrl = baseUrl;
    }

    /**
     * 构造url
     *
     * @return url
     */
    public String build() {
        return this.build(false);
    }

    /**
     * 构造url
     *
     * @param encode 转码
     * @return url
     */
    public String build(boolean encode) {
        if (!this.isDomain()) {
            this.url.insert(0, this.baseUrl);
        }
        String path = this.url.toString();
        if (DggMapUtil.isEmpty(this.params)) {
            return path;
        }
        String baseUrl = DggStringUtil.appendIfNotContain(path, "?", "&");
        String paramString = DggRequestUtil.parseMapToString(this.params, encode);
        return baseUrl + paramString;
    }

    private void appendUri(String uri) {
        this.url.append(buildUrl(uri));
    }

    private String buildUrl(String uri) {
        uri = endsReplace(uri.trim());
        return this.isDomain(uri) ? uri : uri.startsWith(separator) ? uri : separator + uri;
    }

    private String startsReplace(String uri) {
        return uri.startsWith(separator) ? uri.substring(1, uri.length()) : uri;
    }

    private String endsReplace(String uri) {
        return uri.endsWith(separator) ? uri.substring(0, uri.length() - 1) : uri;
    }

    public static void main(String[] args) {
        System.err.println(JSONObject.toJSONString(" http://api.weixin.qq.com/sns/oauth2/access_token ".split("/")[2]));
        UrlBuilder urlBuilder = UrlBuilder.fromBaseUrl(" https://api.weixin.qq.com/sns/oauth2/access_token ")
                .uri(" /1111 ")
                .uri(" 2222/ ")
                .uri(" /3333/ ")
                .uri("/4444/")
                .uri("/5555/")
                .queryParam("appid", "wxda1aeb08ba2d0e6c");
//                .queryParam("secret", "f961870ab9ca1c8b1f2ad01e1ab24e52")
//                .queryParam("code", "021WyWOi1MQiUs0IivMi1APSOi1WyWO0")
//                .queryParam("grant_type", "authorization_code")
        System.err.println(urlBuilder.isDomain());
        String build = urlBuilder.build(true);
        System.err.println(build);

    }
}
