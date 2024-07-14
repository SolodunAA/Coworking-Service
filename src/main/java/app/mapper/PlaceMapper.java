package app.mapper;

import app.dto.HallDto;
import app.dto.RoomDto;
import app.dto.request.DeskDto;
import app.dto.request.DeskRequest;
import app.dto.request.HallRequest;
import app.dto.request.RoomRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaceMapper {
    @Mapping(source = "roomName", target = "roomName")
    RoomDto roomRequestToDto(RoomRequest roomRequest);
    @Mapping(source = "hallName", target = "hallName")
    HallDto hallRequestToDto(HallRequest hallRequest);
    @Mapping(source = "roomName", target = "roomName")
    @Mapping(source = "deskNumber", target = "deskNumber")
    DeskDto deskRequestToDto(DeskRequest deskRequest);

}
