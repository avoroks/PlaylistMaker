package com.practicum.playlistmaker.ui.search.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.search.fragment.rv_tracks.TrackAdapter
import com.practicum.playlistmaker.ui.search.state.HistoryState
import com.practicum.playlistmaker.ui.search.state.SearchState
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.debounce
import com.practicum.playlistmaker.utils.debounceWithoutParamsInAction
import com.practicum.playlistmaker.utils.gone
import com.practicum.playlistmaker.utils.show
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private var textValue = EMPTY_TEXT

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onSearchDebounce: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
            viewModel.saveTrackToHistory(it)
            openPlayer(it)
        }

        onSearchDebounce = debounceWithoutParamsInAction(
            DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {
            val text = binding.edittextSearch.text.toString()
            if (text.isNotEmpty()) viewModel.findTracks(text)
        }

        textValue = savedInstanceState?.getString(SEARCH_TEXT, EMPTY_TEXT) ?: ""

        binding.edittextSearch.setText(textValue)

        trackAdapter = TrackAdapter {
            onTrackClickDebounce(it)
        }
        binding.rvTracks.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter {
            openPlayer(it)
        }
        binding.history.rvHistoryTracks.adapter = trackHistoryAdapter

        binding.history.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.getHistoryState().observe(viewLifecycleOwner) { historyState ->
            trackHistoryAdapter.submitList(null)
            when (historyState) {
                is HistoryState.Content -> {
                    trackHistoryAdapter.submitList(historyState.trackList)
                }

                is HistoryState.EmptyHistory -> {
                    trackHistoryAdapter.submitList(emptyList())
                    binding.history.root.gone()
                }
            }
        }

        viewModel.getSearchState().observe(viewLifecycleOwner) { searchState ->
            if (binding.edittextSearch.text.isEmpty()) binding.rvTracks.gone() else {
                hideErrorBlocks()
                when (searchState) {
                    is SearchState.Loading -> {
                        binding.rvTracks.gone()
                        binding.history.root.gone()
                        binding.progressBar.show()
                    }

                    is SearchState.ConnectionProblem -> {
                        hideSoftKeyboard()
                        binding.progressBar.gone()
                        binding.rvTracks.gone()
                        binding.history.root.gone()
                        binding.connectionProblem.root.show()
                    }

                    is SearchState.Content -> {
                        hideSoftKeyboard()
                        binding.history.root.gone()
                        binding.progressBar.gone()

                        trackAdapter.submitList(searchState.trackList)
                        binding.rvTracks.show()
                    }

                    is SearchState.NothingFound -> {
                        hideSoftKeyboard()
                        binding.progressBar.gone()
                        binding.rvTracks.gone()
                        binding.history.root.gone()

                        binding.emptyResult.root.show()
                    }
                }
            }
        }

        binding.buttonClear.setOnClickListener {
            trackAdapter.submitList(null)
            binding.edittextSearch.setText(EMPTY_TEXT)
            hideSoftKeyboard()
            hideErrorBlocks()
        }

        binding.edittextSearch.addListeners()

        binding.connectionProblem.update.apply {
            setOnClickListener {
                viewModel.findTracks(binding.edittextSearch.text.toString())
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateHistory()
    }

    private fun openPlayer(track: Track) {
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        this.findNavController().navigate(action)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, textValue)
    }

    private fun hideSoftKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edittextSearch.windowToken, 0);
    }

    private fun hideErrorBlocks() =
        listOf(binding.connectionProblem.root, binding.emptyResult.root).forEach {
            it.gone()
        }

    private fun EditText.addListeners() {
        var isBackspaceClicked = false

        this.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                textValue = s.toString()
                binding.buttonClear.isVisible = textValue.isNotBlank()
                controlForHistoryVisibility()
                if (binding.edittextSearch.hasFocus() && textValue.isNotBlank()) onSearchDebounce()
            },
            beforeTextChanged = { _, _, count, after ->
                isBackspaceClicked = after < count
            },
            afterTextChanged = { s ->
                textValue = s.toString()

                if (isBackspaceClicked && textValue.isEmpty()) {
                    trackAdapter.submitList(null)
                    controlForHistoryVisibility()
                    hideErrorBlocks()
                    hideSoftKeyboard()
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
        with(binding.history.root) {
            if ((trackHistoryAdapter.itemCount != 0) &&
                binding.edittextSearch.hasFocus() &&
                binding.edittextSearch.text.isEmpty()
            ) show() else gone()
        }
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val DEBOUNCE_DELAY = 2000L
    }
}