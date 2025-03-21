package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.TestConfig;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class PropertyServiceIntegrationTests {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    void cleanUp(){
        propertyRepository.clear();
    }

    @Test
    void should_get_all_properties_by_owner_id(){

    }

    @Test
    void should_get_all_properties_by_owner_id_if_the_id_is_wrong(){

    }

    @Test
    void should_get_all_properties_by_owner_id_if_owner_does_not_have_property(){

    }

    @Test
    void should_add_new_property_to_certain_owner(){

    }

    @Test
    void should_update_property_for_certain_owner(){

    }

    @Test
    void should_remove_property_for_certain_owner(){

    }
}
