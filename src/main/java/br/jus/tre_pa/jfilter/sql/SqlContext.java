package br.jus.tre_pa.jfilter.sql;

import java.util.List;

import br.jus.tre_pa.jfilter.core.Aggregation;
import br.jus.tre_pa.jfilter.core.Payload;

public interface SqlContext {
	SqlWhereStep selectFrom(String sql);

	List<Aggregation> aggregation(String sql, Payload payload);

	void clear();
}
