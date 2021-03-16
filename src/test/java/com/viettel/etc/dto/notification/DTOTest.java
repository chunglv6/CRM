package com.viettel.etc.dto.notification;
import com.viettel.etc.dto.notification.NotificationRequestDTO;
import com.viettel.etc.dto.notification.SubscriptionRequestDTO;
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
	void NotificationRequestDTO (){
		assertThat(NotificationRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

	@Test
	void SubscriptionRequestDTO (){
		assertThat(SubscriptionRequestDTO.class, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}

}