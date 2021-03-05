package guru.springframework.repositories;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private UnitOfMeasureRepository sut;

	@Before
	public void setUp() throws Exception {
		recipeRepository.deleteAll();
		categoryRepository.deleteAll();
		sut.deleteAll();
		RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryRepository, recipeRepository, sut);
		recipeBootstrap.onApplicationEvent(null);
	}

	@Test
	@Ignore
	public void findByDescription() {
		Optional<UnitOfMeasure> uom = sut.findByDescription("Teaspoon");

		Assert.assertTrue(uom.isPresent());
		Assert.assertEquals("Teaspoon", uom.get().getDescription());
	}

	@Test
	@Ignore
	public void findByDescriptionCup() {
		Optional<UnitOfMeasure> uom = sut.findByDescription("Cup");

		Assert.assertTrue(uom.isPresent());
		Assert.assertEquals("Cup", uom.get().getDescription());
	}
}