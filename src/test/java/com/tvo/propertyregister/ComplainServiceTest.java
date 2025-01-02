package com.tvo.propertyregister;

import com.tvo.propertyregister.model.Complain;
import com.tvo.propertyregister.repository.ComplainRepository;
import com.tvo.propertyregister.service.ComplainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplainServiceTest {

    private static final Complain FIRST_COMPLAIN = new Complain(1,
            "Debts",
            "I am not agree with my debts",
            1);

    private static final Complain SECOND_COMPLAIN = new Complain(2,
            "Taxes",
            "I think that my tax obligation are too high. Taxes in your register are not equal with the law",
            1);

    @Mock
    private ComplainRepository complainRepository;

    @InjectMocks
    private ComplainService complainService;

    @Test
    void should_return_all_complains() {
        List<Complain> expectedResult = List.of(FIRST_COMPLAIN);

        when(complainRepository.findAll()).thenReturn(expectedResult);
        List<Complain> factualResult = complainService.getAllComplains();

        assertEquals(expectedResult, factualResult);
    }

    @Test
    void should_return_complains_by_user_id() {
        int userId = 1;
        List<Complain> expectedResult = List.of(FIRST_COMPLAIN, SECOND_COMPLAIN);

        when(complainRepository.findByUserId(userId)).thenReturn(expectedResult);
        List<Complain> factualResult = complainService.getComplainByUserId(userId);

        assertEquals(expectedResult, factualResult);
    }

    @Test
    void should_return_complains_by_user_id_with_no_complains() {
        int userId = 2;
        List<Complain> expectedResult = List.of();

        when(complainRepository.findByUserId(userId)).thenReturn(expectedResult);
        List<Complain> factualResult = complainService.getComplainByUserId(userId);

        assertEquals(expectedResult, factualResult);
    }

    @Test
    void should_add_new_complain() {
        when(complainRepository.save(FIRST_COMPLAIN)).thenReturn(true);

        boolean actualResult = complainService.addNewComplain(FIRST_COMPLAIN);

        verify(complainRepository, times(1)).save(FIRST_COMPLAIN);
        assertTrue(actualResult);
    }

    @Test
    void should_update_complain_info() {
        when(complainRepository.update(SECOND_COMPLAIN.getId(), SECOND_COMPLAIN)).thenReturn(true);

        boolean actualResult = complainService.updateComplainInfo(SECOND_COMPLAIN.getId(), SECOND_COMPLAIN);

        verify(complainRepository, times(1)).update(SECOND_COMPLAIN.getId(), SECOND_COMPLAIN);
        assertTrue(actualResult);
    }

    @Test
    void should_delete_complain() {
        when(complainRepository.remove(SECOND_COMPLAIN.getId())).thenReturn(true);

        boolean actualResult = complainService.deleteComplain(SECOND_COMPLAIN.getId());

        verify(complainRepository, times(1)).remove(SECOND_COMPLAIN.getId());
        assertTrue(actualResult);
    }

}
