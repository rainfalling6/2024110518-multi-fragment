package com.example.a2024110518

import com.example.a2024110518.model.Card
import com.example.a2024110518.model.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class CardMatchingGameTest {

    @Test
    fun twoCardGameDealsTwentyFourCardsInTwelveGroups() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 2)
        val ranks = game.cards.groupBy { it.rankValue }

        assertEquals(24, game.cards.size)
        assertEquals(12, ranks.size)
        assertTrue(ranks.values.all { it.size == 2 })
    }

    @Test
    fun threeCardGameDealsTwentyFourCardsInEightGroups() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 3)
        val ranks = game.cards.groupBy { it.rankValue }

        assertEquals(24, game.cards.size)
        assertEquals(8, ranks.size)
        assertTrue(ranks.values.all { it.size == 3 })
    }

    @Test
    fun twoMatchingCardsAreLockedAndScoreIncreases() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 2)
        val pair = game.cards.groupBy { it.rankValue }.values.first()
        val firstIndex = game.cards.indexOf(pair[0])
        val secondIndex = game.cards.indexOf(pair[1])

        game.chooseCardAtIndex(firstIndex)
        game.chooseCardAtIndex(secondIndex)

        assertTrue(pair[0].isMatched)
        assertTrue(pair[1].isMatched)
        assertFalse(pair[0].isChosen)
        assertFalse(pair[1].isChosen)
        assertEquals(14, game.score) // -1 -1 + 4 * 4
    }

    @Test
    fun threeMatchingCardsAreLockedAndScoreIncreases() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 3)
        val group = game.cards.groupBy { it.rankValue }.values.first()
        group.forEach { card -> game.chooseCardAtIndex(game.cards.indexOf(card)) }

        assertTrue(group.all { it.isMatched })
        assertEquals(13, game.score) // -1 -1 -1 + 4 * 4
    }

    @Test
    fun twoDifferentCardsAreTurnedBackAndScoreDecreases() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 2)
        val first = game.cards.first()
        val second = game.cards.first {
            it.rankValue != first.rankValue && it.suitValue != first.suitValue
        }

        game.chooseCardAtIndex(game.cards.indexOf(first))
        game.chooseCardAtIndex(game.cards.indexOf(second))

        assertFalse(first.isChosen)
        assertFalse(second.isChosen)
        assertFalse(first.isMatched)
        assertFalse(second.isMatched)
        assertEquals(-4, game.score) // -1 -1 -2
    }

    @Test
    fun clickingChosenCardAgainCancelsChoice() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 2)
        val index = 0

        game.chooseCardAtIndex(index)
        game.chooseCardAtIndex(index)

        assertFalse(game.cards[index].isChosen)
        assertEquals(-1, game.score)
    }

    @Test
    fun resetClearsScoreAndMatchedState() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 2)
        val pair = game.cards.groupBy { it.rankValue }.values.first()
        game.chooseCardAtIndex(game.cards.indexOf(pair[0]))
        game.chooseCardAtIndex(game.cards.indexOf(pair[1]))

        game.reset()

        assertEquals(0, game.score)
        assertEquals(24, game.cards.size)
        assertTrue(game.cards.none { it.isMatched || it.isChosen })
    }

    @Test
    fun gameStateSurvivesSerialization() {
        val game = CardMatchingGame(totalCards = 24, matchCount = 3)
        val group = game.cards.groupBy { it.rankValue }.values.first()
        group.forEach { card -> game.chooseCardAtIndex(game.cards.indexOf(card)) }
        val expectedScore = game.score

        val bytes = ByteArrayOutputStream().use { buffer ->
            ObjectOutputStream(buffer).use { it.writeObject(game) }
            buffer.toByteArray()
        }
        val restored = ObjectInputStream(ByteArrayInputStream(bytes)).use {
            it.readObject() as CardMatchingGame
        }

        assertEquals(expectedScore, restored.score)
        assertEquals(24, restored.cards.size)
        assertEquals(3, restored.requiredMatchCount)
        assertEquals(group.size, restored.cards.count { it.isMatched })
    }

    @Test
    fun cardMatchReturnsFourForSameRankAndOneForSameSuit() {
        val heartQueen = Card("♥", "Q")
        val diamondQueen = Card("♦", "Q")
        val heartKing = Card("♥", "K")
        val spadeKing = Card("♠", "K")

        assertEquals(4, heartQueen.match(arrayOf(diamondQueen)))
        assertEquals(1, heartQueen.match(arrayOf(heartKing)))
        assertEquals(0, heartQueen.match(arrayOf(spadeKing)))
    }
}
