package app;

import app.dto.ReqBookingDeskDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.Test;

public class MapperTest {
    @Test
    public void mapperTest() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());


        var str = """
                {
                    "roomName": "Red",
                    "deskNumber": 2,
                    "date": "2024-01-01",
                    "startTime": "10:00:01",
                    "endTime": "15:00:05"
                }
                """;

        ReqBookingDeskDto reqRoom = mapper.readValue(str, ReqBookingDeskDto.class);
        System.out.println(reqRoom);
    }
}
