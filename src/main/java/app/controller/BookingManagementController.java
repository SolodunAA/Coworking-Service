package app.controller;

import app.annotation.Auditable;
import app.annotation.Loggable;
import app.dao.LoginDao;
import app.dto.*;
import app.dto.request.*;
import app.mapper.BookingMapper;
import app.services.UserOperations;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;
@Auditable
@Loggable
@RestController
@RequestMapping("/booking")
public class BookingManagementController {
    private final UserOperations userOperations;
    private final BookingMapper bookingMapper;
    private final LoginDao loginDao;

    @Autowired
    public BookingManagementController(UserOperations userOperations, BookingMapper bookingMapper, LoginDao loginDao) {
        this.userOperations = userOperations;
        this.bookingMapper = bookingMapper;
        this.loginDao = loginDao;
    }

    /**
     * view all user bookings
     * @param session
     * @return status and all bookings
     */
    @GetMapping(value = "/mybookings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingResponse>> getMyBookings(HttpSession session) {
        String login = (String) session.getAttribute("login");
        if(loginDao.checkIfUserExist(login)){
            List<BookingDto> allBookings = userOperations.getAllUserBooking(login);
            List<BookingResponse> allBookingsResponse = new ArrayList<>();
            for(BookingDto bookingDto: allBookings){
                allBookingsResponse.add(bookingMapper.bookingDtoToBookingResponse(bookingDto));
            }

            return ResponseEntity.ok(allBookingsResponse);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /**
     * book hall
     * @param session
     * @param hallBookRequest jasom with hall namde date and time
     * @return status and massage
     */

    @PostMapping(value = "/bookHall", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> bookHall(HttpSession session, @RequestBody HallBookRequest hallBookRequest) {
        String login = (String) session.getAttribute("login");
        if(loginDao.checkIfUserExist(login)) {
            BookingDto bookingDto = bookingMapper.hallBookRequestToDto(hallBookRequest);
            bookingDto.setUserLogin(login);
            OperationResult operationResult = userOperations.bookHall(bookingDto);
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /**
     * book desk
     * @param session
     * @param hallBookRequest json with room, desk number, date and time
     * @return status and message
     */

    @PostMapping(value = "/bookDesk", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> bookDesk(HttpSession session, @RequestBody HallBookRequest hallBookRequest) {
        String login = (String) session.getAttribute("login");
        BookingDto bookingDto = bookingMapper.hallBookRequestToDto(hallBookRequest);
        bookingDto.setUserLogin(login);
        OperationResult operationResult = userOperations.bookHall(bookingDto);
        return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
    }

    /**
     * view all available slots for hall
     * @param session
     * @param hallAvailableRequest json with date
     * @return status and slots
     */

    @GetMapping(value = "/availableHall", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> availableHall(HttpSession session, @RequestBody HallAvailableRequest hallAvailableRequest) {
        String login = (String) session.getAttribute("login");
        try {
            Map<String, Set<LocalTime>> allSlots = userOperations.getAllAvailableHallSlotsOnDate(hallAvailableRequest.getDate());
            return ResponseEntity.ok(allSlots);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    /**
     * view all available desk slots
     * @param session
     * @param deskAvailableRequest json with date
     * @return status and slots
     */

    @GetMapping(value = "/availableDeskInRoom", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> availableDeskInRoom(HttpSession session, @RequestBody DeskAvailableRequest deskAvailableRequest) {
        String login = (String) session.getAttribute("login");
        try {
            Map<Integer, Set<LocalTime>> allSlots = userOperations.getAllAvailableDeskInRoomSlotsOnDate(
                    deskAvailableRequest.getDate(),
                    deskAvailableRequest.getRoomName());
            return ResponseEntity.ok(allSlots);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    /**
     * delete booking
     * @param session
     * @param bookingDeleteRequest json with id booking
     * @return status and message
     */

    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteBooking(HttpSession session, @RequestBody BookingDeleteRequest bookingDeleteRequest) {
        String login = (String) session.getAttribute("login");
        BookingDeleteDto bookingDeleteDto = bookingMapper.bookingDeleteRequestToDto(bookingDeleteRequest);
        bookingDeleteDto.setLogin(login);
        OperationResult operationResult = userOperations.deleteBookings(bookingDeleteDto.getLogin(), bookingDeleteDto.getBookingId());
        return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
    }

    /**
     * change booking
     * @param session
     * @param bookingChangeRequest
     * @return status and message
     */

    @PutMapping(value = "/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changeBooking(HttpSession session, @RequestBody BookingChangeRequest bookingChangeRequest) {
        String login = (String) session.getAttribute("login");
        OperationResult operationResult = userOperations.changeBookingDateAndTime(
                bookingChangeRequest.getBookingId(),
                bookingChangeRequest.getDate(),
                bookingChangeRequest.getStartTime(),
                bookingChangeRequest.getEndTime());
        return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
    }
}
