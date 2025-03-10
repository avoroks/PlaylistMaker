package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.media.use_case.PlaylistsInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.model.PlaylistDisplayModel
import com.practicum.playlistmaker.ui.media.state.SharingPlaylistsState
import com.practicum.playlistmaker.ui.utils.SingleEventLiveData
import com.practicum.playlistmaker.utils.getMinuteTitle
import com.practicum.playlistmaker.utils.getSecondTitle
import com.practicum.playlistmaker.utils.getTrackTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor
) :
    ViewModel() {

    private val playlistData = MutableLiveData<PlaylistDisplayModel>()
    fun getPlaylistData(): LiveData<PlaylistDisplayModel> = playlistData


    private val sharingPlaylistDetails = SingleEventLiveData<SharingPlaylistsState>()
    fun getSharingPlaylistDetails(): LiveData<SharingPlaylistsState> = sharingPlaylistDetails

    init {
        updatePlaylistData()
    }

    fun deleteTrackFromPlaylist(trackId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(playlistId, trackId)
            updatePlaylistDataLogic()
        }
    }

    fun sharePlaylist() {
        if (playlistData.value?.tracks?.isEmpty() == true)
            sharingPlaylistDetails.postValue(
                SharingPlaylistsState.EmptyTracksState
            )
        else sharingPlaylistDetails.postValue(
            SharingPlaylistsState.ContentState(
                getSharedText(
                    playlistData.value!!
                )
            )
        )
    }

    fun removePlaylist() {
        viewModelScope.launch(Dispatchers.IO){
            playlistsInteractor.removePlaylist(playlistId)
        }
    }

    private fun getSharedText(model: PlaylistDisplayModel) =
        buildString {
            append("${model.name}\n")
            append("${model.description}\n")
            append("${model.trackCount}\n")
            append("Треки:\n")
            model.tracks.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${calculateDuration(track.trackTimeMillis.toLong())})")
            }
        }

    private suspend fun updatePlaylistDataLogic() {
        playlistsInteractor.getPlaylistById(playlistId)
            .combine(playlistsInteractor.getPlaylistTracks(playlistId)) { playlist, tracks ->
                PlaylistDisplayModel(
                    name = playlist.name,
                    description = playlist.description,
                    imageUrl = playlist.imageUrl,
                    trackCount = "${playlist.trackCount} ${getTrackTitle(playlist.trackCount)}",
                    commonDuration = calculateCommonDuration(tracks),
                    tracks = tracks
                )
            }
            .collect { playlistData.postValue(it) }
    }

    fun updatePlaylistData() = viewModelScope.launch {
        updatePlaylistDataLogic()
    }

    private fun calculateCommonDuration(tracks: List<Track>): String {
        val commonDuration = tracks.sumOf { it.trackTimeMillis.toLong() }
        return calculateDuration(commonDuration)
    }

    private fun calculateDuration(timeMills: Long) : String {
        val minutes = timeMills / 60000
        val seconds = timeMills % 60000 / 1000
        return "$minutes ${getMinuteTitle(minutes)} $seconds ${getSecondTitle(seconds)}"
    }
}