package com.practicum.playlistmaker.domain.search.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}