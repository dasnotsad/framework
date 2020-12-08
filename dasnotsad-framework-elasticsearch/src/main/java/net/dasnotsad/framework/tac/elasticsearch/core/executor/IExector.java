package net.dasnotsad.framework.tac.elasticsearch.core.executor;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.client.RequestOptions;

public interface IExector {

	<R, S> S exec(int whichDataSource, IOperator<R, S> operator, R request, RequestOptions requestOptions);
}
