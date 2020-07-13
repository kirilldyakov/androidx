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

package androidx.ui.material

import android.util.DisplayMetrics
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.test.filters.MediumTest
import androidx.ui.core.DensityAmbient
import androidx.ui.core.LayoutDirection
import androidx.ui.core.Modifier
import androidx.ui.core.testTag
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.preferredSize
import androidx.ui.layout.size
import androidx.ui.test.createComposeRule
import androidx.ui.test.performClick
import androidx.ui.test.onNode
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.hasAnyDescendant
import androidx.ui.test.hasTestTag
import androidx.ui.test.isPopup
import androidx.ui.test.runOnIdleCompose
import androidx.ui.test.waitForIdle
import androidx.ui.unit.Density
import androidx.ui.unit.IntBounds
import androidx.ui.unit.IntOffset
import androidx.ui.unit.IntSize
import androidx.ui.unit.Position
import androidx.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@MediumTest
@RunWith(JUnit4::class)
class MenuTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menu_canBeTriggered() {
        var expanded by mutableStateOf(false)

        composeTestRule.clockTestRule.pauseClock()
        composeTestRule.setContent {
            DropdownMenu(
                expanded = expanded,
                toggle = {
                    Box(Modifier.size(20.dp).drawBackground(Color.Blue))
                },
                onDismissRequest = {}
            ) {
                DropdownMenuItem(modifier = Modifier.testTag("MenuContent"), onClick = {}) {
                    Text("Option 1")
                }
            }
        }
        onNodeWithTag("MenuContent").assertDoesNotExist()

        runOnIdleCompose { expanded = true }
        waitForIdle()
        composeTestRule.clockTestRule.advanceClock(InTransitionDuration.toLong())
        onNodeWithTag("MenuContent").assertExists()

        runOnIdleCompose { expanded = false }
        waitForIdle()
        composeTestRule.clockTestRule.advanceClock(OutTransitionDuration.toLong())
        onNodeWithTag("MenuContent").assertDoesNotExist()

