
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOver
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.readBitmapFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.xml.Xml
import kotlin.properties.Delegates

var uiFont: BitmapFont by Delegates.notNull()

private val bitmapHeight = 600
private val symbolImage = mutableListOf<Image>()

private lateinit var sText:Text

suspend fun main() = Korge(width = 1500, height = 900, bgcolor = Colors["#555555"]) {

    uiFont = resourcesVfs["fonts/uifont.fnt"].readBitmapFont()
    val listOfSVG = mutableListOf<String>()

    val filesDir = resourcesVfs["bliss_svg"].listSimple()
    val accept = resourcesVfs["accept.png"].readBitmap()

    filesDir.forEach {
        val s = it.readString()
        listOfSVG.add(s)
            val name =  Xml(s).text.split('.')[1].split('\n')[0]
            println("$name")
    }

    var selected = listOfSVG.shuffled().first()
    val symbol = renderSymbol(selected)
    symbol.forEach {
                symbolImage.add(image(it).centerOnStage())
    }
    val name =  Xml(selected).text.split('.')[1].split('\n')[0]
    sText = text(name, textSize = 22.0, color = Colors.WHITE, font = uiFont).alignBottomToTopOf(symbolImage.last())
        .alignRightToRightOf(symbolImage.last())

    val button = container {
        image(accept).scale(0.5, 0.5).xy(600, 750).centerXOnStage()
    }

    button.onClick{
        println("Click")
        symbolImage.forEach { it.removeFromParent() }
        symbolImage.clear()
         selected = listOfSVG.shuffled().first()
        val newSymbol= renderSymbol(selected)

        newSymbol.forEach {
            symbolImage.add(image(it).centerOnStage())
        }
        val newName =  Xml(selected).text.split('.')[1].split('\n')[0]
        sText.removeFromParent()
        sText = text(newName, textSize = 36.0, color = Colors.WHITE, font = uiFont).alignBottomToTopOf(symbolImage.last())
            .alignRightToRightOf(symbolImage.last())
    }
}

private fun renderSymbol(svg:String):List<Bitmap32> {
    val xml = Xml(svg)

    val dwidth = xml.double("width", 6.0)
    val dheight = xml.double("height", 21.0)
    val viewBox = xml.getString("viewBox") ?: "0 0 $dwidth $dheight"

    val actualWidth = viewBox.split(' ')[2].toDouble()
    val actualHeight = viewBox.split(' ')[3].toDouble()

    println("actualWidth = $actualWidth")
    println("actualHeight = $actualHeight")

    val pathList = xml.allChildren.filter { it.toString().contains("path") }
    println("pathList = $pathList")

    val bitmapWidth = (bitmapHeight*actualWidth/actualHeight).toInt()
    val bitmaps = mutableListOf<Bitmap32>()
    for (item in pathList) {
        val mVector = drawPath(bitmapWidth, bitmapHeight, actualWidth, actualHeight, Colors.LIGHTSKYBLUE, item)
        bitmaps.add(mVector)
    }

    return bitmaps
}
