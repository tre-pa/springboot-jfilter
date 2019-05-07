package br.jus.tre_pa.jfilter.sql.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import br.jus.tre_pa.jfilter.sql.SqlOrderByStep;
import br.jus.tre_pa.jfilter.sql.SqlPaginationStep;

public class OracleOrderByStep implements SqlOrderByStep {

	private JdbcTemplate jdbcTemplate;

	private Map<String, String> sqlFragments = new HashMap<>();

	public OracleOrderByStep(JdbcTemplate jdbcTemplate, Map<String, String> sqlFragments) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.sqlFragments = sqlFragments;
	}

	@Override
	public List<Map<String, Object>> fetchMaps() {
		return jdbcTemplate.queryForList(sqlFragments.get("sql"));
	}

	@Override
	public SqlPaginationStep orderBy(String sortSql) {
		if (StringUtils.isNotBlank(sortSql)) {
			sqlFragments.put("sql", String.format("%s order by %s ", sqlFragments.get("sql"), sortSql));
		}
		return new OraclePaginationStep(jdbcTemplate, sqlFragments);
	}

	@Override
	public SqlPaginationStep orderBy(Sort sort) {
		if (Objects.nonNull(sort)) {
			// @formatter:off
			String orders = sort.stream()
					.map(it -> String.format("%s %s ", it.getProperty(), it.getDirection()))
					.collect(Collectors.joining(","));
			// @formatter:on
			return orderBy(orders);
		}
		return this.orderBy("");
	}

}
