package com.example.a2024110518

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.a2024110518.model.PlayerInfo
import com.example.a2024110518.model.PlayerInfoStore

/**
 * 个人信息 Fragment。
 *
 * 界面展示姓名、性别、学号、电话、邮箱和爱好；点击“修改个人信息”
 * 弹出对话框编辑，保存后刷新界面。离开 Fragment 时用对象序列化写入
 * “player_info.dat”，下次打开应用自动恢复。
 */
class PersonInfoFragment : Fragment() {

    private lateinit var store: PlayerInfoStore
    private lateinit var playerInfo: PlayerInfo

    private lateinit var tvNameValue: TextView
    private lateinit var tvGenderValue: TextView
    private lateinit var tvStudentIdValue: TextView
    private lateinit var tvPhoneValue: TextView
    private lateinit var tvEmailValue: TextView
    private lateinit var tvHobbyValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_person_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNameValue = view.findViewById(R.id.tvNameValue)
        tvGenderValue = view.findViewById(R.id.tvGenderValue)
        tvStudentIdValue = view.findViewById(R.id.tvStudentIdValue)
        tvPhoneValue = view.findViewById(R.id.tvPhoneValue)
        tvEmailValue = view.findViewById(R.id.tvEmailValue)
        tvHobbyValue = view.findViewById(R.id.tvHobbyValue)

        store = PlayerInfoStore(requireContext())
        playerInfo = store.load()

        view.findViewById<Button>(R.id.btnEditInfo).setOnClickListener {
            showEditDialog()
        }

        refreshPlayerInfo()
    }

    override fun onStop() {
        super.onStop()
        if (this::playerInfo.isInitialized) {
            store.save(playerInfo)
        }
    }

    private fun refreshPlayerInfo() {
        tvNameValue.text = getString(R.string.info_name, playerInfo.name)
        tvGenderValue.text = getString(R.string.info_gender, playerInfo.gender)
        tvStudentIdValue.text = getString(R.string.info_student_id, playerInfo.studentId)
        tvPhoneValue.text = getString(R.string.info_phone, playerInfo.phone)
        tvEmailValue.text = getString(R.string.info_email, playerInfo.email)
        tvHobbyValue.text = getString(
            R.string.info_hobby,
            playerInfo.hobbiesText(getString(R.string.hobby_separator))
        )
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_info, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etStudentId = dialogView.findViewById<EditText>(R.id.etStudentId)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val rgGender = dialogView.findViewById<RadioGroup>(R.id.rgGender)
        val cbTravel = dialogView.findViewById<CheckBox>(R.id.cbTravel)
        val cbReading = dialogView.findViewById<CheckBox>(R.id.cbReading)
        val cbGame = dialogView.findViewById<CheckBox>(R.id.cbGame)
        val cbShopping = dialogView.findViewById<CheckBox>(R.id.cbShopping)

        etName.setText(playerInfo.name)
        etStudentId.setText(playerInfo.studentId)
        etPhone.setText(playerInfo.phone)
        etEmail.setText(playerInfo.email)

        if (playerInfo.gender == getString(R.string.gender_female)) {
            rgGender.check(R.id.rbFemale)
        } else {
            rgGender.check(R.id.rbMale)
        }

        cbTravel.isChecked = playerInfo.hobbies.contains(getString(R.string.hobby_travel))
        cbReading.isChecked = playerInfo.hobbies.contains(getString(R.string.hobby_reading))
        cbGame.isChecked = playerInfo.hobbies.contains(getString(R.string.hobby_game))
        cbShopping.isChecked = playerInfo.hobbies.contains(getString(R.string.hobby_shopping))

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_edit_title)
            .setView(dialogView)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = etName.text.toString().trim()
                if (name.isEmpty()) {
                    etName.error = getString(R.string.error_name_empty)
                    return@setOnClickListener
                }

                playerInfo.name = name
                playerInfo.studentId = etStudentId.text.toString().trim()
                playerInfo.phone = etPhone.text.toString().trim()
                playerInfo.email = etEmail.text.toString().trim()
                playerInfo.gender = if (rgGender.checkedRadioButtonId == R.id.rbFemale) {
                    getString(R.string.gender_female)
                } else {
                    getString(R.string.gender_male)
                }

                playerInfo.hobbies = mutableListOf<String>().apply {
                    if (cbTravel.isChecked) add(getString(R.string.hobby_travel))
                    if (cbReading.isChecked) add(getString(R.string.hobby_reading))
                    if (cbGame.isChecked) add(getString(R.string.hobby_game))
                    if (cbShopping.isChecked) add(getString(R.string.hobby_shopping))
                }

                store.save(playerInfo)
                refreshPlayerInfo()
                Toast.makeText(requireContext(), R.string.toast_saved, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
