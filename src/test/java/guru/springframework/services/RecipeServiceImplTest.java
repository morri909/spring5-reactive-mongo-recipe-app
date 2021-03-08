package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RecipeServiceImplTest {

	RecipeServiceImpl recipeService;

	@Mock
	RecipeReactiveRepository recipeRepository;
	@Mock
	RecipeCommandToRecipe recipeCommandToRecipe;
	@Mock
	RecipeToRecipeCommand recipeToRecipeCommand;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
	}

	@Test
	public void getRecipes() {
		Mockito.when(recipeRepository.findAll()).thenReturn(Flux.just(new Recipe()));

		List<Recipe> recipes = recipeService.getRecipes().collectList().block();

		Assert.assertEquals(1, recipes.size());
		Mockito.verify(recipeRepository, Mockito.times(1)).findAll();
	}

	@Test
	public void getRecipeById() {
		Recipe recipe = new Recipe();
		recipe.setId("1");
		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(Mono.just(recipe));

		Recipe result = recipeService.findById("1").block();

		Assert.assertNotNull(result);
		Assert.assertEquals(recipe.getId(), result.getId());
	}

	@Test
	public void getRecipeByIdNotFound() {
		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(Mono.empty());

		Recipe recipe = recipeService.findById("1").block();

		Assert.assertNull(recipe);
	}

	@Test
	public void deleteById() {
		recipeService.deleteById("1");

		Mockito.verify(recipeRepository).deleteById(Mockito.anyString());
	}

}