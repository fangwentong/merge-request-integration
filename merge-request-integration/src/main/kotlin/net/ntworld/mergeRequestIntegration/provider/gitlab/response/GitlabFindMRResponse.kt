package net.ntworld.mergeRequestIntegration.provider.gitlab.response

import net.ntworld.foundation.Error
import net.ntworld.foundation.Response
import org.gitlab4j.api.models.MergeRequest

data class GitlabFindMRResponse(
    override val error: Error?,
    val mergeRequest: MergeRequest
) :Response