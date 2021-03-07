package guru.springframework.repositories.reactive;

import guru.springframework.domain.Recipe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryIT {

	@Autowired
	RecipeReactiveRepository sut;

	@Test
	public void fetchAll() {
		Recipe test = new Recipe();
		test.setDescription("test");

		sut.save(test).block();

		Assert.assertEquals(1, sut.findAll().count().block().longValue());
	}
}
