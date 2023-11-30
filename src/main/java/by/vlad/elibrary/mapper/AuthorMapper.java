package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.AuthorRequestDto;
import by.vlad.elibrary.model.dto.response.AuthorResponseDto;
import by.vlad.elibrary.model.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    Author fromDtoToEntity(AuthorRequestDto dto);

    AuthorResponseDto fromEntityToDto(Author author);

    List<AuthorResponseDto> fromEntitiesToDtos(List<Author> authors);
}
