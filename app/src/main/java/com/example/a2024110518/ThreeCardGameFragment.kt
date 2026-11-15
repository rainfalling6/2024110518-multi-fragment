package com.example.a2024110518

/**
 * 三张牌翻牌游戏 Fragment。
 *
 * 实际逻辑全部继承自 [BaseGameFragment]，这里只声明三张牌规则和存档文件。
 */
class ThreeCardGameFragment : BaseGameFragment() {

    override val matchCount: Int = 3
    override val gameFileName: String = "game_three.dat"
    override val titleRes: Int = R.string.game_three_title
    override val hintRes: Int = R.string.game_three_hint

    companion object {
        fun newInstance(): ThreeCardGameFragment = ThreeCardGameFragment()
    }
}
