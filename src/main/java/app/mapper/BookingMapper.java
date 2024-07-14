package app.mapper;

import app.dto.*;
import app.dto.request.BookingDeleteRequest;
import app.dto.request.HallBookRequest;
import app.dto.request.RoomBookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "hallName", target = "placeName")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    BookingDto hallBookRequestToDto(HallBookRequest hallBookRequest);

    @Mapping(source = "roomName", target = "placeName")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    BookingDto roomBookRequestToDto(RoomBookRequest roomBookRequest);

    @Mapping(source = "bookingId", target = "bookingId")
    BookingDeleteDto bookingDeleteRequestToDto(BookingDeleteRequest bookingDeleteRequest);
}
