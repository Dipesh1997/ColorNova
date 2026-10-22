package asian.paints.colornova.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import asian.paints.colornova.data.ColorItem
import asian.paints.colornova.ui.theme.DarkBackground
import asian.paints.colornova.ui.theme.DarkBorder
import asian.paints.colornova.ui.theme.DarkSurface
import asian.paints.colornova.ui.theme.DarkSurfaceElevated
import asian.paints.colornova.ui.theme.NovaTeal
import asian.paints.colornova.ui.theme.TextPrimary
import asian.paints.colornova.ui.theme.TextSecondary
import asian.paints.colornova.ui.theme.TextTertiary
import java.io.File
import java.io.FileOutputStream

object ColorShareHelper {

    fun generateAndShare(context: Context, colorItem: ColorItem, userNote: String) {
        try {
            val bitmap = createShareBitmap(colorItem, userNote)
            val cachePath = File(context.cacheDir, "shared_colors").apply { mkdirs() }
            val cleanId = colorItem.id.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val imageFile = File(cachePath, "ColorNova_${cleanId}.png")

            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                val caption = buildString {
                    append("${colorItem.name} (${colorItem.id}) • Page ${colorItem.page} • ${colorItem.hex}\n")
                    append("Asian Paints Spectra Fan Deck • ColorNova")
                    if (userNote.isNotBlank()) {
                        append("\nNote: $userNote")
                    }
                }
                putExtra(Intent.EXTRA_TEXT, caption)
                putExtra(Intent.EXTRA_SUBJECT, "Asian Paints Shade: ${colorItem.name} (${colorItem.id})")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Shade Card").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createShareBitmap(colorItem: ColorItem, userNote: String): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background
        val bgPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#0D111D")
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle gradient card border
        val cardBorderPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#2B3652")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(30f, 30f, width - 30f, height - 30f), 32f, 32f, cardBorderPaint)

        // 2. Header text: Brand & Page
        val headerPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#4ECDC4")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.15f
        }
        canvas.drawText("COLORNOVA • ASIAN PAINTS SPECTRA", 60f, 95f, headerPaint)

        val pageTagPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#94A3B8")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("FAN DECK PAGE ${colorItem.page}", width - 60f, 95f, pageTagPaint)

        // 3. Swatch Box
        val swatchLeft = 60f
        val swatchTop = 130f
        val swatchRight = width - 60f
        val swatchBottom = 720f
        val swatchRect = RectF(swatchLeft, swatchTop, swatchRight, swatchBottom)

        val swatchColorInt = try {
            android.graphics.Color.parseColor(colorItem.hex)
        } catch (e: Exception) {
            android.graphics.Color.WHITE
        }

        val swatchPaint = Paint().apply {
            this.color = swatchColorInt
            isAntiAlias = true
        }
        canvas.drawRoundRect(swatchRect, 28f, 28f, swatchPaint)

        // Swatch outline
        val isLightShade = colorItem.isLight()
        val swatchBorderPaint = Paint().apply {
            this.color = if (isLightShade) android.graphics.Color.parseColor("#26000000") else android.graphics.Color.parseColor("#33FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            isAntiAlias = true
        }
        canvas.drawRoundRect(swatchRect, 28f, 28f, swatchBorderPaint)

        // Swatch Pill Badge: Page (top-left inside swatch)
        val pillBgPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#99000000")
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(85f, 155f, 240f, 215f), 16f, 16f, pillBgPaint)

        val pillTextPaint = Paint().apply {
            this.color = android.graphics.Color.WHITE
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PAGE ${colorItem.page}", 162.5f, 195f, pillTextPaint)

        // Swatch Pill Badge: Hex (bottom-right inside swatch)
        canvas.drawRoundRect(RectF(swatchRight - 220f, swatchBottom - 75f, swatchRight - 25f, swatchBottom - 20f), 16f, 16f, pillBgPaint)
        val hexBadgePaint = Paint().apply {
            this.color = android.graphics.Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(colorItem.hex, swatchRight - 122.5f, swatchBottom - 37f, hexBadgePaint)

        // 4. Shade Name
        val namePaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#F8FAFC")
            textSize = 58f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(colorItem.name, 60f, 800f, namePaint)

        // Shade Code & Category line
        val subNamePaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#4ECDC4")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("SHADE CODE: ${colorItem.id}", 60f, 850f, subNamePaint)

        val catTextPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#94A3B8")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Spectra ${colorItem.category.displayName} • Page ${colorItem.page}", width - 60f, 850f, catTextPaint)

        // 5. Specs Panel (Card)
        val specsTop = 880f
        val specsBottom = 1010f
        val specsRect = RectF(60f, specsTop, width - 60f, specsBottom)
        val specsBgPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#161C2C")
            isAntiAlias = true
        }
        canvas.drawRoundRect(specsRect, 20f, 20f, specsBgPaint)
        canvas.drawRoundRect(specsRect, 20f, 20f, cardBorderPaint)

        val specLabelPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#64748B")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val specValuePaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#F8FAFC")
            textSize = 28f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        // Column 1: HEX & RGB
        canvas.drawText("HEX CODE", 95f, specsTop + 45f, specLabelPaint)
        canvas.drawText(colorItem.hex, 95f, specsTop + 85f, specValuePaint)

        canvas.drawText("RGB", 400f, specsTop + 45f, specLabelPaint)
        canvas.drawText("rgb(${colorItem.rgb})", 400f, specsTop + 85f, specValuePaint)

        // Column 2: HSL & TAGS
        canvas.drawText("HSL", 750f, specsTop + 45f, specLabelPaint)
        canvas.drawText(colorItem.hsl, 750f, specsTop + 85f, specValuePaint)

        // 6. User Custom Note Box
        val noteTop = 1035f
        val noteBottom = 1260f
        val noteRect = RectF(60f, noteTop, width - 60f, noteBottom)

        val noteBgPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#1E273D")
            isAntiAlias = true
        }
        canvas.drawRoundRect(noteRect, 20f, 20f, noteBgPaint)

        // Accent indicator bar on left of note box
        val accentBarPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#4ECDC4")
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(60f, noteTop, 72f, noteBottom), 8f, 8f, accentBarPaint)

