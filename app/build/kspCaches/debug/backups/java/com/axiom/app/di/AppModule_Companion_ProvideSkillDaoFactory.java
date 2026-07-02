package com.axiom.app.di;

import com.axiom.app.data.local.AxiomDatabase;
import com.axiom.app.data.local.dao.SkillDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_Companion_ProvideSkillDaoFactory implements Factory<SkillDao> {
  private final Provider<AxiomDatabase> dbProvider;

  private AppModule_Companion_ProvideSkillDaoFactory(Provider<AxiomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SkillDao get() {
    return provideSkillDao(dbProvider.get());
  }

  public static AppModule_Companion_ProvideSkillDaoFactory create(
      Provider<AxiomDatabase> dbProvider) {
    return new AppModule_Companion_ProvideSkillDaoFactory(dbProvider);
  }

  public static SkillDao provideSkillDao(AxiomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.Companion.provideSkillDao(db));
  }
}
