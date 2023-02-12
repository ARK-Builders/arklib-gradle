package space.taran.arklib.domain.kind

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import space.taran.arklib.ResourceId
import space.taran.arklib.domain.index.ResourceMeta
import space.taran.arklib.domain.meta.MetadataStorage
import space.taran.arklib.utils.LogTags.RESOURCES_INDEX
import space.taran.arklib.utils.extension
import java.io.FileNotFoundException
import java.nio.file.Path

object DocumentKindFactory : ResourceKindFactory<ResourceKind.Document> {
    override val acceptedExtensions: Set<String> =
        setOf("pdf", "doc", "docx", "odt", "ods", "md")
    override val acceptedMimeTypes: Set<String>
        get() = setOf("application/pdf")
    override val acceptedKindCode = KindCode.DOCUMENT

    override fun fromPath(
        path: Path,
        meta: ResourceMeta,
    ): ResourceKind.Document {
        if (extension(path) != "pdf") return ResourceKind.Document()

        var parcelFileDescriptor: ParcelFileDescriptor? = null

        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(
                path.toFile(),
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        } catch (e: FileNotFoundException) {
            Log.e(
                RESOURCES_INDEX,
                "Failed to find file at path: $path"
            )
        }
        parcelFileDescriptor ?: return ResourceKind.Document()
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        val totalPages = pdfRenderer.pageCount
        val pages = if (totalPages > 0) totalPages else null

        return ResourceKind.Document(pages)
    }
}
