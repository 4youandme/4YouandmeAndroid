package com.foryouandme.ui.main

import com.foryouandme.core.arch.android.BaseFragment
import com.foryouandme.core.arch.android.getFactory
import com.foryouandme.core.arch.android.viewModelFactory
import com.foryouandme.core.ext.find
import com.foryouandme.core.ext.injector


abstract class MainSectionFragment(
    contentLayoutId: Int
) : BaseFragment(contentLayoutId) {

    protected val mainViewModel: MainViewModel by lazy {
        viewModelFactory(
            mainFragment(),
            getFactory { MainViewModel(navigator, injector.analyticsModule()) }
        )
    }


    protected fun mainFragment(): MainFragment = find()

}