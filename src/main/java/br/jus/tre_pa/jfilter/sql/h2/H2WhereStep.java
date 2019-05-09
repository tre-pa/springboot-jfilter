package br.jus.tre_pa.jfilter.sql.h2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import br.jus.tre_pa.jfilter.core.EmptyFilterable;
import br.jus.tre_pa.jfilter.core.Filterable;
import br.jus.tre_pa.jfilter.sql.SqlOrderByStep;
import br.jus.tre_pa.jfilter.sql.SqlPaginationStep;
import br.jus.tre_pa.jfilter.sql.SqlWhereStep;

public class H2WhereStep implements SqlWhereStep {

	private JdbcTemplate jdbcTemplate;

	private Map<String, String> sqlFragments = new HashMap<>();

	public H2WhereStep(JdbcTemplate jdbcTemplate, Map<String, String> sqlFragments) {
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
		return new H2PaginationStep(jdbcTemplate, sqlFragments);
	}

	@Override
	public SqlPaginationStep orderBy(Sort sort) {
		if (Objects.nonNull(sort)) {
			// @formatter:off
			String orders = sort
					.stream()
					.map(it -> String.format("%s %s ", it.getProperty(), it.getDirection()))
					.collect(Collectors.joining(","));
			// @formatter:on
			return orderBy(orders);
		}
		return orderBy("");
	}

	@Override
	public SqlOrderByStep where(Filterable filter) {
		if ((filter instanceof EmptyFilterable) == false) {
			sqlFragments.put("whereSql", String.format("%s where %s ", sqlFragments.get("sql"), filter.toSql()));
			sqlFragments.put("sql", String.format("%s where %s ", sqlFragments.get("sql"), filter.toSql()));
		}
		return new H2OrderByStep(jdbcTemplate, sqlFragments);
	}

}
