package app.controller;

import app.dao.LoginDao;
import app.dao.PlaceDao;
import app.dto.OperationResult;
import app.dto.RoleDto;
import app.dto.request.DeskRequest;
import app.dto.request.HallRequest;
import app.dto.request.RoomRequest;
import app.mapper.PlaceMapper;
import app.mapper.PlaceMapperImpl;
import app.services.AdminOperations;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CoworkingManagementControllerTest {
    private MockMvc mockMvc;
    @Mock
    private AdminOperations adminOperations;
    @Mock
    private PlaceDao placeDao;
    @Mock
    private LoginDao loginDao;
    @Spy
    private PlaceMapper placeMapper = new PlaceMapperImpl();
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        CoworkingManagementController coworkingManagementController = new CoworkingManagementController(adminOperations, placeDao, loginDao, placeMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(coworkingManagementController).build();
    }
    @Test
    public void getRoomsTest() throws Exception {
        Set<String> rooms = Set.of("Red", "Green");
        when(placeDao.getAllRooms()).thenReturn(rooms);
        String roomsToJson = new ObjectMapper().writeValueAsString(rooms);

        mockMvc.perform(MockMvcRequestBuilders.get("/coworking/room")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(roomsToJson));
        verify(placeDao, times(1)).getAllRooms();
    }
    @Test
    public void getHallsTest() throws Exception {
        Set<String> hall = Set.of("Moscow", "Paris");
        when(placeDao.getAllHalls()).thenReturn(hall);
        String hallsToJson = new ObjectMapper().writeValueAsString(hall);

        mockMvc.perform(MockMvcRequestBuilders.get("/coworking/hall")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(hallsToJson));
        verify(placeDao, times(1)).getAllHalls();
    }
    @Test
    public void getDesksTest() throws Exception {
        Set<Integer> desks = Set.of(1, 2, 3);
        String room = "Red";
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setRoomName(room);
        String desksToJson = new ObjectMapper().writeValueAsString(desks);

        String json = """
                   {
                   "roomName": "Red"
                   }
                """;

        when(placeDao.getSetOfAllDesksInRoom(room)).thenReturn(desks);

        mockMvc.perform(MockMvcRequestBuilders.get("/coworking/desk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(desksToJson));
        verify(placeDao, times(1)).getSetOfAllDesksInRoom(room);
        verify(placeMapper, times(1)).roomRequestToDto(roomRequest);
    }
    @Test
    public void addRoomTest() throws Exception {
        String room = "Red";
        String login = "admin";
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setRoomName(room);

        String json = """
                   {
                   "roomName": "Red"
                   }
                """;
        OperationResult operationResult = new OperationResult("Added successfully", 200);

        when(adminOperations.addRoom(room)).thenReturn(operationResult);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        when(loginDao.getUserRole(login)).thenReturn(RoleDto.ADMIN);


        mockMvc.perform(MockMvcRequestBuilders.post("/coworking/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttr("login", login))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(adminOperations, times(1)).addRoom(room);
        verify(loginDao, times(1)).checkIfUserExist(login);
        verify(placeMapper, times(1)).roomRequestToDto(roomRequest);
    }
    @Test
    public void addHallTest() throws Exception {
        String hall = "Paris";
        String login = "admin";
        HallRequest hallRequest = new HallRequest();
        hallRequest.setHallName(hall);
        String json = """
                   {
                   "hallName": "Paris"
                   }
                """;
        OperationResult operationResult = new OperationResult("Added successfully", 200);

        when(adminOperations.addHall(hall)).thenReturn(operationResult);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        when(loginDao.getUserRole(login)).thenReturn(RoleDto.ADMIN);


        mockMvc.perform(MockMvcRequestBuilders.post("/coworking/hall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttr("login", login))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(adminOperations, times(1)).addHall(hall);
        verify(loginDao, times(1)).checkIfUserExist(login);
    }
    @Test
    public void addDeskTest() throws Exception {
        String room = "Red";
        String login = "admin";
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setRoomName(room);

        String json = """
                   {
                   "roomName": "Red"
                   }
                """;
        OperationResult operationResult = new OperationResult("Added successfully", 200);

        when(adminOperations.addDesk(room)).thenReturn(operationResult);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        when(loginDao.getUserRole(login)).thenReturn(RoleDto.ADMIN);


        mockMvc.perform(MockMvcRequestBuilders.post("/coworking/desk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttr("login", login))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(adminOperations, times(1)).addDesk(room);
        verify(loginDao, times(1)).checkIfUserExist(login);
        verify(placeMapper, times(1)).roomRequestToDto(roomRequest);
    }

    @Test
    public void deleteDeskTest() throws Exception {
        String room = "Red";
        int deskNumber = 1;
        String login = "admin";
        DeskRequest deskRequest = new DeskRequest();
        deskRequest.setRoomName(room);
        deskRequest.setDeskNumber(deskNumber);

        String json = """
                   {
                   "roomName": "Red",
                   "deskNumber": 1
                   }
                """;
        OperationResult operationResult = new OperationResult("Desk deleted successfully", 200);

        when(adminOperations.deleteDesk(room, deskNumber)).thenReturn(operationResult);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        when(loginDao.getUserRole(login)).thenReturn(RoleDto.ADMIN);


        mockMvc.perform(MockMvcRequestBuilders.delete("/coworking/desk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttr("login", login))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(adminOperations, times(1)).deleteDesk(room, deskNumber);
        verify(loginDao, times(1)).checkIfUserExist(login);
        verify(placeMapper, times(1)).deskRequestToDto(deskRequest);
    }
    @Test
    public void deleteRoomTest() throws Exception {
        String room = "Red";
        String login = "admin";
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setRoomName(room);

        String json = """
                   {
                   "roomName": "Red"
                   }
                """;
        OperationResult operationResult = new OperationResult("Deleted successfully", 200);

        when(adminOperations.deleteRoom(room)).thenReturn(operationResult);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        when(loginDao.getUserRole(login)).thenReturn(RoleDto.ADMIN);


        mockMvc.perform(MockMvcRequestBuilders.delete("/coworking/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttr("login", login))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(adminOperations, times(1)).deleteRoom(room);
        verify(loginDao, times(1)).checkIfUserExist(login);
        verify(placeMapper, times(1)).roomRequestToDto(roomRequest);
    }
    @Test
    public void deleteHallTest() throws Exception {
        String hall = "Paris";
        String login = "admin";
        HallRequest hallRequest = new HallRequest();
        hallRequest.setHallName(hall);
        String json = """
                   {
                   "hallName": "Paris"
                   }
                """;
        OperationResult operationResult = new OperationResult("Deleted successfully", 200);

        when(adminOperations.deleteHall(hall)).thenReturn(operationResult);
        when(loginDao.checkIfUserExist(login)).thenReturn(true);
        when(loginDao.getUserRole(login)).thenReturn(RoleDto.ADMIN);


        mockMvc.perform(MockMvcRequestBuilders.delete("/coworking/hall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttr("login", login))
                .andExpect(status().isOk())
                .andExpect(content().string(operationResult.getMessage()));
        verify(adminOperations, times(1)).deleteHall(hall);
        verify(loginDao, times(1)).checkIfUserExist(login);
        verify(placeMapper, times(1)).hallRequestToDto(hallRequest);
    }
}
