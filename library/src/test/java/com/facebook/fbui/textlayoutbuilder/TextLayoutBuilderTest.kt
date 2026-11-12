/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.fbui.textlayoutbuilder

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.text.TextDirectionHeuristicsCompat
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowPicture
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.TextLayoutMode
import org.robolectric.annotation.TextLayoutMode.Mode.LEGACY

/** Tests [TextLayoutBuilder] */
@Config(sdk = [23], shadows = [ShadowPicture::class])
@RunWith(RobolectricTestRunner::class)
class TextLayoutBuilderTest {

  private lateinit var builder: TextLayoutBuilder
  private var mLayout: Layout? = null

  @Before
  fun setup() {
    builder = TextLayoutBuilder()
    builder.setText(TEST)

    // Clear the cache.
    TextLayoutBuilder.sCache.evictAll()
  }

  // Test setters.
  @Test
  fun testSetText() {
    mLayout = builder.setText("Android").build()
    assertThat(builder.text).isEqualTo("Android")
    assertThat(mLayout?.text).isEqualTo("Android")
  }

  @Test
  fun testSetTextNull() {
    mLayout = builder.setText(null).build()
    assertThat(builder.text).isNull()
    assertThat(mLayout).isNull()
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextAllowed() {
    mLayout = builder.setText("").setShouldLayoutZeroLengthText(true).build()
    assertThat(builder.text).isEqualTo("")
    assertThat(mLayout?.text).isEqualTo("")
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextNotAllowed() {
    mLayout = builder.setText("").setShouldLayoutZeroLengthText(false).build()
    assertThat(builder.text).isEqualTo("")
    assertThat(mLayout).isNull()
  }

  @Test
  fun testSetTextSize() {
    mLayout = builder.setTextSize(10).build()
    assertThat(builder.textSize).isEqualTo(10.0f)
    assertThat(mLayout?.paint?.textSize ?: 0f).isEqualTo(10.0f)
  }

  @Test
  fun testSetTextColor() {
    mLayout = builder.setTextColor(0xFFFF0000.toInt()).build()
    assertThat(builder.textColor).isEqualTo(0xFFFF0000.toInt())
    assertThat(mLayout?.paint?.color).isEqualTo(0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextColorStateList() {
    mLayout = builder.setTextColor(ColorStateList.valueOf(0xFFFF0000.toInt())).build()
    assertThat(builder.textColor).isEqualTo(0xFFFF0000.toInt())
    assertThat(mLayout?.paint?.color).isEqualTo(0xFFFF0000.toInt())
  }

  @Test
  fun testSetLinkColor() {
    mLayout = builder.setLinkColor(0xFFFF0000.toInt()).build()
    assertThat(builder.linkColor).isEqualTo(0xFFFF0000.toInt())
    assertThat(mLayout?.paint?.linkColor).isEqualTo(0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextSpacingExtra() {
    mLayout = builder.setTextSpacingExtra(10f).build()
    assertThat(builder.textSpacingExtra).isEqualTo(10.0f)
    assertThat(mLayout?.spacingAdd ?: 0f).isEqualTo(10.0f)
  }

  @Test
  fun testSetTextSpacingMultiplier() {
    mLayout = builder.setTextSpacingMultiplier(1.5f).build()
    assertThat(builder.textSpacingMultiplier).isEqualTo(1.5f)
    assertThat(mLayout?.spacingMultiplier ?: 0f).isEqualTo(1.5f)
  }

  @Test
  @TextLayoutMode(LEGACY)
  fun testSetTextLineHeight() {
    val lineHeight = 15f
    mLayout = builder.setLineHeight(lineHeight).build()
    assertThat(builder.lineHeight).isEqualTo(15f)
    assertThat(mLayout?.spacingMultiplier ?: 0f).isEqualTo(1.0f)
    val layout = mLayout
    if (layout != null) {
      assertThat(layout.spacingAdd).isEqualTo(lineHeight - layout.paint.getFontMetrics(null))
    }
  }

  @Test
  fun testSetIncludeFontPadding() {
    mLayout = builder.setIncludeFontPadding(false).build()
    assertThat(builder.includeFontPadding).isFalse()
  }

  @Test
  fun testSetAlignment() {
    mLayout = builder.setAlignment(Layout.Alignment.ALIGN_CENTER).build()
    assertThat(builder.alignment).isEqualTo(Layout.Alignment.ALIGN_CENTER)
    assertThat(mLayout?.alignment).isEqualTo(Layout.Alignment.ALIGN_CENTER)
  }

  @Test
  fun testSetTextDirection() {
    mLayout = builder.setTextDirection(TextDirectionHeuristicsCompat.LOCALE).build()
    assertThat(builder.textDirection).isEqualTo(TextDirectionHeuristicsCompat.LOCALE)
  }

  @Test
  fun testSetTypeface() {
    mLayout = builder.setTypeface(Typeface.MONOSPACE).build()
    assertThat(builder.typeface).isEqualTo(Typeface.MONOSPACE)
  }

  @Test
  fun testSetEllipsize() {
    mLayout = builder.setEllipsize(TextUtils.TruncateAt.MARQUEE).build()
    assertThat(builder.ellipsize).isEqualTo(TextUtils.TruncateAt.MARQUEE)
  }

  @Test
  fun testSetSingleLine() {
    mLayout = builder.setSingleLine(true).build()
    assertThat(builder.singleLine).isTrue()
  }

  @Test
  fun testSetMaxLines() {
    mLayout = builder.setMaxLines(10).build()
    assertThat(builder.maxLines).isEqualTo(10)
  }

  @Test
  fun testSetShouldCacheLayout() {
    mLayout = builder.setShouldCacheLayout(false).build()
    assertThat(builder.shouldCacheLayout).isFalse()
  }

  @Test
  fun testSetShouldWarmText() {
    mLayout = builder.setShouldWarmText(true).build()
    assertThat(builder.shouldWarmText).isTrue()
  }

  @Test
  fun testSetBreakStrategy() {
    mLayout = builder.setBreakStrategy(1).build()
    assertThat(builder.breakStrategy).isEqualTo(1)
  }

  @Test
  fun testSetHyphenationFrequency() {
    mLayout = builder.setHyphenationFrequency(1).build()
    assertThat(builder.hyphenationFrequency).isEqualTo(1)
  }

  @Config(sdk = [26])
  @TargetApi(26)
  @Test
  fun testSetJustificationMode() {
    mLayout = builder.setJustificationMode(1).build()
    assertThat(builder.justificationMode).isEqualTo(1)
  }

  @Test
  fun testSetLeftIndents() {
    val leftIndents = intArrayOf(0, 1)
    mLayout = builder.setIndents(leftIndents, null).build()
    assertThat(builder.leftIndents).isEqualTo(leftIndents)
  }

  @Test
  fun testSetRightIndents() {
    val rightIndents = intArrayOf(0, 1)
    mLayout = builder.setIndents(null, rightIndents).build()
    assertThat(builder.rightIndents).isEqualTo(rightIndents)
  }

  @Test
  fun testSetGlyphWarmer() {
    val glyphWarmer = FakeGlyphWarmer()
    mLayout = builder.setGlyphWarmer(glyphWarmer).build()
    assertThat(builder.glyphWarmer).isEqualTo(glyphWarmer)
  }

  // Test functionality.
  @Test
  fun testSingleLine() {
    mLayout = builder.setText(LONG_TEXT).setSingleLine(true).setWidth(1000).build()
    assertThat(mLayout?.lineCount).isEqualTo(1)
  }

  @Test
  fun testMaxLines() {
    mLayout = builder.setText(LONG_TEXT).setMaxLines(2).setWidth(1000).build()
    assertThat(mLayout?.lineCount).isEqualTo(2)
  }

  @Test
  fun testMinEms() {
    builder.setText(LONG_TEXT).setMinEms(10).build()
    assertThat(builder.minEms).isEqualTo(10)
    assertThat(builder.minWidth).isEqualTo(-1)
  }

  @Test
  fun testMaxEms() {
    builder.setText(LONG_TEXT).setMaxEms(10).build()
    assertThat(builder.maxEms).isEqualTo(10)
    assertThat(builder.maxWidth).isEqualTo(-1)
  }

  @Test
  fun testMinWidth() {
    builder.setText(LONG_TEXT).setMinWidth(100).build()
    assertThat(builder.minWidth).isEqualTo(100)
    assertThat(builder.minEms).isEqualTo(-1)
  }

  @Test
  fun testMaxWidth() {
    builder.setText(LONG_TEXT).setMaxWidth(100).build()
    assertThat(builder.maxWidth).isEqualTo(100)
    assertThat(builder.maxEms).isEqualTo(-1)
  }

  @Test
  fun testMaxWidthInvalidatesCachedLayout() {
    mLayout = mBuilder.setText(LONG_TEXT).setWidth(1000).build()
    assertThat(mLayout?.width).isEqualTo(1000)

    mBuilder.setMaxWidth(50)
    val newLayout = mBuilder.build()
    assertThat(newLayout).isNotEqualTo(mLayout)
    assertThat(newLayout?.width).isEqualTo(50)
  }

  @Test
  fun testMinWidthInvalidatesCachedLayout() {
    mLayout = mBuilder.setText(LONG_TEXT).setWidth(100).build()
    assertThat(mLayout?.width).isEqualTo(100)

    mBuilder.setMinWidth(500)
    val newLayout = mBuilder.build()
    assertThat(newLayout).isNotEqualTo(mLayout)
    assertThat(newLayout?.width).isEqualTo(500)
  }

  @Test
  fun testSetShouldLayoutZeroLengthTextWithNullText() {
    // The flag must be settable before any text is provided without crashing.
    mBuilder.setShouldLayoutZeroLengthText(true)
    assertThat(mBuilder.build()).isNull()
  }

  @Test
  fun testDensity() {
    builder.setDensity(1.5f).build()
    assertThat(builder.density).isEqualTo(1.5f)
  }

  @Test
  fun testDrawableState() {
    val drawableState = intArrayOf(0, 1)
    mLayout = builder.setDrawableState(drawableState).build()
    assertThat(builder.drawableState).containsExactly(*drawableState)
  }

  @Test
  fun testNewPaint() {
    val oldPaint = builder.mParams.paint

    // Build the current builder.
    builder.build()

    // Change paint properties.
    builder.setShadowLayer(10.0f, 1.0f, 1.0f, 0)
    val newPaint = builder.mParams.paint
    assertThat(newPaint).isNotEqualTo(oldPaint)
  }

  @Test
  fun testWarmText() {
    val warmer = FakeGlyphWarmer()
    mLayout = builder.setShouldWarmText(true).setGlyphWarmer(warmer).build()
    assertThat(warmer.getLayout()).isEqualTo(mLayout)
  }

  @Test
  fun testDoNotWarmText() {
    val warmer = FakeGlyphWarmer()
    mLayout = builder.setShouldWarmText(false).setGlyphWarmer(warmer).build()
    assertThat(warmer.getLayout()).isNull()
  }

  @Test
  fun testCaching() {
    mLayout = builder.setShouldCacheLayout(true).build()
    val newLayout = builder.build()
    assertThat(newLayout).isEqualTo(mLayout)
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(1)
    assertThat(TextLayoutBuilder.sCache.get(builder.mParams.hashCode())).isEqualTo(mLayout)
  }

  @Test
  fun testNoCaching() {
    mLayout = builder.setShouldCacheLayout(false).build()
    val newLayout = builder.build()
    assertThat(newLayout).isNotEqualTo(mLayout)
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(0)
    assertThat(TextLayoutBuilder.sCache.get(builder.mParams.hashCode())).isNull()
  }

  @Test
  fun testTwoBuildersWithSameParamsAndCaching() {
    mLayout = builder.setShouldCacheLayout(true).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(true).build()
    assertThat(newLayout).isEqualTo(mLayout)
  }

  @Test
  fun testTwoBuildersWithDifferentMaxWidthDoNotShareCache() {
    mLayout = mBuilder.setText(TEST).setWidth(1000).setMaxWidth(500).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setWidth(1000).setMaxWidth(100).build()
    assertThat(newLayout).isNotEqualTo(mLayout)
  }

  @Test
  fun testTwoBuildersWithSameParamsAndNoCaching() {
    mLayout = builder.setShouldCacheLayout(false).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(false).build()
    assertThat(newLayout).isNotEqualTo(mLayout)
  }

  @Test
  fun testSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = builder.setText(spannable).build()
    assertThat(mLayout?.text).isEqualTo(spannable)
  }

  @Test
  fun testCachingSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = builder.setText(spannable).setShouldCacheLayout(true).build()
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(1)
    assertThat(TextLayoutBuilder.sCache.get(builder.mParams.hashCode())).isEqualTo(mLayout)
  }

  @Test
  fun testNoCachingSpannableString() {
    val clickableSpan =
        object : ClickableSpan() {
          override fun onClick(widget: View) {
            // Do nothing.
          }
        }

    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(clickableSpan, 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = builder.setText(spannable).setShouldCacheLayout(true).build()
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(0)
    assertThat(TextLayoutBuilder.sCache.get(builder.mParams.hashCode())).isNull()
  }

  @Config(sdk = [23])
  @Test(expected = IllegalArgumentException::class)
  fun testNullSpansAreCaught() {
    val ssb = SpannableStringBuilder().append("abcd", null, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setText(ssb).build()
  }

  private class FakeGlyphWarmer : GlyphWarmer {
    private var layout: Layout? = null

    override fun warmLayout(layout: Layout?) {
      this.layout = layout
    }

    fun getLayout(): Layout? {
      return layout
    }
  }

  companion object {
    private const val TEST = "TEST"
    private const val LONG_TEXT =
        "Lorem ipsum dolor sit amet test \n" +
            "Lorem ipsum dolor sit amet test \n" +
            "Lorem ipsum dolor sit amet test \n" +
            "Lorem ipsum dolor sit amet test \n"
  }
}
