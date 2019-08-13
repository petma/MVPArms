/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jess.arms.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.ArrayList
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater

/**
 * ================================================
 * 处理压缩和解压的工具类
 *
 *
 * Created by JessYan on 10/05/2016
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class ZipHelper private constructor() {

    init {
        throw IllegalStateException("you can't instantiate me!")
    }

    companion object {

        /**
         * zlib decompress 2 String
         *
         * @param bytesToDecompress
         * @param charsetName
         * @return
         */
        @JvmOverloads
        fun decompressToStringForZlib(bytesToDecompress: ByteArray, charsetName: String = "UTF-8"): String? {
            val bytesDecompressed = decompressForZlib(
                    bytesToDecompress
            )

            var returnValue: String? = null

            try {
                returnValue = String(
                        bytesDecompressed,
                        0,
                        bytesDecompressed!!.size,
                        charsetName
                )
            } catch (uee: UnsupportedEncodingException) {
                uee.printStackTrace()
            }

            return returnValue

        }

        /**
         * zlib decompress 2 byte
         *
         * @param bytesToDecompress
         * @return
         */
        fun decompressForZlib(bytesToDecompress: ByteArray): ByteArray? {
            var returnValues: ByteArray? = null

            val inflater = Inflater()

            val numberOfBytesToDecompress = bytesToDecompress.size

            inflater.setInput(
                    bytesToDecompress,
                    0,
                    numberOfBytesToDecompress
            )

            var numberOfBytesDecompressedSoFar = 0
            val bytesDecompressedSoFar = ArrayList<Byte>()

            try {
                while (inflater.needsInput() == false) {
                    val bytesDecompressedBuffer = ByteArray(numberOfBytesToDecompress)

                    val numberOfBytesDecompressedThisTime = inflater.inflate(
                            bytesDecompressedBuffer
                    )

                    numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime

                    for (b in 0 until numberOfBytesDecompressedThisTime) {
                        bytesDecompressedSoFar.add(bytesDecompressedBuffer[b])
                    }
                }

                returnValues = ByteArray(bytesDecompressedSoFar.size)
                for (b in returnValues.indices) {
                    returnValues[b] = bytesDecompressedSoFar[b]
                }

            } catch (dfe: DataFormatException) {
                dfe.printStackTrace()
            }

            inflater.end()

            return returnValues
        }

        /**
         * zlib compress 2 byte
         *
         * @param bytesToCompress
         * @return
         */
        fun compressForZlib(bytesToCompress: ByteArray): ByteArray {
            val deflater = Deflater()
            deflater.setInput(bytesToCompress)
            deflater.finish()

            val bytesCompressed = ByteArray(java.lang.Short.MAX_VALUE)

            val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)

            val returnValues = ByteArray(numberOfBytesAfterCompression)

            System.arraycopy(
                    bytesCompressed,
                    0,
                    returnValues,
                    0,
                    numberOfBytesAfterCompression
            )

            return returnValues
        }

        /**
         * zlib compress 2 byte
         *
         * @param stringToCompress
         * @return
         */
        fun compressForZlib(stringToCompress: String): ByteArray? {
            var returnValues: ByteArray? = null

            try {

                returnValues = compressForZlib(
                        stringToCompress.toByteArray(charset("UTF-8"))
                )
            } catch (uee: UnsupportedEncodingException) {
                uee.printStackTrace()
            }

            return returnValues
        }

        /**
         * gzip compress 2 byte
         *
         * @param string
         * @return
         * @throws IOException
         */
        fun compressForGzip(string: String): ByteArray? {
            var os: ByteArrayOutputStream? = null
            var gos: GZIPOutputStream? = null
            try {
                os = ByteArrayOutputStream(string.length)
                gos = GZIPOutputStream(os)
                gos.write(string.toByteArray(charset("UTF-8")))
                return os.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeQuietly(gos)
                closeQuietly(os)
            }
            return null
        }

        /**
         * gzip decompress 2 string
         *
         * @param compressed
         * @param charsetName
         * @return
         */
        @JvmOverloads
        fun decompressForGzip(compressed: ByteArray, charsetName: String = "UTF-8"): String? {
            val BUFFER_SIZE = compressed.size
            var gis: GZIPInputStream? = null
            var `is`: ByteArrayInputStream? = null
            try {
                `is` = ByteArrayInputStream(compressed)
                gis = GZIPInputStream(`is`, BUFFER_SIZE)
                val string = StringBuilder()
                val data = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while ((bytesRead = gis.read(data)) != -1) {
                    string.append(String(data, 0, bytesRead, charsetName))
                }
                return string.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeQuietly(gis)
                closeQuietly(`is`)
            }
            return null
        }

        fun closeQuietly(closeable: Closeable?) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (rethrown: RuntimeException) {
                    throw rethrown
                } catch (ignored: Exception) {
                }

            }
        }
    }
}
/**
 * zlib decompress 2 String
 *
 * @param bytesToDecompress
 * @return
 */
/**
 * gzip decompress 2 string
 *
 * @param compressed
 * @return
 * @throws IOException
 */
