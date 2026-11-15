package com.example.a2024110518

/**
 * 两张牌翻牌游戏 Fragment。
 *
 * 实际逻辑全部继承自 [BaseGameFragment]，这里只声明两张牌规则和存档文件。
 */
class TwoCardGameFragment : BaseGameFragment() {

    override val matchCount: Int = 2
    override val gameFileName: String = "game_two.dat"
    override val titleRes: Int = R.string.game_two_title
    override val hintRes: Int = R.string.game_two_hint

    companion object {
        fun newInstance(): TwoCardGameFragment = TwoCardGameFragment()
    }
}
