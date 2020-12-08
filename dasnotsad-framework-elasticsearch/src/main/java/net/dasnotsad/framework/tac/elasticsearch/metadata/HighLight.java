package net.dasnotsad.framework.tac.elasticsearch.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 高亮对象封装
 * @Author Created by yan.x on 2020-07-22 .
 **/
public class HighLight {
    private String preTag = "";
    private String postTag = "";
    private List<String> highLightList = null;

    public HighLight() {
        highLightList = new ArrayList<>();
    }

    public HighLight field(String fieldValue) {
        highLightList.add(fieldValue);
        return this;
    }

    public List<String> getHighLightList() {
        return highLightList;
    }

    public String getPreTag() {
        return preTag;
    }

    public void setPreTag(String preTag) {
        this.preTag = preTag;
    }

    public String getPostTag() {
        return postTag;
    }

    public void setPostTag(String postTag) {
        this.postTag = postTag;
    }
}
