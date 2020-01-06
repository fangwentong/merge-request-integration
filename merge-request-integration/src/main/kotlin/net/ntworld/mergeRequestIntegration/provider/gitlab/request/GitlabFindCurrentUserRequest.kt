package net.ntworld.mergeRequestIntegration.provider.gitlab.request

import net.ntworld.foundation.Request
import net.ntworld.mergeRequest.api.ApiCredentials
import net.ntworld.mergeRequestIntegration.provider.gitlab.GitlabRequest
import net.ntworld.mergeRequestIntegration.provider.gitlab.response.GitlabFindUserResponse

data class GitlabFindCurrentUserRequest(
    override val credentials: ApiCredentials
): GitlabRequest, Request<GitlabFindUserResponse>