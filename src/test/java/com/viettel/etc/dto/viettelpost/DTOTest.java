package com.viettel.etc.dto.viettelpost;
import com.viettel.etc.dto.viettelpost.BillRequestDTO;
import com.viettel.etc.dto.viettelpost.BillResponseDTO;
import com.viettel.etc.dto.viettelpost.ItemDTO;
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
	void BillRequestDTO (){
		assertThat(BillRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void BillResponseDTO (){
		assertThat(BillResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ItemDTO (){
		assertThat(ItemDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}