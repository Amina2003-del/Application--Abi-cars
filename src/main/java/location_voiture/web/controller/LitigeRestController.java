package location_voiture.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import location_voiture.persistence.dto.LitigeDTO;
import location_voiture.persistence.model.NoteRequest;
import location_voiture.service.LitigeService;
@RestController
@RequestMapping("/Owner/litige/api")
public class LitigeRestController {
	  @Autowired
	    private LitigeService litigeService;

	    @GetMapping("/{id}")
	    public LitigeDTO getLitige(@PathVariable Long id) {
	        System.out.println("Get Litige id=" + id);
	        return litigeService.getLitigeDetails(id);
	    }

	    @PostMapping("/{id}/note")
	    public void addNote(@PathVariable Long id, @RequestBody NoteRequest request) {
	        System.out.println("Add note to litige id=" + id);
	        litigeService.addNote(id, request.getNote(), request.getStatut());
	    }

	    @PostMapping("/{id}/upload")
	    public void upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
	        System.out.println("Upload document for litige id=" + id);
	        litigeService.uploadDocument(id, file);
	    }

	    @PostMapping("/{id}/notify")
	    public void notify(@PathVariable Long id) {
	        System.out.println("Notify client for litige id=" + id);
	        litigeService.notifyClient(id);
	    }

	    @PostMapping("/{id}/resolve")
	    public void resolve(@PathVariable Long id) {
	        System.out.println("Resolve litige id=" + id);
	        litigeService.resolveLitige(id);
	    }
}
