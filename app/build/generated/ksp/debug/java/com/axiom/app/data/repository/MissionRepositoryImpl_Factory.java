package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.MissionDao;
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
public final class MissionRepositoryImpl_Factory implements Factory<MissionRepositoryImpl> {
  private final Provider<MissionDao> missionDaoProvider;

  private MissionRepositoryImpl_Factory(Provider<MissionDao> missionDaoProvider) {
    this.missionDaoProvider = missionDaoProvider;
  }

  @Override
  public MissionRepositoryImpl get() {
    return newInstance(missionDaoProvider.get());
  }

  public static MissionRepositoryImpl_Factory create(Provider<MissionDao> missionDaoProvider) {
    return new MissionRepositoryImpl_Factory(missionDaoProvider);
  }

  public static MissionRepositoryImpl newInstance(MissionDao missionDao) {
    return new MissionRepositoryImpl(missionDao);
  }
}
