package by.vlad.elibrary.service;

import java.util.List;

public interface BookComponentsService<Response,Request> {

    Response returnComponentById(Long componentId);

    List<Response> returnComponentList();

    Response createNewComponent(Request dto);

    Response updateComponent(Request dto);
}
