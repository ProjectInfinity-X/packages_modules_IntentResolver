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

@file:JvmName("HttpUriMatcher")
package com.android.intentresolver

import com.android.internal.annotations.VisibleForTesting
import java.net.URI

@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
fun String.isHttpUri() =
    kotlin.runCatching {
        URI(this).scheme.takeIf { scheme ->
            "http".compareTo(scheme, true) == 0 || "https".compareTo(scheme, true) == 0
        }
    }.getOrNull() != null