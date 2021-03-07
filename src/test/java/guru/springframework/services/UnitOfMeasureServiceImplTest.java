package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;

public class UnitOfMeasureServiceImplTest {

	@Mock
	UnitOfMeasureReactiveRepository unitOfMeasureRepository;
	@Mock
	UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

	UnitOfMeasureService sut;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sut = new UnitOfMeasureServiceImpl(unitOfMeasureRepository, unitOfMeasureToUnitOfMeasureCommand);
	}

	@Test
	public void listAll() {
		Mockito.when(unitOfMeasureRepository.findAll()).thenReturn(Flux.just(new UnitOfMeasure()));
		UnitOfMeasureCommand unitOfMeasureCommand = new UnitOfMeasureCommand();
		Mockito.when(unitOfMeasureToUnitOfMeasureCommand.convert(Mockito.any(UnitOfMeasure.class)))
				.thenReturn(unitOfMeasureCommand);

		List<UnitOfMeasureCommand> result = sut.listAll().collectList().block();

		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Mockito.verify(unitOfMeasureRepository).findAll();
		Mockito.verify(unitOfMeasureToUnitOfMeasureCommand).convert(Mockito.any(UnitOfMeasure.class));
	}
}