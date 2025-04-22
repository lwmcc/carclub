package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class AmplifyRepo @Inject constructor(private val amplifyApi: KotlinApiFacade) : RemoteRepo {

    sealed class ContactType() {
        data class Received(val type: String) : ContactType()
        data class Sent(val type: String) : ContactType()
        data class Current(val type: String) : ContactType()
    }

    override suspend fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {
        val filter = UserContact.USER.eq(senderUserId)
            .and(UserContact.CONTACT.eq(receiverUserId))

        val response = Amplify.API.query(ModelQuery.list(UserContact::class.java, filter))
        val count = response.data.items.count()
        emit(count > 0)
    }

    override suspend fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {

        val filter = Invite.SENDER.eq(senderUserId)
            .and(Invite.RECEIVER.eq(receiverUserId))

        val response = Amplify.API.query(ModelQuery.list(Invite::class.java, filter))
        if (response.hasData()) {
            val count = response.data.items.count()
            emit(count > 0)
        } else { // TODO: need try catch
            emit(false)
        }
    }

    override suspend fun fetchUserByUserName(userName: String): Flow<NetResult<User?>> = flow {
        try {
            val response =
                Amplify.API.query(ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)))
            if (response.hasData() && response.data.firstOrNull() != null) {
                emit(NetResult.Success(response.data.first()))
            } else {
                emit(NetResult.Error(ResponseException("No User Name Found")))
            }
        } catch (e: ApiException) {
            emit(NetResult.Error(e))
        }
    }

    override suspend fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean {
        // firstName and lastName are required when creating a record,
        // but here we just need the id because we're creating an invite
        val sender =
            User.builder().userId(DUMMY).firstName("").lastName(DUMMY)
            //User.builder().firstName(DUMMY).lastName(DUMMY).id(senderUserId)
                .build()
        // TODO: move these into function for reuse

        val receiver =
            User.builder().userId(receiverUserId).firstName(DUMMY).lastName(DUMMY)
            //User.builder().firstName(DUMMY).lastName(DUMMY).id(receiverUserId)
            //    .build()

        val invite = Invite
            .builder()
            .sender(senderUserId)
            .receiver(receiverUserId)
            .user(sender)
            .build()

        return try {
            val result = Amplify.API.mutate(ModelMutation.create(invite))
            if (result.hasData()) {
                println("AmplifyRepo ***** ${result.data}")
            } else {
                println("AmplifyRepo ***** NO DATA")
            }

            //Amplify.API.mutate(ModelMutation.create(invite)).data.id != null
            false
        } catch (e: ApiException) {
            // TODO: to log
            false
        }
    }

    override suspend fun cancelInviteToConnect(
        senderUserId: String?,
        receiverUserId: String
    ): Boolean {

/*        val sender = getInviteSender(receiverUserId)
        val receiver = getInviteReceiver(receiverUserId)

        if (sender == null && receiver == null) {
            return false
        }

        return try {
            val response = Amplify.API.mutate(
                ModelMutation.delete(
                    Invite
                        .builder()
                        .sender(sender)
                        .id(fetchInviteId(senderUserId, receiverUserId))
                        .receiver(receiver)
                        .build()
                )
            )
            response.data.id != null
        } catch (e: ApiException) {
            false
        }*/
        return false
    }

    private suspend fun fetchInviteId(senderUserId: String?, receiverUserId: String): String? {
        return try {
            Amplify.API.query(
                ModelQuery.list(
                    Invite::class.java, Invite.SENDER.eq(senderUserId)
                        .and(Invite.RECEIVER.eq(receiverUserId))
                )
            ).data.items.firstOrNull()?.id
        } catch (e: ApiException) {
            null
        }
    }

    // TODOi make private
    override suspend fun fetchContacts(loggedInUserId: String): Flow<GraphQLResponse<PaginatedResult<User>>> =

        flow {
            val response =
                Amplify.API.query(ModelQuery.list(User::class.java, User.USER_ID.eq(loggedInUserId)))

            emit(response)
        }

    override suspend fun createContact(user: User) {
        val response = Amplify.API.mutate(ModelMutation.create(user))
    }

    override suspend fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val senderResponse = sentInvites(loggedInUserId)
            emit(fetchInvites(senderResponse))
        }

    override suspend fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {

            // TODO: and return flow
            // TODO: set up so that no invites does not cause crash
            val senderResponse = sentInvites(loggedInUserId)
            fetchInvites(senderResponse)

            // TODO: change name receivedInvites
            val receiverResponse = receivedInvites(loggedInUserId)
            emit(fetchInvites(receiverResponse))
        }

    private suspend fun sentInvites(loggedInUserId: String): Set<String> {
        val set = mutableSetOf<String>()
        val senderResponse = Amplify.API.query(
            ModelQuery.list(
                Invite::class.java,
                Invite.SENDER.eq(loggedInUserId)
            )
        )

        if (senderResponse.hasData()) {
            senderResponse.data.items.forEach { item ->
                println("AmplifyRepo ***** WHO RECEIVED ${item.receiver}")
            }
        }

        return set
    }

    private suspend fun receivedInvites(loggedInUserId: String): Set<String> {

        val set = mutableSetOf<String>()

        return try {
            val receiverResponse = Amplify.API.query(
                ModelQuery.list(
                    Invite::class.java,
                    Invite.RECEIVER.eq(loggedInUserId)
                )
            )
            receiverResponse.data.items.forEach { item ->
                set.add(item.sender)
            }
            set
        } catch (e: ApiException) {
            emptySet()
        }
    }

    private suspend fun fetchRowId(
        senderUserId: String?,
        receiverUserId: String,
    ): String? {

        val predicate = QueryField.field("senderUserId").eq(senderUserId)
            .and(QueryField.field("receiverUserId").eq(receiverUserId))

        val filter = Invite.SENDER.eq(senderUserId)
            .and(Invite.RECEIVER.eq(receiverUserId))

        val invites = Amplify.API.query(
            ModelQuery.list(Invite::class.java, filter)
        ).data

        println("Amplify ***** USER $senderUserId -- $receiverUserId")
        println("AmplifyRepo ***** INVITES DATA ${invites.toString()}")

        return ""
    }

    private fun getInviteSender(senderUserId: String) =
        User.builder().userId(senderUserId).firstName(DUMMY).lastName(DUMMY).build()

    private fun getInviteReceiver(receiverUserId: String) =
        User.builder().userId(receiverUserId).firstName(DUMMY).lastName(DUMMY).build()

    // TODO: change param, change function name
    private suspend fun fetchInvites(connectionInvites: Set<String>): NetWorkResult<List<Contact>> {

        if (connectionInvites.isEmpty()) {
            return NetWorkResult.Success(emptyList())
        }

        val invites = mutableListOf<Contact>()

        try {
            val predicate = createQueryPredicate(connectionInvites)
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            invites.addAll(UserMapper.toUserList(response))

            return NetWorkResult.Success(invites)
        } catch (e: ApiException) {
            return NetWorkResult.Error(e)
        }
    }

    private fun createQueryPredicate(ids: Set<String>) = ids
        .map { User.USER_ID.eq(it) as QueryPredicate }
        .reduce { acc, value -> acc.or(value) }


    companion object {
        const val DUMMY = "dummy"
    }
}

class ResponseException(message: String) : Exception(message)

open class Contact(
    val userId: String,
    val userName: String,
    val name: String,
    val avatarUri: String,
)

class ReceivedContactInvite(
    val receiverUserId: String,
    val receivedDate: Date, // TODO: use correct type
    userId: String, userName: String, name: String, avatarUri: String,
) : Contact(userId, userName, name, avatarUri)

class SentContactInvite(
    val senderUserId: String,
    val sentDate: Date,
    userId: String, userName: String, name: String, avatarUri: String,
) : Contact(userId, userName, name, avatarUri)

class CurrentContact(
    val senderUserId: String,
    userId: String, userName: String, name: String, avatarUri: String,
) : Contact(userId, userName, name, avatarUri)