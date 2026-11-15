package com.example.a2024110518.model

import java.io.Serializable

/**
 * 一张扑克牌。
 *
 * 实现 Serializable 后，整个 CardMatchingGame 对象可以直接写入文件，
 * 下次打开应用时再读回来，从而恢复牌局。
 */
class Card(
    private val suit: String,
    private val rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {

    val suitValue: String
        get() = suit

    val rankValue: String
        get() = rank

    /**
     * 判断当前牌与 [otherCards] 是否组成一组。
     *
     * 所有牌点数相同返回 4，所有牌花色相同返回 1，否则返回 0。
     * 方法接收数组，因此两张牌和三张牌模式都可以复用。
     */
    fun match(otherCards: Array<Card>): Int {
        if (otherCards.isEmpty()) {
            return 0
        }
        if (otherCards.all { it.rank == rank }) {
            return 4
        }
        if (otherCards.all { it.suit == suit }) {
            return 1
        }
        return 0
    }

    override fun toString(): String {
        return suit + rank
    }

    companion object {
        private const val serialVersionUID = 1L

        val rankStrings = arrayOf(
            "A", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "J", "Q", "K"
        )
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }
}
