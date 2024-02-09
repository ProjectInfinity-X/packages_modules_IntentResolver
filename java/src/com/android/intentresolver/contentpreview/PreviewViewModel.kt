/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.intentresolver.contentpreview

import android.app.Application
import android.content.Intent
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.android.intentresolver.R
import com.android.intentresolver.inject.Background
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

/** A trivial view model to keep a [PreviewDataProvider] instance over a configuration change */
@HiltViewModel
class PreviewViewModel
@Inject
constructor(
    private val application: Application,
    @Background private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePreviewViewModel() {
    private var previewDataProvider: PreviewDataProvider? = null

    @MainThread
    override fun createOrReuseProvider(targetIntent: Intent): PreviewDataProvider =
        previewDataProvider
            ?: PreviewDataProvider(
                    viewModelScope + dispatcher,
                    targetIntent,
                    application.contentResolver
                )
                .also { previewDataProvider = it }

    override val imageLoader by lazy {
        ImagePreviewImageLoader(
            viewModelScope + dispatcher,
            thumbnailSize =
                application.resources.getDimensionPixelSize(
                    R.dimen.chooser_preview_image_max_dimen
                ),
            application.contentResolver,
            cacheSize = 16
        )
    }

    override val payloadToggleInteractor: PayloadToggleInteractor? by lazy {
        null // TODO: initialize PayloadToggleInteractor()
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T = PreviewViewModel(checkNotNull(extras[APPLICATION_KEY])) as T
            }
    }
}
