package com.xmbest.ddmlib

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.*
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

/**
 * 剪切板工具类
 */
object ClipboardUtil {

    /**
     * 1. 从剪切板获得文字
     */
    fun getSysClipboardText(): String? {
        var ret = ""
        val sysClip = Toolkit.getDefaultToolkit().systemClipboard
        // 获取剪切板中的内容
        val clipTf = sysClip.getContents(null)
        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = clipTf
                        .getTransferData(DataFlavor.stringFlavor) as String
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return ret
    }

    /**
     * 2.将字符串复制到剪切板。
     */
    fun setSysClipboardText(writeMe: String?) {
        val clip = Toolkit.getDefaultToolkit().systemClipboard
        val tText: Transferable = StringSelection(writeMe)
        clip.setContents(tText, null)
    }

    /**
     * 3. 从剪切板获得图片。
     */
    @Throws(Exception::class)
    fun getImageFromClipboard(): Image? {
        val sysc = Toolkit.getDefaultToolkit().systemClipboard
        val cc = sysc.getContents(null)
        if (cc == null) return null else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) return cc.getTransferData(
            DataFlavor.imageFlavor
        ) as Image
        return null
    }

    /**
     * 4.复制图片到剪切板。
     */
    @Throws(Exception::class)
    fun setClipboardImage(image: Image) {
        val trans: Transferable = object : Transferable {
            override fun getTransferDataFlavors(): Array<DataFlavor> {
                return arrayOf(DataFlavor.imageFlavor)
            }

            override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
                return DataFlavor.imageFlavor.equals(flavor)
            }

            @Throws(UnsupportedFlavorException::class, IOException::class)
            override fun getTransferData(flavor: DataFlavor): Any {
                if (isDataFlavorSupported(flavor)) return image
                throw UnsupportedFlavorException(flavor)
            }
        }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(
            trans,
            null
        )
    }

    /**
     * 5.通过流获取，可读取图文混合
     */
    @Throws(Exception::class)
    fun getImageAndTextFromClipboard() {
        val sysClip = Toolkit.getDefaultToolkit().systemClipboard
        val clipTf = sysClip.getContents(null)
        val dataList = clipTf.transferDataFlavors
        val wholeLength = 0
        for (i in dataList.indices) {
            val data = dataList[i]
            if (data.subType == "rtf") {
                val reader = data.getReaderForText(clipTf)
                val osw = OutputStreamWriter(
                    FileOutputStream("d:\\test.rtf")
                )
                val c = CharArray(1024)
                var leng = -1
                while (reader.read(c).also { leng = it } != -1) {
                    osw.write(c, wholeLength, leng)
                }
                osw.flush()
                osw.close()
            }
        }
    }

}