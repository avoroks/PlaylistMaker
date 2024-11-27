package com.practicum.playlistmaker.domain.search.use_case

import com.practicum.playlistmaker.domain.search.Resource
import com.practicum.playlistmaker.domain.search.consumer.Consumer
import com.practicum.playlistmaker.domain.search.consumer.ConsumerData
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.search.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Error<*> -> Pair(null, result.message)
                is Resource.Success -> Pair(result.data, null)
            }
        }
    }

    override fun saveTrackToHistory(track: Track) = repository.saveTrackToHistory(track)
    override fun getTrackHistory(): Flow<List<Track>> = repository.getHistory()
    override fun clearTrackHistory() = repository.clearHistory()
}