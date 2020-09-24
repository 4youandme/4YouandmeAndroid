package org.fouryouandme.core.arch.deps.modules

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.squareup.moshi.Moshi
import org.fouryouandme.core.arch.error.FourYouAndMeError
import org.fouryouandme.core.arch.error.toFourYouAndMeError
import org.fouryouandme.core.arch.error.unknownError
import org.fouryouandme.core.entity.configuration.Text

data class ErrorModule(val moshi: Moshi)

suspend fun <T> ErrorModule.unwrapToEither(
    text: Text? = null,
    block: suspend () -> T
): Either<FourYouAndMeError, T> =
    Either.catch(block).mapLeft { it.toFourYouAndMeError(this, text) }

suspend fun <T> Either<FourYouAndMeError, T?>.noneToError(
    error: FourYouAndMeError? = null
): Either<FourYouAndMeError, T> =
    fold(
        { it.left() },
        { it?.right() ?: (error ?: unknownError()).left() }
    )