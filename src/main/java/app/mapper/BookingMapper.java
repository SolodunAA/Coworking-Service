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
    default BookingDto hallBookRequestToDto(HallBookRequest hallBookRequest, String login){
        BookingDto bookingDto = hallBookRequestToDto(hallBookRequest);
        bookingDto.setUserLogin(login);
        return bookingDto;
    }

    @Mapping(source = "roomName", target = "placeName")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    BookingDto roomBookRequestToDto(RoomBookRequest roomBookRequest);
    default BookingDto roomBookRequestToDto(RoomBookRequest roomBookRequest, String login){
        BookingDto bookingDto = roomBookRequestToDto(roomBookRequest);
        bookingDto.setUserLogin(login);
        return bookingDto;
    }

    @Mapping(source = "bookingId", target = "bookingId")
    BookingDeleteDto bookingDeleteRequestToDto(BookingDeleteRequest bookingDeleteRequest);
    default BookingDeleteDto bookingDeleteRequestToDto(BookingDeleteRequest bookingDeleteRequest, String login){
        BookingDeleteDto bookingDeleteDto = bookingDeleteRequestToDto(bookingDeleteRequest);
        bookingDeleteDto.setLogin(login);
        return bookingDeleteDto;
    }
}
