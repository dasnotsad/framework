
package net.dasnotsad.framework.tac.elasticsearch.conditions;

/**
 * @Description: TODO
 * @Author Created by yan.x on 2020-07-22 .
 **/
public class IndexBoost {

	private String indexName;
	private float boost;

	public IndexBoost(String indexName, float boost) {
		this.indexName = indexName;
		this.boost = boost;
	}

	public String getIndexName() {
		return indexName;
	}

	public float getBoost() {
		return boost;
	}

}
