package com.example.authapp.utils

import kotlinx.coroutines.flow.MutableStateFlow

var <T> MutableStateFlow<T>.setValue: T
    get() = value
    set(value) { this.value = value }
