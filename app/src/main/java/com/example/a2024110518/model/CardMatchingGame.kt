package com.example.a2024110518.model

import java.io.Serializable
import java.util.Collections

/**
 * 翻牌游戏模型，同时支持“两张牌配对”和“三张牌配对”。
 *
 * 通过构造参数 [matchCount] 区分玩法：
 * - matchCount = 2：24 张牌分成 12 组，每组 2 张同点数牌；
 * - matchCount = 3：24 张牌分成 8 组，每组 3 张同点数牌。
 *
 * 模型实现了 Serializable，GameFragment 在 onStop() 中把整个对象写入文件，
 * 再次进入游戏时通过 ObjectInputStream 恢复分数和已翻开的牌。
 */
class CardMatchingGame(
    private val totalCards: Int = DEFAULT_CARD_COUNT,
    private val matchCount: Int = DEFAULT_MATCH_COUNT
) : Serializable {

    var score = 0
        private set

    private val cardList = mutableListOf<Card>()

    val cards: List<Card>
        get() = cardList

    /** 本局需要同时翻开的牌数（2 或 3）。 */
    val requiredMatchCount: Int
        get() = matchCount

    init {
        require(totalCards > 0 && totalCards % matchCount == 0) {
            "totalCards 必须是 matchCount 的正整数倍"
        }
        require(totalCards / matchCount <= Card.rankStrings.size) {
            "牌数过多，没有足够的点数组成牌局"
        }
        reset()
    }

    /** 重新发牌并清零分数。 */
    fun reset() {
        score = 0
        cardList.clear()

        val groupCount = totalCards / matchCount
        val ranks = Card.rankStrings.toMutableList().apply { Collections.shuffle(this) }
        for (rank in ranks.take(groupCount)) {
            val suits = Card.validSuits.toMutableList().apply { Collections.shuffle(this) }
            for (suit in suits.take(matchCount)) {
                cardList.add(Card(suit = suit, rank = rank))
            }
        }
        Collections.shuffle(cardList)
    }

    fun cardAtIndex(index: Int): Card {
        return cardList[index]
    }

    /**
     * 翻开一张牌。每翻开一张扣 1 分；当桌面上同时有 [matchCount] 张
     * 已翻开的牌时统一判断：配对成功按 match() 的返回值加分并锁定，
     * 配对失败扣 2 分并把已翻开的牌全部扣回背面。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) {
            return
        }
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true

        val chosenCards = cardList.filter { it.isChosen && !it.isMatched }
        if (chosenCards.size == matchCount) {
            val matchScore = chosenCards.first().match(chosenCards.drop(1).toTypedArray())
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                chosenCards.forEach {
                    it.isChosen = false
                    it.isMatched = true
                }
            } else {
                score -= MISMATCH_PENALTY
                chosenCards.forEach { it.isChosen = false }
            }
        }
    }

    /** 是否所有牌都已经配对成功。 */
    fun allCardsMatched(): Boolean = cardList.all { it.isMatched }

    companion object {
        private const val serialVersionUID = 1L

        const val DEFAULT_CARD_COUNT = 24
        const val DEFAULT_MATCH_COUNT = 2

        private const val COST_TO_CHOOSE = 1
        private const val MATCH_BONUS = 4
        private const val MISMATCH_PENALTY = 2
    }
}
