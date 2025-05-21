package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.client.RestTemplate;

/**
 * Integration tests for the PetClinic application.
 *
 * @author
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PetClinicIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private VetRepository vets;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Test
	void testFindAllVetsCaching() {
		vets.findAll();
		vets.findAll(); // served from cache
	}

	@Test
	void testOwnerDetailsEndpointReturnsOk() {
		RestTemplate restTemplate = restTemplateBuilder
			.rootUri("http://localhost:" + port)
			.build();

		RequestEntity<Void> request = RequestEntity
			.get("/owners/1")
			.build();

		ResponseEntity<String> response = restTemplate.exchange(request, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}
}
