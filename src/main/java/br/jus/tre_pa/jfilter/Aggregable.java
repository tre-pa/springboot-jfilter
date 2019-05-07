package br.jus.tre_pa.jfilter;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Aggregable {

	@JsonAlias("selector")
	private String dataField;

	@JsonAlias("summaryType")
	private Operation operation;

	public enum Operation {
		// @formatter:off
		@JsonProperty("sum")
		SUM, 
		@JsonProperty("max")
		MAX, 
		@JsonProperty("min")
		MIN, 
		@JsonProperty("count")
		COUNT, 
		@JsonProperty("avg")
		AVG
		// @formatter:on
	}
}
