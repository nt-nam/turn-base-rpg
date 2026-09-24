package com.pxworld.assets

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.tools.bmfont.BitmapFontWriter
import com.badlogic.gdx.utils.GdxNativesLoader
import com.pxworld.content.ContentLoader
import com.pxworld.content.compiler.ContentCompilation
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.security.MessageDigest
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.ceil
import kotlin.math.sqrt
import kotlin.system.exitProcess

data class FontSpec(val key: String, val file: String, val ttf: String, val size: Int)

data class PipelineResult(val files: Map<String, Long>, val problems: List<String>)

class AssetPipeline(private val source: File, private val art: File, private val content: File, private val output: File) {

    private val flipMask = 0x1FFFFFFFL
    private val problems = mutableListOf<String>()
    private val written = mutableMapOf<String, Long>()

    fun run(): PipelineResult {
        output.deleteRecursively()
        output.mkdirs()
        val bundle = ContentLoader.load(ContentCompilation.readTree(content))
        val referenced = bundle.assetMap.values.map { it.substringBefore("#") }.toSortedSet()
        referenced.forEach { path ->
            when {
                path.endsWith(".atlas") -> copyAtlas(path)
                path.endsWith(".tmx") -> compactMap(path)
                path.startsWith(BACKGROUND_PREFIX) || path.startsWith(FONT_PREFIX) -> Unit
                else -> copy(path)
            }
        }
        BATTLE_BACKGROUNDS.forEach { (season, target) -> background("texture/battle/$season.png", target) }
        generateFonts(charsetFrom(bundle.localization.values.flatMap { it.values }))
        verifyTextures()
        writeManifest()
        return PipelineResult(written.toSortedMap(), problems.toList())
    }

    private fun copy(path: String) {
        val from = File(source, path)
        if (!from.isFile) {
            problems += "missing source $path"
            return
        }
        val to = File(output, path)
        to.parentFile.mkdirs()
        from.copyTo(to, overwrite = true)
        written[path] = to.length()
    }

    private fun copyAtlas(path: String) {
        copy(path)
        val directory = path.substringBeforeLast('/', "")
        File(source, path).readLines().map { it.trim() }.filter { it.endsWith(".png") }.forEach { page ->
            copy(if (directory.isEmpty()) page else "$directory/$page")
        }
    }

    private fun compactMap(path: String) {
        val tmxFile = File(source, path)
        val document = parse(tmxFile)
        val map = document.documentElement
        val tilesets = map.children("tileset").map { element -> Tileset.load(element, tmxFile.parentFile, ::parse) }
        val tileWidth = tilesets.first().tileWidth
        val tileHeight = tilesets.first().tileHeight
        if (tilesets.any { it.tileWidth != tileWidth || it.tileHeight != tileHeight }) problems += "$path mixes tile sizes"
        val layers = map.children("layer").map { layer -> layer to layer.children("data").single() }
        val gids = layers.flatMap { (_, data) -> parseCsv(data.textContent) }.map { it and flipMask }.filter { it != 0L }.distinct().sorted()
        val columns = ceil(sqrt(gids.size.toDouble())).toInt().coerceAtLeast(1)
        val rows = ceil(gids.size / columns.toDouble()).toInt().coerceAtLeast(1)
        val atlas = BufferedImage(columns * tileWidth, rows * tileHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = atlas.createGraphics()
        val remap = mutableMapOf<Long, Long>()
        gids.forEachIndexed { index, gid ->
            val tileset = tilesets.last { it.firstGid <= gid }
            val tile = tileset.tile((gid - tileset.firstGid).toInt())
            graphics.drawImage(tile, (index % columns) * tileWidth, (index / columns) * tileHeight, null)
            remap[gid] = index + 1L
        }
        graphics.dispose()
        val base = tmxFile.nameWithoutExtension
        val tilesetName = "${base}_tiles"
        val directory = path.substringBeforeLast('/')
        val pngFile = File(output, "$directory/$tilesetName.png").apply { parentFile.mkdirs() }
        ImageIO.write(atlas, "png", pngFile)
        written["$directory/$tilesetName.png"] = pngFile.length()
        val tsx = File(output, "$directory/$tilesetName.tsx")
        tsx.writeText(
            """<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" name="$tilesetName" tilewidth="$tileWidth" tileheight="$tileHeight" tilecount="${gids.size}" columns="$columns">
 <image source="$tilesetName.png" width="${atlas.width}" height="${atlas.height}"/>
</tileset>
""",
        )
        written["$directory/$tilesetName.tsx"] = tsx.length()
        map.children("tileset").forEach { map.removeChild(it) }
        val replacement = document.createElement("tileset").apply {
            setAttribute("firstgid", "1")
            setAttribute("source", "$tilesetName.tsx")
        }
        map.insertBefore(replacement, map.firstChildElement())
        layers.forEach { (_, data) ->
            val values = parseCsv(data.textContent).map { raw ->
                val gid = raw and flipMask
                if (gid == 0L) 0L else (raw and flipMask.inv()) or remap.getValue(gid)
            }
            val width = data.parentNode.attributes.getNamedItem("width").nodeValue.toInt()
            data.textContent = "\n" + values.chunked(width).joinToString(",\n") { row -> row.joinToString(",") } + "\n"
        }
        val tmxOut = File(output, path)
        write(document, tmxOut)
        written[path] = tmxOut.length()
    }

    private fun background(sourcePath: String, target: String) {
        val image = ImageIO.read(File(source, sourcePath)) ?: run {
            problems += "missing background $sourcePath"
            return
        }
        val scale = minOf(1.0, BACKGROUND_WIDTH / image.width.toDouble())
        val width = (image.width * scale).toInt()
        val height = (image.height * scale).toInt()
        val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        resized.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
            drawImage(image, 0, 0, width, height, null)
            dispose()
        }
        val file = File(output, target).apply { parentFile.mkdirs() }
        val writer = ImageIO.getImageWritersByFormatName("jpg").next()
        val parameters = writer.defaultWriteParam.apply {
            compressionMode = ImageWriteParam.MODE_EXPLICIT
            compressionQuality = JPEG_QUALITY
        }
        ImageIO.createImageOutputStream(file).use { stream ->
            writer.output = stream
            writer.write(null, IIOImage(resized, null, null), parameters)
        }
        writer.dispose()
        written[target] = file.length()
    }

