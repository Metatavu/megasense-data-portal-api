package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.SystemApi

import java.util.UUID
import java.util.stream.Collectors
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.Response


/**
 * System API REST endpoints
 */
@RequestScoped
@Stateful
open class SystemApiImpl(): SystemApi {

    override fun ping(): Response? {
        return Response.ok("pong").build()
    }

}