package com.foryouandme.aboutyou.integration

import com.foryouandme.core.arch.android.BaseViewModel
import com.foryouandme.core.arch.android.Empty
import com.foryouandme.core.arch.navigation.Navigator

class AboutYouIntegrationLoginViewModel(
    navigator: Navigator,
) :
    BaseViewModel<
            Empty,
            Empty,
            Empty,
            Empty>
        (navigator, Empty)