        val noteHeaderPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#4ECDC4")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.1f
        }
        val noteLabel = if (userNote.isNotBlank()) "CLIENT SPECIFICATION / APPLICATION NOTE:" else "ASIAN PAINTS SPECTRA FANDECK SPECIFICATION:"
        canvas.drawText(noteLabel, 95f, noteTop + 45f, noteHeaderPaint)

        val displayText = if (userNote.isNotBlank()) {
            userNote
        } else {
            "Certified Asian Paints Spectra fan deck shade ${colorItem.id} (${colorItem.name}) from Page ${colorItem.page}. Curated 1,747 professional architectural color system."
        }

        val noteContentPaint = TextPaint().apply {
            this.color = android.graphics.Color.parseColor("#F1F5F9")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, if (userNote.isNotBlank()) Typeface.BOLD else Typeface.NORMAL)
            isAntiAlias = true
        }

        val noteWidth = (width - 190).coerceAtLeast(100)
        val noteLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(displayText, 0, displayText.length, noteContentPaint, noteWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(6f, 1f)
                .setMaxLines(4)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(displayText, noteContentPaint, noteWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 6f, false)
        }

        canvas.save()
        canvas.translate(95f, noteTop + 70f)
        noteLayout.draw(canvas)
        canvas.restore()

        // 7. Footer
        val footerPaint = Paint().apply {
            this.color = android.graphics.Color.parseColor("#64748B")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated with ColorNova • 1,747 Shades Fan Deck", width / 2f, height - 55f, footerPaint)

        return bitmap
    }
}

@Composable
fun ShareColorDialog(
    color: ColorItem,
    onDismiss: () -> Unit,
    onShare: (userNote: String) -> Unit
) {
    val appColors = asian.paints.colornova.ui.theme.LocalAppColors.current
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.toComposeColor())
                        .border(1.dp, if (color.isLight()) Color.Black.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = color.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.textPrimary
                    )
                    Text(
                        text = "Shade ${color.id} • Page ${color.page} • ${color.hex}",
                        fontSize = 12.sp,
                        color = appColors.textSecondary
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Add note or room name to embed in image (optional):",
                    fontSize = 12.5.sp,
                    color = appColors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text(
                            text = "e.g. Living room accent wall, Client Name, 2 coats Royale...",
                            fontSize = 12.sp,
                            color = appColors.textTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = appColors.surfaceElevated,
                        unfocusedContainerColor = appColors.surfaceElevated,
                        focusedBorderColor = appColors.accentTeal,
                        unfocusedBorderColor = appColors.border,
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        cursorColor = appColors.accentTeal
                    ),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onShare(noteText) },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accentTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = if (appColors.isDark) DarkBackground else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Share Image",
                    color = if (appColors.isDark) DarkBackground else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = appColors.textSecondary)
            }
        }
    )
}

