package br.jus.tre_pa.jfilter.repository.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Bar {

	@Id
	private Long id;

	@Column
	private String name;

	public Bar(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

}
