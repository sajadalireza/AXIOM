package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.DungeonRepository;
import com.axiom.app.domain.repository.HunterRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class DefeatBossUseCase_Factory implements Factory<DefeatBossUseCase> {
  private final Provider<DungeonRepository> dungeonRepositoryProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private DefeatBossUseCase_Factory(Provider<DungeonRepository> dungeonRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider) {
    this.dungeonRepositoryProvider = dungeonRepositoryProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
  }

  @Override
  public DefeatBossUseCase get() {
    return newInstance(dungeonRepositoryProvider.get(), hunterRepositoryProvider.get());
  }

  public static DefeatBossUseCase_Factory create(
      Provider<DungeonRepository> dungeonRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider) {
    return new DefeatBossUseCase_Factory(dungeonRepositoryProvider, hunterRepositoryProvider);
  }

  public static DefeatBossUseCase newInstance(DungeonRepository dungeonRepository,
      HunterRepository hunterRepository) {
    return new DefeatBossUseCase(dungeonRepository, hunterRepository);
  }
}
