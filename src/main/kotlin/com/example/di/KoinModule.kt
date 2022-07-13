package com.example.di

import com.example.repository.HeroRepository
import com.example.repository.HeroRepositoryAlternative
import com.example.repository.HeroRepositoryImpl
import com.example.repository.HeroRepositoryImplAlternative
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinModule = module {
    singleOf(::HeroRepositoryImpl) {
        bind<HeroRepository>()
    }
    singleOf(::HeroRepositoryImplAlternative) {
        bind<HeroRepositoryAlternative>()
    }
//    single<HeroRepositoryAlternative> {
//        HeroRepositoryImplAlternative()
//    }

}