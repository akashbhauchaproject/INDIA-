package com.example.security

import java.security.MessageDigest
import java.security.SecureRandom

object CryptoEngine {
    private val secureRandom = SecureRandom()

    private val BIP39_WORDS = listOf(
        "abandon", "ability", "able", "about", "above", "absent", "absorb", "abstract",
        "absurd", "abuse", "access", "accident", "account", "accuse", "achieve", "acid",
        "acoustic", "acquire", "across", "act", "action", "actor", "actress", "actual",
        "adapt", "add", "addict", "address", "adjust", "admit", "adult", "advance",
        "advice", "aerobic", "affair", "afford", "afraid", "again", "age", "agent",
        "agree", "ahead", "aim", "air", "airport", "aisle", "alarm", "album",
        "alcohol", "alert", "alien", "all", "alley", "allow", "almost", "alone",
        "alpha", "already", "also", "alter", "always", "amateur", "amazing", "among",
        "amount", "amused", "analyst", "anchor", "ancient", "anger", "angle", "angry",
        "animal", "ankle", "announce", "annual", "another", "answer", "antenna", "antique",
        "anxiety", "any", "apart", "apology", "appear", "apple", "approve", "april",
        "arch", "arctic", "area", "arena", "argue", "arm", "armed", "armor"
    )

    fun generate24WordMnemonic(): List<String> {
        val words = mutableListOf<String>()
        for (i in 0 until 24) {
            val idx = secureRandom.nextInt(BIP39_WORDS.size)
            words.add(BIP39_WORDS[idx])
        }
        return words
    }

    fun generateKeyFingerprint(): String {
        val randomBytes = ByteArray(32)
        secureRandom.nextBytes(randomBytes)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(randomBytes)
        val hex = digest.joinToString("") { "%02x".format(it) }
        return "AES-256-GCM : ${hex.substring(0, 8)}...${hex.substring(hex.length - 8)}"
    }

    fun maskText(text: String, isMasked: Boolean): String {
        if (!isMasked) return text
        return "••••••••••••"
    }

    fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
