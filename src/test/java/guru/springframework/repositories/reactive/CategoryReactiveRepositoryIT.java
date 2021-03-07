package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryIT {

	@Autowired
	CategoryReactiveRepository sut;

	@Test
	public void fetchAll() {
		Category category = new Category();
		category.setDescription("test");

		sut.save(category).block();

		Assert.assertEquals(1, sut.findAll().count().block().longValue());
	}
}
