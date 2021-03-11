package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ImageControllerTest {
	@Mock
	ImageService imageService;
	@Mock
	RecipeService recipeService;

	ImageController sut;

	MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sut = new ImageController(imageService, recipeService);
		mockMvc = MockMvcBuilders.standaloneSetup(sut)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@Test
	public void imageForm() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");

		Mockito.when(recipeService.findCommandById(Mockito.anyString())).thenReturn(Mono.just(recipeCommand));

		mockMvc.perform(get("/recipe/1/image"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/imageuploadform"))
				.andExpect(model().attributeExists("recipe"));

		Mockito.verify(recipeService).findCommandById(Mockito.anyString());
	}

	@Test
	public void imageUpload() throws Exception {
		MockMultipartFile file = new MockMultipartFile("imagefile", "testing.txt", "text/plain",
				"This is a test".getBytes());

		Mockito.when(imageService.saveImageFile(Mockito.anyString(), Mockito.any())).thenReturn(Mono.empty());

		mockMvc.perform(multipart("/recipe/1/image").file(file))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", "/recipe/1/show"));

		Mockito.verify(imageService).saveImageFile(Mockito.anyString(), Mockito.any());
	}

//	@Test
//	public void renderImageFromDb() throws Exception {
//		RecipeCommand command = new RecipeCommand();
//		command.setId("1");
//
//		String test = "this is a test";
//		Byte[] bytesBoxed = new Byte[test.getBytes().length];
//		for (int i = 0; i < test.getBytes().length; i++) {
//			bytesBoxed[i] = test.getBytes()[i];
//		}
//
//		command.setImage(bytesBoxed);
//
//		Mockito.when(recipeService.findCommandById(Mockito.anyString())).thenReturn(Mono.just(command));
//
//		//when
//		MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
//				.andExpect(status().isOk())
//				.andReturn().getResponse();
//
//		byte[] responseBytes = response.getContentAsByteArray();
//
//		Assert.assertEquals(test.getBytes().length, responseBytes.length);
//	}

}