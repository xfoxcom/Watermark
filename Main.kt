package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {

    val image = image()
    val watermarkList = watermark(image)

    val watermark = watermarkList[0] as BufferedImage
    val percentage = watermarkList[1] as Int
    val flag = watermarkList[2] as Boolean

    println("Input the output image filename (jpg or png extension): ")
    val newFileName = readln()
    if (newFileName.substringAfter('.') != "jpg" && newFileName.substringAfter('.') != "png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }
    val newFile = File(newFileName)
    val newImage = BufferedImage(watermark.width, watermark.height, BufferedImage.TYPE_INT_RGB)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val i = Color(image.getRGB(x, y))
            var w = Color(watermark.getRGB(x, y))
            if(flag) w = Color(watermark.getRGB(x, y), true)
            val color = Color(
                (percentage * w.red + (100 - percentage) * i.red) / 100,
                (percentage * w.green + (100 - percentage) * i.green) / 100,
                (percentage * w.blue + (100 - percentage) * i.blue) / 100,
            )
            if (w.alpha == 255) newImage.setRGB(x,y,color.rgb) else newImage.setRGB(x,y,i.rgb)
        }
    }
    ImageIO.write(newImage, newFileName.substringAfter('.'), newFile)
    println("The watermarked image $newFileName has been created.")
}

fun image(): BufferedImage {
    println("Input the image filename: ")
    val pathImage = readln()
    val fileImage = File(pathImage)
    if (!fileImage.exists()) {
        println("The file $pathImage doesn't exist.")
        exitProcess(0)
    }
    val image = ImageIO.read(fileImage)
    if (image.colorModel.numColorComponents != 3) {
        println("The number of image color components isn't 3.")
        exitProcess(0)
    }
    if (image.colorModel.pixelSize != 24 && image.colorModel.pixelSize != 32) {
        println("The image isn't 24 or 32-bit.")
        exitProcess(0)
    }
    return image
}

fun watermark(image: BufferedImage): MutableList<Any> {
    val flag : Boolean
    println("Input the watermark image filename: ")
    val waterPath = readln()
    val waterFile = File(waterPath)
    if (!waterFile.exists()) {
        println("The file $waterPath doesn't exist.")
        exitProcess(0)
    }
    val watermark = ImageIO.read(waterFile)
    if (watermark.colorModel.numColorComponents != 3) {
        println("The number of watermark color components isn't 3.")
        exitProcess(0)
    }
    if (watermark.colorModel.pixelSize != 24 && watermark.colorModel.pixelSize != 32) {
        println("The watermark isn't 24 or 32-bit.")
        exitProcess(0)
    }
    if (watermark.height != image.height || watermark.width != image.width) {
        println("The image and watermark dimensions are different.")
        exitProcess(0)
    }

    flag = if (watermark.transparency == Transparency.TRANSLUCENT) {
        println("Do you want to use the watermark's Alpha channel?")
        val answer = readln().lowercase()
        answer == "yes"
    } else false

    println("Input the watermark transparency percentage (Integer 0-100): ")
    val percentage = readln()
    if (!percentage.matches("\\d+".toRegex())) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(0)
    }
    if (percentage.toInt() !in 0..100) {
        println("The transparency percentage is out of range.")
        exitProcess(0)
    }
    return mutableListOf(watermark, percentage.toInt(), flag)
}
