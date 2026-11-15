package com.example.a2024110518.model

import android.content.Context
import com.example.a2024110518.R
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 个人信息模型。
 *
 * 实现 Serializable 后，个人信息的修改结果会在离开界面时写入
 * “player_info.dat”，下次打开应用时自动恢复。
 */
data class PlayerInfo(
    var name: String = "",
    var gender: String = "",
    var studentId: String = "",
    var phone: String = "",
    var email: String = "",
    var hobbies: MutableList<String> = mutableListOf()
) : Serializable {

    fun hobbiesText(separator: String): String = hobbies.joinToString(separator)

    companion object {
        private const val serialVersionUID = 1L

        /** 首次运行时的默认个人信息，全部来自字符串资源。 */
        fun createDefault(context: Context): PlayerInfo {
            return PlayerInfo(
                name = context.getString(R.string.default_name),
                gender = context.getString(R.string.gender_male),
                studentId = context.getString(R.string.default_student_id),
                phone = context.getString(R.string.default_phone),
                email = context.getString(R.string.default_email),
                hobbies = mutableListOf(
                    context.getString(R.string.hobby_travel),
                    context.getString(R.string.hobby_reading)
                )
            )
        }
    }
}

/**
 * 个人信息的文件读写工具：写文件 openFileOutput，读文件 openFileInput，
 * 对象使用 ObjectOutputStream / ObjectInputStream 序列化。
 */
class PlayerInfoStore(private val context: Context) {

    fun load(): PlayerInfo {
        return try {
            ObjectInputStream(context.openFileInput(FILE_NAME)).use { input ->
                input.readObject() as PlayerInfo
            }
        } catch (e: Exception) {
            PlayerInfo.createDefault(context)
        }
    }

    fun save(info: PlayerInfo) {
        try {
            ObjectOutputStream(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)).use { output ->
                output.writeObject(info)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val FILE_NAME = "player_info.dat"
    }
}