        runOnIdleCompose { expanded = true }
        waitForIdle()
        composeTestRule.clockTestRule.advanceClock(InTransitionDuration.toLong())
        onNodeWithTag("MenuContent").assertExists()
    }

    @Test
    fun menu_hasExpectedSize() {
        composeTestRule.setContent {
            with(DensityAmbient.current) {
                DropdownMenu(
                    expanded = true,
                    toggle = {
                        Box(Modifier.size(20.toDp()).drawBackground(Color.Blue))
                    },
                    onDismissRequest = {}
                ) {
                    Box(Modifier.testTag("MenuContent1").preferredSize(70.toDp()))
                    Box(Modifier.testTag("MenuContent2").preferredSize(130.toDp()))
                }
            }
        }

        onNodeWithTag("MenuContent1").assertExists()
        onNodeWithTag("MenuContent2").assertExists()
        val node = onNode(
            isPopup() and hasAnyDescendant(hasTestTag("MenuContent1")) and
                    hasAnyDescendant(hasTestTag("MenuContent2"))
        ).assertExists().fetchSemanticsNode()
        with(composeTestRule.density) {
            assertThat(node.size.width).isEqualTo(130 + MenuElevationInset.toIntPx() * 2)
            assertThat(node.size.height).isEqualTo(200 +
                    DropdownMenuVerticalPadding.toIntPx() * 2 + MenuElevationInset.toIntPx() * 2
            )
        }
    }

    @Test
    fun menu_positioning_bottomEnd() {
        val screenWidth = 500
        val screenHeight = 1000
        val density = Density(1f)
        val displayMetrics = DisplayMetrics().apply {
            widthPixels = screenWidth
            heightPixels = screenHeight
        }
        val anchorPosition = IntOffset(100, 200)
        val anchorSize = IntSize(10, 20)
        val inset = with(density) { MenuElevationInset.toIntPx() }
        val offsetX = 20
        val offsetY = 40
        val popupSize = IntSize(50, 80)

        val ltrPosition = DropdownMenuPositionProvider(
            Position(offsetX.dp, offsetY.dp),
            density,
            displayMetrics
        ).calculatePosition(
            IntBounds(anchorPosition, anchorSize),
            LayoutDirection.Ltr,
            popupSize
        )

        assertThat(ltrPosition.x).isEqualTo(
            anchorPosition.x + anchorSize.width - inset + offsetX
        )
        assertThat(ltrPosition.y).isEqualTo(
            anchorPosition.y + anchorSize.height - inset + offsetY
        )

        val rtlPosition = DropdownMenuPositionProvider(
            Position(offsetX.dp, offsetY.dp),
            density,
            displayMetrics
        ).calculatePosition(
            IntBounds(anchorPosition, anchorSize),
            LayoutDirection.Rtl,
            popupSize
        )

        assertThat(rtlPosition.x).isEqualTo(
            anchorPosition.x - popupSize.width + inset - offsetX
        )
        assertThat(rtlPosition.y).isEqualTo(
            anchorPosition.y + anchorSize.height - inset + offsetY
        )
    }

    @Test
    fun menu_positioning_topStart() {
        val screenWidth = 500
        val screenHeight = 1000
        val density = Density(1f)
        val displayMetrics = DisplayMetrics().apply {
            widthPixels = screenWidth
            heightPixels = screenHeight
        }
        val anchorPosition = IntOffset(450, 950)
        val anchorPositionRtl = IntOffset(50, 950)
        val anchorSize = IntSize(10, 20)
        val inset = with(density) { MenuElevationInset.toIntPx() }
        val offsetX = 20
        val offsetY = 40
        val popupSize = IntSize(150, 80)

        val ltrPosition = DropdownMenuPositionProvider(
            Position(offsetX.dp, offsetY.dp),
            density,
            displayMetrics
        ).calculatePosition(
            IntBounds(anchorPosition, anchorSize),
            LayoutDirection.Ltr,
            popupSize
        )

        assertThat(ltrPosition.x).isEqualTo(
            anchorPosition.x - popupSize.width + inset - offsetX
        )
        assertThat(ltrPosition.y).isEqualTo(
            anchorPosition.y - popupSize.height + inset - offsetY
        )

        val rtlPosition = DropdownMenuPositionProvider(
            Position(offsetX.dp, offsetY.dp),
            density,
            displayMetrics
        ).calculatePosition(
            IntBounds(anchorPositionRtl, anchorSize),
            LayoutDirection.Rtl,
            popupSize
        )

        assertThat(rtlPosition.x).isEqualTo(
            anchorPositionRtl.x + anchorSize.width - inset + offsetX
        )
        assertThat(rtlPosition.y).isEqualTo(
            anchorPositionRtl.y - popupSize.height + inset - offsetY
        )
    }

    @Test
    fun menu_positioning_callback() {
        val screenWidth = 500
        val screenHeight = 1000
        val density = Density(1f)
        val displayMetrics = DisplayMetrics().apply {
            widthPixels = screenWidth
            heightPixels = screenHeight
        }
        val anchorPosition = IntOffset(100, 200)
        val anchorSize = IntSize(10, 20)
        val inset = with(density) { MenuElevationInset.toIntPx() }
        val offsetX = 20
        val offsetY = 40
        val popupSize = IntSize(50, 80)

        var obtainedParentBounds = IntBounds(0, 0, 0, 0)
        var obtainedMenuBounds = IntBounds(0, 0, 0, 0)
        DropdownMenuPositionProvider(
            Position(offsetX.dp, offsetY.dp),
            density,
            displayMetrics
        ) { parentBounds, menuBounds ->
            obtainedParentBounds = parentBounds
            obtainedMenuBounds = menuBounds
        }.calculatePosition(
            IntBounds(anchorPosition, anchorSize),
            LayoutDirection.Ltr,
            popupSize
        )

        assertThat(obtainedParentBounds).isEqualTo(IntBounds(anchorPosition, anchorSize))
        assertThat(obtainedMenuBounds).isEqualTo(
            IntBounds(
                anchorPosition.x + anchorSize.width - inset + offsetX,
                anchorPosition.y + anchorSize.height - inset + offsetY,
                anchorPosition.x + anchorSize.width - inset + offsetX + popupSize.width,
                anchorPosition.y + anchorSize.height - inset + offsetY + popupSize.height
            )
        )
    }

    @Test
    fun dropdownMenuItem_emphasis() {
        var onSurface = Color.Unset
        var enabledContentColor = Color.Unset
        var disabledContentColor = Color.Unset
        lateinit var enabledEmphasis: Emphasis
        lateinit var disabledEmphasis: Emphasis

        composeTestRule.setContent {
            onSurface = MaterialTheme.colors.onSurface
            enabledEmphasis = EmphasisAmbient.current.high
            disabledEmphasis = EmphasisAmbient.current.disabled
            DropdownMenu(
                toggle = { Box(Modifier.size(20.dp)) },
                onDismissRequest = {},
                expanded = true
            ) {
                DropdownMenuItem(onClick = {}) {
                    enabledContentColor = contentColor()
                }
                DropdownMenuItem(enabled = false, onClick = {}) {
                    disabledContentColor = contentColor()
                }
            }
        }

        assertThat(enabledContentColor).isEqualTo(enabledEmphasis.applyEmphasis(onSurface))
        assertThat(disabledContentColor).isEqualTo(disabledEmphasis.applyEmphasis(onSurface))
    }

    @Test
    fun dropdownMenuItem_onClick() {
        var clicked = false
        val onClick: () -> Unit = { clicked = true }

        composeTestRule.setContent {
            DropdownMenuItem(
                onClick,
                modifier = Modifier.testTag("MenuItem").clickable(onClick = onClick)
            ) {
                Box(Modifier.size(40.dp))
            }
        }

        onNodeWithTag("MenuItem").performClick()

        runOnIdleCompose {
            assertThat(clicked).isTrue()
        }
    }
}
