/**
 * FileName: PageNoGroup
 * Author:   tumq
 * Date:     2018/12/19 15:35
 * Description: 页码对
 */
package net.dasnotsad.framework.tac.elasticsearch.core.page.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 〈页码对〉
 *
 * @author tumq
 * @create 2018/12/19
 */
@Data
@AllArgsConstructor
public class PageNoGroup {
    private int inputPageNo;
    private int startPageNo;
    private int endPageNo;

    public boolean onlyHasStartPageNoOfClosest() {
        return (startPageNo != inputPageNo && startPageNo > 0) && (endPageNo == inputPageNo || endPageNo <= 0);
    }

    public boolean onlyHasEndPageNoOfClosest() {
        return (inputPageNo != endPageNo && endPageNo > 0) && (startPageNo == inputPageNo || startPageNo <= 0);
    }
}