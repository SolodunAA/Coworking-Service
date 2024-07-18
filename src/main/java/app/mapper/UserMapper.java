package app.mapper;

import app.dto.UserDto;
import app.dto.request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "login", target = "login")
    @Mapping(source = "password", target = "password")
    UserDto requestToDto(UserRequest userRequest);
}
