package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryIT {

	@Autowired
	UnitOfMeasureReactiveRepository sut;

	@Test
	public void fetchAll() {
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription("test");

		sut.save(uom).block();

		Assert.assertEquals(1, sut.findAll().count().block().longValue());
	}
}