    private fun charsetFrom(texts: List<String>): String {
        val letters = (FreeTypeFontGenerator.DEFAULT_CHARS + texts.joinToString("") + EXTRA_GLYPHS).toSet()
        return letters.filter { !it.isISOControl() }.sorted().joinToString("")
    }

    private fun generateFonts(charset: String) {
        GdxNativesLoader.load()
        BitmapFontWriter.setOutputFormat(BitmapFontWriter.OutputFormat.Text)
        FONTS.forEach { spec ->
            val generator = FreeTypeFontGenerator(FileHandle(File(art, spec.ttf)))
            val packer = PixmapPacker(FONT_PAGE, FONT_PAGE, Pixmap.Format.RGBA8888, 2, false)
            val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                size = spec.size
                characters = charset
                this.packer = packer
                kerning = true
            }
            val data = generator.generateData(parameter)
            val directory = File(output, spec.file.substringBeforeLast('/')).apply { mkdirs() }
            val name = spec.file.substringAfterLast('/').substringBeforeLast('.')
            val pageFiles: Array<String> = BitmapFontWriter.writePixmaps(packer.pages, FileHandle(directory), name)
            val info = BitmapFontWriter.FontInfo().apply {
                face = "Be Vietnam Pro"
                size = spec.size
                padding = BitmapFontWriter.Padding(1, 1, 1, 1)
            }
            BitmapFontWriter.writeFont(data, pageFiles, FileHandle(File(output, spec.file)), info, FONT_PAGE, FONT_PAGE)
            written[spec.file] = File(output, spec.file).length()
            pageFiles.forEach { page -> written["${spec.file.substringBeforeLast('/')}/$page"] = File(directory, page).length() }
            packer.dispose()
            generator.dispose()
        }
        File(art, "fonts/OFL.txt").copyTo(File(output, "fonts/OFL.txt"), overwrite = true)
    }

    private fun verifyTextures() {
        written.keys.filter { it.endsWith(".png") || it.endsWith(".jpg") }.forEach { path ->
            val image = ImageIO.read(File(output, path)) ?: return@forEach
            if (image.width > MAX_TEXTURE || image.height > MAX_TEXTURE) problems += "$path is ${image.width}x${image.height}, above $MAX_TEXTURE"
        }
        written.keys.groupBy { sha1(File(output, it)) }.values.filter { it.size > 1 }.forEach { problems += "duplicate files: $it" }
    }

    private fun writeManifest() {
        val entries = written.toSortedMap().entries.joinToString(",\n") { (path, bytes) -> """  "$path": {"bytes": $bytes, "sha1": "${sha1(File(output, path))}"}""" }
        File(output, "asset-manifest.json").writeText("{\n$entries\n}\n")
    }

    private fun sha1(file: File): String = MessageDigest.getInstance("SHA-1").digest(file.readBytes()).joinToString("") { "%02x".format(it) }

    private fun parse(file: File): Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

    private fun write(document: Document, file: File) {
        file.parentFile.mkdirs()
        TransformerFactory.newInstance().newTransformer().apply { setOutputProperty(OutputKeys.ENCODING, "UTF-8") }
            .transform(DOMSource(document), StreamResult(file))
    }

    private fun parseCsv(text: String): List<Long> = text.split(',').map { it.trim() }.filter { it.isNotEmpty() }.map { it.toLong() }

    private class Tileset(val firstGid: Long, private val image: BufferedImage, val tileWidth: Int, val tileHeight: Int, private val columns: Int, private val margin: Int, private val spacing: Int) {
        fun tile(index: Int): BufferedImage {
            val x = margin + (index % columns) * (tileWidth + spacing)
            val y = margin + (index / columns) * (tileHeight + spacing)
            val cell = BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB)
            val width = minOf(tileWidth, image.width - x)
            val height = minOf(tileHeight, image.height - y)
            if (width > 0 && height > 0) cell.createGraphics().apply { drawImage(image.getSubimage(x, y, width, height), 0, 0, null); dispose() }
            return cell
        }

        companion object {
            fun load(element: Element, directory: File, parse: (File) -> Document): Tileset {
                val firstGid = element.getAttribute("firstgid").toLong()
                val tsxFile = File(directory, element.getAttribute("source"))
                val tileset = parse(tsxFile).documentElement
                val imageElement = tileset.children("image").single()
                val image = ImageIO.read(File(tsxFile.parentFile, imageElement.getAttribute("source")))
                val tileWidth = tileset.getAttribute("tilewidth").toInt()
                val tileHeight = tileset.getAttribute("tileheight").toInt()
                val margin = tileset.getAttribute("margin").toIntOrNull() ?: 0
                val spacing = tileset.getAttribute("spacing").toIntOrNull() ?: 0
                val columns = tileset.getAttribute("columns").toIntOrNull() ?: ((image.width - 2 * margin + spacing) / (tileWidth + spacing))
                return Tileset(firstGid, image, tileWidth, tileHeight, columns, margin, spacing)
            }
        }
    }

    companion object {
        const val MAX_TEXTURE: Int = 2048
        const val FONT_PAGE: Int = 1024
        const val BACKGROUND_WIDTH: Double = 1920.0
        const val JPEG_QUALITY: Float = 0.85f
        const val BACKGROUND_PREFIX: String = "backgrounds/"
        const val FONT_PREFIX: String = "fonts/"
        const val EXTRA_GLYPHS: String = "·…×★☆—–«»“”‘’•→←↑↓"
        val BATTLE_BACKGROUNDS: List<Pair<String, String>> = listOf("spring", "summer", "autumn", "winter").map { it to "backgrounds/battle_$it.jpg" }
        val FONTS: List<FontSpec> = listOf(
            FontSpec("font:title", "fonts/pxworld_title.fnt", "fonts/BeVietnamPro-Bold.ttf", 30),
            FontSpec("font:body", "fonts/pxworld_body.fnt", "fonts/BeVietnamPro-Medium.ttf", 20),
            FontSpec("font:small", "fonts/pxworld_small.fnt", "fonts/BeVietnamPro-Medium.ttf", 16),
        )
    }
}

private fun Element.children(tag: String): List<Element> =
    (0 until childNodes.length).map { childNodes.item(it) }.filterIsInstance<Element>().filter { it.tagName == tag }

private fun Element.firstChildElement(): org.w3c.dom.Node? =
    (0 until childNodes.length).map { childNodes.item(it) }.firstOrNull { it is Element }

fun main(arguments: Array<String>) {
    require(arguments.size == 4) { "usage: AssetPipeline <legacyAssets> <art> <content> <output>" }
    val (source, art, content, output) = arguments.map(::File)
    val result = AssetPipeline(source, art, content, output).run()
    val total = result.files.values.sum()
    println("asset pipeline: ${result.files.size} files, ${"%.1f".format(total / 1_000_000.0)} MB")
    result.files.entries.sortedByDescending { it.value }.take(8).forEach { (path, bytes) -> println("  ${"%.2f".format(bytes / 1_000_000.0)} MB  $path") }
    result.problems.forEach { println("PROBLEM $it") }
    if (result.problems.isNotEmpty()) exitProcess(1)
}
