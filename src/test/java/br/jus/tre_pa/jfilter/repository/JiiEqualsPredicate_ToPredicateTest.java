package br.jus.tre_pa.jfilter.repository;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import br.jus.tre_pa.jfilter.Filterable;
import br.jus.tre_pa.jfilter.JFilterModuleConfiguration;
import br.jus.tre_pa.jfilter.Page;
import br.jus.tre_pa.jfilter.Payload;
import br.jus.tre_pa.jfilter.Projectable;
import br.jus.tre_pa.jfilter.predicate.Conjunction;
import br.jus.tre_pa.jfilter.predicate.EqualsPredicate;
import br.jus.tre_pa.jfilter.repository.domain.Bar;
import br.jus.tre_pa.jfilter.repository.domain.Foo;
import br.jus.tre_pa.jfilter.repository.domain.FooType;
import br.jus.tre_pa.jfilter.repository.specification.FooSpecification;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { JFilterModuleConfiguration.class })
public class JiiEqualsPredicate_ToPredicateTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private FooRepository fooRepository;

	private void createFoo() {
		Foo foo1 = new Foo(1L, "Fulano1", LocalDate.now(), true, FooType.A);
		Foo foo2 = new Foo(2L, "Fulano2", LocalDate.now().plusDays(2), true, FooType.B);
		entityManager.persist(foo1);
		entityManager.persist(foo2);
		entityManager.flush();
	}

	private void createFooWithBar() {
		Foo foo1 = new Foo(1L, "Fulano1", LocalDate.now(), true, FooType.A);
		Foo foo2 = new Foo(2L, "Fulano2", LocalDate.now().plusDays(2), true, FooType.B);
		Bar bar1 = new Bar(1L, "Bar1");
		Bar bar2 = new Bar(2L, "Bar2");

		foo1.setBar(bar1);
		foo2.setBar(bar2);

		entityManager.persist(foo1);
		entityManager.persist(foo2);
		entityManager.flush();
	}

	@Test
	public void findAllTest() {
		List<Foo> foos = fooRepository.findAll();
		assertTrue(foos.size() == 0);
	}

	/**
	 * Teste da projeção do JiiRepository
	 */
	@Test
	public void findWithProjectionByName() {
		createFoo();

		Projectable projectable = new Projectable();
		projectable.setFields(Arrays.asList("id", "name"));
		Page<Foo> foos = fooRepository.findAll(Foo.class, PageRequest.of(0, 20), projectable, null, null);
		assertTrue(foos.getPagination().getContent().size() == 2);

		fooRepository.deleteAll();

	}

	@Test
	public void findByName_StringFieldTest() {
		createFoo();

		Payload payload = new Payload();
		Filterable filterable = new Conjunction();
		EqualsPredicate equalsPredicate = new EqualsPredicate();
		equalsPredicate.setDataField("name");
		equalsPredicate.setType("=");
		equalsPredicate.setValue("Fulano1");

		filterable.getPredicates().add(equalsPredicate);
		payload.setFilterable(filterable);

		Page<Foo> foos = fooRepository.findAll(Foo.class, PageRequest.of(0, 20), null, FooSpecification.class, payload);
		assertTrue(foos.getPagination().getContent().size() == 1);
		assertTrue(foos.getPagination().getContent().get(0).getName().equals("Fulano1"));

		fooRepository.deleteAll();

	}

	@Test
	public void findByEnabled_BooleanFieldTest() {
		createFoo();

		Payload payload = new Payload();
		Filterable filterable = new Conjunction();
		EqualsPredicate equalsPredicate = new EqualsPredicate();
		equalsPredicate.setDataField("enabled");
		equalsPredicate.setType("=");
		equalsPredicate.setValue(true);

		filterable.getPredicates().add(equalsPredicate);
		payload.setFilterable(filterable);

		Page<Foo> foos = fooRepository.findAll(Foo.class, PageRequest.of(0, 20), null, FooSpecification.class, payload);
		assertTrue(foos.getPagination().getContent().size() == 2);

		fooRepository.deleteAll();

	}

	@Test
	public void findByFooType_EnumFieldTest() {
		createFoo();

		Payload payload = new Payload();
		Filterable filterable = new Conjunction();
		EqualsPredicate equalsPredicate = new EqualsPredicate();
		equalsPredicate.setDataField("fooType");
		equalsPredicate.setType("=");
		equalsPredicate.setValue("A");

		filterable.getPredicates().add(equalsPredicate);
		payload.setFilterable(filterable);

		Page<Foo> foos = fooRepository.findAll(Foo.class, PageRequest.of(0, 20), null, FooSpecification.class, payload);
		assertTrue(foos.getPagination().getContent().size() == 1);
		assertTrue(foos.getPagination().getContent().get(0).getName().equals("Fulano1"));

		fooRepository.deleteAll();

	}

	@Test
	public void findByBarName_EntityFieldTest() {
		createFooWithBar();

		Payload payload = new Payload();
		Filterable filterable = new Conjunction();
		EqualsPredicate equalsPredicate = new EqualsPredicate();
		equalsPredicate.setDataField("bar.name");
		equalsPredicate.setType("=");
		equalsPredicate.setValue("Bar1");

		filterable.getPredicates().add(equalsPredicate);
		payload.setFilterable(filterable);

		Page<Foo> foos = fooRepository.findAll(Foo.class, PageRequest.of(0, 20), null, FooSpecification.class, payload);
		assertTrue(foos.getPagination().getContent().size() == 1);
		assertTrue(foos.getPagination().getContent().get(0).getName().equals("Fulano1"));

		fooRepository.deleteAll();

	}

}
