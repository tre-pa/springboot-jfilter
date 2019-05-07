package br.jus.tre_pa.jfilter.sql;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface SqlPageableSelectStep {
	Page<List<Map<String, Object>>> fetchMaps();
}
