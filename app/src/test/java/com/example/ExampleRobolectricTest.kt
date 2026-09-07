package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.CryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VaultTasks", appName)
  }

  @Test
  fun `verify 24 word mnemonic generation`() {
    val words = CryptoEngine.generate24WordMnemonic()
    assertEquals(24, words.size)
    assertTrue(words.all { it.isNotBlank() })
  }

  @Test
  fun `verify aes key fingerprint generation`() {
    val fingerprint = CryptoEngine.generateKeyFingerprint()
    assertNotNull(fingerprint)
    assertTrue(fingerprint.startsWith("AES-256-GCM :"))
  }
}

