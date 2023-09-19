package metospherus.app.modules

import android.text.Layout

data class FormattingInfo(
    val start: Int,
    val end: Int,
    val style: Int,
    val alignment: Layout.Alignment?
)