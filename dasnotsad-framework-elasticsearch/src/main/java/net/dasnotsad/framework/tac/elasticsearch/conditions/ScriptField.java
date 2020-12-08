package net.dasnotsad.framework.tac.elasticsearch.conditions;

import org.elasticsearch.script.Script;

/**
 * @Description: 脚本
 * @Author Created by yan.x on 2020-07-22 .
 **/
public class ScriptField {

    private final String fieldName;
    private final Script script;

    public ScriptField(String fieldName, Script script) {
        this.fieldName = fieldName;
        this.script = script;
    }

    public String fieldName() {
        return fieldName;
    }

    public Script script() {
        return script;
    }
}
