package com.example.a2024110518

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 主 Activity：只负责承载 Fragment，并通过底部导航在三个 Fragment 之间切换。
 *
 * - 个人信息：PersonInfoFragment
 * - 两张牌翻牌游戏：TwoCardGameFragment
 * - 三张牌翻牌游戏：ThreeCardGameFragment
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // 首次创建时先显示个人信息；FragmentManager 会在重建时自动恢复 Fragment。
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_person
            replaceFragment(PersonInfoFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_person -> replaceFragment(PersonInfoFragment())
                R.id.nav_game_two -> replaceFragment(TwoCardGameFragment())
                R.id.nav_game_three -> replaceFragment(ThreeCardGameFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
