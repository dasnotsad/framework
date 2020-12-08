package net.dasnotsad.framework.tac.elasticsearch.core.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.search.SearchHit;

import com.alibaba.fastjson.JSON;

/**
 * @Author: tumq
 * @Date: 2018/12/18
 * @Description: 深度分页返回对象
 */

public class ESPageResult<I extends IPageModel> {

	/** 当前页号 */
	private int pageNumber;

	/** 每页大小 */
	private int pageSize;

	/** 查询总数 */
	private long total;

	/** 结果列表 */
	private List<I> list;

	public ESPageResult() {
	}


	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return total;
	}


	public void setTotal(long total) {
		this.total = total;
	}


	public List<I> getList() {
		return list;
	}

	public void setList(List<I> list) {
		this.list = list;
	}

	/**
	 * 将hits数组转换为list数组，但reverse为true时，list数组元素的顺序与hits数组元素的顺序相反
	 * @param hits
	 * @param beanCls
	 * @param reverse
	 */
	public void setList(SearchHit[] hits, Class<I> beanCls, boolean reverse){
		list = new ArrayList<>(hits.length);
		for(int i=0; i<hits.length; i++)
			list.add(JSON.parseObject(hits[i].getSourceAsString(), beanCls));
		if(reverse)
			Collections.reverse(list);
	}
}