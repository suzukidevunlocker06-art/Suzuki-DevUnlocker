package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppLanguage
import com.example.data.DevToolsCatalog
import com.example.data.LanguageManager
import com.example.model.DevCategory
import com.example.system.SystemDevBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Suzuki DevUnlocker", appName)
  }

  @Test
  fun `verify catalog has more than 300 tools`() {
    val totalTools = DevToolsCatalog.allTools.size
    assertTrue("Total tools must exceed 300, found $totalTools", totalTools >= 300)
  }

  @Test
  fun `verify catalog has more than 1500 options`() {
    val totalOptions = DevToolsCatalog.totalOptionsCount
    assertTrue("Total options must exceed 1500, found $totalOptions", totalOptions >= 1500)
  }

  @Test
  fun `verify multi language translations work for all supported languages`() {
    for (lang in AppLanguage.values()) {
      val catName = LanguageManager.getCategoryName(DevCategory.GPU_GRAPHICS, lang)
      assertTrue("Category translation for $lang should not be empty", catName.isNotEmpty())
      val toolsBadge = LanguageManager.getUiString("tools_count_badge", lang)
      assertTrue("Tools badge for $lang should not be empty", toolsBadge.isNotEmpty())
    }
  }

  @Test
  fun `verify system telemetry safely initializes`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val telemetry = SystemDevBridge.getTelemetry(context)
    assertTrue("Device model should not be empty", telemetry.deviceModel.isNotEmpty())
    assertTrue("Total RAM should be >= 0", telemetry.totalRamMb >= 0)
  }
}

