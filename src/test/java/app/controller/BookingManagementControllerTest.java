package app.controller;

import app.dao.LoginDao;
import app.dto.BookingDto;
import app.dto.OperationResult;
import app.dto.UserDto;
import app.dto.request.UserRequest;
import app.mapper.BookingMapper;
import app.mapper.BookingMapperImpl;
import app.services.UserOperations;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class BookingManagementControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserOperations userOperations;
    @Mock
    private LoginDao loginDao;
    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        BookingManagementController bookingManagementController = new BookingManagementController(userOperations, bookingMapper, loginDao);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingManagementController).build();
    }
    @Test
    public void getMyBookingsTest() throws Exception {
        String login = "user";
        List<BookingDto> bookings = new ArrayList<>();
        String hallName = "Moscow";
        LocalDate date = LocalDate.parse("2024-01-01");
        LocalTime startTime = LocalTime.parse("09:00:00");
        LocalTime endTime = LocalTime.parse("15:00:00");
        BookingDto bookingDto = new BookingDto(1, login, hallName, 0, date, startTime, endTime);
        bookings.add(bookingDto);
        objectMapper.registerModule(new JSR310Module());

        when(userOperations.getAllUserBooking(login)).thenReturn(bookings);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        String bookingToJson = objectMapper.writeValueAsString(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/booking/mybookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(bookingToJson));
        verify(userOperations, times(1)).getAllUserBooking(login);
    }
}
