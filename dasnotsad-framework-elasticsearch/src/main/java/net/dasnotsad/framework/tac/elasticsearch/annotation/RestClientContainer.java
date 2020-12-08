package net.dasnotsad.framework.tac.elasticsearch.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * 数据源容器
 *
 * @author liuliwei
 * @create 2019-11-1
 */
@Slf4j
public class RestClientContainer {

	private Map<Integer, RestHighLevelClient> containers = new LinkedHashMap<>();

	private Map<Integer, String> nodesContainers = new LinkedHashMap<>();

	public RestHighLevelClient get(int whichDataSource) {
		RestHighLevelClient rc = containers.get(whichDataSource);
		Objects.requireNonNull(rc, "'dasnotsad.paas.elasticsearch' index " + whichDataSource + " not found");
		return rc;
	}

	public int getWhichDataSource(String nodes) {
		int whichDataSource = dscode(nodes);
		if (containers.get(whichDataSource) != null) {
			return whichDataSource;
		}
		put(whichDataSource, nodes);
		return whichDataSource;
	}

	public String getNodes(int whichDataSource){
		return nodesContainers.get(whichDataSource);
	}

	public void put(int whichDataSource, String nodes) {
		Objects.requireNonNull(nodes, "put['nodes'] must not be null");
		String[] esarr = nodes.split("[,;]");
		HttpHost[] hosts = new HttpHost[esarr.length];
		for (int i = 0; i < esarr.length; i++) {
			String[] s = esarr[i].split(":");
			if (s.length == 2) {
				hosts[i] = new HttpHost(s[0], Integer.valueOf(s[1]));
			}
		}
		RestHighLevelClient rc = new RestHighLevelClient(RestClient.builder(hosts));
		put(whichDataSource, rc, nodes);
		log.info("*****************dasnotsad.paas.elasticsearch成功添加数据源{}: {}", whichDataSource, nodes);
	}

	public void put(int whichDataSource, RestHighLevelClient restHighLevelClient, String nodes) {
		containers.put(whichDataSource, restHighLevelClient);
		nodesContainers.put(whichDataSource, nodes);
	}

	private int dscode(String nodes) {
		Objects.requireNonNull(nodes, "dscode['nodes'] must not be null");
		return Math.abs(nodes.hashCode());
	}

}
