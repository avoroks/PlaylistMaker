package com.practicum.playlistmaker.presentation.tracks

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.presentation.PlayerActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.consumer.Consumer
import com.practicum.playlistmaker.domain.consumer.ConsumerData

class SearchActivity : AppCompatActivity() {

    private val getTracksInteractor = Creator.provideTracksInteractor()

    private val searchRunnable = Runnable {
        progressBar.visibility = VISIBLE
        findTracks(editTextField.text.toString(), trackAdapter)
    }

    private var runnableForMainThread: Runnable? = null

    private val handler = Handler(Looper.getMainLooper())

    private var textValue = EMPTY_TEXT

    private val trackList = ArrayList<Track>()

    private lateinit var connectionProblemBlock: LinearLayout
    private lateinit var emptyResultProblemBlock: LinearLayout

    private lateinit var clearButton: ImageButton
    private lateinit var editTextField: EditText
    private lateinit var historyBlock: NestedScrollView
    private lateinit var progressBar: ProgressBar

    private lateinit var trackAdapter: TrackAdapter

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val backButton = this.findViewById<TextView>(R.id.back_from_search)

        backButton.setOnClickListener {
            this.finish()
        }

        progressBar = findViewById(R.id.progressBar)
        clearButton = this.findViewById(R.id.button_clear)
        editTextField = this.findViewById(R.id.edittext_search)
        editTextField.setText(textValue)

        connectionProblemBlock = findViewById(R.id.connection_problem)
        emptyResultProblemBlock = findViewById(R.id.empty_result)

        trackAdapter = TrackAdapter(trackList) {
            if (clickDebounce()) {
                getTracksInteractor.saveTrackToHistory(it)
                openPlayer(it)
            }
        }

        val rvTracks = findViewById<RecyclerView>(R.id.rv_tracks)
        rvTracks.adapter = trackAdapter

        val trackHistoryAdapter = TrackAdapter(getTracksInteractor.getTrackHistory().toList()) {
            if (clickDebounce()) {
                openPlayer(it)
            }
        }

        val rvHistoryTracks = findViewById<RecyclerView>(R.id.rv_history_tracks)
        rvHistoryTracks.adapter = trackHistoryAdapter

        clearButton.setOnClickListener {
            editTextField.setText(EMPTY_TEXT)
            hideSoftKeyboard(editTextField)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideErrorBlocks()
        }

        historyBlock = findViewById(R.id.history)

        editTextField.addListeners()

        getTracksInteractor.doActionWhenTrackHistoryChanged {
            val history = getTracksInteractor.getTrackHistory()
            trackHistoryAdapter.updateData(history.toList())
            if (history.isNotEmpty()) historyBlock.visibility = VISIBLE
        }

        val clearHistoryButton = findViewById<Button>(R.id.clear_history)

        clearHistoryButton.setOnClickListener {
            getTracksInteractor.clearTrackHistory()
            trackHistoryAdapter.notifyDataSetChanged()
            historyBlock.visibility = GONE
        }

        findViewById<Button>(R.id.update).apply {
            setOnClickListener {
                findTracks(editTextField.text.toString(), trackAdapter)
            }
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(TRACK, track)
        }
        this.startActivity(intent)
    }

    private fun findTracks(
        text: String,
        trackAdapter: TrackAdapter
    ) = getTracksInteractor.searchTracks(
        expression = editTextField.text.toString(),
        consumer = object : Consumer<List<Track>> {
            override fun consume(data: ConsumerData<List<Track>>) {
                val currentRunnable = runnableForMainThread
                if (currentRunnable != null) {
                    handler.removeCallbacks(currentRunnable)
                }

                val newRunnable = Runnable {
                    when (data) {
                        is ConsumerData.Data -> {
                            progressBar.visibility = GONE
                            trackList.clear()
                            hideErrorBlocks()

                            if (data.value.isNotEmpty()) trackList.addAll(data.value)
                            if (trackList.isEmpty()) emptyResultProblemBlock.isVisible =
                                true

                            trackAdapter.notifyDataSetChanged()
                        }

                        is ConsumerData.Error -> {
                            progressBar.visibility = GONE
                            trackList.clear()
                            trackAdapter.notifyDataSetChanged()

                            hideErrorBlocks()
                            connectionProblemBlock.isVisible = true
                        }
                    }
                }
                runnableForMainThread = newRunnable
                handler.post(newRunnable)
            }
        }
    )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textValue = savedInstanceState.getString(SEARCH_TEXT, EMPTY_TEXT)
    }

    private fun hideSoftKeyboard(input: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0);
    }

    private fun hideErrorBlocks() =
        listOf(connectionProblemBlock, emptyResultProblemBlock).forEach {
            it.isVisible = false
        }

    private fun EditText.addListeners() {
        var isBackspaceClicked = false

        this.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                textValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                historyBlock.visibility =
                    if (editTextField.hasFocus() && textValue.isEmpty() && getTracksInteractor.getTrackHistory()
                            .isNotEmpty()
                    ) VISIBLE else GONE
                if (editTextField.hasFocus() && textValue.isNotEmpty()) searchDebounce()
            },
            beforeTextChanged = { _, _, count, after ->
                isBackspaceClicked = after < count
            },
            afterTextChanged = { s ->
                textValue = s.toString()

                if (isBackspaceClicked && textValue.isEmpty()) {
                    historyBlock.visibility =
                        if (getTracksInteractor.getTrackHistory().isNotEmpty()) VISIBLE else GONE
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                }
            }
        )

        this.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && getTracksInteractor.getTrackHistory()
                    .isNotEmpty() && editTextField.text.isEmpty()
            ) historyBlock.visibility = VISIBLE
        }

        this.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTracks(
                    view.text.toString(),
                    trackAdapter
                )
                true
            }
            false
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DEBOUNCE_DELAY)
        }
        return current
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val DEBOUNCE_DELAY = 2000L
        const val TRACK = "TRACK"
    }
}

