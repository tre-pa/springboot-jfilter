package br.jus.tre_pa.jfilter;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jcruz
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Page<T> {

	/**
	 * 
	 */
	private org.springframework.data.domain.Page<T> pagination;

	/**
	 * 
	 */
	private List<Aggregation> aggregations;

	public Page(org.springframework.data.domain.Page<T> pagination) {
		super();
		this.pagination = pagination;
	}

	public Page(org.springframework.data.domain.Page<T> pagination, List<Aggregation> aggregations) {
		super();
		this.pagination = pagination;
		this.aggregations = aggregations;
	}

}
