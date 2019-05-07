package br.jus.tre_pa.jfilter.sql;

import java.util.List;
import java.util.Map;

public interface SqlFetchStep {
	List<Map<String, Object>> fetchMaps();
}
