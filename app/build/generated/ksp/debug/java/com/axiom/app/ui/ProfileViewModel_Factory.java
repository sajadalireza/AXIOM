package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.data.local.dao.WeeklyReviewDao;
import com.axiom.app.domain.usecase.GetDungeonsUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetMissionsUseCase;
import com.axiom.app.domain.usecase.GetShadowsUseCase;
import com.axiom.app.domain.usecase.GetSkillsUseCase;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<GetShadowsUseCase> getShadowsUseCaseProvider;

  private final Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider;

  private final Provider<GetSkillsUseCase> getSkillsUseCaseProvider;

  private final Provider<WeeklyReviewDao> weeklyReviewDaoProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private ProfileViewModel_Factory(
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetShadowsUseCase> getShadowsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<WeeklyReviewDao> weeklyReviewDaoProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.getShadowsUseCaseProvider = getShadowsUseCaseProvider;
    this.getDungeonsUseCaseProvider = getDungeonsUseCaseProvider;
    this.getSkillsUseCaseProvider = getSkillsUseCaseProvider;
    this.weeklyReviewDaoProvider = weeklyReviewDaoProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(getHunterProfileUseCaseProvider.get(), getMissionsUseCaseProvider.get(), getShadowsUseCaseProvider.get(), getDungeonsUseCaseProvider.get(), getSkillsUseCaseProvider.get(), weeklyReviewDaoProvider.get(), preferencesProvider.get());
  }

  public static ProfileViewModel_Factory create(
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetShadowsUseCase> getShadowsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<WeeklyReviewDao> weeklyReviewDaoProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new ProfileViewModel_Factory(getHunterProfileUseCaseProvider, getMissionsUseCaseProvider, getShadowsUseCaseProvider, getDungeonsUseCaseProvider, getSkillsUseCaseProvider, weeklyReviewDaoProvider, preferencesProvider);
  }

  public static ProfileViewModel newInstance(GetHunterProfileUseCase getHunterProfileUseCase,
      GetMissionsUseCase getMissionsUseCase, GetShadowsUseCase getShadowsUseCase,
      GetDungeonsUseCase getDungeonsUseCase, GetSkillsUseCase getSkillsUseCase,
      WeeklyReviewDao weeklyReviewDao, AxiomPreferences preferences) {
    return new ProfileViewModel(getHunterProfileUseCase, getMissionsUseCase, getShadowsUseCase, getDungeonsUseCase, getSkillsUseCase, weeklyReviewDao, preferences);
  }
}
