package com.example.a2024110518

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a2024110518.model.CardMatchingGame
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 两个翻牌游戏 Fragment 的公共父类。
 *
 * 子类只需要给出配对张数、存档文件名和界面文字，发牌、计分、网格布局、
 * 序列化存档等逻辑都在这里复用。
 */
abstract class BaseGameFragment : Fragment(), CardAdapter.OnCardClickListener {

    /** 同时翻开的牌数：两张牌游戏为 2，三张牌游戏为 3。 */
    protected abstract val matchCount: Int

    /** 本游戏的存档文件名，两个游戏使用不同文件，互不覆盖。 */
    protected abstract val gameFileName: String

    @get:StringRes
    protected abstract val titleRes: Int

    @get:StringRes
    protected abstract val hintRes: Int

    private lateinit var rvCards: RecyclerView
    private lateinit var tvScore: TextView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private var gameOverAnnounced = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCards = view.findViewById(R.id.rvCards)
        tvScore = view.findViewById(R.id.tvScore)
        view.findViewById<TextView>(R.id.tvGameTitle).setText(titleRes)
        view.findViewById<TextView>(R.id.tvHint).setText(hintRes)

        // 先尝试从文件恢复牌局，没有存档时才创建新游戏。
        game = loadData() ?: CardMatchingGame(CARD_COUNT, matchCount)

        layoutManager = GridLayoutManager(
            requireContext(),
            spanCountForOrientation(resources.configuration)
        )
        rvCards.layoutManager = layoutManager
        rvCards.setHasFixedSize(true)

        adapter = CardAdapter(game.cards, this)
        rvCards.adapter = adapter

        view.findViewById<Button>(R.id.btnReset).setOnClickListener {
            game.reset()
            gameOverAnnounced = false
            adapter.notifyDataSetChanged()
            updateScore()
        }

        updateScore()
    }

    override fun onCardClick(position: Int) {
        game.chooseCardAtIndex(position)
        adapter.notifyDataSetChanged()
        updateScore()

        if (game.allCardsMatched() && !gameOverAnnounced) {
            gameOverAnnounced = true
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_all_matched, game.score),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (this::layoutManager.isInitialized) {
            layoutManager.spanCount = spanCountForOrientation(newConfig)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }

    private fun updateScore() {
        tvScore.text = getString(R.string.score_format, game.score)
    }

    private fun spanCountForOrientation(configuration: Configuration): Int {
        return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LANDSCAPE_SPAN_COUNT
        } else {
            PORTRAIT_SPAN_COUNT
        }
    }

    /** 写文件 openFileOutput + ObjectOutputStream。 */
    private fun saveData() {
        if (!this::game.isInitialized) {
            return
        }
        try {
            requireContext()
                .openFileOutput(gameFileName, android.content.Context.MODE_PRIVATE)
                .use { output ->
                    ObjectOutputStream(output).use { it.writeObject(game) }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 读文件 openFileInput + ObjectInputStream。 */
    private fun loadData(): CardMatchingGame? {
        return try {
            requireContext().openFileInput(gameFileName).use { input ->
                ObjectInputStream(input).use { it.readObject() as CardMatchingGame }
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val CARD_COUNT = 24
        private const val PORTRAIT_SPAN_COUNT = 4
        private const val LANDSCAPE_SPAN_COUNT = 6
    }
}
