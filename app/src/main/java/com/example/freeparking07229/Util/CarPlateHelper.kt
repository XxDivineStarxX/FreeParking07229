package com.example.freeparking07229.Util

import java.lang.StringBuilder

object CarPlateHelper {

    val CHARS = listOf(
        "jing", "hu", "jin", "yu", "yi", "jin", "meng", "liao", "ji", "hei",
        "su", "zhe", "wan", "min", "gan", "lu", "yu", "e", "xiang", "yue",
        "gui", "qiong", "chuan", "gui", "yun", "zang", "shan", "gan", "qing", "ning",
        "xin"
    )

    val WORLDS = listOf(
        "京", "津", "沪", "渝", "YI", "琼", "蒙", "粤", "豫", "鄂", "湘",
        "浙", "苏", "鲁", "川", "冀", "晋", "辽", "吉", "黑", "陕", "甘",
        "青", "云", "贵", "宁", "新", "藏", "赣", "皖", "闽", "台"
    )

    val map = mapOf(
        "jing" to "京", "hu" to "沪", "jin" to "津", "yu" to "豫",
        "meng" to "蒙", "liao" to "辽", "ji" to "冀", "hei" to "黑",
        "su" to "苏", "zhe" to "浙", "wan" to "皖", "min" to "闽",
        "gan" to "甘", "lu" to "鲁", "yu" to "豫", "e" to "鄂",
        "xiang" to "湘", "yue" to "粤", "gui" to "贵", "qiong" to "琼",
        "chuan" to "川", "yun" to "云", "zang" to "藏", "shan" to "陕",
        "qing" to "青", "ning" to "宁", "xin" to "新"
    )

    public fun carPlateNormlize(str: String): String {
        val builder = StringBuilder()
        var pre = ""
        var after = ""

        var i = 0
        while (i < str.length) {
            val c = str[i]
            if (c >= 'a' && c <= 'z') {
                builder.append(c)
                i = i + 1
            } else break
        }
        pre = builder.toString()
        pre = map[pre].toString()


        builder.clear()
        while (i < str.length) {
            builder.append(str[i])
            i++
        }

        after = builder.toString()
        builder.clear()
        builder.append(pre)
        builder.append(after)

        return builder.toString()
    }
}