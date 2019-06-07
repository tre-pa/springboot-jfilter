package br.jus.tre_pa.jfilter.sql.postgre;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import br.jus.tre_pa.jfilter.core.Aggregation;
import br.jus.tre_pa.jfilter.core.EmptyFilterable;
import br.jus.tre_pa.jfilter.core.Payload;
import br.jus.tre_pa.jfilter.sql.SqlContext;
import br.jus.tre_pa.jfilter.sql.SqlWhereStep;

public class PostgreContextImpl implements SqlContext {

	private JdbcTemplate jdbcTemplate;

	private Map<String, String> sqlFragments = new HashMap<>();

	public PostgreContextImpl(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public SqlWhereStep selectFrom(String sql) {
		String selectSql = StringUtils.prependIfMissing(sql, "select * from (");
		selectSql = StringUtils.appendIfMissing(selectSql, " ) ");

		sqlFragments.put("sourceSql", sql);
		sqlFragments.put("sql", selectSql);
		return new PostgreWhereStep(jdbcTemplate, sqlFragments);
	}

	@Override
	public List<Aggregation> aggregation(String sql, Payload payload) {
		if (Objects.nonNull(payload) && Objects.nonNull(payload.getAggregables())) return null;
		List<Aggregation> aggregrations = Arrays.asList(payload.getAggregables()).stream().map(it -> {
			String whereSql = payload.getFilterable() instanceof EmptyFilterable ? "" : String.format("where %s", payload.getFilterable().toSql());
			String aggSql = String.format("select %s(%s) from ( %s ) %s ", it.getOperation().name(), it.getDataField(), sql, whereSql);
			Object result = jdbcTemplate.queryForObject(aggSql, Object.class);
			return new Aggregation(it.getDataField(), result, it.getOperation());
		}).collect(Collectors.toList());
		return aggregrations;
	}

	@Override
	public void clear() {
		sqlFragments.clear();
	}

}
