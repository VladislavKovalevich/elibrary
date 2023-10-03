package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import by.vlad.elibrary.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    Client convertUserRegistrationDataRequestDtoToClientEntity(UserRegisterDataRequestDto dto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    ClientResponseDto fromEntityToDto(Client client);
}
