package com.newbet.app

import android.app.Application
import com.newbet.app.data.affiliate.AffiliateManager
import com.newbet.app.data.repository.SportsRepository

class NewbetApplication : Application() {
    lateinit var affiliateManager: AffiliateManager
        private set

    lateinit var sportsRepository: SportsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        affiliateManager = AffiliateManager(this)
        affiliateManager.checkClipboardForReferral()

        sportsRepository = SportsRepository()
    }
}
