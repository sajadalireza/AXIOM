package com.axiom.app;

import com.axiom.app.data.SeedDataHelper;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.repository.SkillRepository;
import com.axiom.app.domain.usecase.CheckStreakOnOpenUseCase;
import com.axiom.app.domain.usecase.GrantDailyLoginBonusUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<CheckStreakOnOpenUseCase> checkStreakOnOpenUseCaseProvider;

  private final Provider<GrantDailyLoginBonusUseCase> grantDailyLoginBonusUseCaseProvider;

  private final Provider<SeedDataHelper> seedDataHelperProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<SkillRepository> skillRepositoryProvider;

  private MainActivity_MembersInjector(Provider<AxiomPreferences> preferencesProvider,
      Provider<CheckStreakOnOpenUseCase> checkStreakOnOpenUseCaseProvider,
      Provider<GrantDailyLoginBonusUseCase> grantDailyLoginBonusUseCaseProvider,
      Provider<SeedDataHelper> seedDataHelperProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider) {
    this.preferencesProvider = preferencesProvider;
    this.checkStreakOnOpenUseCaseProvider = checkStreakOnOpenUseCaseProvider;
    this.grantDailyLoginBonusUseCaseProvider = grantDailyLoginBonusUseCaseProvider;
    this.seedDataHelperProvider = seedDataHelperProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.skillRepositoryProvider = skillRepositoryProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPreferences(instance, preferencesProvider.get());
    injectCheckStreakOnOpenUseCase(instance, checkStreakOnOpenUseCaseProvider.get());
    injectGrantDailyLoginBonusUseCase(instance, grantDailyLoginBonusUseCaseProvider.get());
    injectSeedDataHelper(instance, seedDataHelperProvider.get());
    injectHunterRepository(instance, hunterRepositoryProvider.get());
    injectSkillRepository(instance, skillRepositoryProvider.get());
  }

  public static MembersInjector<MainActivity> create(Provider<AxiomPreferences> preferencesProvider,
      Provider<CheckStreakOnOpenUseCase> checkStreakOnOpenUseCaseProvider,
      Provider<GrantDailyLoginBonusUseCase> grantDailyLoginBonusUseCaseProvider,
      Provider<SeedDataHelper> seedDataHelperProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider) {
    return new MainActivity_MembersInjector(preferencesProvider, checkStreakOnOpenUseCaseProvider, grantDailyLoginBonusUseCaseProvider, seedDataHelperProvider, hunterRepositoryProvider, skillRepositoryProvider);
  }

  @InjectedFieldSignature("com.axiom.app.MainActivity.preferences")
  public static void injectPreferences(MainActivity instance, AxiomPreferences preferences) {
    instance.preferences = preferences;
  }

  @InjectedFieldSignature("com.axiom.app.MainActivity.checkStreakOnOpenUseCase")
  public static void injectCheckStreakOnOpenUseCase(MainActivity instance,
      CheckStreakOnOpenUseCase checkStreakOnOpenUseCase) {
    instance.checkStreakOnOpenUseCase = checkStreakOnOpenUseCase;
  }

  @InjectedFieldSignature("com.axiom.app.MainActivity.grantDailyLoginBonusUseCase")
  public static void injectGrantDailyLoginBonusUseCase(MainActivity instance,
      GrantDailyLoginBonusUseCase grantDailyLoginBonusUseCase) {
    instance.grantDailyLoginBonusUseCase = grantDailyLoginBonusUseCase;
  }

  @InjectedFieldSignature("com.axiom.app.MainActivity.seedDataHelper")
  public static void injectSeedDataHelper(MainActivity instance, SeedDataHelper seedDataHelper) {
    instance.seedDataHelper = seedDataHelper;
  }

  @InjectedFieldSignature("com.axiom.app.MainActivity.hunterRepository")
  public static void injectHunterRepository(MainActivity instance,
      HunterRepository hunterRepository) {
    instance.hunterRepository = hunterRepository;
  }

  @InjectedFieldSignature("com.axiom.app.MainActivity.skillRepository")
  public static void injectSkillRepository(MainActivity instance, SkillRepository skillRepository) {
    instance.skillRepository = skillRepository;
  }
}
