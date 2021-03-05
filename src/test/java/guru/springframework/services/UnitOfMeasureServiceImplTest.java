package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

public class UnitOfMeasureServiceImplTest {

	@Mock
	UnitOfMeasureRepository unitOfMeasureRepository;
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
		Set<UnitOfMeasure> unitOfMeasures = new HashSet<>();
		unitOfMeasures.add(new UnitOfMeasure());
		Mockito.when(unitOfMeasureRepository.findAll()).thenReturn(unitOfMeasures);
		UnitOfMeasureCommand unitOfMeasureCommand = new UnitOfMeasureCommand();
		Mockito.when(unitOfMeasureToUnitOfMeasureCommand.convert(Mockito.any(UnitOfMeasure.class)))
				.thenReturn(unitOfMeasureCommand);

		Set<UnitOfMeasureCommand> result = sut.listAll();

		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Mockito.verify(unitOfMeasureRepository).findAll();
		Mockito.verify(unitOfMeasureToUnitOfMeasureCommand).convert(Mockito.any(UnitOfMeasure.class));
	}
}