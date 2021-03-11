package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class IngredientServiceImplTest {

	IngredientCommandToIngredient ingredientCommandToIngredient;
	IngredientToIngredientCommand ingredientToIngredientCommand;
	@Mock
	RecipeRepository recipeRepository;
	@Mock
	UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
	@Mock
	RecipeReactiveRepository recipeReactiveRepository;

	@Captor
	ArgumentCaptor<Recipe> recipeArgumentCaptor;

	IngredientService sut;

	//init converters
	public IngredientServiceImplTest() {
		this.ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
		this.ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sut = new IngredientServiceImpl(
				ingredientToIngredientCommand,
				ingredientCommandToIngredient,
				recipeReactiveRepository,
				recipeRepository,
				unitOfMeasureReactiveRepository
			);
	}

	@Test
	public void saveIngredientCommand() {
		// given
		IngredientCommand ingredientCommand = new IngredientCommand();
		ingredientCommand.setId("2");
		ingredientCommand.setRecipeId("1");
		UnitOfMeasureCommand unitOfMeasureCommand = new UnitOfMeasureCommand();
		unitOfMeasureCommand.setId("3");
		ingredientCommand.setUnitOfMeasure(unitOfMeasureCommand);

		Recipe recipe = new Recipe();
		Ingredient ingredient1 = new Ingredient();
		ingredient1.setId("1");
		recipe.getIngredients().add(ingredient1);;
		Ingredient ingredient2 = new Ingredient();
		ingredient2.setId("2");
		recipe.getIngredients().add(ingredient2);
		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(Optional.of(recipe));
		Mockito.when(unitOfMeasureReactiveRepository.findById(Mockito.anyString())).thenReturn(Mono.just(new UnitOfMeasure()));
		Mockito.when(recipeReactiveRepository.save(Mockito.any(Recipe.class))).thenReturn(Mono.just(recipe));

		// when
		IngredientCommand result = sut.saveIngredientCommand(ingredientCommand).block();

		// then
		Assert.assertNotNull(result);
		Mockito.verify(recipeReactiveRepository).save(Mockito.any(Recipe.class));
	}

	@Test
	public void deleteById() {
		Recipe recipe = new Recipe();
		Ingredient ingredient1 = new Ingredient();
		ingredient1.setId("1");
		recipe.getIngredients().add(ingredient1);
		Ingredient ingredient2 = new Ingredient();
		ingredient2.setId("2");
		recipe.getIngredients().add(ingredient2);
		Ingredient ingredient3 = new Ingredient();
		ingredient3.setId("3");
		recipe.getIngredients().add(ingredient3);
		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(Optional.of(recipe));

		sut.deleteById("1", "2");

		Mockito.verify(recipeRepository).save(recipeArgumentCaptor.capture());
		Recipe savedReciped = recipeArgumentCaptor.getValue();
		Assert.assertNotNull(savedReciped);
		Assert.assertEquals(2, savedReciped.getIngredients().size());
	}
}