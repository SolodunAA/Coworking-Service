package app.controller;

import app.annotation.Auditable;
import app.annotation.Loggable;
import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dto.*;
import app.dto.request.DeskDto;
import app.dto.request.DeskRequest;
import app.dto.request.HallRequest;
import app.dto.request.RoomRequest;
import app.mapper.PlaceMapper;
import app.services.AdminOperations;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
@Auditable
@Loggable
@RestController
@RequestMapping("/coworking")
public class CoworkingManagementController {
    private final AdminOperations adminOperations;
    private final PlaceDao placeDao;
    private final LoginDao loginDao;
    private final PlaceMapper placeMapper;

    @Autowired
    public CoworkingManagementController(AdminOperations adminOperations, PlaceDao placeDao, LoginDao loginDao, PlaceMapper placeMapper) {
        this.adminOperations = adminOperations;
        this.placeDao = placeDao;
        this.loginDao = loginDao;
        this.placeMapper = placeMapper;
    }

    @GetMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllRooms(HttpSession session) {
        try {
            Set<String> allRooms = placeDao.getAllRooms();
            return ResponseEntity.ok(allRooms);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @GetMapping(value = "/hall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllHalls(HttpSession session) {
        try {
            Set<String> allHalls = placeDao.getAllHalls();
            return ResponseEntity.ok(allHalls);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @GetMapping(value = "/desk", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllDesksInRoom(HttpSession session, @RequestBody RoomRequest roomRequest) {
        RoomDto roomDto = placeMapper.roomRequestToDto(roomRequest);
        try {
            Set<Integer> allDesksInRoom = placeDao.getSetOfAllDesksInRoom(roomDto.getRoomName());
            return ResponseEntity.ok(allDesksInRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PostMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addRoom(HttpSession session, @RequestBody RoomRequest roomRequest) {
        String login = (String) session.getAttribute("login");
        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {
            RoomDto roomDto = placeMapper.roomRequestToDto(roomRequest);
            OperationResult operationResult = adminOperations.addRoom(roomDto.getRoomName());
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return ResponseEntity.status(401).body("You don't have administrator rights");
    }

    @PostMapping(value = "/hall", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addHall(HttpSession session, @RequestBody HallRequest hallRequest) {
        String login = (String) session.getAttribute("login");
        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {
            HallDto hallDto = placeMapper.hallRequestToDto(hallRequest);
            OperationResult operationResult = adminOperations.addHall(hallDto.getHallName());
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return ResponseEntity.status(401).body("You don't have administrator rights");
    }

    @PostMapping(value = "/desk", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addDesk(HttpSession session, @RequestBody RoomRequest roomRequest) {
        String login = (String) session.getAttribute("login");
        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {
            RoomDto roomDto = placeMapper.roomRequestToDto(roomRequest);
            OperationResult operationResult = adminOperations.addDesk(roomDto.getRoomName());
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return ResponseEntity.status(401).body("You don't have administrator rights");
    }

    @DeleteMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteRoom(HttpSession session, @RequestBody RoomRequest roomRequest) {
        String login = (String) session.getAttribute("login");
        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {
            RoomDto roomDto = placeMapper.roomRequestToDto(roomRequest);
            OperationResult operationResult = adminOperations.deleteRoom(roomDto.getRoomName());
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return ResponseEntity.status(401).body("You don't have administrator rights");
    }

    @DeleteMapping(value = "/desk", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteDesk(HttpSession session, @RequestBody DeskRequest deskRequest) {
        String login = (String) session.getAttribute("login");
        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {
            DeskDto deskDto = placeMapper.deskRequestToDto(deskRequest);
            OperationResult operationResult = adminOperations.deleteDesk(deskDto.getRoomName(), deskDto.getDeskNumber());
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return ResponseEntity.status(401).body("You don't have administrator rights");
    }
    @DeleteMapping(value = "/hall", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteHall(HttpSession session, @RequestBody HallRequest hallRequest) {
        String login = (String) session.getAttribute("login");
        if (loginDao.checkIfUserExist(login) && loginDao.getUserRole(login).equals(RoleDto.ADMIN)) {
            HallDto hallDto = placeMapper.hallRequestToDto(hallRequest);
            OperationResult operationResult = adminOperations.deleteHall(hallDto.getHallName());
            return ResponseEntity.status(operationResult.getErrCode()).body(operationResult.getMessage());
        }
        return ResponseEntity.status(401).body("You don't have administrator rights");
    }
}
