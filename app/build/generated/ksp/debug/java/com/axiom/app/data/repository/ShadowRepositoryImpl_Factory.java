package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.ShadowDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ShadowRepositoryImpl_Factory implements Factory<ShadowRepositoryImpl> {
  private final Provider<ShadowDao> shadowDaoProvider;

  private ShadowRepositoryImpl_Factory(Provider<ShadowDao> shadowDaoProvider) {
    this.shadowDaoProvider = shadowDaoProvider;
  }

  @Override
  public ShadowRepositoryImpl get() {
    return newInstance(shadowDaoProvider.get());
  }

  public static ShadowRepositoryImpl_Factory create(Provider<ShadowDao> shadowDaoProvider) {
    return new ShadowRepositoryImpl_Factory(shadowDaoProvider);
  }

  public static ShadowRepositoryImpl newInstance(ShadowDao shadowDao) {
    return new ShadowRepositoryImpl(shadowDao);
  }
}
