package com.viettel.etc.dto.keycloak;
import com.viettel.etc.dto.keycloak.AdminDTO;
import com.viettel.etc.dto.keycloak.ReqChangePassDTO;
import com.viettel.etc.dto.keycloak.ReqChangePassUserKeycloakDTO;
import com.viettel.etc.dto.keycloak.ReqLoginDTO;
import com.viettel.etc.dto.keycloak.ReqResetPassDTO;
import com.viettel.etc.dto.keycloak.ReqUserKeycloakDTO;
import com.viettel.etc.dto.keycloak.ResKeycloakDTO;
import com.viettel.etc.dto.keycloak.ResLoginDTO;
import com.viettel.etc.dto.keycloak.ResUserKeycloakDTO;
import com.viettel.etc.dto.keycloak.UserDTO;
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
	void AdminDTO (){
		assertThat(AdminDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqChangePassDTO (){
		assertThat(ReqChangePassDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqChangePassUserKeycloakDTO (){
		assertThat(ReqChangePassUserKeycloakDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqLoginDTO (){
		assertThat(ReqLoginDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqResetPassDTO (){
		assertThat(ReqResetPassDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ReqUserKeycloakDTO (){
		assertThat(ReqUserKeycloakDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResKeycloakDTO (){
		assertThat(ResKeycloakDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResLoginDTO (){
		assertThat(ResLoginDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void ResUserKeycloakDTO (){
		assertThat(ResUserKeycloakDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResUserKeycloakDTO.UserAttributeKeycloak.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
		assertThat(ResUserKeycloakDTO.GroupUserKeycloak.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void UserDTO (){
		assertThat(UserDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}