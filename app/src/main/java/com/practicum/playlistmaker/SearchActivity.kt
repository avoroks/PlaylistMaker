package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.data.Track
import com.practicum.playlistmaker.data.responses.TracksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var textValue = EMPTY_TEXT

    private val trackService = retrofit.create(ItunesApi::class.java)

    private val trackList = ArrayList<Track>()

    private lateinit var connectionProblemBlock: LinearLayout
    private lateinit var emptyResultProblemBlock: LinearLayout

    private lateinit var clearButton: ImageButton
    private lateinit var editTextField: EditText
    private lateinit var historyBlock: NestedScrollView

    private lateinit var searchHistory: SearchHistory
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var listener: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = this.findViewById<TextView>(R.id.back_from_search)

        backButton.setOnClickListener {
            this.finish()
        }

        clearButton = this.findViewById(R.id.button_clear)
        editTextField = this.findViewById(R.id.edittext_search)
        editTextField.setText(textValue)

        connectionProblemBlock = findViewById(R.id.connection_problem)
        emptyResultProblemBlock = findViewById(R.id.empty_result)

        searchHistory = SearchHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))

        trackAdapter = TrackAdapter(trackList) {
            searchHistory.saveTrackToHistory(it)
        }

        val rvTracks = findViewById<RecyclerView>(R.id.rv_tracks)
        rvTracks.adapter = trackAdapter

        val trackHistoryAdapter = TrackAdapter(searchHistory.getHistory().toList()){}
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

        listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == TRACK_HISTORY) {
                val history = searchHistory.getHistory()
                trackHistoryAdapter.updateData(history.toList())
                if (history.isNotEmpty()) historyBlock.visibility = VISIBLE
            }
        }

        getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener)

        val clearHistoryButton = findViewById<Button>(R.id.clear_history)

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            trackHistoryAdapter.notifyDataSetChanged()
            historyBlock.visibility = GONE
        }

        findViewById<Button>(R.id.update).apply {
            setOnClickListener {
                findTracks(editTextField.text.toString(), trackAdapter)
            }
        }
    }

    private fun findTracks(
        text: String,
        trackAdapter: TrackAdapter
    ) = trackService.findTracks(text).enqueue(object :
        Callback<TracksResponse> {
        override fun onResponse(
            call: Call<TracksResponse>,
            response: Response<TracksResponse>
        ) {
            trackList.clear()
            hideErrorBlocks()

            if (response.isSuccessful) {
                val results = response.body()?.results ?: emptyList()
                if (results.isNotEmpty()) trackList.addAll(results)
                if (trackList.isEmpty()) emptyResultProblemBlock.isVisible = true
            } else connectionProblemBlock.isVisible = true

            trackAdapter.notifyDataSetChanged()
        }

        override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            hideErrorBlocks()
            connectionProblemBlock.isVisible = true
        }
    })

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
        this.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                textValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                historyBlock.visibility = if (editTextField.hasFocus() && textValue.isEmpty()) VISIBLE else GONE
            }
        )

        this.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchHistory.getHistory().isNotEmpty() && editTextField.text.isEmpty())  historyBlock.visibility = VISIBLE
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

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
    }
}

