package net.dasnotsad.framework.elasticsearch.core.executor;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;

public interface IExector {

	<R, S> S exec(int whichDataSource, IOperator<R, S> operator, R request);
}
