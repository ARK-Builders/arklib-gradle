package space.taran.arklib.domain.index

import space.taran.arklib.ResourceId
import space.taran.arklib.domain.kind.ResourceKind
import space.taran.arklib.domain.meta.MetadataStorage
import space.taran.arklib.utils.MetaResult
import space.taran.arklib.utils.extension
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime

data class ResourceMeta(
    val id: ResourceId,
    val name: String,
    val extension: String,
    val modified: FileTime,
    var kind: ResourceKind?,
) {

    fun size() = id.dataSize

    companion object {
        fun fromPath(
            id: ResourceId,
            path: Path,
            metadataStorage: MetadataStorage
        ): MetaResult {
            val size = Files.size(path)
            if (size < 1) {
                return MetaResult.failure(IOException("Invalid file size"))
            }

            val meta = ResourceMeta(
                id = id,
                name = path.fileName.toString(),
                extension = extension(path),
                modified = Files.getLastModifiedTime(path),
                kind = null
            )

            var kindDetectException: Throwable? = null

            metadataStorage.locateOrGenerateKind(path, meta)
                .onSuccess { meta.kind = it }
                .onFailure { kindDetectException = it }

            return MetaResult(meta, kindDetectException)
        }
    }
}
