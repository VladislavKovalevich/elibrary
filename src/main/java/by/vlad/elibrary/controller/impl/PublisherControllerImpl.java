package by.vlad.elibrary.controller.impl;

import by.vlad.elibrary.controller.PublisherController;
import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
import by.vlad.elibrary.service.BookComponentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublisherControllerImpl implements PublisherController {

    private final BookComponentsService<PublisherResponseDto, PublisherRequestDto> publisherService;

    @Override
    public ResponseEntity<List<PublisherResponseDto>> returnPublishers() {
        return new ResponseEntity<>(publisherService.returnComponentList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PublisherResponseDto> returnPublisherById(Long id) {
        return new ResponseEntity<>(publisherService.returnComponentById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PublisherResponseDto> createNewPublisher(PublisherRequestDto dto) {
        return new ResponseEntity<>(publisherService.createNewComponent(dto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PublisherResponseDto> updatePublisher(PublisherRequestDto dto) {
        return new ResponseEntity<>(publisherService.updateComponent(dto), HttpStatus.OK);
    }
}
