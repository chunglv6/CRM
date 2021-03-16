package com.viettel.etc.dto.ocs;
import com.viettel.etc.dto.ocs.OCSCreateContractForm;
import com.viettel.etc.dto.ocs.OCSDTO;
import com.viettel.etc.dto.ocs.OCSResponse;
import com.viettel.etc.dto.ocs.OCSUpdateContractForm;
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
	void OCSCreateContractForm (){
		assertThat(OCSCreateContractForm.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void OCSDTO (){
		assertThat(OCSDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(OCSDTO.Language.values().length, Matchers.greaterThanOrEqualTo(1));
	}

	@Test
	void OCSResponse (){
		assertThat(OCSResponse.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void OCSUpdateContractForm (){
		assertThat(OCSUpdateContractForm.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(OCSUpdateContractForm.ChargeMethod.values().length, Matchers.greaterThanOrEqualTo(1));
	}

}