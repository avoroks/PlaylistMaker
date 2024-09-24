package com.practicum.playlistmaker.ui.search.activity

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
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.search.activity.rv_tracks.TrackAdapter
import com.practicum.playlistmaker.ui.search.state.HistoryState
import com.practicum.playlistmaker.ui.search.state.SearchState
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private val searchRunnable = Runnable {
        if (binding.edittextSearch.text.toString().isNotEmpty())
            viewModel.findTracks(binding.edittextSearch.text.toString())
    }

    private val handler = Handler(Looper.getMainLooper())

    private var textValue = EMPTY_TEXT

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.factory()
        )[SearchViewModel::class.java]


        binding.backFromSearch.setOnClickListener {
            this.finish()
        }

        binding.edittextSearch.setText(textValue)

        trackAdapter = TrackAdapter {
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(it)
                openPlayer(it)
            }
        }
        binding.rvTracks.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                openPlayer(it)
            }
        }
        binding.history.rvHistoryTracks.adapter = trackHistoryAdapter

        binding.history.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.getHistoryState().observe(this) { historyState ->
            trackHistoryAdapter.submitList(null)
            when (historyState) {
                is HistoryState.Content -> {
                    trackHistoryAdapter.submitList(historyState.trackList)
                }

                is HistoryState.EmptyHistory -> {
                    trackHistoryAdapter.submitList(emptyList())
                    binding.history.root.visibility = GONE
                }
            }
        }

        viewModel.getSearchState().observe(this) { searchState ->
            hideErrorBlocks()
            when (searchState) {
                is SearchState.Loading -> {
                    binding.rvTracks.visibility = GONE
                    binding.history.root.visibility = GONE
                    binding.progressBar.visibility = VISIBLE
                }

                is SearchState.ConnectionProblem -> {
                    binding.progressBar.visibility = GONE
                    binding.rvTracks.visibility = GONE
                    binding.history.root.visibility = GONE
                    binding.connectionProblem.root.isVisible = true
                }

                is SearchState.Content -> {
                    binding.history.root.visibility = GONE
                    binding.progressBar.visibility = GONE

                    trackAdapter.submitList(searchState.trackList)
                    binding.rvTracks.visibility = VISIBLE
                }

                is SearchState.NothingFound -> {
                    binding.progressBar.visibility = GONE
                    binding.rvTracks.visibility = GONE
                    binding.history.root.visibility = GONE

                    binding.emptyResult.root.isVisible = true
                }
            }
        }

        binding.buttonClear.setOnClickListener {
            trackAdapter.submitList(null)
            binding.edittextSearch.setText(EMPTY_TEXT)
            hideSoftKeyboard(binding.edittextSearch)
            hideErrorBlocks()
        }

        binding.edittextSearch.addListeners()

        binding.connectionProblem.update.apply {
            setOnClickListener {
                viewModel.findTracks(binding.edittextSearch.text.toString())
            }
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(TRACK, track)
        }
        this.startActivity(intent)
    }

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
        listOf(binding.connectionProblem.root, binding.emptyResult.root).forEach {
            it.isVisible = false
        }

    private fun EditText.addListeners() {
        var isBackspaceClicked = false

        this.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                textValue = s.toString()
                binding.buttonClear.isVisible = textValue.isNotBlank()
                controlForHistoryVisibility()
                if (binding.edittextSearch.hasFocus() && textValue.isNotBlank()) searchDebounce()
            },
            beforeTextChanged = { _, _, count, after ->
                isBackspaceClicked = after < count
            },
            afterTextChanged = { s ->
                textValue = s.toString()

                if (isBackspaceClicked && textValue.isEmpty()) {
                    trackAdapter.submitList(null)
                    controlForHistoryVisibility()
                }
            }
        )

        this.setOnFocusChangeListener { _, _ ->
            controlForHistoryVisibility()
        }

        this.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.findTracks(view.text.toString())
                true
            }
            false
        }
    }

    private fun controlForHistoryVisibility() {
        binding.history.root.visibility = if ((trackHistoryAdapter.itemCount != 0) &&
            binding.edittextSearch.hasFocus() &&
            binding.edittextSearch.text.isEmpty()
        ) VISIBLE else GONE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, DEBOUNCE_DELAY)
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val DEBOUNCE_DELAY = 2000L
        const val TRACK = "TRACK"
    }
}

