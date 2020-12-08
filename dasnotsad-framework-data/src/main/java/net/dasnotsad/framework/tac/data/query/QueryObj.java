package net.dasnotsad.framework.tac.data.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dasnotsad.framework.tac.data.enums.Query;

/**
 * 单一条件类
 *
 * @author liuliwei
 * @create 2020-08-24
 */
@Data
@AllArgsConstructor
public class QueryObj {

    private Query query;

    private String column;

    private Object value;

    public static QueryObj build(Query query, String column, Object value){
        return new QueryObj(query, column, value);
    }
}
