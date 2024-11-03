package io.nyando.factorix.rest.workplace;

import io.nyando.factorix.model.work.Workplace;

import java.util.Collection;

public interface WorkplaceSerializer {

    String getContentTypeHeader();

    String marshal(Workplace workplace);

    String marshalCollection(Collection<Workplace> workplaces);

    QueueModification unmarshalQueueMod(String serializedQueueMod);

}
