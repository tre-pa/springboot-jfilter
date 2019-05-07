package br.jus.tre_pa.jfilter.sql;

import br.jus.tre_pa.jfilter.Filterable;

public interface SqlWhereStep extends SqlFetchStep, SqlOrderByStep {
	SqlOrderByStep where(Filterable filter);
}
