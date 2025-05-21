package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link PetTypeFormatter}
 *
 * @author Colin But
 */
@ExtendWith(MockitoExtension.class)
@DisabledInNativeImage
class PetTypeFormatterTests {

	@Mock
	private OwnerRepository pets;

	private PetTypeFormatter petTypeFormatter;

	@BeforeEach
	void setup() {
		this.petTypeFormatter = new PetTypeFormatter(pets);
	}

	@Test
	void testPrint() {
		PetType petType = new PetType();
		petType.setName("Hamster");
		String petTypeName = this.petTypeFormatter.print(petType, Locale.ENGLISH);
		assertThat(petTypeName).isEqualTo("Hamster");
	}

	@Test
	void shouldParse() throws ParseException {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		PetType petType = petTypeFormatter.parse("Bird", Locale.ENGLISH);
		assertThat(petType.getName()).isEqualTo("Bird");
	}

	@Test
	void shouldThrowParseException() {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		Assertions.assertThrows(ParseException.class, () -> {
			petTypeFormatter.parse("Fish", Locale.ENGLISH);
		});
	}

	/**
	 * Helper method to produce some sample pet types just for test purpose
	 * @return {@link Collection} of {@link PetType}
	 */
	private List<PetType> makePetTypes() {
		List<PetType> petTypes = new ArrayList<>();
		petTypes.add(new PetType() {
			{
				setName("Dog");
			}
		});
		petTypes.add(new PetType() {
			{
				setName("Bird");
			}
		});
		return petTypes;
	}
}
