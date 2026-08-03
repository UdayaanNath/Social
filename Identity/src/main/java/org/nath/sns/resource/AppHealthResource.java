package org.nath.sns.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class AppHealthResource {

    @GET
    public Map getHealth() {
        return Map.of("message", "Healthy!");
    }
}
