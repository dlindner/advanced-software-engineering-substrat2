package io.nyando.factorix.rest.workplace;

import com.google.gson.Gson;
import io.nyando.factorix.model.work.Workplace;

import java.util.Collection;

public class WorkplaceJSONSerializer implements WorkplaceSerializer {

    private final Gson gson;

    public WorkplaceJSONSerializer() {
        this.gson = new Gson();
    }

    private WorkplaceRenderModel createWorkplaceModel(Workplace workplace) {
        return new WorkplaceRenderModel(
                workplace.getWorkplaceID(),
                workplace.getProcessType(),
                workplace.currentProductID(),
                workplace.getTaskQueue().toArray(new String[0])
        );
    }

    @Override
    public String getContentTypeHeader() {
        return "application/json";
    }

    @Override
    public String marshal(Workplace workplace) {
        return this.gson.toJson(this.createWorkplaceModel(workplace));
    }

    @Override
    public String marshalCollection(Collection<Workplace> workplaces) {
        return this.gson.toJson(workplaces.stream().map(this::createWorkplaceModel).toArray());
    }

    @Override
    public QueueModification unmarshalQueueMod(String serializedQueueMod) {
        return this.gson.fromJson(serializedQueueMod, QueueModification.class);
    }

}
