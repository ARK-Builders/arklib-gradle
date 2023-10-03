package space.taran.arklib.lib

import dev.arkbuilders.arklib.arkFolder
import dev.arkbuilders.arklib.arkMetadata
import dev.arkbuilders.arklib.arkPreviews
import dev.arkbuilders.arklib.createLinkFile
import dev.arkbuilders.arklib.fetchLinkData
import dev.arkbuilders.arklib.getLinkHash
import dev.arkbuilders.arklib.loadLinkFile
import junit.framework.Assert
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import space.taran.arklib.utils.TestFiles
import kotlin.io.path.exists

class LinkTest {

    val root = TestFiles.root1
    val url = "https://github.com/ARK-Builders/arklib-android"

    @Before
    fun before() {
        System.loadLibrary("arklib")
    }

    @Test
    fun linkIsCreated() {
        createLinkFile()

        val linkId = getLinkHash(url)
        assert(root.resolve("$linkId.link").exists())
        println(root.arkFolder().arkMetadata().resolve(linkId))
        assert(root.resolve(".ark").resolve("meta").resolve(linkId).exists())
        assert(root.resolve(".ark").resolve("previews").resolve(linkId).exists())
    }

    @Test
    fun linkIsFetched() {
        val linkData = fetchLinkData(url)!!
        assertNotNull(linkData.url)
        assertNotNull(linkData.title)
        assertNotNull(linkData.desc)
        assertNotNull(linkData.imageUrl)
    }

    @Test
    fun linkFileIsLoaded() {
        createLinkFile()

        val linkId = getLinkHash(url)
        val linkData = loadLinkFile(root, root.resolve("$linkId.link"))
        assertNotNull(linkData.url)
        assertNotNull(linkData.title)
        assertNotNull(linkData.desc)
    }

    private fun createLinkFile() = createLinkFile(
        root,
        title = "",
        desc = "",
        url,
        root,
        downloadPreview = true
    )
}