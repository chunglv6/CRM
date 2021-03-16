package com.viettel.etc.dto.momo;
import com.viettel.etc.dto.momo.MoMoBaseDTO;
import com.viettel.etc.dto.momo.MoMoConfirmRequestDTO;
import com.viettel.etc.dto.momo.MoMoConfirmResponseDTO;
import com.viettel.etc.dto.momo.MoMoNotifyRequestDTO;
import com.viettel.etc.dto.momo.MoMoNotifyResponseDTO;
import com.viettel.etc.dto.momo.MoMoPayAppRequestDTO;
import com.viettel.etc.dto.momo.MoMoPayAppResponseDTO;
import com.viettel.etc.dto.momo.MoMoRawDataDTO;
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
	void MoMoBaseDTO (){
		assertThat(MoMoBaseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoConfirmRequestDTO (){
		assertThat(MoMoConfirmRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoConfirmResponseDTO (){
		assertThat(MoMoConfirmResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoNotifyRequestDTO (){
		assertThat(MoMoNotifyRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoNotifyResponseDTO (){
		assertThat(MoMoNotifyResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoPayAppRequestDTO (){
		assertThat(MoMoPayAppRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoPayAppResponseDTO (){
		assertThat(MoMoPayAppResponseDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void MoMoRawDataDTO (){
		assertThat(MoMoRawDataDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}