package pl.mikoch.asystentsocjalny.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Helpers for launching system intents (phone, SMS, e-mail, maps).
 *
 * Uses ACTION_DIAL (no CALL_PHONE permission required) so the user can
 * confirm the call manually — important for stress-resistant UX.
 */
object IntentLauncher {

    fun dialPhone(context: Context, phone: String) {
        val cleaned = phone.filter { it.isDigit() || it == '+' }
        if (cleaned.isBlank()) {
            toast(context, "Brak numeru telefonu")
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        safeStart(context, intent, "Brak aplikacji telefonu")
    }

    fun sendSms(context: Context, phone: String, body: String = "") {
        val cleaned = phone.filter { it.isDigit() || it == '+' }
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$cleaned")).apply {
            if (body.isNotBlank()) putExtra("sms_body", body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStart(context, intent, "Brak aplikacji SMS")
    }

    fun sendEmail(
        context: Context,
        email: String,
        subject: String = "",
        body: String = ""
    ) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
            if (subject.isNotBlank()) putExtra(Intent.EXTRA_SUBJECT, subject)
            if (body.isNotBlank()) putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStart(context, intent, "Brak aplikacji poczty")
    }

    fun openMap(context: Context, address: String) {
        if (address.isBlank()) {
            toast(context, "Brak adresu")
            return
        }
        val encoded = Uri.encode(address)
        val geo = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encoded"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(geo)
            return
        } catch (_: ActivityNotFoundException) {
            // fall through to web fallback
        }
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        safeStart(context, web, "Brak aplikacji map / przeglądarki")
    }

    private fun safeStart(context: Context, intent: Intent, fallbackMsg: String) {
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            toast(context, fallbackMsg)
        }
    }

    private fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
