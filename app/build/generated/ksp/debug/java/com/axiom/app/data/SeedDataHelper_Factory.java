package com.axiom.app.data;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.repository.MuscleGroupRepository;
import com.axiom.app.domain.repository.SkillRepository;
import com.axiom.app.domain.repository.WarriorProfileRepository;
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
public final class SeedDataHelper_Factory implements Factory<SeedDataHelper> {
  private final Provider<SkillRepository> skillRepositoryProvider;

  private final Provider<MuscleGroupRepository> muscleGroupRepositoryProvider;

  private final Provider<WarriorProfileRepository> warriorProfileRepositoryProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private SeedDataHelper_Factory(Provider<SkillRepository> skillRepositoryProvider,
      Provider<MuscleGroupRepository> muscleGroupRepositoryProvider,
      Provider<WarriorProfileRepository> warriorProfileRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.skillRepositoryProvider = skillRepositoryProvider;
    this.muscleGroupRepositoryProvider = muscleGroupRepositoryProvider;
    this.warriorProfileRepositoryProvider = warriorProfileRepositoryProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public SeedDataHelper get() {
    return newInstance(skillRepositoryProvider.get(), muscleGroupRepositoryProvider.get(), warriorProfileRepositoryProvider.get(), hunterRepositoryProvider.get(), preferencesProvider.get());
  }

  public static SeedDataHelper_Factory create(Provider<SkillRepository> skillRepositoryProvider,
      Provider<MuscleGroupRepository> muscleGroupRepositoryProvider,
      Provider<WarriorProfileRepository> warriorProfileRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new SeedDataHelper_Factory(skillRepositoryProvider, muscleGroupRepositoryProvider, warriorProfileRepositoryProvider, hunterRepositoryProvider, preferencesProvider);
  }

  public static SeedDataHelper newInstance(SkillRepository skillRepository,
      MuscleGroupRepository muscleGroupRepository,
      WarriorProfileRepository warriorProfileRepository, HunterRepository hunterRepository,
      AxiomPreferences preferences) {
    return new SeedDataHelper(skillRepository, muscleGroupRepository, warriorProfileRepository, hunterRepository, preferences);
  }
}
