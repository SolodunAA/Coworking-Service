package app.mapper;

import app.dto.ReqBookingDeskDto;
import app.dto.BookingDto;
import app.dto.ReqBookingHallDto;
import app.dto.RespBookingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    ReqBookingHallDto bookingDtoToBookingHallDto(BookingDto bookingDto);
    BookingDto bookingHallDtoToBookingDto(ReqBookingHallDto reqBookingHallDto);

    default BookingDto addLoginAndDeskToBookingHallDTO(ReqBookingHallDto reqBookingHallDto, String login) {
        BookingDto bookingDto = bookingHallDtoToBookingDto(reqBookingHallDto);
        bookingDto.setUserLogin(login);
        bookingDto.setDeskNumber(0);
        return bookingDto;
    }
    ReqBookingDeskDto bookingDtoToBookingDeskDto(BookingDto bookingDto);

    BookingDto bookingDeskDtoToBookingDto(ReqBookingDeskDto reqBookingDeskDto);

    default BookingDto addLoginToBookingDeskDTO(ReqBookingDeskDto reqBookingDeskDto, String login) {
        BookingDto bookingDto = bookingDeskDtoToBookingDto(reqBookingDeskDto);
        bookingDto.setUserLogin(login);
        return bookingDto;
    }

    RespBookingDto bookingDtoToRespBookingDto(BookingDto bookingDto);
}
