package com.practicum.playlistmaker.domain.search.use_case

import com.practicum.playlistmaker.domain.search.Resource
import com.practicum.playlistmaker.domain.search.consumer.Consumer
import com.practicum.playlistmaker.domain.search.consumer.ConsumerData
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.search.repository.TrackRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            when (val tracksResource = repository.searchTracks(expression)) {
                is Resource.Error -> consumer.consume(ConsumerData.Error("Сетевая ошибка"))
                is Resource.Success -> consumer.consume(ConsumerData.Data(tracksResource.data))
            }
        }
    }

    override fun saveTrackToHistory(track: Track) = repository.saveTrackToHistory(track)
    override fun getTrackHistory(): List<Track> = repository.getHistory()
    override fun clearTrackHistory() = repository.clearHistory()
}