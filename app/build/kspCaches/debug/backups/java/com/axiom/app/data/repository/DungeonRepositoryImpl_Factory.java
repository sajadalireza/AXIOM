package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.DungeonDao;
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
public final class DungeonRepositoryImpl_Factory implements Factory<DungeonRepositoryImpl> {
  private final Provider<DungeonDao> dungeonDaoProvider;

  private DungeonRepositoryImpl_Factory(Provider<DungeonDao> dungeonDaoProvider) {
    this.dungeonDaoProvider = dungeonDaoProvider;
  }

  @Override
  public DungeonRepositoryImpl get() {
    return newInstance(dungeonDaoProvider.get());
  }

  public static DungeonRepositoryImpl_Factory create(Provider<DungeonDao> dungeonDaoProvider) {
    return new DungeonRepositoryImpl_Factory(dungeonDaoProvider);
  }

  public static DungeonRepositoryImpl newInstance(DungeonDao dungeonDao) {
    return new DungeonRepositoryImpl(dungeonDao);
  }
}
