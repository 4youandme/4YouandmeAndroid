package org.fouryouandme.core.arch.deps

import androidx.annotation.DrawableRes

interface ImageConfiguration {

    /* --- common ---*/

    @DrawableRes
    fun loading(): Int

    @DrawableRes
    fun back(): Int

    @DrawableRes
    fun close(): Int

    @DrawableRes
    fun logo(): Int

    @DrawableRes
    fun logoStudy(): Int

    /* --- auth --- */

    @DrawableRes
    fun welcome(): Int

    @DrawableRes
    fun entryWrong(): Int

    @DrawableRes
    fun entryValid(): Int

    @DrawableRes
    fun signUpNextStep(): Int
}