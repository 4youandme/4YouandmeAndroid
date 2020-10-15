package org.fouryouandme.researchkit.utils

import android.graphics.drawable.BitmapDrawable
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.DrawableRes
import org.fouryouandme.core.entity.configuration.button.button
import org.fouryouandme.core.ext.decodeBase64ImageFx
import org.fouryouandme.core.ext.evalOnMain
import org.fouryouandme.core.ext.startCoroutineAsync

sealed class ImageResource {

    data class AndroidResource(@DrawableRes val image: Int) : ImageResource() {

        companion object {

            fun Int.toAndroidResource(): AndroidResource = AndroidResource(this)

        }

    }

    data class Base64(val image: String) : ImageResource()

}

fun ImageView.applyImage(imageResource: ImageResource): Unit =
    when (imageResource) {
        is ImageResource.AndroidResource -> setImageResource(imageResource.image)
        is ImageResource.Base64 -> startCoroutineAsync {

            val bitmap =
                imageResource.image.decodeBase64ImageFx()
                    .orNull()

            evalOnMain { bitmap?.let { setImageBitmap(it) } }

        }
    }

fun Button.applyImage(imageResource: ImageResource): Unit =
    when (imageResource) {
        is ImageResource.AndroidResource -> setBackgroundResource(imageResource.image)
        is ImageResource.Base64 -> startCoroutineAsync {

            val bitmap =
                imageResource.image.decodeBase64ImageFx()
                    .map { BitmapDrawable(context.resources, it) }
                    .orNull()

            evalOnMain { bitmap?.let { background = it } }

        }
    }

fun ImageView.applyImageAsButton(imageResource: ImageResource): Unit {
    when (imageResource) {
        is ImageResource.AndroidResource -> background =
            button(context.resources, imageResource.image)
        is ImageResource.Base64 -> startCoroutineAsync {

            val bitmap =
                imageResource.image.decodeBase64ImageFx()
                    .map { BitmapDrawable(context.resources, it) }
                    .orNull()

            evalOnMain { bitmap?.let { background = it } }

        }
    }
}