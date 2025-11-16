package location_voiture.service;

import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import location_voiture.persistence.model.Localisation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import location_voiture.repository.VilleRepository;

@Service

public class LocalisationService {
	

	    @Autowired
	    private VilleRepository localisationRepository;

	    @PostConstruct
	    public void init() throws IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        TypeReference<List<Localisation>> typeReference = new TypeReference<>() {};
	        InputStream inputStream = TypeReference.class.getResourceAsStream("/villes-maroc.json");
	        List<Localisation> villes = mapper.readValue(inputStream, typeReference);
	        localisationRepository.saveAll(villes);
	        System.out.println("Villes importées avec succès !");
	    }
	}

