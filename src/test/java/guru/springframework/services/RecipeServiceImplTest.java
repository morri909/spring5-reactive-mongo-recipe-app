package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RecipeServiceImplTest {

	RecipeServiceImpl recipeService;

	@Mock
	RecipeRepository recipeRepository;
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
		Recipe recipe = new Recipe();
		Set<Recipe> recipeSet = new HashSet<>();
		recipeSet.add(recipe);

		Mockito.when(recipeRepository.findAll()).thenReturn(recipeSet);

		Set<Recipe> recipes = recipeService.getRecipes();

		Assert.assertEquals(1, recipes.size());
		Mockito.verify(recipeRepository, Mockito.times(1)).findAll();
	}

	@Test
	public void getRecipeById() {
		Recipe recipe = new Recipe();
		recipe.setId("1");
		Optional<Recipe> recipeOptional = Optional.of(recipe);
		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(recipeOptional);

		Recipe result = recipeService.findById("1");

		Assert.assertNotNull(result);
		Assert.assertEquals(recipe.getId(), result.getId());
	}

	@Test(expected = NotFoundException.class)
	public void getRecipeByIdNotFound() {
		Optional<Recipe> recipeOptional = Optional.empty();
		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(recipeOptional);

		recipeService.findById("1");
	}

	@Test
	public void deleteById() {
		recipeService.deleteById("1");

		Mockito.verify(recipeRepository).deleteById(Mockito.anyString());
	}

}