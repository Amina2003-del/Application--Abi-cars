package location_voiture.repository;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import location_voiture.persistence.model.Document;

@Repository  // facultatif, car JpaRepository est déjà détecté par Spring
public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Service
	public class DocumentService {

	    private final DocumentRepository documentRepository;

	    @Autowired
	    public DocumentService(DocumentRepository documentRepository) {
	        this.documentRepository = documentRepository;
	    }

	    public Document findById(Long id) {
	        return documentRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
	    

}
}}