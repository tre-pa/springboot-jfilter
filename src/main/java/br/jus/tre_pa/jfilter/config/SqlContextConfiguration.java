package br.jus.tre_pa.jfilter.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import br.jus.tre_pa.jfilter.sql.SqlContext;
import br.jus.tre_pa.jfilter.sql.h2.H2ContextImpl;
import br.jus.tre_pa.jfilter.sql.oracle.OracleContextImpl;
import br.jus.tre_pa.jfilter.sql.postgre.PostgreContextImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SqlContextConfiguration {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource datasource;

	@Bean
	public SqlContext sqlContext() throws SQLException, IllegalAccessException {
		log.info("Iniciando configurações do SqlContext: {}", datasource.getConnection().getMetaData().getDatabaseProductName());
		if (datasource.getConnection().getMetaData().getDatabaseProductName().equalsIgnoreCase("h2")) {
			log.info("Data Context definido para o H2.");
			return new H2ContextImpl(jdbcTemplate);
		}
		if (datasource.getConnection().getMetaData().getDatabaseProductName().equalsIgnoreCase("Oracle")) {
			log.info("Data Context definido para o Oracle.");
			return new OracleContextImpl(jdbcTemplate);
		}
		if (datasource.getConnection().getMetaData().getDatabaseProductName().equalsIgnoreCase("postgresql")) {
			log.info("Data Context definido para o PostgreSQL.");
			return new PostgreContextImpl(jdbcTemplate);
		}
		throw new IllegalAccessException("Nenhuma implementação de contexto de dados encontado.");
	}

}
