package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.UsersApi
import fi.metatavu.megasense.dataportal.users.UsersController
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Endpoints to control users
 */
@Stateful
@RequestScoped
class UsersApiImpl: UsersApi, AbstractApi() {
    @Inject
    private lateinit var usersController: UsersController

    override fun deleteUser(): Response {
        usersController.deleteUser(loggerUserId!!)
        return createNoContent()
    }
}