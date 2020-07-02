package org.fouryouandme.core.cases.optins

import arrow.Kind
import arrow.core.Either
import org.fouryouandme.core.arch.deps.Runtime
import org.fouryouandme.core.arch.error.FourYouAndMeError
import org.fouryouandme.core.entity.optins.OptIns
import org.fouryouandme.core.ext.mapResult
import org.fouryouandme.core.ext.noneToError
import org.fouryouandme.core.ext.unwrapToEither

object OptInsRepository {

    internal fun <F> getOptIns(
        runtime: Runtime<F>,
        token: String
    ): Kind<F, Either<FourYouAndMeError, OptIns>> =
        runtime.fx.concurrent {

            !runtime.injector.optInsApi
                .getOptIns(token, runtime.injector.environment.studyId())
                .async(runtime.fx.M)
                .attempt()
                .unwrapToEither(runtime)
                .mapResult(runtime.fx) { it.get().toOptIns(it) }
                .noneToError(runtime)

        }

}