package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class ImageServiceImplTest {

	@Mock
	RecipeReactiveRepository recipeReactiveRepository;

	ImageServiceImpl sut;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sut = new ImageServiceImpl(recipeReactiveRepository);
	}

	@Test
	public void saveImageFile() throws IOException {
		String id = "1";
		MultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain",
				"This is a test".getBytes());

		Recipe recipe = new Recipe();
		recipe.setId(id);
		Mockito.when(recipeReactiveRepository.findById(Mockito.anyString())).thenReturn(Mono.just(recipe));
		Mockito.when(recipeReactiveRepository.save(Mockito.any(Recipe.class))).thenReturn(Mono.just(recipe));

		ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);

		//when
		sut.saveImageFile(id, multipartFile).block();

		//then
		Mockito.verify(recipeReactiveRepository).save(argumentCaptor.capture());
		Recipe savedRecipe = argumentCaptor.getValue();
		Assert.assertEquals(multipartFile.getBytes().length, savedRecipe.getImage().length);
	}
}