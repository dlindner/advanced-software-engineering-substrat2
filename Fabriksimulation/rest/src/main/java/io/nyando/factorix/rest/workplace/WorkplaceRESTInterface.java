package io.nyando.factorix.rest.workplace;

import io.nyando.factorix.model.work.Workplace;
import io.nyando.factorix.services.WorkplaceManager;

import java.util.Optional;

import static spark.Spark.*;
import static java.net.HttpURLConnection.*;

public class WorkplaceRESTInterface {

    private final WorkplaceManager workplaceManager;
    private final WorkplaceSerializer serializer;

    public WorkplaceRESTInterface(WorkplaceManager workplaceManager,
                                  WorkplaceSerializer serializer) {
        this.workplaceManager = workplaceManager;
        this.serializer = serializer;
    }

    public void createRESTInterface() {

        get("/workplace", (req, res) -> {
            res.status(HTTP_OK);
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            return this.serializer.marshalCollection(this.workplaceManager.getWorkplaces());
        });

        get("/workplace/:workplaceID", (req, res) -> {
            Optional<Workplace> workplace = this.workplaceManager.getWorkplace(req.params(":workplaceID"));
            String responseBody = workplace.map(this.serializer::marshal).orElse("");
            res.status(!responseBody.equals("") ? HTTP_OK : HTTP_NOT_FOUND);
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            return responseBody;
        });

        patch("/workplace/:workplaceID/move", (req, res) -> {
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            QueueModification queueMod = this.serializer.unmarshalQueueMod(req.body());
            Optional<Workplace> workplace = this.workplaceManager.getWorkplace(req.params(":workplaceID"));
            if (workplace.isPresent()) {
                res.status(HTTP_OK);
                workplace.get().moveTask(queueMod.sourceIndex(), queueMod.destIndex());
                return this.serializer.marshal(workplace.get());
            } else {
                res.status(HTTP_NOT_FOUND);
                return "{}";
            }
        });

    }

}
