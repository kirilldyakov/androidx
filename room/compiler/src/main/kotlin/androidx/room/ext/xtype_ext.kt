/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.room.ext

import androidx.room.compiler.processing.XType

fun XType.isNotVoid() = !isVoid()

/**
 * Returns `true` if this type represents a valid resolvable type.
 */
fun XType.isNotError() = !isError()

/**
 * Returns `true` if this is not the None type.
 */
fun XType.isNotNone() = !isNone()

/**
 * Returns `true` if this is not `byte` type.
 */
fun XType.isNotByte() = !isByte()
