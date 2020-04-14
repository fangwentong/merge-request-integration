package net.ntworld.mergeRequestIntegrationIde.infrastructure

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project as IdeaProject
import com.intellij.openapi.vcs.changes.Change
import com.intellij.util.messages.MessageBusConnection
import git4idea.repo.GitRepository
import net.ntworld.mergeRequest.*
import net.ntworld.mergeRequestIntegrationIde.util.RepositoryUtil

class ReviewContextImpl(
    override val project: IdeaProject,
    override val providerData: ProviderData,
    override val mergeRequestInfo: MergeRequestInfo,
    override val messageBusConnection: MessageBusConnection
) : ReviewContext {
    private val myLogger = Logger.getInstance(this.javaClass)
    private val myOpeningChange = mutableListOf<Change>()
    private val myCommentsMap = mutableMapOf<String, MutableList<Comment>>()

    override var diffReference: DiffReference? = null

    override val repository: GitRepository? = RepositoryUtil.findRepository(project, providerData)

    override var commits: List<Commit> = listOf()

    override var comments: List<Comment> = listOf()
        set(value) {
            field = value
            buildCommentsMap(value)
        }

    override fun getCommentsByPath(path: String): List<Comment> {
        val crossPlatformsPath = RepositoryUtil.transformToCrossPlatformsPath(path)
        val comments = myCommentsMap[crossPlatformsPath]
        if (null !== comments) {
            return comments
        }
        myLogger.info("There is no comments for $crossPlatformsPath")
        return listOf()
    }

    override fun openChange(change: Change) {
    }

    override fun hasAnyChangeOpened(): Boolean {
        return myOpeningChange.isNotEmpty()
    }

    override fun closeAllChanges() {
    }

    private fun buildCommentsMap(value: Collection<Comment>) {
        if (null === repository) {
            return
        }
        myCommentsMap.clear()
        for (comment in value) {
            val position = comment.position
            if (null === position) {
                continue
            }
            if (null !== position.newPath) {
                doHashComment(repository, position.newPath!!, comment)
            }
            if (null !== position.oldPath) {
                doHashComment(repository, position.oldPath!!, comment)
            }
        }
        myLogger.info("myCommentsMap was built successfully")
        myCommentsMap.forEach { (path, comments) ->
            val commentIds = comments.map { it.id }
            myLogger.info("$path contains ${commentIds.joinToString(",")}")
        }
    }

    private fun doHashComment(repository: GitRepository, path: String, comment: Comment) {
        val fullPath = RepositoryUtil.findAbsoluteCrossPlatformsPath(repository, path)
        val list = myCommentsMap[fullPath]
        if (null === list) {
            myCommentsMap[fullPath] = mutableListOf(comment)
        } else {
            if (!list.contains(comment)) {
                list.add(comment)
            }
        }
    }
}