package guru.springframework.controllers;

import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/recipe")
public class ImageController {
	private final ImageService imageService;
	private final RecipeService recipeService;

	public ImageController(ImageService imageService, RecipeService recipeService) {
		this.imageService = imageService;
		this.recipeService = recipeService;
	}

	@GetMapping("/{id}/image")
	public String imageForm(@PathVariable String id, Model model) {
		model.addAttribute("recipe", recipeService.findCommandById(id));
		return "recipe/imageuploadform";
	}

	@PostMapping("/{id}/image")
	public String imageUpload(@PathVariable String id, @RequestParam("imagefile") MultipartFile file) throws IOException {
		imageService.saveImageFile(id, file).block();
		return "redirect:/recipe/" + id + "/show";
	}

//	@GetMapping("/{id}/recipeimage")
//	public void renderImageFromDb(@PathVariable String id, HttpServletResponse response) throws IOException {
//		RecipeCommand recipeCommand = recipeService.findCommandById(id).block();
//		if (recipeCommand.getImage() != null) {
//			byte[] image = new byte[recipeCommand.getImage().length];
//			for (int i = 0; i < recipeCommand.getImage().length; i++) {
//				image[i] = recipeCommand.getImage()[i];
//			}
//			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//			InputStream inputStream = new ByteArrayInputStream(image);
//			IOUtils.copy(inputStream, response.getOutputStream());
//		}
//	}
}
