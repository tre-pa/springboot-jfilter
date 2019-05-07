package br.jus.tre_pa.jfilter.repository.domain;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Foo {

	@Id
	private Long id;

	@Column
	private String name;

	@Column
	private Boolean enabled;

	@Column
	private LocalDate createdAt;

	@Enumerated(EnumType.STRING)
	private FooType fooType;

	@ManyToOne(cascade = CascadeType.ALL)
	private Bar bar;

	public Foo(Long id, String name, LocalDate createdAt, Boolean enabled, FooType fooType) {
		super();
		this.id = id;
		this.name = name;
		this.enabled = enabled;
		this.createdAt = createdAt;
		this.fooType = fooType;
	}

}
