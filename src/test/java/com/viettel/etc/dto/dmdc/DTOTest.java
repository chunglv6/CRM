package com.viettel.etc.dto.dmdc;
import com.viettel.etc.dto.dmdc.VehicleGroupDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.hamcrest.Matchers;
import static org.hamcrest.CoreMatchers.allOf;
import static com.google.code.beanmatchers.BeanMatchers.*;
import org.junit.jupiter.api.Test;

class DTOTest {

	@BeforeEach
	void setUp() {
	}

	@Test
	void VehicleGroupDTO (){
		assertThat(VehicleGroupDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}