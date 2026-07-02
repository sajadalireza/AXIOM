package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.HunterDao;
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
public final class HunterRepositoryImpl_Factory implements Factory<HunterRepositoryImpl> {
  private final Provider<HunterDao> hunterDaoProvider;

  private HunterRepositoryImpl_Factory(Provider<HunterDao> hunterDaoProvider) {
    this.hunterDaoProvider = hunterDaoProvider;
  }

  @Override
  public HunterRepositoryImpl get() {
    return newInstance(hunterDaoProvider.get());
  }

  public static HunterRepositoryImpl_Factory create(Provider<HunterDao> hunterDaoProvider) {
    return new HunterRepositoryImpl_Factory(hunterDaoProvider);
  }

  public static HunterRepositoryImpl newInstance(HunterDao hunterDao) {
    return new HunterRepositoryImpl(hunterDao);
  }
}
