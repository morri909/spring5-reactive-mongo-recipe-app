package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IndexControllerTest {

	@Mock
	private RecipeService recipeService;
	@Mock
	private Model model;

	private IndexController indexController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		indexController = new IndexController(recipeService);
	}

	@Test
	public void getIndexPage() {
		Recipe recipe1 = new Recipe();
		recipe1.setId("1");
		Recipe recipe2 = new Recipe();
		recipe2.setId("2");
		Mockito.when(recipeService.getRecipes()).thenReturn(Flux.just(recipe1, recipe2));

		ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);

		String view = indexController.getIndexPage(model);

		Assert.assertEquals("index", view);
		Mockito.verify(recipeService, Mockito.times(1)).getRecipes();
		Mockito.verify(model, Mockito.times(1))
				.addAttribute(ArgumentMatchers.eq("recipes"), argumentCaptor.capture());

		List<Recipe> results = argumentCaptor.getValue();
		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testMockMvc() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();

		Recipe recipe1 = new Recipe();
		recipe1.setId("1");
		Recipe recipe2 = new Recipe();
		recipe2.setId("2");
		Mockito.when(recipeService.getRecipes()).thenReturn(Flux.just(recipe1, recipe2));

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
	}
}